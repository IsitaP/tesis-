import {onDocumentCreated} from "firebase-functions/v2/firestore";
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

const DISTANCIA_MAXIMA_METROS =
  6000;

const CANTIDAD_SOLICITUDES_RUTA =
  5;

/*
 * Google permite como máximo:
 *
 * 25 orígenes + 25 destinos = 50 Place IDs
 * 25 x 25 = 625 elementos
 */
const MAX_CANDIDATAS_MATRIZ =
  25;

/*
 * Para la búsqueda inicial:
 *
 * 1 origen + 49 destinos = 50 Place IDs.
 */
const TAMANO_LOTE_PREFILTRO =
  49;


// ============================================================
// TIPOS
// ============================================================

interface SolicitudDisponible {

  id: string;

  placeId: string;

  latitud: number;

  longitud: number;
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
// CREAR WAYPOINT PARA GOOGLE ROUTES
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
          crearWaypoint(
            solicitud
          ),
      })
    );


  const destinations =
    destinos.map(
      (solicitud) => ({

        waypoint:
          crearWaypoint(
            solicitud
          ),
      })
    );


  const response =
    await fetch(

      "https://routes.googleapis.com/" +
      "distanceMatrix/v2:computeRouteMatrix",

      {

        method:
          "POST",


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


  return await response.json() as ElementoMatriz[];
}


// ============================================================
// OBTENER UNA DISTANCIA DE LA MATRIZ
// ============================================================

function obtenerDistancia(

  matriz: ElementoMatriz[],

  origenIndex: number,

  destinoIndex: number

): number | null {


  const elemento =
    matriz.find(
      (item) =>

        item.originIndex ===
          origenIndex &&

        item.destinationIndex ===
          destinoIndex
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
// DISTANCIA SIMÉTRICA
// ============================================================

/*
 * ESTA ES LA REGLA QUE DEFINIMOS:
 *
 * Math.max(
 *     distanciaA_B,
 *     distanciaB_A
 * ) <= 6000
 *
 * Es decir:
 *
 * A -> B debe cumplir.
 * B -> A debe cumplir.
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
// COMPROBAR SI DOS SOLICITUDES CUMPLEN LOS 6 KM
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


  if (
    distancia === null
  ) {

    return false;
  }


  return (
    distancia <=
    DISTANCIA_MAXIMA_METROS
  );
}


// ============================================================
// VALIDAR GRUPO COMPLETO
// ============================================================

/*
 * Para 5 solicitudes comprobamos
 * todas las parejas:
 *
 * A-B
 * A-C
 * A-D
 * A-E
 * B-C
 * B-D
 * B-E
 * C-D
 * C-E
 * D-E
 */

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
// BUSCAR GRUPO DE 5
// ============================================================

/*
 * La solicitud nueva siempre queda
 * en la posición 0.
 *
 * Por eso buscamos otras 4 solicitudes
 * que junto con ella formen un grupo válido.
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


  /*
   * Índice 0 =
   * solicitud que activó la Cloud Function.
   */

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
// PREFILTRO CON GOOGLE ROUTES
// ============================================================

/*
 * Antes de mandar hasta 25 x 25,
 * buscamos cuáles solicitudes están
 * realmente cerca de la solicitud nueva.
 *
 * También usamos Google Routes aquí.
 *
 * NO usamos Haversine.
 */

async function buscarSolicitudesCercanas(

  nuevaSolicitud:
    SolicitudDisponible,

  otrasSolicitudes:
    SolicitudDisponible[]

): Promise<Array<{
  solicitud: SolicitudDisponible;
  distanciaMaxima: number;
}>> {


  const resultados:
    Array<{
      solicitud:
        SolicitudDisponible;

      distanciaMaxima:
        number;
    }> = [];


  // ==========================================================
  // PROCESAR EN LOTES DE 49
  // ==========================================================

  for (
    let inicio = 0;
    inicio < otrasSolicitudes.length;
    inicio += TAMANO_LOTE_PREFILTRO
  ) {


    const lote =
      otrasSolicitudes.slice(

        inicio,

        inicio +
          TAMANO_LOTE_PREFILTRO
      );


    /*
     * Calculamos:
     *
     * nueva -> lote
     *
     * y
     *
     * lote -> nueva
     *
     * porque las carreteras no son
     * necesariamente simétricas.
     */

    const [
      matrizIda,
      matrizRegreso,
    ] =
      await Promise.all([

        calcularMatrizDistancias(
          [nuevaSolicitud],
          lote
        ),

        calcularMatrizDistancias(
          lote,
          [nuevaSolicitud]
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
        ) ?
          ida.distanceMeters :
          null;


      const distanciaRegreso =
        (
          regreso?.condition ===
            "ROUTE_EXISTS" &&
          typeof regreso.distanceMeters ===
            "number"
        ) ?
          regreso.distanceMeters :
          null;


      if (
        distanciaIda === null ||
        distanciaRegreso === null
      ) {

        continue;
      }


      // ======================================================
      // Math.max(A -> B, B -> A)
      // ======================================================

      const distanciaMaxima =
        Math.max(

          distanciaIda,

          distanciaRegreso
        );


      // ======================================================
      // SOLO CONSERVAR <= 6 KM
      // ======================================================

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


  // ==========================================================
  // MÁS CERCANAS PRIMERO
  // ==========================================================

  resultados.sort(
    (a, b) =>

      a.distanciaMaxima -
      b.distanciaMaxima
  );


  return resultados;
}


// ============================================================
// ELEGIR EXTREMOS DE LA RUTA
// ============================================================

/*
 * Todavía no tenemos un punto de salida
 * del recolector.
 *
 * Por eso, dentro del grupo de 5,
 * elegimos como origen y destino
 * las dos solicitudes más alejadas entre sí.
 *
 * Las otras tres serán intermediarias.
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

  let mayorDistancia =
    -1;


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

  ordenIntermedias:
    number[];

  distanciaTotalMetros:
    number;

  duracionTotalSegundos:
    number;

  polyline:
    string;

}> {


  const response =
    await fetch(

      "https://routes.googleapis.com/" +
      "directions/v2:computeRoutes",

      {

        method:
          "POST",


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
              crearWaypoint(
                origen
              ),

            destination:
              crearWaypoint(
                destino
              ),

            intermediates:
              intermedias.map(
                (solicitud) =>

                  crearWaypoint(
                    solicitud
                  )
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
    await response.json() as RespuestaComputeRoutes;


  const ruta =
    resultado.routes?.[0];


  if (!ruta) {

    throw new Error(
      "Google Routes no devolvió una ruta."
    );
  }


  // ==========================================================
  // ORDEN DE INTERMEDIAS
  // ==========================================================

  const ordenIntermedias =

    ruta
      .optimizedIntermediateWaypointIndex
      ??
      intermedias.map(
        (_, index) => index
      );


  // ==========================================================
  // DURACIÓN
  // ==========================================================

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

  solicitudes:
    SolicitudDisponible[],

  ordenSolicitudes:
    string[],

  distanciaTotalMetros:
    number,

  duracionTotalSegundos:
    number,

  polyline:
    string

): Promise<string> {


  const contadorRef =

    db
      .collection(
        "contadores"
      )
      .doc(
        "rutas"
      );


  return await db.runTransaction(
    async (transaction) => {


      // ======================================================
      // LEER CONTADOR
      // ======================================================

      const contadorSnapshot =
        await transaction.get(
          contadorRef
        );


      const ultimoId =
        contadorSnapshot
          .get(
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
      // REFERENCIAS DE LAS 5 SOLICITUDES
      // ======================================================

      const referencias =

        solicitudes.map(
          (solicitud) =>

            db
              .collection(
                "solicitudes"
              )
              .doc(
                solicitud.id
              )
        );


      // ======================================================
      // LEER LAS 5 ANTES DE ESCRIBIR
      // ======================================================

      const snapshots = [];


      for (
        const referencia of referencias
      ) {

        const snapshot =
          await transaction.get(
            referencia
          );

        snapshots.push(
          snapshot
        );
      }


      // ======================================================
      // COMPROBAR QUE SIGAN DISPONIBLES
      // ======================================================

      for (
        const snapshot of snapshots
      ) {


        if (!snapshot.exists) {

          throw new Error(
            "Una solicitud ya no existe."
          );
        }


        const data =
          snapshot.data();


        if (
          data?.estado !==
          "PUBLICADA"
        ) {

          throw new Error(
            `La solicitud ${snapshot.id} ` +
            "ya no está PUBLICADA."
          );
        }


        if (
          data?.rutaId !== null &&
          data?.rutaId !== undefined
        ) {

          throw new Error(
            `La solicitud ${snapshot.id} ` +
            "ya pertenece a otra ruta."
          );
        }
      }


      // ======================================================
      // CREAR DOCUMENTO DE RUTA
      // ======================================================

      const rutaRef =

        db
          .collection(
            "rutas"
          )
          .doc(
            rutaId
          );


      transaction.set(
        rutaRef,
        {

          solicitudesIds:
            solicitudes.map(
              (solicitud) =>
                solicitud.id
            ),

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
            FieldValue
              .serverTimestamp(),
        }
      );


      // ======================================================
      // ACTUALIZAR LAS 5 SOLICITUDES
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
  onDocumentCreated(
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
        // SOLICITUD NUEVA
        // ====================================================

        const snapshot =
          event.data;


        if (!snapshot) {

          logger.warn(
            "No se encontraron datos."
          );

          return;
        }


        const data =
          snapshot.data();


        const solicitudId =
          event.params.solicitudId;


        logger.info(
          "Nueva solicitud detectada",
          {

            solicitudId:
              solicitudId,

            estado:
              data.estado,

            rutaId:
              data.rutaId,

            placeId:
              data.placeId,
          }
        );


        // ====================================================
        // VALIDACIONES
        // ====================================================

        if (
          data.estado !==
          "PUBLICADA"
        ) {

          return;
        }


        if (
          data.rutaId !== null &&
          data.rutaId !== undefined
        ) {

          return;
        }


        if (
          typeof data.placeId !==
            "string" ||

          data.placeId.trim() ===
            ""
        ) {

          logger.warn(
            "La solicitud nueva no tiene Place ID."
          );

          return;
        }


        // ====================================================
        // BUSCAR SOLICITUDES DISPONIBLES
        // ====================================================

        const disponiblesSnapshot =

          await db
            .collection(
              "solicitudes"
            )
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
            .get();


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
                  "number"
            );


        logger.info(
          "Solicitudes disponibles",
          {

            cantidad:
              solicitudesDisponibles.length,
          }
        );


        // ====================================================
        // NECESITAMOS AL MENOS 5
        // ====================================================

        if (
          solicitudesDisponibles.length <
          CANTIDAD_SOLICITUDES_RUTA
        ) {

          logger.info(
            "Todavía no hay 5 solicitudes disponibles."
          );

          return;
        }


        // ====================================================
        // ENCONTRAR LA SOLICITUD NUEVA
        // ====================================================

        const nuevaSolicitud =

          solicitudesDisponibles.find(
            (solicitud) =>

              solicitud.id ===
              solicitudId
          );


        if (!nuevaSolicitud) {

          logger.warn(
            "No se encontró la solicitud nueva " +
            "entre las disponibles."
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

            nuevaSolicitud,

            otrasSolicitudes
          );


        logger.info(
          "Solicitudes a máximo 6 km " +
          "de la solicitud nueva en ambos sentidos",
          {

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


        // Necesitamos nueva + otras 4.
        if (
          cercanas.length < 4
        ) {

          logger.info(
            "La nueva solicitud no tiene " +
            "otras 4 solicitudes compatibles."
          );

          return;
        }


        // ====================================================
        // ARMAR CANDIDATAS PARA MATRIZ COMPLETA
        // ====================================================

        /*
         * Posición 0 siempre será
         * la solicitud nueva.
         */

        const candidatas = [

          nuevaSolicitud,

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

            cantidad:
              candidatas.length,
          }
        );


        // ====================================================
        // MATRIZ COMPLETA NxN
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
            "5 solicitudes donde todas las parejas " +
            "cumplan la regla de 6 km."
          );

          return;
        }


        const grupo =

          indicesGrupo.map(
            (indice) =>
              candidatas[indice]
          );


        logger.info(
          "Grupo válido encontrado",
          {

            solicitudes:
              grupo.map(
                (solicitud) =>
                  solicitud.id
              ),
          }
        );


        // ====================================================
        // ELEGIR ORIGEN Y DESTINO
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
        // OPTIMIZAR ORDEN CON COMPUTE ROUTES
        // ====================================================

        const rutaOptimizada =

          await calcularRutaOptimizada(

            origen,

            destino,

            intermedias
          );


        // ====================================================
        // CONSTRUIR ORDEN FINAL
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
        // CREAR RUTA TRANSACCIONALMENTE
        // ====================================================

        const rutaId =

          await crearRutaEnFirestore(

            grupo,

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