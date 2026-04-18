package com.example.routex_app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Presupuesto(
    val id: Int,

    @SerialName("Valor")
    val price: String,

    @SerialName("Ruta") // O el nombre exacto que devuelva tu DTO en C#
    val route: String,

    @SerialName("Concepto")
    val description: String,

    @SerialName("RaoRebuig")
    val rejection_reason: String?, // Usamos ? porque puede ser null si no está rechazado

    @SerialName("TipusTransportId")
    val transport_type_id: Int
)