package com.example.routex_app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.routex_app.commercial.CommercialHomeActivity
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.AuthRepository
import com.example.routex_app.ui.login.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // 1. Conecta el ViewModel
    private val viewModel: LoginViewModel by viewModels {
        // Aquí armamos la cadena de dependencias
        val apiService = ApiService(KtorClient.httpClient)
        val repository = AuthRepository(apiService)
        LoginViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // 2. Clic en botón Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validación básica
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.onLoginClick(email, password)
        }

        // 3. Observa el resultado del login
        lifecycleScope.launch {
            // Esto hace que la observación se detenga si la app no está visible
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    // Manejar Loading
                    btnLogin.isEnabled = !state.isLoading
                    btnLogin.text = if (state.isLoading) "Cargando..." else "Iniciar sesión"

                    // Manejar Error
                    state.error?.let {
                        Toast.makeText(this@LoginActivity, it, Toast.LENGTH_LONG).show()
                        // Limpiamos el error después de mostrarlo para que no se repita
                        // viewModel.clearError()
                    }

                    // Manejar Éxito
                    if (state.success && state.token != null) {
                        // 1. Extraemos el usuario del estado (asegúrate de que tu LoginState guarde el objeto usuari)
                        val usuario = state.usuari
                        val rolId = usuario?.rol_id?.toIntOrNull() ?: 0

                        // 2. Definimos a qué Activity ir según el rol
                        val destinoActivity = when (rolId) {
                            1, 2, 4 -> CommercialHomeActivity::class.java
                            ///3 -> ClientHomeActivity::class.java
                            else -> null
                        }

                        if (destinoActivity != null) {
                            Toast.makeText(this@LoginActivity, "Bienvenido, ${usuario?.nom}", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, destinoActivity)
                            val userObj = usuario?.id

                            // Opcional: Pasar el token o el nombre a la siguiente pantalla
                            intent.putExtra("USER_TOKEN", state.token)
                            intent.putExtra("USER_ID", userObj)
                            startActivity(intent)
                            finish() // Cerramos el Login para que no puedan volver atrás
                        } else {
                            Toast.makeText(this@LoginActivity, "Rol no reconocido ($rolId)", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
    class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}