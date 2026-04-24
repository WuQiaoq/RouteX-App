package com.example.routex_app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDashboardDto(
    @SerialName("user_name")
    val userName: String,

    @SerialName("active_count")
    val activeCount: Int,

    @SerialName("pending_count")
    val pendingCount: Int
)
