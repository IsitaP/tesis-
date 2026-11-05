package com.rebottle.app.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


// ============================================================
// PERMISO DE GALERÍA
// ============================================================
//
// Devuelve una función que, antes de abrir la galería, pide el
// permiso de fotos del sistema (igual que los de ubicación y
// notificaciones). Si ya está concedido abre la galería directo.
// ============================================================

private fun permisosGaleria(): Array<String> = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    )
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES
    )
    else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}

@Composable
fun rememberAbrirGaleriaConPermiso(abrirGaleria: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val permisos = permisosGaleria()

    val launcherPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        // En Android 14+ basta con acceso total o parcial (fotos seleccionadas)
        if (resultado.values.any { it }) {
            abrirGaleria()
        } else {
            Toast.makeText(
                context,
                "Debes permitir el acceso a tus fotos para cambiar la foto de perfil",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    return {
        val concedido = permisos.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (concedido) abrirGaleria() else launcherPermiso.launch(permisos)
    }
}
