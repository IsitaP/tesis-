# Pruebas de integración — ReBottle

Las pruebas de integración verifican que varios componentes funcionan juntos
(ViewModel/pantalla + Firebase Auth + Cloud Firestore, y Cloud Functions +
Firestore). Se ejecutan contra el **Firebase Emulator Suite**, así que nunca
tocan los datos de producción.

## Requisitos

- Node 22 y `npm install` dentro de `functions/` (instala el CLI de Firebase
  como dependencia de desarrollo).
- Java 17 o superior para el emulador de Firestore. Con Java 17 funciona,
  pero muestra un aviso de que `firebase-tools@15` va a exigir Java 21.
- Un emulador de Android encendido (por ejemplo, el AVD `Pixel_9`) para las
  pruebas de la app.

## 1. Cloud Functions (Jest + emulador de Firestore)

```bash
cd functions
npm run test:integracion
```

El script levanta el emulador de Firestore, corre
`functions/test/integracion/` y apaga el emulador al terminar. Google Routes
API se reemplaza por un doble de prueba (fetch simulado), así que no hace
falta la API key ni se gasta cuota.

Las pruebas unitarias se siguen corriendo aparte con `npm test`.

## 2. App Android (pruebas instrumentadas + emuladores de Auth y Firestore)

Terminal 1, para dejar los emuladores encendidos (la interfaz queda en
http://localhost:4000):

```bash
cd functions
npm run emuladores
```

Terminal 2:

```bash
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=com.rebottle.app.integracion
```

El reporte HTML queda en
`app/build/reports/androidTests/connected/debug/index.html`.

**Dispositivo físico:** ejecuta `adb reverse tcp:8080 tcp:8080` y
`adb reverse tcp:9099 tcp:9099`, y agrega
`-Pandroid.testInstrumentationRunnerArguments.emuladorHost=127.0.0.1`.

Cada prueba borra los datos de los emuladores antes de empezar, por lo que
los resultados no dependen del orden en que se ejecuten.

## Catálogo de casos

| ID | Componentes | Caso | Resultado esperado |
|----|-------------|------|--------------------|
| I-01 | SolicitudViewModel + Auth + Firestore | Crear una solicitud con el formulario completo | Documento `RB-0001` en estado PUBLICADA con usuario, peso, fecha, materiales y `rutaId = null` |
| I-02 | SolicitudViewModel + Firestore (transacción) | Crear dos solicitudes | IDs consecutivos `RB-0001`, `RB-0002` y contador en 2 |
| I-03 | SolicitudViewModel + Auth | Crear una solicitud sin sesión iniciada | Error "No hay un usuario autenticado" y nada guardado |
| I-04 | SolicitudViewModel + listener de Firestore | Crear una solicitud | Aparece en el historial en tiempo real |
| I-05 | SolicitudViewModel + Firestore | Cancelar una solicitud | Estado CANCELADA |
| I-06 | SolicitudViewModel + Firestore (transacción) | Marcar como recolectada dos veces (12 kg) | RECOLECTADA, 60 puntos, sumados una sola vez |
| I-07 | LoginScreen + Auth + `users` + navegación | Ciudadano con credenciales válidas | Navega al inicio de ciudadano |
| I-08 | LoginScreen + Auth + `users` + navegación | Recolector con credenciales válidas | Navega al inicio de recolector |
| I-09 | LoginScreen + Auth + `users` + navegación | Fundación con credenciales válidas | Navega al inicio de fundación |
| I-10 | LoginScreen + Auth | Contraseña incorrecta | No inicia sesión ni navega |
| I-11 | LoginScreen + Auth | Sesión ya abierta al entrar | Entra directo al inicio según el rol |
| I-12 | procesarSolicitudPublicada + Firestore | 5 solicitudes a ≤ 6 km en la misma fecha | Ruta `RT-0001` con las 5 en EN_RUTA, orden optimizado, distancia y duración |
| I-13 | procesarSolicitudPublicada + Firestore | Solo 4 solicitudes | No crea ruta ni llama a Google Routes |
| I-14 | procesarSolicitudPublicada + Firestore | Una solicitud a más de 6 km | No crea ruta |
| I-15 | procesarSolicitudPublicada + Firestore | Una solicitud de otra fecha | No mezcla fechas ni crea ruta |
| I-16 | procesarSolicitudPublicada + Firestore | La solicitud que dispara el evento está CANCELADA | No crea ruta |
| I-17 | procesarSolicitudPublicada + Firestore | Una sexta solicitud cuando las demás ya están en una ruta | No crea una segunda ruta |

## Archivos

- `firebase.json`: configuración de los emuladores (Auth 9099, Firestore 8080,
  Functions 5001, interfaz 4000).
- `app/src/debug/`: permite HTTP hacia el emulador solo en builds de debug.
- `app/src/androidTest/java/com/rebottle/app/integracion/`: pruebas de la app.
- `functions/test/integracion/` y `functions/jest.integracion.config.js`:
  pruebas de Cloud Functions.
