package com.example.routex_app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Presupuesto(
    val id: Int,

    @SerialName("price")
    val Valor: String,

    @SerialName("route")
    val Ruta: String,

    @SerialName("description")
    val Concepto: String,

    @SerialName("rejection_reason")
    val RaoRebuig: String? = null,

    @SerialName("transport_type_id")
    val TipusTransportId: Int
)