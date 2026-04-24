package com.example.routex_app.ui.commercial.envios


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackingStep(
    val id: Int,
    val titol: String,
    @SerialName("data_hora")
    val dataHora: String,
    @SerialName("te_document")
    val teDocument: Boolean,
    @SerialName("nom_fitxer")
    val nomFitxer: String?,
    val comentari: String,
    @SerialName("estaCompletado")
    val estaCompletado: Int = 0
)

@Serializable
data class DetalleEnvio(
    val id: Int,
    @SerialName("order_number") // Mapea "#SHP-12"
    val orderNumber: String,
    val cliente: String,
    @SerialName("ruta_completa") // Mapea "Puerto de Los Ángeles..."
    val rutaCompleta: String,
    val concepto: String,
    @SerialName("estado_actual") // Mapea "Aceptada"
    val estadoActual: String,
    @SerialName("fecha_creacion") // Mapea "14/04/2026"
    val fechaCreacion: String,
    @SerialName("tracking_steps") // Mapea la lista de pasos
    val trackingSteps: List<TrackingStep>
)

@Serializable
data class EnvioActivo(
    val id: Int,
    val cliente: String,
    @SerialName("ruta_origen") val rutaOrigen: String,
    @SerialName("ruta_destino") val rutaDestino: String,
    val concepto: String,
    val estado: String,
    @SerialName("estado_id") val estadoId: Int, // Aparece en el log
    @SerialName("fecha_creacion") val fechaCreacion: String,
    @SerialName("transport_type_id") val transportTypeId: Int,
    val precio: String? = null
)