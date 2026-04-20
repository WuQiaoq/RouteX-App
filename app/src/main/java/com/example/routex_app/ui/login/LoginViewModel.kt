package com.example.routex_app.ui.login



import android.util.Log
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

    fun onLoginClick(email: String, clave: String) {
        viewModelScope.launch {
            // Estado inicial de carga
            _state.value = LoginState(isLoading = true)

            try {
                val result = repository.login(email, clave)

                when (result) {
                    is Resource.Success -> {
                        val loginResponse = result.data

                        if (loginResponse != null && loginResponse.token != null) {
                            // Log de depuración técnica
                            Log.d("LOGIN_VM", "Token recibido con éxito: ${loginResponse.token.take(10)}...")

                            _state.value = LoginState(
                                isLoading = false,
                                success = true,
                                usuari = loginResponse.usuari,
                                token = loginResponse.token // Asignación crucial
                            )
                        } else {
                            _state.value = LoginState(
                                isLoading = false,
                                error = "El servidor no devolvió un token válido"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = LoginState(
                            isLoading = false,
                            error = result.message ?: "Credenciales incorrectas"
                        )
                    }
                    else -> {
                        _state.value = _state.value.copy(isLoading = false)
                    }
                }
            } catch (e: Exception) {
                Log.e("LOGIN_VM", "Excepción en login", e)
                _state.value = LoginState(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }
}