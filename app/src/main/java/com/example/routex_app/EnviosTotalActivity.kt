package com.example.routex_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.adapters.EnviosAdapter
import com.example.routex_app.data.Envio
import com.example.routex_app.databinding.ActivityEnviosBinding
import com.example.routex_app.models.ClientEnviosViewModel
import com.example.routex_app.models.ClientEnviosViewModelFactory
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
import kotlinx.coroutines.launch

class EnviosTotalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnviosBinding

    // 🔥 ViewModel + Factory
    private val viewModel: ClientEnviosViewModel by viewModels {
        ClientEnviosViewModelFactory(
            ClientRepository(ApiService(KtorClient.httpClient))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEnviosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = "Mis Envíos"

        val token = intent.getStringExtra("USER_TOKEN") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)

        // RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 🚀 调 API
        if (token.isNotEmpty() && userId != -1) {
            viewModel.loadEnvios(token, userId)
        }

        observarEstado()
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    // ✅ 成功
                    state.data?.let { lista ->

                        val envios = lista.map {
                            Envio(
                                code = "#SHP-${it.id}",
                                tipo = "Tipo ${it.TipusTransportId}",
                                origen = it.Ruta.split("-").getOrNull(0) ?: "Origen",
                                destino = it.Ruta.split("-").getOrNull(1) ?: "Destino",
                                estado = "Pendiente",
                                fecha = it.Valor
                            )
                        }

                        binding.recyclerView.adapter =
                            EnviosAdapter(envios) { envio ->
                                Toast.makeText(
                                    this@EnviosTotalActivity,
                                    envio.code,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                    // ❌ error
                    state.error?.let {
                        Toast.makeText(
                            this@EnviosTotalActivity,
                            it,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}