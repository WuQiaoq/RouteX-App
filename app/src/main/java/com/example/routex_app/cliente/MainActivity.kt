package com.example.routex_app.cliente

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.routex_app.NavigationUtilsClient
import com.example.routex_app.R
import com.example.routex_app.adapters.RecentActivityAdapter
import com.example.routex_app.data.MainState
import com.example.routex_app.databinding.ActivityMainBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
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
            binding.viewNotificationDot.visibility = View.GONE
            Toast.makeText(this, "Sin notificaciones nuevas", Toast.LENGTH_SHORT).show()
        }

        // solicitar presupuesto
        binding.bottomNavigation.menu.clear()
        binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_cliente)

        // 2. Marcar el item actual
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        // 3. Configurar la navegación pasándole los datos necesarios para que no se pierdan al navegar
        NavigationUtilsClient().setupBottomNavigation(
            activity = this,
            bottomNav = binding.bottomNavigation,
            currentItemId = R.id.nav_home
        )

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