package com.example.routex_app.models

import kotlinx.serialization.Serializable

@Serializable
data class PortModel(
    val id: Int,
    val nom: String,
    val idCiutat: Int? = null
)