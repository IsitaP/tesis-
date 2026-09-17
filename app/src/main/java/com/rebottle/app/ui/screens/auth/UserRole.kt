package com.rebottle.app.ui.screens.auth

enum class UserRole(val displayName: String, val firestoreValue: String) {
    CIUDADANO("Ciudadano", "ciudadano"),
    FUNDACION("Fundación Recolectora", "fundacion"),
    RECOLECTOR("Recolector", "recolector")
}