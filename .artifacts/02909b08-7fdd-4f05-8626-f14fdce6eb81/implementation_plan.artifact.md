# Implementación de la pantalla "Nueva solicitud de recolección"

Este plan detalla la creación de la pantalla de creación de solicitudes para la aplicación ReBottle, siguiendo el diseño proporcionado en la imagen y manteniendo la consistencia con el estilo visual existente de la aplicación.

## Cambios Propuestos

### UI de Usuario

#### [NEW] [CreateRequestScreen.kt](file:///C:/Users/cjuan/AndroidStudioProjects/tesis-/app/src/main/java/com/rebottle/app/ui/screens/usuario/CreateRequestScreen.kt)
Se creará un nuevo archivo Composable para la pantalla, que incluirá:
- Paleta de colores consistente con `HomeUserScreen.kt`.
- Encabezado con botón de retroceso y título centrado.
- Selector de ubicación (diseño simplificado).
- Tarjeta de selección de materiales con checkboxes.
- Contador de peso estimado.
- Vista de calendario para selección de fecha.
- Botón "Solicitar" con el estilo de la aplicación.

## Verificación Plan

### Verificación Manual
- Previsualización del Composable `CreateRequestScreenPreview` en Android Studio para asegurar que el diseño coincida con la imagen proporcionada.
- Verificación de la consistencia de colores y fuentes con las pantallas existentes.
