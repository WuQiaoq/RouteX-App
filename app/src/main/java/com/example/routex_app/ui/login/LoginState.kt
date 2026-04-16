package com.example.routex_app.ui.login

import com.example.routex_app.models.UsuariModel

data class LoginState(
    val isLoading: Boolean = false,      // ¿Muestro el circulito de carga?
    val success: Boolean = false,        // ¿Entramos a la app?
    val error: String? = null,           // ¿Hay un mensaje de error que mostrar?
    val token: String? = null,            // El token recibido
    val usuari: UsuariModel? = null
                     )