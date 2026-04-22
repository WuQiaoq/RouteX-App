package com.example.routex_app.models
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ClienteActivo (
    @SerialName("id") val id: Int,
    @SerialName("companyName") val companyName: String,
    @SerialName("industria") val industria: String,
    @SerialName("enviosActivosCount") val enviosActivosCount: Int,
    @SerialName("ultimaCargaResumen") val ultimaCargaResumen: String?,
    @SerialName("tipusTransportId") val tipusTransportId: Int?
)