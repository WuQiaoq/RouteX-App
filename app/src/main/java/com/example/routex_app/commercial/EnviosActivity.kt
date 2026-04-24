package com.example.routex_app.commercial

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
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.NavigationUtils
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityCommercialEnviosBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.ui.commercial.envios.EnvioAdapter
import com.example.routex_app.viewmodel.EnviosViewModel
import kotlinx.coroutines.launch

class EnviosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommercialEnviosBinding
    private var estatFiltreActual = "Todos"
    private var token: String = ""
    private var userId: Int = -1

    // 1. Declaramos el adaptador
    private lateinit var envioAdapter: EnvioAdapter

    private val viewModel: EnviosViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        val repository = CommercialRepository(apiService)
        EnviosViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommercialEnviosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationUtils.setupBottomNavigation(this, binding.bottomNav, R.id.nav_ofertas)

        token = intent.getStringExtra("USER_TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)

        // 2. Inicializar el RecyclerView antes de cargar datos
        setupRecyclerView()

        if (token.isNotEmpty() && userId != -1) {
            viewModel.loadOfertas(token, userId)
        }

        configurarEscoltadors()
        observarEstado()
    }

    private fun setupRecyclerView() {
        envioAdapter = EnvioAdapter(emptyList()) { envio ->
            // Click en la card: vamos a detalles
            val intent = Intent(this, DetallesEnvioActivity::class.java)
            intent.putExtra("PEDIDO_ID", envio.id.toString())
            intent.putExtra("USER_TOKEN", token)
            startActivity(intent)
        }

        binding.rvEnvios.apply {
            layoutManager = LinearLayoutManager(this@EnviosActivity)
            adapter = envioAdapter
        }
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    // 3. Pasamos TODA la lista al adaptador
                    state.data?.let { llista ->
                        envioAdapter.updateData(llista)

                        // Si no hay datos, podrías mostrar un texto de "No hay envíos"
                        binding.rvEnvios.visibility = if (llista.isNotEmpty()) View.VISIBLE else View.GONE
                    }

                    state.error?.let { msg ->
                        Toast.makeText(this@EnviosActivity, "Error: $msg", Toast.LENGTH_SHORT).show()
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

    class EnviosViewModelFactory(private val repository: CommercialRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = EnviosViewModel(repository) as T
    }
}