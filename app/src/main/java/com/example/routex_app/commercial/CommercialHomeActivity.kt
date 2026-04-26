package com.example.routex_app.commercial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.routex_app.JuegoActivity
import com.example.routex_app.NavigationUtilsCommercial
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

        NavigationUtilsCommercial.setupBottomNavigation(this, binding.bottomNav, R.id.nav_home)

        val token = intent.getStringExtra("USER_TOKEN") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)

        if (token.isNotEmpty() && userId != -1) {
            viewModel.loadDashboard(token, userId)
        } else {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnNuevoCliente.setOnClickListener {
            startActivity(Intent(this, CommercialNewClientActivity::class.java))
        }

        // --- ARREGLADO: Llamamos a la función que ya creaste abajo ---
        binding.btnJugarRouteX.setOnClickListener {
            mostrarDialogoSeleccionTransporte()
        }

        observarEstado()
    }

    private fun mostrarDialogoSeleccionTransporte() {
        val opciones = arrayOf("CAMION", "BARCO", "AVION")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Selecciona tu transporte")
        builder.setItems(opciones) { _, which ->
            val transporteElegido = opciones[which]

            // Ahora el Intent funcionará perfectamente sin pedir 'provider'
            val intent = Intent(this, JuegoActivity::class.java)
            intent.putExtra("TIPO_VEHICULO", transporteElegido)
            startActivity(intent)
        }
        builder.show()
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        binding.tvUserName.text = "Cargando..."
                    }
                    state.data?.let { info ->
                        binding.tvUserName.text = info.userName.uppercase()
                        binding.tvPendingCount.text = info.pendingCount.toString()
                        binding.tvActiveOpsCount.text = info.activeOpsCount.toString()
                        binding.tvRejectedCount.text = info.rejectedCount.toString()
                    }
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