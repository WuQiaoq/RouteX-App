package com.example.routex_app.data

data class Presupuesto (
    val id: String,
    val clienteId: String,
    val descripcionMercancia: String,
    val precio: Double,
    val estado: EstadoPresupuesto,
    val justificacionRechazo: String?,
    val fechaEmision: Long
)

enum class EstadoPresupuesto{
    PENDIENTE,
    ACEPTADA,
    RECHAZADA
}