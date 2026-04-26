package com.example.routex_app.ui.cliente.envios


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClientDetalleViewModelFactory(private val repository: ClientRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClientDetalleEnvioViewModel(repository) as T
    }
}