package com.example.routex_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.adapters.RecentActivityAdapter
import com.example.routex_app.data.MainState
import com.example.routex_app.databinding.ActivityMainBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.ui.main.MainViewModel
import kotlinx.coroutines.launch
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecentActivityAdapter

    private val viewModel: MainViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        val repository = ClientRepository(apiService)
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupListeners()

        val token = intent.getStringExtra("USER_TOKEN") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)
        val userName = intent.getStringExtra("USER_NAME") ?: "Cliente"

        if (token.isNotEmpty() && userId != -1) {
            viewModel.loadDashboard(token, userId, userName)
        } else {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show()
            finish()
        }

        observarEstado()
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    actualizarUI(state)
                }
            }
        }
    }

    private fun actualizarUI(state: MainState) {
        binding.tvWelcomeName.text =
            if (state.isLoading) "Cargando, ${state.userName}..."
            else "Bienvenido de nuevo, ${state.userName}"

        binding.tvActiveCount.text = state.activeCount.toString()
        binding.tvPendingCount.text = state.pendingCount.toString()

        adapter.updateData(state.recentActivities)

        state.error?.let {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = RecentActivityAdapter(emptyList())
        binding.rvRecentActivity.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = this@MainActivity.adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupListeners() {
        binding.btnNotifications.setOnClickListener {
            binding.viewNotificationDot.visibility = android.view.View.GONE
            Toast.makeText(this, "Sin notificaciones nuevas", Toast.LENGTH_SHORT).show()
        }

        // solicitar presupuesto
        binding.btnRequestQuote.setOnClickListener {
            val token = this.intent.getStringExtra("USER_TOKEN") ?: ""
            val userId = this.intent.getIntExtra("USER_ID", -1)
            val userName = this.intent.getStringExtra("USER_NAME") ?: "Cliente"

            val nextIntent = Intent(this, SolicitarPresupuestoActivity::class.java).apply {
                putExtra("USER_TOKEN", token)
                putExtra("USER_ID", userId)
                putExtra("USER_NAME", userName)
            }

            startActivity(nextIntent)
        }

        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    true
                }

                R.id.nav_budgets -> {
                    Toast.makeText(this, "Cotizar", Toast.LENGTH_SHORT).show()
                    val nextIntent = Intent(this, PresupuestosTotalActivity::class.java).apply {
                        putExtra("USER_TOKEN", intent.getStringExtra("USER_TOKEN") ?: "")
                        putExtra("USER_ID", intent.getIntExtra("USER_ID", -1))
                        putExtra("USER_NAME", intent.getStringExtra("USER_NAME") ?: "Cliente")
                    }
                    startActivity(nextIntent)
                    true
                }

                R.id.nav_shipping -> {
                    Toast.makeText(this, "Envíos", Toast.LENGTH_SHORT).show()
                    val nextIntent = Intent(this, EnviosTotalActivity::class.java).apply {
                        putExtra("USER_TOKEN", intent.getStringExtra("USER_TOKEN") ?: "")
                        putExtra("USER_ID", intent.getIntExtra("USER_ID", -1))
                        putExtra("USER_NAME", intent.getStringExtra("USER_NAME") ?: "Cliente")
                    }
                    startActivity(nextIntent)
                    true
                }

                R.id.nav_chat -> {
                    Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show()
                    // startActivity(Intent(this, ChatActivity::class.java))
                    true
                }

                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                    // startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }

                else -> false
            }
        }

    }

    class MainViewModelFactory(private val repository: ClientRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
