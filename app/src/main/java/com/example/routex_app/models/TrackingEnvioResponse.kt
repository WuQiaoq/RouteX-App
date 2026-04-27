package com.example.routex_app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackingEnvioResponse(
    @SerialName("estado")
    val estado: String,
    @SerialName("progreso")
    val progreso: Int,
    @SerialName("origen")
    val origen: String,
    @SerialName("destino")
    val destino: String,
    @SerialName("ruta")
    val ruta: String,
    @SerialName("llegada_estimada")
    val llegadaEstimada: String,
    @SerialName("hitos")
    val hitos: List<HitoTracking> = emptyList()
)

@Serializable
data class HitoTracking(
    @SerialName("titulo")
    val titulo: String,
    @SerialName("descripcion")
    val descripcion: String,
    @SerialName("fecha")
    val fecha: String? = null,
    @SerialName("completado")
    val completado: Boolean = false
)
