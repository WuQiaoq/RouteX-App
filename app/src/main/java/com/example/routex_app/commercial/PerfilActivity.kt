package com.example.routex_app.commercial

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.routex_app.NavigationUtils
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityPerfilCommercialBinding
import com.example.routex_app.models.UserProfileModel
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.utils.Resource
import com.example.routex_app.ui.commercial.profile.ProfileViewModelFactory
import com.example.routex_app.ui.commercial.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.jvm.java

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilCommercialBinding

    // Factory específico inyectando el repositorio
    private val viewModel: ProfileViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        ProfileViewModelFactory(CommercialRepository(apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilCommercialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Recuperar los datos del Intent (Igual que en ClientesActivosActivity)
        val token = intent.getStringExtra("USER_TOKEN") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)

        setupUI()
        observeViewModel()

        // 2. Configurar el menú inferior (NavigationUtils se encarga de pasar los tokens al navegar)
        NavigationUtils.setupBottomNavigation(this, binding.bottomNav, R.id.nav_profile)

        // 3. Validar y llamar a la API
        if (userId != -1 && token.isNotEmpty()) {
            viewModel.fetchUserProfile(userId, token)
        } else {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_LONG).show()
            // Si no hay token, volvemos al login
            val loginIntent = Intent(this, com.example.routex_app.auth.LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }

    private fun setupUI() {
        // Configuramos los iconos de los layouts incluidos manualmente
        binding.optionEmail.ivIcon.setImageResource(R.drawable.ic_email)
        binding.optionPhone.ivIcon.setImageResource(R.drawable.ic_phone)
        binding.optionPassword.ivIcon.setImageResource(R.drawable.ic_lock)
        binding.switch2FA.ivIcon.setImageResource(R.drawable.ic_security)
        binding.switchPush.ivIcon.setImageResource(R.drawable.ic_notifications)

        binding.btnLogout.setOnClickListener {
            // Para cerrar sesión simplemente limpiamos el stack y vamos al login
            val intent = Intent(this, com.example.routex_app.auth.LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.profile.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Opcional: mostrar un loader
                    }
                    is Resource.Success -> {
                        resource.data?.let { profile ->
                            updateFields(profile)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@PerfilActivity, resource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateFields(profile: UserProfileModel) {
        with(binding) {
            tvUserName.text = profile.fullName
            // ID de colaborador generado en tu DTO de C#
            tvUserSubtitle.text = "${profile.roleName} | ${profile.companyName}\nID: ${profile.colaboradorId}"

            optionEmail.tvTitle.text = "Correo Electrónico"
            optionEmail.tvSubtitle.text = profile.email

            optionPhone.tvTitle.text = "Teléfono"
            optionPhone.tvSubtitle.text = profile.phone

            optionPassword.tvTitle.text = "Cambiar Contraseña"
            optionPassword.tvSubtitle.text = "Actualizada recientemente"

            switch2FA.tvTitle.text = "Autenticación de dos pasos"
            switchPush.tvTitle.text = "Notificaciones Push"
        }
    }
}