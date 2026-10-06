import {onDocumentWritten} from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";
import {defineSecret} from "firebase-functions/params";

import {initializeApp} from "firebase-admin/app";
import {
  FieldValue,
  getFirestore,
} from "firebase-admin/firestore";


// ============================================================
// FIREBASE ADMIN
// ============================================================

initializeApp();

const db = getFirestore();


// ============================================================
// GOOGLE ROUTES API KEY
// ============================================================

const routesApiKey =
  defineSecret("ROUTES_API_KEY");


// ============================================================
// CONSTANTES
// ============================================================

const DISTANCIA_MAXIMA_METROS = 6000;

const CANTIDAD_SOLICITUDES_RUTA = 5;

/*
 * Matriz completa:
 *
 * 25 orígenes + 25 destinos = 50 Place IDs
 * 25 x 25 = 625 elementos
 */
const MAX_CANDIDATAS_MATRIZ = 25;

/*
 * Prefiltro:
 *
 * 1 origen + 49 destinos = 50 Place IDs.
 */
const TAMANO_LOTE_PREFILTRO = 49;


// ============================================================
// TIPOS
// ============================================================

interface SolicitudDisponible {
  id: string;

  placeId: string;

  latitud: number;

  longitud: number;

  fechaRecoleccion: string;
}


interface ElementoMatriz {
  originIndex?: number;

  destinationIndex?: number;

  status?: {
    code?: number;
    message?: string;
  };

  condition?: string;

  distanceMeters?: number;
}


interface RutaGoogle {
  distanceMeters?: number;

  duration?: string;

  optimizedIntermediateWaypointIndex?: number[];

  polyline?: {
    encodedPolyline?: string;
  };
}


interface RespuestaComputeRoutes {
  routes?: RutaGoogle[];
}


// ============================================================
// CREAR WAYPOINT
// ============================================================

function crearWaypoint(
  solicitud: SolicitudDisponible
) {
  return {
    placeId:
      solicitud.placeId,
  };
}


// ============================================================
// COMPUTE ROUTE MATRIX
// ============================================================

async function calcularMatrizDistancias(
  origenes: SolicitudDisponible[],
  destinos: SolicitudDisponible[]
): Promise<ElementoMatriz[]> {

  const origins =
    origenes.map(
      (solicitud) => ({
        waypoint:
          crearWaypoint(solicitud),
      })
    );


  const destinations =
    destinos.map(
      (solicitud) => ({
        waypoint:
          crearWaypoint(solicitud),
      })
    );


  const response =
    await fetch(
      "https://routes.googleapis.com/" +
      "distanceMatrix/v2:computeRouteMatrix",
      {
        method: "POST",

        headers: {
          "Content-Type":
            "application/json",

          "X-Goog-Api-Key":
            routesApiKey.value(),

          "X-Goog-FieldMask":
            "originIndex," +
            "destinationIndex," +
            "status," +
            "condition," +
            "distanceMeters",
        },

        body:
          JSON.stringify({
            origins:
              origins,

            destinations:
              destinations,

            travelMode:
              "DRIVE",

            routingPreference:
              "TRAFFIC_AWARE",

            regionCode:
              "CO",

            units:
              "METRIC",
          }),
      }
    );


  if (!response.ok) {

    const errorText =
      await response.text();


    throw new Error(
      "Error Google Route Matrix " +
      `${response.status}: ${errorText}`
    );
  }


  return (await response.json()) as ElementoMatriz[];
}


// ============================================================
// OBTENER DISTANCIA
// ============================================================

function obtenerDistancia(
  matriz: ElementoMatriz[],
  origenIndex: number,
  destinoIndex: number
): number | null {

  const elemento =
    matriz.find(
      (item) =>
        item.originIndex === origenIndex &&
        item.destinationIndex === destinoIndex
    );


  if (!elemento) {
    return null;
  }


  if (
    elemento.condition !==
    "ROUTE_EXISTS"
  ) {
    return null;
  }


  if (
    typeof elemento.distanceMeters !==
    "number"
  ) {
    return null;
  }


  return elemento.distanceMeters;
}


// ============================================================
// DISTANCIA EN AMBOS SENTIDOS
// ============================================================

/*
 * Regla ReBottle:
 *
 * Math.max(
 *   distanciaA_B,
 *   distanciaB_A
 * ) <= 6000
 */

function obtenerDistanciaSimetrica(
  matriz: ElementoMatriz[],
  indiceA: number,
  indiceB: number
): number | null {

  const distanciaAB =
    obtenerDistancia(
      matriz,
      indiceA,
      indiceB
    );


  const distanciaBA =
    obtenerDistancia(
      matriz,
      indiceB,
      indiceA
    );


  if (
    distanciaAB === null ||
    distanciaBA === null
  ) {
    return null;
  }


  return Math.max(
    distanciaAB,
    distanciaBA
  );
}


// ============================================================
// COMPROBAR LOS 6 KM
// ============================================================

function cumplenDistanciaMaxima(
  matriz: ElementoMatriz[],
  indiceA: number,
  indiceB: number
): boolean {

  const distancia =
    obtenerDistanciaSimetrica(
      matriz,
      indiceA,
      indiceB
    );


  if (distancia === null) {
    return false;
  }


  return (
    distancia <=
    DISTANCIA_MAXIMA_METROS
  );
}


// ============================================================
// VALIDAR TODAS LAS PAREJAS DE UN GRUPO
// ============================================================

function grupoCumpleDistancia(
  indices: number[],
  matriz: ElementoMatriz[]
): boolean {

  for (
    let i = 0;
    i < indices.length;
    i++
  ) {

    for (
      let j = i + 1;
      j < indices.length;
      j++
    ) {

      if (
        !cumplenDistanciaMaxima(
          matriz,
          indices[i],
          indices[j]
        )
      ) {
        return false;
      }
    }
  }


  return true;
}


// ============================================================
// BUSCAR GRUPO DE CINCO
// ============================================================

/*
 * La posición 0 siempre corresponde a la solicitud
 * que acaba de crearse o modificarse.
 *
 * Buscamos otras cuatro.
 */

function buscarGrupoDeCinco(
  candidatas: SolicitudDisponible[],
  matriz: ElementoMatriz[]
): number[] | null {

  if (
    candidatas.length <
    CANTIDAD_SOLICITUDES_RUTA
  ) {
    return null;
  }


  for (
    let a = 1;
    a < candidatas.length - 3;
    a++
  ) {

    for (
      let b = a + 1;
      b < candidatas.length - 2;
      b++
    ) {

      for (
        let c = b + 1;
        c < candidatas.length - 1;
        c++
      ) {

        for (
          let d = c + 1;
          d < candidatas.length;
          d++
        ) {

          const grupo = [
            0,
            a,
            b,
            c,
            d,
          ];


          if (
            grupoCumpleDistancia(
              grupo,
              matriz
            )
          ) {
            return grupo;
          }
        }
      }
    }
  }


  return null;
}


// ============================================================
// PREFILTRO DE SOLICITUDES CERCANAS
// ============================================================

async function buscarSolicitudesCercanas(
  solicitudActual: SolicitudDisponible,
  otrasSolicitudes: SolicitudDisponible[]
): Promise<Array<{
  solicitud: SolicitudDisponible;
  distanciaMaxima: number;
}>> {

  const resultados:
    Array<{
      solicitud: SolicitudDisponible;
      distanciaMaxima: number;
    }> = [];


  for (
    let inicio = 0;
    inicio < otrasSolicitudes.length;
    inicio += TAMANO_LOTE_PREFILTRO
  ) {

    const lote =
      otrasSolicitudes.slice(
        inicio,
        inicio + TAMANO_LOTE_PREFILTRO
      );


    /*
     * Calculamos ambos sentidos:
     *
     * actual -> otras
     * otras  -> actual
     */

    const [
      matrizIda,
      matrizRegreso,
    ] =
      await Promise.all([

        calcularMatrizDistancias(
          [solicitudActual],
          lote
        ),

        calcularMatrizDistancias(
          lote,
          [solicitudActual]
        ),
      ]);


    for (
      let i = 0;
      i < lote.length;
      i++
    ) {

      const ida =
        matrizIda.find(
          (item) =>
            item.originIndex === 0 &&
            item.destinationIndex === i
        );


      const regreso =
        matrizRegreso.find(
          (item) =>
            item.originIndex === i &&
            item.destinationIndex === 0
        );


      const distanciaIda =
        (
          ida?.condition ===
            "ROUTE_EXISTS" &&
          typeof ida.distanceMeters ===
            "number"
        )
          ? ida.distanceMeters
          : null;


      const distanciaRegreso =
        (
          regreso?.condition ===
            "ROUTE_EXISTS" &&
          typeof regreso.distanceMeters ===
            "number"
        )
          ? regreso.distanceMeters
          : null;


      if (
        distanciaIda === null ||
        distanciaRegreso === null
      ) {
        continue;
      }


      const distanciaMaxima =
        Math.max(
          distanciaIda,
          distanciaRegreso
        );


      if (
        distanciaMaxima <=
        DISTANCIA_MAXIMA_METROS
      ) {

        resultados.push({
          solicitud:
            lote[i],

          distanciaMaxima:
            distanciaMaxima,
        });
      }
    }
  }


  // Más cercanas primero.
  resultados.sort(
    (a, b) =>
      a.distanciaMaxima -
      b.distanciaMaxima
  );


  return resultados;
}


// ============================================================
// ELEGIR EXTREMOS
// ============================================================

/*
 * Como todavía no tenemos definido un depósito/punto
 * de salida del recolector, usamos como extremos las
 * dos solicitudes más alejadas dentro del grupo.
 */

function elegirExtremos(
  indicesGrupo: number[],
  matriz: ElementoMatriz[]
): {
  origen: number;
  destino: number;
} {

  let origen =
    indicesGrupo[0];

  let destino =
    indicesGrupo[
      indicesGrupo.length - 1
    ];

  let mayorDistancia = -1;


  for (
    let i = 0;
    i < indicesGrupo.length;
    i++
  ) {

    for (
      let j = i + 1;
      j < indicesGrupo.length;
      j++
    ) {

      const distancia =
        obtenerDistanciaSimetrica(
          matriz,
          indicesGrupo[i],
          indicesGrupo[j]
        );


      if (
        distancia !== null &&
        distancia > mayorDistancia
      ) {

        mayorDistancia =
          distancia;

        origen =
          indicesGrupo[i];

        destino =
          indicesGrupo[j];
      }
    }
  }


  return {
    origen,
    destino,
  };
}


// ============================================================
// COMPUTE ROUTES
// ============================================================

async function calcularRutaOptimizada(
  origen: SolicitudDisponible,
  destino: SolicitudDisponible,
  intermedias: SolicitudDisponible[]
): Promise<{
  ordenIntermedias: number[];
  distanciaTotalMetros: number;
  duracionTotalSegundos: number;
  polyline: string;
}> {

  const response =
    await fetch(
      "https://routes.googleapis.com/" +
      "directions/v2:computeRoutes",
      {
        method: "POST",

        headers: {
          "Content-Type":
            "application/json",

          "X-Goog-Api-Key":
            routesApiKey.value(),

          "X-Goog-FieldMask":
            "routes.distanceMeters," +
            "routes.duration," +
            "routes.polyline.encodedPolyline," +
            "routes.optimizedIntermediateWaypointIndex",
        },

        body:
          JSON.stringify({
            origin:
              crearWaypoint(origen),

            destination:
              crearWaypoint(destino),

            intermediates:
              intermedias.map(
                (solicitud) =>
                  crearWaypoint(solicitud)
              ),

            travelMode:
              "DRIVE",

            routingPreference:
              "TRAFFIC_AWARE",

            optimizeWaypointOrder:
              true,

            regionCode:
              "CO",

            units:
              "METRIC",
          }),
      }
    );


  if (!response.ok) {

    const errorText =
      await response.text();


    throw new Error(
      "Error Google Compute Routes " +
      `${response.status}: ${errorText}`
    );
  }


  const resultado =
    (await response.json()) as RespuestaComputeRoutes;


  const ruta =
    resultado.routes?.[0];


  if (!ruta) {

    throw new Error(
      "Google Routes no devolvió una ruta."
    );
  }


  const ordenIntermedias =
    ruta.optimizedIntermediateWaypointIndex
    ??
    intermedias.map(
      (_, index) => index
    );


  const duracionTexto =
    ruta.duration ?? "0s";


  const duracionTotalSegundos =
    Math.round(
      Number.parseFloat(
        duracionTexto.replace(
          "s",
          ""
        )
      )
    );


  return {
    ordenIntermedias:
      ordenIntermedias,

    distanciaTotalMetros:
      ruta.distanceMeters ?? 0,

    duracionTotalSegundos:
      duracionTotalSegundos,

    polyline:
      ruta.polyline
        ?.encodedPolyline ?? "",
  };
}


// ============================================================
// CREAR RUTA EN FIRESTORE
// ============================================================

async function crearRutaEnFirestore(
  solicitudes: SolicitudDisponible[],
  fechaRecoleccion: string,
  ordenSolicitudes: string[],
  distanciaTotalMetros: number,
  duracionTotalSegundos: number,
  polyline: string
): Promise<string> {

  const contadorRef =
    db
      .collection("contadores")
      .doc("rutas");


  return await db.runTransaction(
    async (transaction) => {

      // ======================================================
      // CONTADOR
      // ======================================================

      const contadorSnapshot =
        await transaction.get(
          contadorRef
        );


      const ultimoId =
        contadorSnapshot.get(
          "ultimoId"
        ) as number | undefined;


      const nuevoNumero =
        (ultimoId ?? 0) + 1;


      const rutaId =
        `RT-${
          nuevoNumero
            .toString()
            .padStart(
              4,
              "0"
            )
        }`;


      // ======================================================
      // REFERENCIAS
      // ======================================================

      const referencias =
        solicitudes.map(
          (solicitud) =>
            db
              .collection("solicitudes")
              .doc(solicitud.id)
        );


      // ======================================================
      // LEER LAS CINCO SOLICITUDES
      // ======================================================

      const snapshots = [];


      for (
        const referencia of referencias
      ) {

        const solicitudSnapshot =
          await transaction.get(
            referencia
          );


        snapshots.push(
          solicitudSnapshot
        );
      }


      // ======================================================
      // VOLVER A VALIDAR ANTES DE ESCRIBIR
      // ======================================================

      for (
        const solicitudSnapshot of snapshots
      ) {

        if (!solicitudSnapshot.exists) {

          throw new Error(
            "Una solicitud ya no existe."
          );
        }


        const data =
          solicitudSnapshot.data();


        if (
          data?.estado !==
          "PUBLICADA"
        ) {

          throw new Error(
            `La solicitud ${solicitudSnapshot.id} ` +
            "ya no está PUBLICADA."
          );
        }


        if (
          data?.rutaId !== null &&
          data?.rutaId !== undefined
        ) {

          throw new Error(
            `La solicitud ${solicitudSnapshot.id} ` +
            "ya pertenece a otra ruta."
          );
        }


        // ====================================================
        // VALIDAR FECHA NUEVAMENTE
        // ====================================================

        if (
          data?.fechaRecoleccion !==
          fechaRecoleccion
        ) {

          throw new Error(
            `La solicitud ${solicitudSnapshot.id} ` +
            "no tiene la misma fecha de recolección."
          );
        }
      }


      // ======================================================
      // CREAR RUTA
      // ======================================================

      const rutaRef =
        db
          .collection("rutas")
          .doc(rutaId);


      transaction.set(
        rutaRef,
        {
          solicitudesIds:
            solicitudes.map(
              (solicitud) =>
                solicitud.id
            ),

          fechaRecoleccion:
            fechaRecoleccion,

          estado:
            "PUBLICADA",

          recolectorId:
            null,

          ordenSolicitudes:
            ordenSolicitudes,

          distanciaTotalMetros:
            distanciaTotalMetros,

          duracionTotalSegundos:
            duracionTotalSegundos,

          polyline:
            polyline,

          creadaEn:
            FieldValue.serverTimestamp(),
        }
      );


      // ======================================================
      // ACTUALIZAR SOLICITUDES
      // ======================================================

      for (
        const referencia of referencias
      ) {

        transaction.update(
          referencia,
          {
            estado:
              "EN_RUTA",

            rutaId:
              rutaId,
          }
        );
      }


      // ======================================================
      // ACTUALIZAR CONTADOR
      // ======================================================

      transaction.set(
        contadorRef,
        {
          ultimoId:
            nuevoNumero,
        },
        {
          merge:
            true,
        }
      );


      return rutaId;
    }
  );
}


// ============================================================
// CLOUD FUNCTION
// ============================================================

export const procesarSolicitudPublicada =
  onDocumentWritten(
    {
      document:
        "solicitudes/{solicitudId}",

      secrets: [
        routesApiKey,
      ],
    },

    async (event) => {

      try {

        // ====================================================
        // DATOS DEL EVENTO
        // ====================================================

        const cambio =
          event.data;


        if (!cambio) {

          logger.warn(
            "No se encontraron datos del evento."
          );

          return;
        }


        const antes =
          cambio.before;


        const despues =
          cambio.after;


        const solicitudId =
          event.params.solicitudId;


        // ====================================================
        // IGNORAR ELIMINACIONES
        // ====================================================

        /*
         * onDocumentWritten se ejecuta al:
         *
         * - crear
         * - modificar
         * - eliminar
         *
         * Para formar rutas no necesitamos procesar
         * eliminaciones.
         */

        if (!despues.exists) {

          logger.info(
            "Solicitud eliminada. No se procesa para rutas.",
            {
              solicitudId:
                solicitudId,
            }
          );

          return;
        }


        const data =
          despues.data();


        if (!data) {

          logger.warn(
            "La solicitud no contiene datos."
          );

          return;
        }


        // ====================================================
        // CREACIÓN O MODIFICACIÓN
        // ====================================================

        const esCreacion =
          !antes.exists;


        const dataAnterior =
          antes.exists
            ? antes.data()
            : undefined;


        // ====================================================
        // DETERMINAR SI EL CAMBIO AFECTA LAS RUTAS
        // ====================================================

        /*
         * Queremos recalcular solamente si cambia:
         *
         * - fechaRecoleccion
         * - placeId
         * - latitud
         * - longitud
         * - estado
         * - rutaId
         *
         * Cambiar peso o materiales no necesita
         * consultar nuevamente Google Routes.
         */

        const cambioRelevante =
          esCreacion ||

          dataAnterior?.fechaRecoleccion !==
            data.fechaRecoleccion ||

          dataAnterior?.placeId !==
            data.placeId ||

          dataAnterior?.latitud !==
            data.latitud ||

          dataAnterior?.longitud !==
            data.longitud ||

          dataAnterior?.estado !==
            data.estado ||

          dataAnterior?.rutaId !==
            data.rutaId;


        if (!cambioRelevante) {

          logger.info(
            "La solicitud fue modificada, " +
            "pero el cambio no afecta la creación de rutas.",
            {
              solicitudId:
                solicitudId,
            }
          );

          return;
        }


        // ====================================================
        // LOG
        // ====================================================

        logger.info(
          esCreacion
            ? "Nueva solicitud detectada"
            : "Solicitud modificada detectada",
          {
            solicitudId:
              solicitudId,

            tipoEvento:
              esCreacion
                ? "CREACION"
                : "MODIFICACION",

            estado:
              data.estado,

            rutaId:
              data.rutaId,

            placeId:
              data.placeId,

            fechaRecoleccion:
              data.fechaRecoleccion,

            latitud:
              data.latitud,

            longitud:
              data.longitud,
          }
        );


        // ====================================================
        // VALIDAR ESTADO
        // ====================================================

        /*
         * Esto evita ciclos.
         *
         * Cuando creamos una ruta hacemos:
         *
         * PUBLICADA -> EN_RUTA
         *
         * El trigger vuelve a ejecutarse,
         * pero termina aquí.
         */

        if (
          data.estado !==
          "PUBLICADA"
        ) {

          logger.info(
            "La solicitud no está PUBLICADA. " +
            "No se intenta crear una ruta.",
            {
              solicitudId:
                solicitudId,

              estado:
                data.estado,
            }
          );

          return;
        }


        // ====================================================
        // VALIDAR RUTA ID
        // ====================================================

        if (
          data.rutaId !== null &&
          data.rutaId !== undefined
        ) {

          logger.info(
            "La solicitud ya pertenece a una ruta.",
            {
              solicitudId:
                solicitudId,

              rutaId:
                data.rutaId,
            }
          );

          return;
        }


        // ====================================================
        // VALIDAR PLACE ID
        // ====================================================

        if (
          typeof data.placeId !==
            "string" ||

          data.placeId.trim() ===
            ""
        ) {

          logger.warn(
            "La solicitud no tiene un Place ID válido.",
            {
              solicitudId:
                solicitudId,
            }
          );

          return;
        }


        // ====================================================
        // VALIDAR COORDENADAS
        // ====================================================

        if (
          typeof data.latitud !==
            "number" ||

          typeof data.longitud !==
            "number"
        ) {

          logger.warn(
            "La solicitud no tiene coordenadas válidas.",
            {
              solicitudId:
                solicitudId,
            }
          );

          return;
        }


        // ====================================================
        // VALIDAR FECHA
        // ====================================================

        if (
          typeof data.fechaRecoleccion !==
            "string" ||

          data.fechaRecoleccion.trim() ===
            ""
        ) {

          logger.warn(
            "La solicitud no tiene " +
            "fecha de recolección válida.",
            {
              solicitudId:
                solicitudId,
            }
          );

          return;
        }


        const fechaRecoleccion =
          data.fechaRecoleccion.trim();


        logger.info(
          "Buscando solicitudes para la misma fecha",
          {
            solicitudId:
              solicitudId,

            fechaRecoleccion:
              fechaRecoleccion,
          }
        );


        // ====================================================
        // BUSCAR SOLICITUDES DE LA MISMA FECHA
        // ====================================================

        const disponiblesSnapshot =
          await db
            .collection("solicitudes")

            .where(
              "estado",
              "==",
              "PUBLICADA"
            )

            .where(
              "rutaId",
              "==",
              null
            )

            .where(
              "fechaRecoleccion",
              "==",
              fechaRecoleccion
            )

            .get();


        // ====================================================
        // CONVERTIR A SolicitudDisponible
        // ====================================================

        const solicitudesDisponibles:
          SolicitudDisponible[] =

          disponiblesSnapshot
            .docs
            .map(
              (document) => {

                const solicitud =
                  document.data();


                return {
                  id:
                    document.id,

                  placeId:
                    solicitud.placeId,

                  latitud:
                    solicitud.latitud,

                  longitud:
                    solicitud.longitud,

                  fechaRecoleccion:
                    solicitud.fechaRecoleccion,
                };
              }
            )
            .filter(
              (solicitud) =>

                typeof solicitud.placeId ===
                  "string" &&

                solicitud.placeId.trim() !==
                  "" &&

                typeof solicitud.latitud ===
                  "number" &&

                typeof solicitud.longitud ===
                  "number" &&

                typeof solicitud.fechaRecoleccion ===
                  "string" &&

                solicitud.fechaRecoleccion ===
                  fechaRecoleccion
            );


        logger.info(
          "Solicitudes disponibles para la misma fecha",
          {
            fechaRecoleccion:
              fechaRecoleccion,

            cantidad:
              solicitudesDisponibles.length,
          }
        );


        // ====================================================
        // NECESITAMOS CINCO
        // ====================================================

        if (
          solicitudesDisponibles.length <
          CANTIDAD_SOLICITUDES_RUTA
        ) {

          logger.info(
            "Todavía no hay 5 solicitudes " +
            "disponibles para esta fecha.",
            {
              fechaRecoleccion:
                fechaRecoleccion,

              disponibles:
                solicitudesDisponibles.length,

              necesarias:
                CANTIDAD_SOLICITUDES_RUTA,
            }
          );

          return;
        }


        // ====================================================
        // ENCONTRAR LA SOLICITUD ACTUAL
        // ====================================================

        const solicitudActual =
          solicitudesDisponibles.find(
            (solicitud) =>
              solicitud.id ===
              solicitudId
          );


        if (!solicitudActual) {

          logger.warn(
            "No se encontró la solicitud que activó " +
            "el evento entre las disponibles.",
            {
              solicitudId:
                solicitudId,
            }
          );

          return;
        }


        const otrasSolicitudes =
          solicitudesDisponibles.filter(
            (solicitud) =>
              solicitud.id !==
              solicitudId
          );


        // ====================================================
        // PREFILTRO GOOGLE ROUTES
        // ====================================================

        const cercanas =
          await buscarSolicitudesCercanas(
            solicitudActual,
            otrasSolicitudes
          );


        logger.info(
          "Solicitudes de la misma fecha a máximo " +
          "6 km de la solicitud actual en ambos sentidos",
          {
            fechaRecoleccion:
              fechaRecoleccion,

            cantidad:
              cercanas.length,

            solicitudes:
              cercanas.map(
                (item) => ({
                  id:
                    item.solicitud.id,

                  distanciaMaxima:
                    item.distanciaMaxima,
                })
              ),
          }
        );


        // ====================================================
        // NECESITAMOS OTRAS CUATRO
        // ====================================================

        if (
          cercanas.length < 4
        ) {

          logger.info(
            "La solicitud no tiene otras " +
            "4 solicitudes compatibles para la misma fecha."
          );

          return;
        }


        // ====================================================
        // CANDIDATAS
        // ====================================================

        /*
         * La solicitud que activó el evento
         * siempre va en posición 0.
         */

        const candidatas = [
          solicitudActual,

          ...cercanas
            .slice(
              0,
              MAX_CANDIDATAS_MATRIZ - 1
            )
            .map(
              (item) =>
                item.solicitud
            ),
        ];


        logger.info(
          "Calculando matriz completa",
          {
            fechaRecoleccion:
              fechaRecoleccion,

            cantidad:
              candidatas.length,
          }
        );


        // ====================================================
        // MATRIZ NxN
        // ====================================================

        const matrizCompleta =
          await calcularMatrizDistancias(
            candidatas,
            candidatas
          );


        // ====================================================
        // BUSCAR GRUPO DE CINCO
        // ====================================================

        const indicesGrupo =
          buscarGrupoDeCinco(
            candidatas,
            matrizCompleta
          );


        if (!indicesGrupo) {

          logger.info(
            "No existe todavía un grupo de " +
            "5 solicitudes de la misma fecha donde " +
            "todas las parejas cumplan la regla de 6 km.",
            {
              fechaRecoleccion:
                fechaRecoleccion,
            }
          );

          return;
        }


        const grupo =
          indicesGrupo.map(
            (indice) =>
              candidatas[indice]
          );


        // ====================================================
        // VALIDACIÓN EXTRA DE FECHA
        // ====================================================

        const todasMismaFecha =
          grupo.every(
            (solicitud) =>
              solicitud.fechaRecoleccion ===
              fechaRecoleccion
          );


        if (!todasMismaFecha) {

          logger.error(
            "Se intentó formar una ruta con " +
            "fechas de recolección diferentes."
          );

          return;
        }


        logger.info(
          "Grupo válido encontrado",
          {
            fechaRecoleccion:
              fechaRecoleccion,

            solicitudes:
              grupo.map(
                (solicitud) =>
                  solicitud.id
              ),
          }
        );


        // ====================================================
        // EXTREMOS
        // ====================================================

        const extremos =
          elegirExtremos(
            indicesGrupo,
            matrizCompleta
          );


        const origen =
          candidatas[
            extremos.origen
          ];


        const destino =
          candidatas[
            extremos.destino
          ];


        const intermedias =
          indicesGrupo
            .filter(
              (indice) =>
                indice !==
                  extremos.origen &&

                indice !==
                  extremos.destino
            )
            .map(
              (indice) =>
                candidatas[indice]
            );


        logger.info(
          "Extremos elegidos",
          {
            origen:
              origen.id,

            destino:
              destino.id,

            intermedias:
              intermedias.map(
                (solicitud) =>
                  solicitud.id
              ),
          }
        );


        // ====================================================
        // GOOGLE COMPUTE ROUTES
        // ====================================================

        const rutaOptimizada =
          await calcularRutaOptimizada(
            origen,
            destino,
            intermedias
          );


        // ====================================================
        // ORDEN FINAL
        // ====================================================

        const intermediasOrdenadas =
          rutaOptimizada
            .ordenIntermedias
            .map(
              (indice) =>
                intermedias[indice]
            );


        const ordenSolicitudes = [
          origen.id,

          ...intermediasOrdenadas.map(
            (solicitud) =>
              solicitud.id
          ),

          destino.id,
        ];


        logger.info(
          "Orden optimizado por Google Routes",
          {
            fechaRecoleccion:
              fechaRecoleccion,

            orden:
              ordenSolicitudes,

            distanciaTotalMetros:
              rutaOptimizada
                .distanciaTotalMetros,

            duracionTotalSegundos:
              rutaOptimizada
                .duracionTotalSegundos,
          }
        );


        // ====================================================
        // CREAR RUTA
        // ====================================================

        const rutaId =
          await crearRutaEnFirestore(
            grupo,

            fechaRecoleccion,

            ordenSolicitudes,

            rutaOptimizada
              .distanciaTotalMetros,

            rutaOptimizada
              .duracionTotalSegundos,

            rutaOptimizada
              .polyline
          );


        // ====================================================
        // ÉXITO
        // ====================================================

        logger.info(
          "Ruta creada correctamente",
          {
            rutaId:
              rutaId,

            fechaRecoleccion:
              fechaRecoleccion,

            solicitudes:
              grupo.map(
                (solicitud) =>
                  solicitud.id
              ),

            orden:
              ordenSolicitudes,
          }
        );


      } catch (error) {

        logger.error(
          "Error procesando la solicitud",
          error
        );
      }
    }
  );