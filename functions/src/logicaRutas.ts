/*
 * Lógica pura de agrupación de solicitudes y armado de rutas.
 * Se separa de index.ts para poder probarla con pruebas
 * unitarias sin depender de Firebase ni de Routes API.
 */

// Distancia máxima permitida entre dos solicitudes del mismo grupo.
export const DISTANCIA_MAXIMA_METROS = 6000;

// Cantidad exacta de solicitudes que forman una ruta.
export const CANTIDAD_SOLICITUDES_RUTA = 5;

export interface SolicitudDisponible {
  id: string;

  placeId: string;

  latitud: number;

  longitud: number;

  fechaRecoleccion: string;
}

export interface ElementoMatriz {
  originIndex?: number;

  destinationIndex?: number;

  status?: {
    code?: number;
    message?: string;
  };

  condition?: string;

  distanceMeters?: number;
}

export function crearWaypoint(
  solicitud: SolicitudDisponible
) {
  return {
    placeId:
      solicitud.placeId,
  };
}

export function obtenerDistancia(
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

export function obtenerDistanciaSimetrica(
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

export function cumplenDistanciaMaxima(
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

export function grupoCumpleDistancia(
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

export function buscarGrupoDeCinco(
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

export function elegirExtremos(
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
