package com.example.routex_app.commercial


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.routex_app.NavigationUtils
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityCommercialHomeBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.ui.commercial.home.CommercialHomeViewModel
import kotlinx.coroutines.launch

class CommercialHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommercialHomeBinding
    private val viewModel: CommercialHomeViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        val repository = CommercialRepository(apiService)
        CommercialViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommercialHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationUtils.setupBottomNavigation(this, binding.bottomNav, R.id.nav_home)

        val token = intent.getStringExtra("USER_TOKEN") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)

        if (token.isNotEmpty() && userId != -1) {
            viewModel.loadDashboard(token, userId)
        } else {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show()
            finish() // Si no hay sesión, cerramos para evitar bugs
        }

        binding.btnNuevoCliente.setOnClickListener {
            startActivity(Intent(this, CommercialNewClientActivity::class.java))
        }

        observarEstado()
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    // 1. Control de visualización de Carga
                    if (state.isLoading) {
                        binding.tvUserName.text = "Cargando..."
                    }

                    // 2. Control de Datos (Solo si no es nulo)
                    state.data?.let { info ->
                        // Aquí usamos el nombre que viene de C# (user_name)
                        // pero asegúrate que tu CommercialDashboardModel use @SerialName
                        binding.tvUserName.text = info.userName.uppercase()

                        binding.tvPendingCount.text = info.pendingCount.toString()
                        binding.tvActiveOpsCount.text = info.activeOpsCount.toString()
                        binding.tvRejectedCount.text = info.rejectedCount.toString()
                    }

                    // 3. Control de Errores
                    state.error?.let {
                        binding.tvUserName.text = "Error al cargar"
                        Toast.makeText(this@CommercialHomeActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    class CommercialViewModelFactory(private val repository: CommercialRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommercialHomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CommercialHomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}