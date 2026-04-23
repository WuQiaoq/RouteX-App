package com.example.routex_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.models.Oferta // CAMBIADO: De Presupuesto a Oferta
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// El estado ahora espera una lista de Ofertas
data class EnviosState(
    val isLoading: Boolean = false,
    val data: List<Oferta>? = null, // CAMBIADO
    val error: String? = null
)

class EnviosViewModel(private val repository: CommercialRepository) : ViewModel() {

    private val _state = MutableStateFlow(EnviosState())
    val state: StateFlow<EnviosState> = _state.asStateFlow()

    fun loadOfertas(token: String) {
        viewModelScope.launch {
            _state.value = EnviosState(isLoading = true)

            // El repositorio devuelve Resource<List<Oferta>>
            val result = repository.getOfertasResource(token)

            when (result) {
                is Resource.Success -> {
                    // Ahora result.data es List<Oferta>, que coincide con EnviosState
                    _state.value = EnviosState(isLoading = false, data = result.data)
                }
                is Resource.Error -> {
                    _state.value = EnviosState(isLoading = false, error = result.message)
                }
                else -> {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }
}