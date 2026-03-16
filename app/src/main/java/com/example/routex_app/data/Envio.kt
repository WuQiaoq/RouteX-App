package com.example.routex_app.data

data class Envio(
    val id: String,
    val clienteId: String,
    val origen: String,
    val destino: String,
    val estadoActual: EstadoEnvio,
    val historialEstados: List<RegistroEstado>,
    val documentosId: List<String>,
    val fechaCreacion: Long,
    val completado: Boolean
)

enum class EstadoEnvio {
    EN_PREPARACION,
    EN_PUERTO_ORIGEN,
    TRANSITO_MARITIMO,
    EN_PUERTO_DESTINO,
    TRANSITO_TERRESTRE,
    ENTREGADO
}