package com.example.routex_app


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.example.routex_app.ui.login.LoginState
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // Inyección de dependencias manual a través del Factory
    private val viewModel: LoginViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        val repository = AuthRepository(apiService)
        LoginViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Configuración de visualización Edge-to-Edge
        val mainView = findViewById<android.view.View>(R.id.loginActivity)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

        // Acción del botón Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.onLoginClick(email, password)
        }

        // Observar el flujo de estado del ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    actualizarUI(state, btnLogin)
                }
            }
        }
    }

    private fun actualizarUI(state: LoginState, btnLogin: MaterialButton) {
        // Estado de carga
        btnLogin.isEnabled = !state.isLoading
        btnLogin.text = if (state.isLoading) "Validando..." else "Iniciar sesión"

        // Mostrar errores si los hay
        state.error?.let {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            // Limpiamos el error en el ViewModel si fuera necesario para que no se repita el Toast
        }

        // Si el login es exitoso y tenemos al usuario
        if (state.success && state.usuari != null) {
            manejarNavegacion(state)
        }
    }

    private fun manejarNavegacion(state: LoginState) {
        val usuario = state.usuari!!
        val token = state.token // <--- Recuperamos el token que el VM ya guardó en el state

        val rolId = usuario.rolId

        val destinoActivity = when (rolId) {
            1, 2, 4 -> CommercialHomeActivity::class.java
            else -> null
        }

        if (destinoActivity != null) {
            Toast.makeText(this, "¡Bienvenido, ${usuario.nom}!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, destinoActivity).apply {
                // PASO DE DATOS CRUCIAL:
                putExtra("USER_ID", usuario.id)
                putExtra("USER_TOKEN", token)
                putExtra("USER_NAME", usuario.nom)
            }

            // Log de control para estar seguros antes de saltar
            android.util.Log.d("DEBUG_APP", "Navegando con Token: ${token?.take(10)}...")

            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Acceso denegado: Rol $rolId no autorizado", Toast.LENGTH_LONG).show()
        }
    }

    // Factory para construir el ViewModel con sus dependencias
    class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}