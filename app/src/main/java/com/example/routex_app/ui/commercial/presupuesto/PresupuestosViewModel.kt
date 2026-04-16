package com.example.routex_app.ui.commercial.presupuesto


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.models.Presupuesto
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PresupuestosViewModel(private val repository: CommercialRepository) : ViewModel() {

    // Este es el estado que la UI va a observar (StateFlow)
    private val _presupuestosState = MutableStateFlow<Resource<List<Presupuesto>>>(Resource.Loading())
    val presupuestosState: StateFlow<Resource<List<Presupuesto>>> = _presupuestosState

    fun fetchPresupuestos(userId: Int, token: String, type: String) {
        viewModelScope.launch {
            _presupuestosState.value = Resource.Loading()

            // Llamamos a la función específica de tu repositorio según el "type"
            val result = when (type) {
                "SENT" -> repository.getSentQuotes(userId, token)
                "ACCEPTED" -> repository.getAcceptedQuotes(userId, token)
                "REJECTED" -> repository.getRejectedQuotes(userId, token)
                else -> Resource.Error("Tipo de presupuesto no reconocido")
            }

            _presupuestosState.value = result
        }
    }
}