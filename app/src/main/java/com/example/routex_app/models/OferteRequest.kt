package com.example.routex_app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OferteRequest(

    @SerialName("TipusTransportId")
    val tipusTransportId: Int = 1,

    @SerialName("TipusFluxeId")
    val tipusFluxeId: Int = 1,

    @SerialName("TipusCarregaId")
    val tipusCarregaId: Int = 1,

    @SerialName("IncotermId")
    val incotermId: Int,

    @SerialName("ClientId")
    val clientId: Int,

    @SerialName("TipusValidacioId")
    val tipusValidacioId: Int = 1,

    @SerialName("EstatOfertaId")
    val estatOfertaId: Int = 1,

    @SerialName("OperadorId")
    val operadorId: Int = 1,

    @SerialName("DescripMercancia")
    val descripMercancia: String?,

    @SerialName("PortOrigenId")
    val portOrigenId: Int? = null,

    @SerialName("PortDestiId")
    val portDestiId: Int? = null,

    @SerialName("AeroportOrigenId")
    val aeroportOrigenId: Int? = null,

    @SerialName("AeroportDestiId")
    val aeroportDestiId: Int? = null,

    @SerialName("TipusContenidorId")
    val tipusContenidorId: Int? = null,

    @SerialName("Concepto")
    val concepto: String?,

    @SerialName("Bultos")
    val bultos: String?,

    @SerialName("DataCreacio")
    val dataCreacio: String
)