package com.example.routex_app.cliente

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.routex_app.LoginActivity
import com.example.routex_app.NavigationUtilsClient
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityPerfilBinding
import com.example.routex_app.models.UserProfileModel
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.ui.commercial.profile.ProfileViewModel
import com.example.routex_app.ui.commercial.profile.ProfileViewModelFactory
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    private val viewModel: ProfileViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        ProfileViewModelFactory(CommercialRepository(apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupProfilePlaceholder()
        setupNavigation()
        setupListeners()
        observeProfile()
        loadProfile()
    }

    private fun setupProfilePlaceholder() {
        val userId = intent.getIntExtra("USER_ID", -1)
        val userName = intent.getStringExtra("USER_NAME").orEmpty()

        binding.tvName.text = userName.ifBlank { "Cliente" }
        binding.tvId.text = if (userId != -1) "ID Cliente: $userId" else "ID Cliente: --"
        binding.tvEmail.text = "Pendiente de conectar con backend"
        binding.tvPhone.text = "Pendiente de conectar con backend"
    }

    private fun loadProfile() {
        val token = intent.getStringExtra("USER_TOKEN").orEmpty()
        val userId = intent.getIntExtra("USER_ID", -1)

        if (token.isNotEmpty() && userId != -1) {
            viewModel.fetchUserProfile(userId, token)
        } else {
            Toast.makeText(this, "Error: Sesion no valida", Toast.LENGTH_LONG).show()
        }
    }

    private fun observeProfile() {
        lifecycleScope.launch {
            viewModel.profile.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> resource.data?.let { updateProfile(it) }
                    is Resource.Error -> {
                        Toast.makeText(this@PerfilActivity, resource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateProfile(profile: UserProfileModel) {
        binding.tvName.text = profile.fullName
        binding.tvId.text = "ID Cliente: ${profile.userId}"
        binding.tvEmail.text = profile.email
        binding.tvPhone.text = profile.phone
    }

    private fun setupNavigation() {
        NavigationUtilsClient().setupBottomNavigation(
            activity = this,
            bottomNav = binding.bottomNavigation,
            currentItemId = R.id.nav_profile
        )
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
