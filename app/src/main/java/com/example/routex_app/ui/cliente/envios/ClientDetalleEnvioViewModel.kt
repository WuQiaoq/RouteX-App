package com.example.routex_app.ui.cliente.envios


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.ui.commercial.envios.DetalleEnvio
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado para la pantalla de detalle
data class ClientDetalleState(
    val isLoading: Boolean = false,
    val data: DetalleEnvio? = null,
    val error: String? = null
)

class ClientDetalleEnvioViewModel(
    private val repository: ClientRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClientDetalleState())
    val state: StateFlow<ClientDetalleState> = _state.asStateFlow()

    fun loadDetalle(token: String, envioId: Int, userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.getDetalleEnvioCliente(token, envioId, userId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(isLoading = false, data = result.data)
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                // ESTO ES LO QUE FALTA:
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}