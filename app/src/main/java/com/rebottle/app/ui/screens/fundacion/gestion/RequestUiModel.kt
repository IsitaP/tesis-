package com.rebottle.app.ui.screens.fundacion.gestion


enum class RequestStatus {
    COMPLETED,
    PENDING,
    CANCELLED
}


data class RequestUiModel(
    val id: Int,
    val address: String,
    val items: String,
    val weight: String,
    val date: String,
    val time: String,
    val assignee: String,
    val status: RequestStatus
)