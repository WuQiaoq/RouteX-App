package com.example.routex_app.cliente

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.NavigationUtilsClient
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityClienteEnviosBinding
import com.example.routex_app.databinding.ActivityEnviosBinding
import com.example.routex_app.ui.cliente.envios.ClientEnviosViewModel
import com.example.routex_app.ui.cliente.envios.ClientEnviosViewModelFactory
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.ui.cliente.envios.ClientRepository
import com.example.routex_app.ui.commercial.envios.EnvioAdapter
import kotlinx.coroutines.launch

class EnviosTotalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClienteEnviosBinding
    private var estatFiltreActual = "Todos"
    private var token: String = ""
    private var userId: Int = -1
    private lateinit var envioAdapter: EnvioAdapter

    private val viewModel: ClientEnviosViewModel by viewModels {
        ClientEnviosViewModelFactory(
            ClientRepository(ApiService(KtorClient.httpClient))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClienteEnviosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de navegación (Ajusta el ID nav_shipping según tu menú de cliente)
        NavigationUtilsClient().setupBottomNavigation(this, binding.bottomNav, R.id.nav_shipping)

        token = intent.getStringExtra("USER_TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)

        setupRecyclerView()

        if (token.isNotEmpty() && userId != -1) {
            viewModel.loadEnvios(token, userId)
        }

        configurarEscoltadors()
        observarEstado()
    }

    private fun setupRecyclerView() {
        envioAdapter = EnvioAdapter(emptyList()) { envio ->
            val intent = Intent(this, DetallesClientEnvioActivity::class.java).apply { // <--- AQUÍ
                putExtra("PEDIDO_ID", envio.id)
                putExtra("USER_ID", userId)
                putExtra("USER_TOKEN", token)
            }
            startActivity(intent)
        }

        binding.rvEnvios.apply {
            layoutManager = LinearLayoutManager(this@EnviosTotalActivity)
            adapter = envioAdapter
        }
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    // Si tienes un ProgressBar en el XML con ID 'loading', descomenta esta línea:
                    // binding.loading.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    state.data?.let { lista ->
                        envioAdapter.updateData(lista)
                        binding.rvEnvios.visibility = if (lista.isNotEmpty()) View.VISIBLE else View.GONE
                    }

                    state.error?.let { msg ->
                        Toast.makeText(this@EnviosTotalActivity, "Error: $msg", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun configurarEscoltadors() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.aplicarFiltre(s.toString(), estatFiltreActual)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnFilterAll.setOnClickListener { actualizarFiltro("Todos", it as Button) }
        binding.btnFilterTransit.setOnClickListener { actualizarFiltro("En Tránsito", it as Button) }
        binding.btnFilterPort.setOnClickListener { actualizarFiltro("En Puerto", it as Button) }
    }

    private fun actualizarFiltro(estado: String, boton: Button) {
        estatFiltreActual = estado
        marcarBotoActiu(boton)
        viewModel.aplicarFiltre(binding.searchBar.text.toString(), estatFiltreActual)
    }

    private fun marcarBotoActiu(botoSeleccionat: Button) {
        val botons = listOf(binding.btnFilterAll, binding.btnFilterTransit, binding.btnFilterPort)
        botons.forEach { btn ->
            val isSelected = btn == botoSeleccionat
            btn.setBackgroundColor(ContextCompat.getColor(this, if (isSelected) R.color.color_primary else android.R.color.white))
            btn.setTextColor(ContextCompat.getColor(this, if (isSelected) android.R.color.white else R.color.color_secondary_light))
        }
    }
}