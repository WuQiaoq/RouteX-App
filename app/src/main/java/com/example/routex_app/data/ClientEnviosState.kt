package com.example.routex_app.data

import com.example.routex_app.models.Presupuesto

data class ClientEnviosState(
    val isLoading: Boolean = false,
    val data: List<Presupuesto>? = null,
    val error: String? = null
)
