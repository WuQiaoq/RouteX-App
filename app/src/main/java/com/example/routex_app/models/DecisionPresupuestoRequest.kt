package com.example.routex_app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DecisionPresupuestoRequest(
    @SerialName("clientId")
    val clientId: Int,

    @SerialName("rejectionReason")
    val rejectionReason: String? = null
)
