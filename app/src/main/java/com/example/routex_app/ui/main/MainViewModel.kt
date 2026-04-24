package com.example.routex_app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.data.MainState
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ClientRepository) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun loadDashboard(token: String, userId: Int, userName: String) {
        _state.update {
            it.copy(
                isLoading = true,
                userName = userName,
                error = null
            )
        }

        viewModelScope.launch {
            when (val result = repository.getClientDashboard(token, userId)) {

                is Resource.Success -> {
                    val data = result.data

                    if (data != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                userName = data.userName,
                                activeCount = data.activeCount,
                                pendingCount = data.pendingCount,
                                error = null
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "No se recibieron datos"
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error desconocido"
                        )
                    }
                }

                is Resource.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }
            }
        }
    }
}