package com.example.routex_app.models

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmarSubidaRequest(
    val stepId: Int,
    val fileName: String
)