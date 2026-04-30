package com.example.routex_app.cliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.NavigationUtilsClient
import com.example.routex_app.R
import com.example.routex_app.adapters.PresupuestoClienteAdapter
import com.example.routex_app.adapters.PresupuestoClienteItem
import com.example.routex_app.databinding.ActivityPresupuestototalClienteBinding
import com.example.routex_app.models.Presupuesto
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.launch

class PresupuestosTotalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresupuestototalClienteBinding

    private var token: String = ""
    private var userId: Int = -1
    private var userName: String = "Cliente"
    private lateinit var adapter: PresupuestoClienteAdapter
    private var todosLosPresupuestos: List<PresupuestoClienteItem> = emptyList()
    private val repository by lazy {
        ClientRepository(ApiService(KtorClient.httpClient))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresupuestototalClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra("USER_TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)
        userName = intent.getStringExtra("USER_NAME") ?: "Cliente"

        setupRecyclerView()
        setupListeners()
        setupBottomNavigation()
        actualizarTabs("Todos")
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            cargarPresupuestos()
        }
    }

    private fun setupRecyclerView() {
        adapter = PresupuestoClienteAdapter(emptyList()) { presupuesto ->
            val nextIntent = Intent(this, ClienteDetallePresupuestoActivity::class.java).apply {
                putExtra("USER_TOKEN", token)
                putExtra("USER_ID", userId)
                putExtra("USER_NAME", userName)
                putExtra("PRESUPUESTO_ID", presupuesto.id)
                putExtra("CODIGO", presupuesto.codigo)
                putExtra("TITULO", presupuesto.titulo)
                putExtra("RUTA", presupuesto.ruta)
                putExtra("FECHA", presupuesto.fecha)
                putExtra("PRECIO", presupuesto.precio)
                putExtra("ESTADO", presupuesto.estado)
            }
            startActivity(nextIntent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PresupuestosTotalActivity)
            adapter = this@PresupuestosTotalActivity.adapter
        }
    }

    private fun cargarPresupuestos() {
        if (token.isEmpty() || userId == -1) {
            Toast.makeText(this, "Sesion no valida", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            when (val result = repository.obtenerPresupuestosCliente(token, userId)) {
                is Resource.Success -> {
                    todosLosPresupuestos = result.data
                        ?.map { it.toClienteItem() }
                        ?: emptyList()
                    filtrarPresupuestos("Todos")
                }

                is Resource.Error -> {
                    Toast.makeText(
                        this@PresupuestosTotalActivity,
                        result.message ?: "Error al cargar presupuestos",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Loading -> Unit
            }
        }
    }

    private fun Presupuesto.toClienteItem(): PresupuestoClienteItem {
        return PresupuestoClienteItem(
            id = id,
            codigo = "#QUO-$id",
            titulo = Concepto.ifBlank { "Presupuesto de transporte" },
            ruta = Ruta,
            fecha = Fecha ?: "Fecha pendiente",
            precio = Valor,
            estado = normalizarEstado(Estado, EstadoId)
        )
    }

    private fun normalizarEstado(estado: String?, estadoId: Int?): String {
        val estadoLimpio = estado?.trim().orEmpty()
        if (estadoLimpio.isNotEmpty()) {
            return when {
                estadoLimpio.contains("accept", ignoreCase = true) ||
                        estadoLimpio.contains("activa", ignoreCase = true) -> "Aceptado"
                estadoLimpio.contains("cancel", ignoreCase = true) ||
                        estadoLimpio.contains("rebuig", ignoreCase = true) ||
                        estadoLimpio.contains("rechaz", ignoreCase = true) -> "Rechazado"
                estadoLimpio.contains("expir", ignoreCase = true) -> "Expirado"
                else -> "Pendiente"
            }
        }

        return when (estadoId) {
            1 -> "Aceptado"
            4 -> "Rechazado"
            else -> "Pendiente"
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            val nextIntent = Intent(this, SolicitarPresupuestoActivity::class.java).apply {
                putExtra("USER_TOKEN", token)
                putExtra("USER_ID", userId)
                putExtra("USER_NAME", userName)
            }
            startActivity(nextIntent)
        }

        binding.btnNotifications.setOnClickListener {
            binding.viewNotificationDot.visibility = android.view.View.GONE
            Toast.makeText(this, "Sin notificaciones nuevas", Toast.LENGTH_SHORT).show()
        }

        binding.tabTodos.setOnClickListener { filtrarPresupuestos("Todos") }
        binding.tabPendientes.setOnClickListener { filtrarPresupuestos("Pendiente") }
        binding.tabAceptados.setOnClickListener { filtrarPresupuestos("Aceptado") }
        binding.tabRechazados.setOnClickListener { filtrarPresupuestos("Rechazado") }
    }

    private fun filtrarPresupuestos(estado: String) {
        val filtrados = if (estado == "Todos") {
            todosLosPresupuestos
        } else {
            todosLosPresupuestos.filter { it.estado.equals(estado, ignoreCase = true) }
        }

        adapter.actualizarDatos(filtrados)
        actualizarTabs(estado)
    }

    private fun actualizarTabs(estadoSeleccionado: String) {
        // filtrar
        val tabs = listOf(
            binding.tabTodos to "Todos",
            binding.tabPendientes to "Pendiente",
            binding.tabAceptados to "Aceptado",
            binding.tabRechazados to "Rechazado"
        )

        tabs.forEach { (tab, estado) ->
            val seleccionado = estado == estadoSeleccionado
            tab.setTextColor(Color.parseColor(if (seleccionado) "#F97316" else "#64748B"))
            tab.setTypeface(
                null,
                if (seleccionado) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL
            )
        }
    }

    private fun setupBottomNavigation() {
        NavigationUtilsClient().setupBottomNavigation(
            activity = this,
            bottomNav = binding.bottomNavigation,
            currentItemId = R.id.nav_budgets
        )
    }
}
