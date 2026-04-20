package com.example.routex_app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(
    val Email: String
                       )


@Serializable
data class LoginResponse(
    @SerialName("usuari")
    val usuari: UsuariModel,

    @SerialName("token")
    val token: String
                        )