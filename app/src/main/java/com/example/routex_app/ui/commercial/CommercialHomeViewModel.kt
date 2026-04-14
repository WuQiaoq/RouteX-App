package com.example.routex_app.ui.commercial


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.models.CommercialDashboardResponse
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CommercialState(
    val isLoading: Boolean = false,
    val data: CommercialDashboardResponse? = null,
    val error: String? = null
)

class CommercialHomeViewModel(private val repository: CommercialRepository) : ViewModel() {

    private val _state = MutableStateFlow(CommercialState())
    val state: StateFlow<CommercialState> = _state

    // Ahora pedimos el userId que viene del Login
    fun loadDashboard(token: String, userId: Int) {
        viewModelScope.launch {
            _state.value = CommercialState(isLoading = true)

            // Pasamos ambos parámetros al repositorio
            when (val result = repository.getDashboardData(token, userId)) {
                is Resource.Success -> {
                    _state.value = CommercialState(isLoading = false, data = result.data)
                }
                is Resource.Error -> {
                    _state.value = CommercialState(isLoading = false, error = result.message)
                }
                else -> Unit
            }
        }
    }
}