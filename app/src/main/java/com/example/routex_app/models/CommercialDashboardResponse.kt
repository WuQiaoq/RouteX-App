package com.example.routex_app.models

import kotlinx.serialization.Serializable

@Serializable
data class CommercialDashboardResponse(
    val user_name: String,
    val pending_count: Int,
    val active_ops_count: Int,
    val rejected_count: Int
)