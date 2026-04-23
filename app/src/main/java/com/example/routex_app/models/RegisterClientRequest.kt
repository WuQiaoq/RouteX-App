package com.example.routex_app.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class RegisterClientRequest(
    val companyName: String,
    val industryName: String, // Lo que el usuario escribió o seleccionó
    val taxId: String,
    val currencyId: String,
    val correu: String,
    val nom: String,
    val cognoms: String,
    val tlfn: String,
    val representativeIdImage: String? = null
)

@Serializable
data class IndustryModel(
    @SerialName("id") val id: Int,
    @SerialName("categoria") val categoria: String
)

@Serializable
data class CurrencyModel(
    @SerialName("id") val id: String, // "EUR", "USD"...
    @SerialName("currency1") val nombre: String? = null
)