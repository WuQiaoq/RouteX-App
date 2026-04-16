package com.example.routex_app.models

import kotlinx.serialization.Serializable

// Lo que enviamos al PHP
@Serializable
data class LoginRequest(
    val correu: String,
    val contrasenya: String
                       )

// Lo que el PHP nos devuelve
@Serializable
data class LoginResponse(
    val usuari: UsuariModel,
    val token: String
                        )