package com.example.routex_app.data

class Documento (
    val id: String,
    val envioId: String?,
    val tipo: TipoDocumento,
    val nombreArchivo: String,
    val urlDescarga: String,
    val fechaSubida: Long
)
enum class TipoDocumento{
    PRESUPUESTO,
    ALBARAN,
    FACTURA
}