package com.example.routex_app.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Oferta(
    val id: Int,
    @SerialName("concepto") val concepto: String? = null,
    // Objetos anidados del JSON
    @SerialName("port_origen") val portOrigen: Port? = null,
    @SerialName("port_desti") val portDesti: Port? = null,
    @SerialName("estats_ofertes") val estadoInfo: EstadoInfo? = null,
    @SerialName("data_validessa_fina") val fechaLlegada: String? = null
)

@Serializable
data class Port(
    val nom: String? = null
)

@Serializable
data class EstadoInfo(
    val estat: String? = null
)