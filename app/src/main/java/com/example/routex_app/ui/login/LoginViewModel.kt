package com.example.routex_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.repository.AuthRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    // Esta es nuestra tubería de datos privada (Mutable)
    private val _state = MutableStateFlow(LoginState())
    // Esta es la versión pública que la UI puede leer pero no modificar
    val state: StateFlow<LoginState> = _state

    fun onLoginClick(usuario: String, clave: String) {
        // Iniciamos la corrutina (proceso de fondo)
        viewModelScope.launch {
            // 1. Decimos a la UI que estamos cargando
            _state.value = LoginState(isLoading = true)

            // 2. Llamamos al repositorio
            when (val result = repository.login(usuario, clave)) {
                is Resource.Success -> {
                    // 3. Éxito: Guardamos el token y avisamos a la UI
                    _state.value = LoginState(
                        isLoading = false,
                        success = true,
                        token = result.data?.token,
                        usuari = result.data?.usuari
                                             )
                }
                is Resource.Error -> {
                    // 4. Error: Pasamos el mensaje de error del PHP o de red
                    _state.value = LoginState(
                        isLoading = false,
                        error = result.message
                                             )
                }
                else -> Unit
            }
        }
    }
}