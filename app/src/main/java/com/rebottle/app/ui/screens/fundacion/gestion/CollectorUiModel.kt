package com.rebottle.app.ui.screens.fundacion.gestion

import androidx.annotation.DrawableRes


data class CollectorUiModel(

    val id: String,

    val name: String,

    val zone: String,

    val schedule: String,

    val status: String,

    @DrawableRes
    val imageRes: Int
)