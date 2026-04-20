package com.example.routex_app.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val email: String,
    val contraseña: String
)