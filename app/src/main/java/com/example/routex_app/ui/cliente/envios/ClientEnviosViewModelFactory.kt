package com.example.routex_app.ui.cliente.envios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClientEnviosViewModelFactory(
    private val repository: ClientRepository // Aquí usará el del mismo paquete (ui.cliente.envios)
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientEnviosViewModel::class.java)) {
            return ClientEnviosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}