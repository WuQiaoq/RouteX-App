package com.example.routex_app.ui.commercial.presupuesto


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.routex_app.repository.CommercialRepository

class PresupuestosViewModelFactory(private val repository: CommercialRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PresupuestosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PresupuestosViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}