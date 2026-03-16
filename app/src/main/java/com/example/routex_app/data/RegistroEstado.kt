package com.example.routex_app.data

data class RegistroEstado(
    val id: String,
    val estado: EstadoEnvio,
    val fechaActualizacion: Long,
    val ubicacion: String,
    val actualizadoPor: String
)