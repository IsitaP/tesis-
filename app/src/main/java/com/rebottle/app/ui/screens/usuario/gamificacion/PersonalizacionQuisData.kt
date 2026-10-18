package com.rebottle.app.ui.screens.usuario.gamificacion

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rebottle.app.R


// ============================================================
// CATEGORÍAS
// ============================================================

enum class CategoriaQuis {
    ROPA,
    ACCESORIOS
}


// ============================================================
// ZONA DEL CUERPO
// ============================================================

enum class SlotQuis {

    // Tenis
    PIES,

    // Camisa
    TORSO_BASE,

    // Chaleco
    TORSO_EXTERIOR,

    // Guantes
    MANOS,

    // Gafas
    OJOS,

    // Bufanda
    CUELLO,

    // Sombrero / casco
    CABEZA,

    // Otros
    EXTRA
}


// ============================================================
// MODELO DE ITEM
// ============================================================
data class ItemQuis(
    val id: String,
    val nombre: String,
    val precio: Int,
    val categoria: CategoriaQuis,
    val slot: SlotQuis,

    @DrawableRes
    val imagen: Int,

    @DrawableRes
    val overlay: Int
)


// ============================================================
// ROPA
// ============================================================

val ropaQuis = emptyList<ItemQuis>()

// ============================================================
// ACCESORIOS
// ============================================================

val accesoriosQuis = listOf(

    ItemQuis(
        id = "guantes",
        nombre = "Guantes",
        precio = 0,
        categoria = CategoriaQuis.ACCESORIOS,
        slot = SlotQuis.MANOS,
        imagen = R.drawable.acc_guantes,
        overlay = R.drawable.overlay_guantes
    ),

    ItemQuis(
        id = "gafas_fruta",
        nombre = "Gafas",
        precio = 25,
        categoria = CategoriaQuis.ACCESORIOS,
        slot = SlotQuis.OJOS,
        imagen = R.drawable.acc_gafas_fruta,
        overlay = R.drawable.overlay_gafas_fruta
    ),

    ItemQuis(
        id = "gafas_estrellas",
        nombre = "Gafas de estrellas",
        precio = 40,
        categoria = CategoriaQuis.ACCESORIOS,
        slot = SlotQuis.OJOS,
        imagen = R.drawable.acc_gafas_estrellas,
        overlay = R.drawable.overlay_gafas_estrellas
    ),

    ItemQuis(
        id = "bufanda",
        nombre = "Bufanda",
        precio = 50,
        categoria = CategoriaQuis.ACCESORIOS,
        slot = SlotQuis.CUELLO,
        imagen = R.drawable.acc_bufanda,
        overlay = R.drawable.overlay_bufanda
    ),

    ItemQuis(
        id = "sombrero",
        nombre = "Sombrero",
        precio = 100,
        categoria = CategoriaQuis.ACCESORIOS,
        slot = SlotQuis.CABEZA,
        imagen = R.drawable.acc_sombrero,
        overlay = R.drawable.overlay_sombrero
    ),

    ItemQuis(
        id = "casco",
        nombre = "Casco",
        precio = 100,
        categoria = CategoriaQuis.ACCESORIOS,
        slot = SlotQuis.CABEZA,
        imagen = R.drawable.acc_casco,
        overlay = R.drawable.overlay_casco
    ),

    ItemQuis(
        id = "pato",
        nombre = "Pato",
        precio = 120,
        categoria = CategoriaQuis.ACCESORIOS,
        slot = SlotQuis.EXTRA,
        imagen = R.drawable.acc_pato,
        overlay = R.drawable.overlay_pato
    )
)