package com.example.routex_app.models

import kotlinx.serialization.Serializable

@Serializable
data class UsuariModel(
    val id: Int,
    val correu: String,
    val nom: String,
    val cognoms: String,
    val rol_id: String,
    val company_id: String,
    val status: String,
    val tlfn: String? = null,
    val ultima_conex: String? = null
)