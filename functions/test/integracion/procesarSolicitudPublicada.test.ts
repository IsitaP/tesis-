/*
 * Pruebas de integración de la Cloud Function procesarSolicitudPublicada
 * (RF-RUT-001): trigger de Firestore + consultas + transacción que crea
 * la ruta, contra el emulador de Firestore.
 *
 * Google Routes API se reemplaza por un doble de prueba (fetch simulado)
 * que calcula las distancias a partir de posiciones ficticias en metros,
 * así las pruebas no gastan cuota ni necesitan la API key real.
 *
 * Ejecutar con:  npm run test:integracion
 */
import functionsTest from "firebase-functions-test";
import {getFirestore, Firestore} from "firebase-admin/firestore";

const PROYECTO = process.env.GCLOUD_PROJECT ?? "demo-rebottle";
const FECHA = "2026-12-01";

if (!process.env.FIRESTORE_EMULATOR_HOST) {
  throw new Error(
    "FIRESTORE_EMULATOR_HOST no está definido. " +
    "Ejecuta las pruebas con `npm run test:integracion`."
  );
}

// La función lee el secreto desde el entorno durante las pruebas.
process.env.ROUTES_API_KEY = "clave-de-prueba";

const fft = functionsTest({projectId: PROYECTO});

// Se importa después de inicializar firebase-functions-test.
// eslint-disable-next-line @typescript-eslint/no-require-imports
const {procesarSolicitudPublicada} = require("../../src/index");
const procesar = fft.wrap(procesarSolicitudPublicada);

let db: Firestore;

// ------------------------------------------------------------
// DOBLE DE PRUEBA DE GOOGLE ROUTES
// ------------------------------------------------------------

/** Posición (en metros sobre una línea) de cada Place ID. */
const posiciones = new Map<string, number>();

const fetchReal = global.fetch;
let llamadasRoutes: {url: string; body: any}[] = [];

function distancia(placeA: string, placeB: string): number {
  return Math.abs(posiciones.get(placeA)! - posiciones.get(placeB)!);
}

function respuestaJson(cuerpo: unknown): Response {
  return new Response(JSON.stringify(cuerpo), {
    status: 200,
    headers: {"Content-Type": "application/json"},
  });
}

const fetchSimulado = jest.fn(
  async (entrada: string | URL | Request, init?: RequestInit) => {
    const url = entrada.toString();

    if (!url.startsWith("https://routes.googleapis.com/")) {
      return fetchReal(entrada, init);
    }

    const body = JSON.parse(String(init?.body));
    llamadasRoutes.push({url, body});

    if (url.includes("computeRouteMatrix")) {
      const elementos = [];
      for (const [i, origen] of body.origins.entries()) {
        for (const [j, destino] of body.destinations.entries()) {
          elementos.push({
            originIndex: i,
            destinationIndex: j,
            condition: "ROUTE_EXISTS",
            distanceMeters: distancia(
              origen.waypoint.placeId,
              destino.waypoint.placeId
            ),
          });
        }
      }
      return respuestaJson(elementos);
    }

    // computeRoutes: devuelve las intermedias en orden inverso.
    const intermedias = body.intermediates.length;
    return respuestaJson({
      routes: [{
        distanceMeters: 4200,
        duration: "900s",
        polyline: {encodedPolyline: "polyline_de_prueba"},
        optimizedIntermediateWaypointIndex:
          Array.from({length: intermedias}, (_, i) => intermedias - 1 - i),
      }],
    });
  }
);

// ------------------------------------------------------------
// AYUDANTES
// ------------------------------------------------------------

async function limpiarFirestore() {
  const host = process.env.FIRESTORE_EMULATOR_HOST;
  const respuesta = await fetchReal(
    `http://${host}/emulator/v1/projects/${PROYECTO}` +
    "/databases/(default)/documents",
    {method: "DELETE"}
  );
  if (!respuesta.ok) {
    throw new Error(`No se pudo limpiar Firestore: ${respuesta.status}`);
  }
}

/** Crea una solicitud PUBLICADA cuyo lugar está en `metros`. */
async function crearSolicitud(
  id: string,
  metros: number,
  extra: Record<string, unknown> = {}
) {
  const placeId = `place-${id}`;
  posiciones.set(placeId, metros);
  await db.collection("solicitudes").doc(id).set({
    usuarioId: "usuario-prueba",
    materiales: ["PLASTICO"],
    pesoKg: 10,
    fechaRecoleccion: FECHA,
    estado: "PUBLICADA",
    rutaId: null,
    placeId: placeId,
    latitud: 4.6,
    longitud: -74.08,
    ...extra,
  });
}

/** Simula el evento de creación del documento y ejecuta la función. */
async function dispararCreacion(id: string) {
  const ruta = `solicitudes/${id}`;
  const actual = await db.doc(ruta).get();
  const antes = fft.firestore.makeDocumentSnapshot({}, ruta);
  const despues = fft.firestore.makeDocumentSnapshot(actual.data()!, ruta);

  await procesar({
    data: fft.makeChange(antes, despues),
    params: {solicitudId: id},
  } as any);
}

async function rutasCreadas() {
  return (await db.collection("rutas").get()).docs;
}

// ------------------------------------------------------------
// CICLO DE VIDA
// ------------------------------------------------------------

beforeAll(() => {
  db = getFirestore();
  global.fetch = fetchSimulado as unknown as typeof fetch;
});

beforeEach(async () => {
  await limpiarFirestore();
  posiciones.clear();
  llamadasRoutes = [];
  fetchSimulado.mockClear();
});

afterAll(async () => {
  global.fetch = fetchReal;
  fft.cleanup();
  await limpiarFirestore();
});

// ------------------------------------------------------------
// PRUEBAS
// ------------------------------------------------------------

describe("procesarSolicitudPublicada (integración con Firestore)", () => {
  test("I-12: con 5 solicitudes cercanas de la misma fecha crea la ruta", async () => {
    const metros = [0, 500, 1000, 1500, 2000];
    for (const [i, m] of metros.entries()) {
      await crearSolicitud(`RB-000${i + 1}`, m);
    }

    await dispararCreacion("RB-0005");

    const rutas = await rutasCreadas();
    expect(rutas).toHaveLength(1);

    const ruta = rutas[0];
    expect(ruta.id).toBe("RT-0001");
    expect(ruta.get("estado")).toBe("PUBLICADA");
    expect(ruta.get("recolectorId")).toBeNull();
    expect(ruta.get("fechaRecoleccion")).toBe(FECHA);
    expect(ruta.get("distanciaTotalMetros")).toBe(4200);
    expect(ruta.get("duracionTotalSegundos")).toBe(900);
    expect(ruta.get("polyline")).toBe("polyline_de_prueba");
    expect([...ruta.get("solicitudesIds")].sort()).toEqual(
      ["RB-0001", "RB-0002", "RB-0003", "RB-0004", "RB-0005"]
    );

    // Origen y destino son las dos solicitudes más alejadas entre sí.
    const orden: string[] = ruta.get("ordenSolicitudes");
    expect(orden).toHaveLength(5);
    expect(new Set([orden[0], orden[4]])).toEqual(new Set(["RB-0001", "RB-0005"]));

    // Las intermedias quedan en el orden que devolvió Google Routes.
    const peticionRuta = llamadasRoutes.find((l) => l.url.includes("computeRoutes"))!;
    const intermediasEnviadas: string[] = peticionRuta.body.intermediates
      .map((w: {placeId: string}) => w.placeId.replace("place-", ""));
    expect(orden.slice(1, 4)).toEqual([...intermediasEnviadas].reverse());

    // Todas las solicitudes quedan asignadas a la ruta.
    const solicitudes = await db.collection("solicitudes").get();
    for (const doc of solicitudes.docs) {
      expect(doc.get("estado")).toBe("EN_RUTA");
      expect(doc.get("rutaId")).toBe("RT-0001");
    }

    const contador = await db.doc("contadores/rutas").get();
    expect(contador.get("ultimoId")).toBe(1);
  });

  test("I-13: con menos de 5 solicitudes no crea ruta ni llama a Google", async () => {
    for (const [i, m] of [0, 500, 1000, 1500].entries()) {
      await crearSolicitud(`RB-000${i + 1}`, m);
    }

    await dispararCreacion("RB-0004");

    expect(await rutasCreadas()).toHaveLength(0);
    expect(llamadasRoutes).toHaveLength(0);
    const doc = await db.doc("solicitudes/RB-0004").get();
    expect(doc.get("estado")).toBe("PUBLICADA");
    expect(doc.get("rutaId")).toBeNull();
  });

  test("I-14: no agrupa una solicitud que está a más de 6 km", async () => {
    for (const [i, m] of [0, 500, 1000, 1500].entries()) {
      await crearSolicitud(`RB-000${i + 1}`, m);
    }
    await crearSolicitud("RB-0005", 9000);

    await dispararCreacion("RB-0005");

    expect(await rutasCreadas()).toHaveLength(0);
    const lejana = await db.doc("solicitudes/RB-0005").get();
    expect(lejana.get("estado")).toBe("PUBLICADA");
  });

  test("I-15: no mezcla solicitudes de fechas diferentes", async () => {
    for (const [i, m] of [0, 500, 1000, 1500].entries()) {
      await crearSolicitud(`RB-000${i + 1}`, m);
    }
    await crearSolicitud("RB-0005", 2000, {fechaRecoleccion: "2026-12-02"});

    await dispararCreacion("RB-0005");

    expect(await rutasCreadas()).toHaveLength(0);
  });

  test("I-16: una solicitud cancelada no forma ruta", async () => {
    for (const [i, m] of [0, 500, 1000, 1500].entries()) {
      await crearSolicitud(`RB-000${i + 1}`, m);
    }
    await crearSolicitud("RB-0005", 2000, {estado: "CANCELADA"});

    await dispararCreacion("RB-0005");

    expect(await rutasCreadas()).toHaveLength(0);
    expect(llamadasRoutes).toHaveLength(0);
  });

  test("I-17: solicitudes que ya están en una ruta no se vuelven a agrupar", async () => {
    for (const [i, m] of [0, 500, 1000, 1500, 2000].entries()) {
      await crearSolicitud(`RB-000${i + 1}`, m);
    }
    await dispararCreacion("RB-0005");
    expect(await rutasCreadas()).toHaveLength(1);

    // Una sexta solicitud cercana no alcanza a formar otra ruta.
    await crearSolicitud("RB-0006", 800);
    await dispararCreacion("RB-0006");

    expect(await rutasCreadas()).toHaveLength(1);
    const sexta = await db.doc("solicitudes/RB-0006").get();
    expect(sexta.get("rutaId")).toBeNull();
  });
});
