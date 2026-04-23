package com.example.routex_app.models

data class DashboardResponse(
    val activeCount: Int,
    val pendingCount: Int,
    val recentActivities: List<RecentActivity>
)