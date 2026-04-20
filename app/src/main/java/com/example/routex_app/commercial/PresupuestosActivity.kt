package com.example.routex_app.commercial

import com.example.routex_app.ui.commercial.presupuesto.PresupuestoListFragment
import com.example.routex_app.ui.commercial.presupuesto.PresupuestosViewModel
import com.example.routex_app.ui.commercial.presupuesto.PresupuestosViewModelFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.routex_app.NavigationUtils
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityPresupuestosBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.CommercialRepository
import com.google.android.material.tabs.TabLayoutMediator

class PresupuestosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresupuestosBinding

    // Inyección manual de ViewModel siguiendo el estilo de Home
    private val viewModel: PresupuestosViewModel by viewModels {
        val apiService = ApiService(KtorClient.httpClient)
        val repository = CommercialRepository(apiService)
        PresupuestosViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresupuestosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Configurar Barra de Navegación Inferior (Misma lógica que Home)
        val bottomNav = binding.bottomNav
        NavigationUtils.setupBottomNavigation(this, bottomNav, R.id.nav_budgets)

        // 2. Recuperar TOKEN e ID
        val token = intent.getStringExtra("TOKEN") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)

        // 3. Configurar el ViewPager2 con el adaptador de fragmentos
        binding.viewPager.adapter = PresupuestosPagerAdapter(this, userId, token)

        // 4. Vincular Tabs con ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Enviados"
                1 -> "Aceptados"
                2 -> "Rechazados"
                else -> ""
            }
        }.attach()

        // 5. Botón Atrás
        binding.btnBack.setOnClickListener { finish() }
    }

    private class PresupuestosPagerAdapter(
        activity: FragmentActivity,
        private val userId: Int,
        private val token: String
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            val tipo = when (position) {
                0 -> "SENT"
                1 -> "ACCEPTED"
                2 -> "REJECTED"
                else -> throw IllegalStateException("Posición inválida")
            }
            return PresupuestoListFragment.newInstance(userId, token, tipo)
        }
    }

}