package com.example.routex_app.commercial

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.routex_app.databinding.ActivityCommercialEnviosBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.NavigationUtils
import com.example.routex_app.R
import com.example.routex_app.viewmodel.EnviosViewModel
import kotlinx.coroutines.launch

class EnviosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommercialEnviosBinding

    private val viewModel: EnviosViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        val repository = CommercialRepository(apiService)
        EnviosViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommercialEnviosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // IMPORTANTE: Asegúrate que R.id.nav_envios existe en tu menu
        NavigationUtils.setupBottomNavigation(this, binding.bottomNav, R.id.nav_ofertas)

        val token = intent.getStringExtra("USER_TOKEN") ?: ""

        if (token.isNotEmpty()) {
            viewModel.loadOfertas(token)
        }

        observarEstado()
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    state.data?.let { lista ->
                        if (lista.isNotEmpty()) {
                            // Si aquí te dice que es 'Presupuesto', es que el ViewModel
                            // está emitiendo List<Presupuesto>
                            val oferta = lista[0]

                            binding.apply {
                                // Usamos .toString() o el operador elvis (?:) con un String
                                // para garantizar que el tipo sea CharSequence
                                tvOrigin.text = oferta.portOrigen?.nom ?: "Origen no definido"
                                tvDest.text = oferta.portDesti?.nom ?: "Destino no definido"

                                // Si usas el ID del pedido, conviértelo a String explícitamente
                                tvOrderNumber.text = "Pedido #${oferta.id}"

                                // Para el estado, accedemos al objeto anidado que vimos en el LOG
                                tvStatus.text = oferta.estadoInfo?.estat ?: "Pendiente"
                            }
                        }
                    }

                    state.error?.let { msg ->
                        Toast.makeText(this@EnviosActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    class EnviosViewModelFactory(private val repository: CommercialRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EnviosViewModel(repository) as T
        }
    }
}