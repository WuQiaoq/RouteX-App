package com.example.routex_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.models.ClienteActivo
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClienteViewModel(private val repository: CommercialRepository) : ViewModel() {

    private val _clientesActivos = MutableStateFlow<Resource<List<ClienteActivo>>>(Resource.Loading())
    val clientesActivos: StateFlow<Resource<List<ClienteActivo>>> = _clientesActivos

    fun fetchActiveClients(userId: Int, token: String) {
        viewModelScope.launch {
            _clientesActivos.value = Resource.Loading()
            val result = repository.getActiveClients(userId, token)
            _clientesActivos.value = result
        }
    }
}