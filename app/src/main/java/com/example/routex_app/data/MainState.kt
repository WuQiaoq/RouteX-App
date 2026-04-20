package com.example.routex_app.data

import com.example.routex_app.data.RecentActivity

data class MainState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "",
    val activeCount: Int = 0,
    val pendingCount: Int = 0,
    val recentActivities: List<RecentActivity> = emptyList()
)