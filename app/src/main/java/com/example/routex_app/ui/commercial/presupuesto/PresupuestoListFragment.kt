package com.example.routex_app.ui.commercial.presupuesto


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.R
import com.example.routex_app.commercial.DetallesEnvioActivity
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

        val userId = arguments?.getInt("USER_ID") ?: 0
        val token = arguments?.getString("TOKEN") ?: ""
        val tipo = arguments?.getString("TIPO") ?: "SENT"

        // Configurar MVVM
        val apiService = ApiService(KtorClient.httpClient)
        val repository = CommercialRepository(apiService)
        val factory = PresupuestosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PresupuestosViewModel::class.java]

        // 3. Inicializar RecyclerView con el Listener de Clic
        val rv = view.findViewById<RecyclerView>(R.id.rvPresupuestos)

        // --- AQUÍ CONECTAMOS EL CLIC ---
        adaptador = PresupuestoAdapter(emptyList(), tipo) { presupuesto ->
            // Definimos la navegación a la actividad de gestión (la del XML de detalles)
            val intent = Intent(requireContext(), DetallesEnvioActivity::class.java).apply {
                putExtra("PEDIDO_ID", presupuesto.id.toString())
                putExtra("RUTA", presupuesto.Ruta)
                putExtra("CONCEPTO", presupuesto.Concepto)
                putExtra("PRECIO", presupuesto.Valor)
                putExtra("TOKEN", token) // Pasamos el token para futuras gestiones
            }
            startActivity(intent)
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adaptador

        // Suscribirse al estado del ViewModel
        lifecycleScope.launchWhenStarted {
            viewModel.presupuestosState.collectLatest { recurso ->
                when (recurso) {
                    is Resource.Success -> {
                        adaptador.actualizarDatos(recurso.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), recurso.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> { /* Spinner si quieres */ }
                }
            }
        }

        viewModel.fetchPresupuestos(userId, token, tipo)
    }
}