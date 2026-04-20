package com.example.routex_app // 根据你的实际包名调整

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
import com.example.routex_app.ui.main.MainState
import com.example.routex_app.ui.main.MainViewModel
import kotlinx.coroutines.launch

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
        if (state.isLoading) {
            binding.tvWelcomeName.text = "Cargando, ${state.userName}..."
        } else {
            binding.tvWelcomeName.text = "Bienvenido de nuevo, ${state.userName}"
        }

        binding.tvActiveCount.text = state.activeCount.toString()
        binding.tvPendingCount.text = state.pendingCount.toString()

        if (state.recentActivities.isNotEmpty()) {
            adapter.updateData(state.recentActivities)
        }

        // 错误提示
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

        binding.btnRequestQuote.setOnClickListener {
            Toast.makeText(this, "Solicitando presupuesto...", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, RequestQuoteActivity::class.java))
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