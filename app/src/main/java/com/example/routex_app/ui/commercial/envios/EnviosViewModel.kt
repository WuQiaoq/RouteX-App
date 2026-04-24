package com.example.routex_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.ui.commercial.envios.EnvioActivo
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// El estado ahora maneja EnvioActivo
data class EnviosState(
    val isLoading: Boolean = false,
    val data: List<EnvioActivo>? = null, // CAMBIADO
    val error: String? = null
)

class EnviosViewModel(private val repository: CommercialRepository) : ViewModel() {

    private val _state = MutableStateFlow(EnviosState())
    val state: StateFlow<EnviosState> = _state.asStateFlow()

    private var llistaOriginal: List<EnvioActivo> = emptyList()

    fun loadOfertas(token: String, userId: Int) { // Añadido userId
        viewModelScope.launch {
            _state.value = EnviosState(isLoading = true)

            // Llamamos al nuevo método del repositorio corregido
            val result = repository.getEnviosActivos(token, userId)

            when (result) {
                is Resource.Success -> {
                    llistaOriginal = result.data ?: emptyList()
                    _state.value = EnviosState(isLoading = false, data = llistaOriginal)
                }
                is Resource.Error -> {
                    _state.value = EnviosState(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }

    // Función de filtrado actualizada con los campos de EnvioActivo
    fun aplicarFiltre(query: String, estatBoto: String = "Todos") {
        val llistaFiltrada = llistaOriginal.filter { envio ->
            // Filtro de texto
            val coincideixQuery = query.isEmpty() ||
                    envio.cliente.contains(query, ignoreCase = true) ||
                    envio.concepto.contains(query, ignoreCase = true)

            // Filtro de estado (Mapeo de UI a Base de Datos)
            val coincideixEstat = when (estatBoto) {
                "Todos" -> true
                "En Puerto" -> envio.estadoId == 5 // "Aceptada" en tu DB
                "En Tránsito" -> envio.estadoId == 6 // "Tránsito" en tu DB
                else -> true
            }

            coincideixQuery && coincideixEstat
        }
        _state.value = _state.value.copy(data = llistaFiltrada)
    }
}