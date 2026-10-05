import {onDocumentCreated} from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";

import {initializeApp} from "firebase-admin/app";
import {getFirestore} from "firebase-admin/firestore";


initializeApp();

const db = getFirestore();


// ============================================================
// PROCESAR NUEVA SOLICITUD PUBLICADA
// ============================================================

export const procesarSolicitudPublicada =
  onDocumentCreated(
    "solicitudes/{solicitudId}",
    async (event) => {

      // ========================================================
      // DOCUMENTO CREADO
      // ========================================================

      const snapshot = event.data;

      if (!snapshot) {
        logger.warn(
          "No se encontraron datos en la solicitud."
        );

        return;
      }


      const solicitud =
        snapshot.data();

      const solicitudId =
        event.params.solicitudId;


      // ========================================================
      // MOSTRAR SOLICITUD DETECTADA
      // ========================================================

      logger.info(
        "Nueva solicitud detectada",
        {
          solicitudId: solicitudId,
          estado: solicitud.estado,
          rutaId: solicitud.rutaId,
          latitud: solicitud.latitud,
          longitud: solicitud.longitud,
        }
      );


      // ========================================================
      // VALIDAR ESTADO
      // ========================================================

      if (
        solicitud.estado !== "PUBLICADA"
      ) {

        logger.info(
          "La solicitud no está PUBLICADA. Se ignora.",
          {
            solicitudId: solicitudId,
          }
        );

        return;
      }


      // ========================================================
      // VALIDAR QUE NO TENGA RUTA
      // ========================================================

      if (
        solicitud.rutaId !== null &&
        solicitud.rutaId !== undefined
      ) {

        logger.info(
          "La solicitud ya pertenece a una ruta.",
          {
            solicitudId: solicitudId,
            rutaId: solicitud.rutaId,
          }
        );

        return;
      }


      // ========================================================
      // VALIDAR COORDENADAS
      // ========================================================

      if (
        typeof solicitud.latitud !== "number" ||
        typeof solicitud.longitud !== "number"
      ) {

        logger.warn(
          "La solicitud no tiene coordenadas válidas.",
          {
            solicitudId: solicitudId,
          }
        );

        return;
      }


      // ========================================================
      // BUSCAR TODAS LAS SOLICITUDES DISPONIBLES
      // ========================================================

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
          .get();


      const cantidadDisponibles =
        disponiblesSnapshot.size;


      logger.info(
        "Solicitudes disponibles para crear rutas",
        {
          cantidad: cantidadDisponibles,
        }
      );


      // ========================================================
      // VERIFICAR SI YA HAY 5
      // ========================================================

      if (
        cantidadDisponibles < 5
      ) {

        logger.info(
          "Todavía no hay suficientes solicitudes para crear una ruta.",
          {
            disponibles: cantidadDisponibles,
            necesarias: 5,
          }
        );

        return;
      }


      // ========================================================
      // HAY AL MENOS 5
      // ========================================================

      logger.info(
        "Hay al menos 5 solicitudes disponibles. " +
        "Se puede intentar crear una ruta.",
        {
          cantidad: cantidadDisponibles,
        }
      );


      /*
       * SIGUIENTE PASO:
       *
       * Aquí vamos a:
       *
       * 1. Obtener las solicitudes candidatas.
       * 2. Comparar sus coordenadas.
       * 3. Hacer un filtro inicial de cercanía.
       * 4. Consultar Google Routes API.
       * 5. Verificar la regla de máximo 6 km.
       * 6. Seleccionar exactamente 5 solicitudes.
       * 7. Crear una ruta RT-XXXX.
       * 8. Actualizar esas 5 solicitudes:
       *
       *    estado = EN_RUTA
       *    rutaId = RT-XXXX
       */
    }
  );