package com.example.routex_app.ui.commercial.clientes



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.viewmodels.ClienteViewModel

class ClienteViewModelFactory(private val repository: CommercialRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClienteViewModel(repository) as T
    }
}