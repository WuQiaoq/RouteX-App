package com.example.routex_app.commercial


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.NavigationUtils
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityClientesActivosBinding
import com.example.routex_app.ui.commercial.clientes.ClientesActivosAdapter
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.ui.commercial.clientes.ClienteViewModelFactory
import com.example.routex_app.utils.Resource
import com.example.routex_app.viewmodels.ClienteViewModel
import com.example.routex_app.network.KtorClient
import com.example.routex_app.network.ApiService
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClientesActivosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesActivosBinding
    private lateinit var adapter: ClientesActivosAdapter

    // Delegamos la creación del ViewModel al Factory usando tu KtorClient
    private val viewModel: ClienteViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        ClienteViewModelFactory(CommercialRepository(apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientesActivosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupTabs()
        observeViewModel()

        binding.fabAddClient.setOnClickListener {
            val intent = Intent(this, CommercialNewClientActivity::class.java)
        }
        val token = intent.getStringExtra("USER_TOKEN") ?: "" // Antes tenías "AUTH_TOKEN"
        val userId = intent.getIntExtra("USER_ID", -1)

        NavigationUtils.setupBottomNavigation(this, binding.bottomNav, R.id.nav_clients)

        if (userId != 0 && token.isNotEmpty()) {
            viewModel.fetchActiveClients(userId, token)
        } else {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_LONG).show()
            finish() // Cerramos la actividad si no hay datos de sesión
        }
    }

    private fun setupUI() {
        // Inicializamos el adapter con una lista vacía y la acción de clic
        adapter = ClientesActivosAdapter(emptyList()) { cliente ->
            Toast.makeText(this, "Empresa: ${cliente.companyName}", Toast.LENGTH_SHORT).show()
        }

        binding.rvClientes.apply {
            layoutManager = LinearLayoutManager(this@ClientesActivosActivity)
            adapter = this@ClientesActivosActivity.adapter
        }

        // Botón volver del header
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Botón añadir cliente (FAB)
        binding.fabAddClient.setOnClickListener {
            Toast.makeText(this, "Nuevo cliente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val status = tab?.text.toString()
                // Aquí podrías filtrar la lista localmente
                filterListByStatus(status)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun filterListByStatus(status: String) {
        // Por ahora solo mostramos un aviso, la lógica dependerá de tu DTO
        Toast.makeText(this, "Filtrando por: $status", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.clientesActivos.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let { list ->
                            if (list.isEmpty()) {
                                Toast.makeText(this@ClientesActivosActivity, "No tienes clientes activos", Toast.LENGTH_SHORT).show()
                            }
                            adapter.updateData(list)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@ClientesActivosActivity, resource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}