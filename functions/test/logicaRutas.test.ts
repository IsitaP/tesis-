/*
 * Pruebas unitarias de la lógica de generación de rutas (RF-RUT-001).
 * Se usan matrices de distancias simuladas, por lo que no se llama
 * a Routes API ni a Firestore.
 */
import {
  DISTANCIA_MAXIMA_METROS,
  CANTIDAD_SOLICITUDES_RUTA,
  SolicitudDisponible,
  ElementoMatriz,
  crearWaypoint,
  obtenerDistancia,
  obtenerDistanciaSimetrica,
  cumplenDistanciaMaxima,
  grupoCumpleDistancia,
  buscarGrupoDeCinco,
  elegirExtremos,
} from "../src/logicaRutas";

// Crea una solicitud de prueba con datos mínimos.
function solicitud(n: number): SolicitudDisponible {
  return {
    id: `sol-${n}`,
    placeId: `place-${n}`,
    latitud: 4.6 + n / 1000,
    longitud: -74.08,
    fechaRecoleccion: "2026-11-20",
  };
}

// Convierte una tabla de distancias (metros) en la matriz que
// devuelve computeRouteMatrix. null = no existe ruta.
function matrizDesde(tabla: (number | null)[][]): ElementoMatriz[] {
  const matriz: ElementoMatriz[] = [];
  tabla.forEach((fila, i) =>
    fila.forEach((d, j) => {
      matriz.push(
        d === null ?
          {originIndex: i, destinationIndex: j, condition: "ROUTE_NOT_FOUND"} :
          {originIndex: i, destinationIndex: j, condition: "ROUTE_EXISTS", distanceMeters: d}
      );
    })
  );
  return matriz;
}

// Tabla de n puntos donde todos están a la misma distancia entre sí.
function tablaUniforme(n: number, distancia: number): number[][] {
  return Array.from({length: n}, (_, i) =>
    Array.from({length: n}, (_, j) => (i === j ? 0 : distancia)));
}

function matrizUniforme(n: number, distancia: number): ElementoMatriz[] {
  return matrizDesde(tablaUniforme(n, distancia));
}

describe("Constantes de negocio", () => {
  test("U-01: la distancia máxima es 6 km y una ruta tiene 5 solicitudes", () => {
    expect(DISTANCIA_MAXIMA_METROS).toBe(6000);
    expect(CANTIDAD_SOLICITUDES_RUTA).toBe(5);
  });
});

describe("crearWaypoint", () => {
  test("U-02: construye el waypoint con el placeId de la solicitud", () => {
    expect(crearWaypoint(solicitud(1))).toEqual({placeId: "place-1"});
  });
});

describe("obtenerDistancia", () => {
  const matriz = matrizDesde([
    [0, 2500],
    [null, 0],
  ]);

  test("U-03: devuelve la distancia cuando la ruta existe", () => {
    expect(obtenerDistancia(matriz, 0, 1)).toBe(2500);
  });

  test("U-04: devuelve null cuando no existe ruta entre los puntos", () => {
    expect(obtenerDistancia(matriz, 1, 0)).toBeNull();
  });

  test("U-05: devuelve null cuando el par no está en la matriz", () => {
    expect(obtenerDistancia(matriz, 0, 5)).toBeNull();
  });
});

describe("obtenerDistanciaSimetrica", () => {
  test("U-06: toma la mayor distancia entre ida y vuelta", () => {
    const matriz = matrizDesde([
      [0, 3000],
      [4200, 0],
    ]);
    expect(obtenerDistanciaSimetrica(matriz, 0, 1)).toBe(4200);
  });

  test("U-07: devuelve null si falta uno de los dos sentidos", () => {
    const matriz = matrizDesde([
      [0, 3000],
      [null, 0],
    ]);
    expect(obtenerDistanciaSimetrica(matriz, 0, 1)).toBeNull();
  });
});

describe("cumplenDistanciaMaxima", () => {
  test("U-08: acepta dos solicitudes a exactamente 6 km", () => {
    expect(cumplenDistanciaMaxima(matrizUniforme(2, 6000), 0, 1)).toBe(true);
  });

  test("U-09: rechaza dos solicitudes a más de 6 km", () => {
    expect(cumplenDistanciaMaxima(matrizUniforme(2, 6001), 0, 1)).toBe(false);
  });
});

describe("grupoCumpleDistancia", () => {
  test("U-10: acepta un grupo donde todos los pares están a 6 km o menos", () => {
    expect(grupoCumpleDistancia([0, 1, 2, 3, 4], matrizUniforme(5, 3500))).toBe(true);
  });

  test("U-11: rechaza el grupo si un solo par supera los 6 km", () => {
    const tabla = tablaUniforme(5, 3000);
    tabla[2][4] = 7000;
    tabla[4][2] = 7000;
    expect(grupoCumpleDistancia([0, 1, 2, 3, 4], matrizDesde(tabla))).toBe(false);
  });
});

describe("buscarGrupoDeCinco", () => {
  const candidatas = (n: number) => Array.from({length: n}, (_, i) => solicitud(i));

  test("U-12: no forma ruta con solo 4 solicitudes", () => {
    expect(buscarGrupoDeCinco(candidatas(4), matrizUniforme(4, 1000))).toBeNull();
  });

  test("U-13: forma una ruta con 5 solicitudes cercanas", () => {
    expect(buscarGrupoDeCinco(candidatas(5), matrizUniforme(5, 1000))).toEqual([0, 1, 2, 3, 4]);
  });

  test("U-14: descarta la solicitud lejana y usa las demás", () => {
    const tabla = tablaUniforme(6, 2000);
    for (let k = 0; k < 6; k++) {
      if (k !== 2) {
        tabla[2][k] = 9000;
        tabla[k][2] = 9000;
      }
    }
    expect(buscarGrupoDeCinco(candidatas(6), matrizDesde(tabla))).toEqual([0, 1, 3, 4, 5]);
  });

  test("U-15: no forma ruta si la solicitud nueva (índice 0) está lejos de las demás", () => {
    const tabla = tablaUniforme(6, 2000);
    for (let k = 1; k < 6; k++) {
      tabla[0][k] = 8000;
      tabla[k][0] = 8000;
    }
    expect(buscarGrupoDeCinco(candidatas(6), matrizDesde(tabla))).toBeNull();
  });
});

describe("elegirExtremos", () => {
  test("U-16: elige como inicio y fin las dos solicitudes más alejadas", () => {
    const tabla = tablaUniforme(5, 1500);
    tabla[1][3] = 5200;
    tabla[3][1] = 5200;
    expect(elegirExtremos([0, 1, 2, 3, 4], matrizDesde(tabla))).toEqual({origen: 1, destino: 3});
  });

  test("U-17: si no hay distancias usa la primera y la última solicitud", () => {
    const tabla = Array.from({length: 5}, () => Array.from({length: 5}, (): number | null => null));
    expect(elegirExtremos([0, 1, 2, 3, 4], matrizDesde(tabla))).toEqual({origen: 0, destino: 4});
  });

  test("U-46: ignora los pares sin ruta al buscar los extremos", () => {
    const tabla: (number | null)[][] = tablaUniforme(5, 1500);
    tabla[0][4] = null; // el par sin ruta no puede ser el más lejano
    tabla[2][3] = 4000;
    tabla[3][2] = 4000;
    expect(elegirExtremos([0, 1, 2, 3, 4], matrizDesde(tabla))).toEqual({origen: 2, destino: 3});
  });
});

describe("Casos borde", () => {
  const candidatas = (n: number) => Array.from({length: n}, (_, i) => solicitud(i));

  test("U-47: devuelve null si la ruta existe pero no trae distancia", () => {
    const matriz: ElementoMatriz[] = [
      {originIndex: 0, destinationIndex: 1, condition: "ROUTE_EXISTS"},
    ];
    expect(obtenerDistancia(matriz, 0, 1)).toBeNull();
  });

  test("U-48: dos solicitudes sin ruta entre ellas no cumplen la distancia", () => {
    const matriz = matrizDesde([
      [0, null],
      [null, 0],
    ]);
    expect(cumplenDistanciaMaxima(matriz, 0, 1)).toBe(false);
  });

  test("U-49: no forma ruta con 5 solicitudes si una está lejos", () => {
    const tabla = tablaUniforme(5, 2000);
    tabla[1][4] = 6500;
    tabla[4][1] = 6500;
    expect(buscarGrupoDeCinco(candidatas(5), matrizDesde(tabla))).toBeNull();
  });

  test("U-50: con muchas solicitudes cercanas devuelve el primer grupo e incluye la nueva", () => {
    const grupo = buscarGrupoDeCinco(candidatas(8), matrizUniforme(8, 1000));
    expect(grupo).toEqual([0, 1, 2, 3, 4]);
    expect(grupo).toHaveLength(CANTIDAD_SOLICITUDES_RUTA);
  });

  test("U-51: un grupo de una sola solicitud siempre cumple la distancia", () => {
    expect(grupoCumpleDistancia([0], [])).toBe(true);
  });
});
