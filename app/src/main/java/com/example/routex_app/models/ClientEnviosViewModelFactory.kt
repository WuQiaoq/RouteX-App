package com.example.routex_app.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.routex_app.repository.ClientRepository

class ClientEnviosViewModelFactory(
    private val repository: ClientRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClientEnviosViewModel(repository) as T
    }
}