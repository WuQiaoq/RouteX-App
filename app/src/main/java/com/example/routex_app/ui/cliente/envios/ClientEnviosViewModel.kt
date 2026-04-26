package com.example.routex_app.ui.cliente.envios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.ui.commercial.envios.EnvioActivo
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientEnviosViewModel(
    private val repository: ClientRepository
) : ViewModel() {

    // Referenciamos la data class que está en el otro archivo
    private val _state = MutableStateFlow(ClientEnvioListState())
    val state: StateFlow<ClientEnvioListState> = _state.asStateFlow()

    private var llistaOriginal: List<EnvioActivo> = emptyList()

    fun loadEnvios(token: String, userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.getEnviosActivos(token, userId)) {
                is Resource.Success -> {
                    llistaOriginal = result.data ?: emptyList()
                    _state.update {
                        it.copy(isLoading = false, data = llistaOriginal)
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun aplicarFiltre(query: String, estatBoto: String = "Todos") {
        val llistaFiltrada = llistaOriginal.filter { envio ->
            val coincideixQuery = query.isEmpty() ||
                    envio.id.toString().contains(query) ||
                    envio.concepto.contains(query, ignoreCase = true)

            val coincideixEstat = when (estatBoto) {
                "Todos" -> true
                "En Puerto" -> envio.estadoId == 5
                "En Tránsito" -> envio.estadoId == 6
                else -> true
            }
            coincideixQuery && coincideixEstat
        }
        _state.update { it.copy(data = llistaFiltrada) }
    }
}