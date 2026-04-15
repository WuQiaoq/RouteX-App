package com.example.routex_app.commercial


import android.os.Bundle
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

        val bottomNav = binding.bottomNav
        NavigationUtils.setupBottomNavigation(this, bottomNav, R.id.nav_home)

        // 2. Recuperamos TOKEN e ID (importante que el nombre coincida con el Login)
        val token = intent.getStringExtra("USER_TOKEN") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)


        // LOG DE PRUEBA: Mira esto en el Logcat
        android.util.Log.d("DEBUG_APP", "Token: $token")
        android.util.Log.d("DEBUG_APP", "ID Recibido: $userId")
        if (token.isNotEmpty() && userId != -1) {
            viewModel.loadDashboard(token, userId)
        } else {
            android.util.Log.e("DEBUG_APP", "DATOS INCOMPLETOS: No se llamará a la API")
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show()
        }

        // Observamos el estado del ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    state.data?.let { info ->
                        binding.tvUserName.text = info.user_name.uppercase()

                        // Vinculamos los conteos con los IDs del XML
                        binding.tvPendingCount.text = info.pending_count.toString()
                        binding.tvActiveOpsCount.text = info.active_ops_count.toString()
                        binding.tvRejectedCount.text = info.rejected_count.toString()
                    }

                    if (state.isLoading) {
                        binding.tvUserName.text = "Cargando..."
                    }

                    state.error?.let {
                        Toast.makeText(this@CommercialHomeActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

class CommercialViewModelFactory(private val repository: CommercialRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommercialHomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommercialHomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}