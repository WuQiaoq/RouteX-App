package com.example.routex_app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommercialDashboardResponse(
    @SerialName("user_name") val userName: String,
    @SerialName("pending_count") val pendingCount: Int,
    @SerialName("active_ops_count") val activeOpsCount: Int,
    @SerialName("rejected_count") val rejectedCount: Int
)