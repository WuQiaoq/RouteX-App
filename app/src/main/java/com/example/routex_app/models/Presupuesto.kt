package com.example.routex_app.models

import kotlinx.serialization.Serializable

@Serializable
data class Presupuesto(
    val id: Int,
    val price: String, // El C# devuelve "Valor" mapeado como price
    val route: String, // Lógica de puertos/aeropuertos del C#
    val description: String, // El C# devuelve "Concepto"
    val rejection_reason: String, // El C# devuelve "RaoRebuig"
    val transport_type_id: Int // El C# devuelve "TipusTransportId"
)