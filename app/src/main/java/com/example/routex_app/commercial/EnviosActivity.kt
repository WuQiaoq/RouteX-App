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
                                tvOrigin.text = oferta.portOrigen?.nom ?: "Origen no definido"
                                tvDest.text = oferta.portDesti?.nom ?: "Destino no definido"
                                tvOrderNumber.text = "Pedido #${oferta.id}"
                                tvStatus.text = oferta.estadoInfo?.estat ?: "Pendiente"

                                // --- AÑADE ESTO ---
                                // Suponiendo que tu CardView o el contenedor principal en el XML se llama 'cardOferta'
                                // Si no tiene ID, ponle uno al contenedor principal en activity_commercial_envios.xml
                                root.setOnClickListener {
                                    val intent = android.content.Intent(this@EnviosActivity, DetallesEnvioActivity::class.java)
                                    intent.putExtra("PEDIDO_ID", oferta.id.toString())
                                    intent.putExtra("CLIENTE", "ID Cliente: ${oferta.id}") // Ajustar según campo real
                                    intent.putExtra("ORIGEN", oferta.portOrigen?.nom ?: "N/A")
                                    intent.putExtra("DESTINO", oferta.portDesti?.nom ?: "N/A")
                                    intent.putExtra("ESTADO", oferta.estadoInfo?.estat ?: "PENDIENTE")
                                    startActivity(intent)
                                }
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