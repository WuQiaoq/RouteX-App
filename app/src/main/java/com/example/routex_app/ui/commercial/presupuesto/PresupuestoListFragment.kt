package com.example.routex_app.ui.commercial.presupuesto

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.R
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.collectLatest

class PresupuestoListFragment : Fragment(R.layout.fragment_presupuesto_list) {

    private lateinit var viewModel: PresupuestosViewModel
    private lateinit var adaptador: PresupuestoAdapter

    companion object {
        fun newInstance(userId: Int, token: String, tipo: String) = PresupuestoListFragment().apply {
            arguments = Bundle().apply {
                putInt("USER_ID", userId)
                putString("TOKEN", token)
                putString("TIPO", tipo)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recuperar parámetros de la instancia
        val userId = arguments?.getInt("USER_ID") ?: 0
        val token = arguments?.getString("TOKEN") ?: ""
        val tipo = arguments?.getString("TIPO") ?: "SENT"

        // 2. Configurar Arquitectura MVVM
        val apiService = ApiService(KtorClient.httpClient)
        val repository = CommercialRepository(apiService)
        val factory = PresupuestosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PresupuestosViewModel::class.java]

        // 3. Inicializar RecyclerView con el nuevo Adaptador
        val rv = view.findViewById<RecyclerView>(R.id.rvPresupuestos)
        adaptador = PresupuestoAdapter(emptyList(), tipo)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adaptador

        // 4. Suscribirse al estado del ViewModel
        lifecycleScope.launchWhenStarted {
            viewModel.presupuestosState.collectLatest { recurso ->
                when (recurso) {
                    is Resource.Success -> {
                        adaptador.actualizarDatos(recurso.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), recurso.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        // Opcional: Mostrar un indicador de carga
                    }
                }
            }
        }

        // 5. Cargar los datos
        viewModel.fetchPresupuestos(userId, token, tipo)
    }
}