package com.example.routex_app.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.models.Presupuesto
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClientEnviosState(
    val isLoading: Boolean = false,
    val data: List<Presupuesto>? = null,
    val error: String? = null
)

class ClientEnviosViewModel(
    private val repository: ClientRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClientEnviosState())
    val state: StateFlow<ClientEnviosState> = _state.asStateFlow()

    fun loadEnvios(token: String, userId: Int) {
        viewModelScope.launch {

            _state.value = ClientEnviosState(isLoading = true)

            when (val result = repository.getEnvios(token, userId)) {

                is Resource.Success -> {
                    _state.value = ClientEnviosState(
                        data = result.data
                    )
                }

                is Resource.Error -> {
                    _state.value = ClientEnviosState(
                        error = result.message
                    )
                }

                else -> {
                    _state.value = ClientEnviosState()
                }
            }
        }
    }
}