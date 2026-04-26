package com.example.routex_app.ui.cliente.envios

import com.example.routex_app.ui.commercial.envios.EnvioActivo

// Solo la data class aquí
data class ClientEnvioListState(
    val isLoading: Boolean = false,
    val data: List<EnvioActivo>? = null,
    val error: String? = null
)