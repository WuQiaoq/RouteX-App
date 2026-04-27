package com.example.routex_app

import android.os.Bundle
import android.widget.Toast
import android.content.Intent
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
    private var token: String = ""
    private var userId: Int = -1
    private var userName: String = "Cliente"

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

        token = intent.getStringExtra("USER_TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)
        userName = intent.getStringExtra("USER_NAME") ?: "Cliente"

        // RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        setupBottomNavigation()
        
        if (token.isNotEmpty() && userId != -1) {
            viewModel.loadEnvios(token, userId)
        }

        observarEstado()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_shipping

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val nextIntent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_TOKEN", token)
                        putExtra("USER_ID", userId)
                        putExtra("USER_NAME", userName)
                    }
                    startActivity(nextIntent)
                    finish()
                    true
                }

                R.id.nav_budgets -> {
                    val nextIntent = Intent(this, PresupuestosTotalActivity::class.java).apply {
                        putExtra("USER_TOKEN", token)
                        putExtra("USER_ID", userId)
                        putExtra("USER_NAME", userName)
                    }
                    startActivity(nextIntent)
                    finish()
                    true
                }

                R.id.nav_shipping -> true

                R.id.nav_chat -> {
                    Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    
                    state.data?.let { lista ->

                        val envios = lista.map {
                            Envio(
                                id = it.id,
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
                                val detailIntent = Intent(
                                    this@EnviosTotalActivity,
                                    ClienteDetallesEnvioActivity::class.java
                                ).apply {
                                    putExtra("USER_TOKEN", token)
                                    putExtra("USER_ID", userId)
                                    putExtra("USER_NAME", userName)
                                    putExtra("ENVIO_ID", envio.id)
                                    putExtra("CODE", envio.code)
                                    putExtra("TIPO", envio.tipo)
                                    putExtra("ORIGEN", envio.origen)
                                    putExtra("DESTINO", envio.destino)
                                    putExtra("ESTADO", envio.estado)
                                    putExtra("FECHA", envio.fecha)
                                }
                                startActivity(detailIntent)
                            }
                    }

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
