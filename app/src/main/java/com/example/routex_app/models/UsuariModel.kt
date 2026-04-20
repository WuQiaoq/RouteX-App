package com.example.routex_app.models

import kotlinx.serialization.Serializable

@Serializable
data class UsuariModel(
    val id: Int,
    val correu: String,
    val contrasenya: String,
    val nom: String,
    val cognoms: String,
    val rolId: Int,
    val companyId: Int,
    val status: String,
    val tlfn: String? = null,
    val ultimaConex: String? = null
)

