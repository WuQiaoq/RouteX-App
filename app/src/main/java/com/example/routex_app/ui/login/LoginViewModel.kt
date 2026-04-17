package com.example.routex_app.ui.login

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.repository.AuthRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onLoginClick(usuario: String, clavePlana: String) {
        viewModelScope.launch {
            // 1. Indicar carga a la UI
            _state.value = LoginState(isLoading = true)

            try {
                // 2. "Disfrazamos" la contraseña con Base64 (Reversible)
                // Usamos NO_WRAP para evitar saltos de línea que rompan la URL/JSON
                val claveCodificada = Base64.encodeToString(clavePlana.toByteArray(), Base64.NO_WRAP)

                // 3. Llamada al repositorio con la clave codificada
                when (val result = repository.login(usuario, claveCodificada)) {
                    is Resource.Success -> {
                        _state.value = LoginState(
                            isLoading = false,
                            success = true,
                            token = result.data?.token,
                            usuari = result.data?.usuari
                                                 )
                    }
                    is Resource.Error -> {
                        _state.value = LoginState(
                            isLoading = false,
                            error = result.message ?: "Error desconocido"
                                                 )
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                _state.value = LoginState(
                    isLoading = false,
                    error = "Error en el proceso: ${e.message}"
                                         )
            }
        }
    }
}