package com.example.routex_app.data

import com.example.routex_app.data.RecentActivity

data class MainState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "",       // nombre de usuario pasado desdes el inicio
    val enviosActivos: Int = 0,
    val ofertasPendientes: Int = 0,
    val recentActivities: List<RecentActivity> = emptyList()
)