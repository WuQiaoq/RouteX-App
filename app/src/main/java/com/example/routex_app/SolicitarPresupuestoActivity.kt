package com.example.routex_app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.routex_app.databinding.ActivitySolicitarPresupuestoBinding
import com.example.routex_app.models.OferteRequest
import com.example.routex_app.models.PortModel
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SolicitarPresupuestoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySolicitarPresupuestoBinding
    private var portsList: List<PortModel> = emptyList()

    private var token: String = ""
    private var userId: Int = -1
    private var userName: String = "Cliente"

    private val repository by lazy {
        ClientRepository(ApiService(KtorClient.httpClient))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySolicitarPresupuestoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra("USER_TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)
        userName = intent.getStringExtra("USER_NAME") ?: "Cliente"

        if (token.isEmpty() || userId == -1) {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupWindowInsets()
        setupBottomNavigation()
        loadPorts()
        setupListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_budgets

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    val nextIntent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_TOKEN", token)
                        putExtra("USER_ID", userId)
                        putExtra("USER_NAME", userName)
                    }
                    startActivity(nextIntent)
                    finish()
                    true
                }

                R.id.nav_budgets -> {
                    val nextIntent = Intent(this, PresupuestosTotalActivity::class.java).apply {
                        putExtra("USER_TOKEN", token)
                        putExtra("USER_ID", userId)
                        putExtra("USER_NAME", userName)
                    }
                    startActivity(nextIntent)
                    finish()
                    true
                }

                R.id.nav_shipping -> {
                    Toast.makeText(this, "Envíos", Toast.LENGTH_SHORT).show()
                    val nextIntent = Intent(this, EnviosTotalActivity::class.java).apply {
                        putExtra("USER_TOKEN", token)
                        putExtra("USER_ID", userId)
                        putExtra("USER_NAME", userName)
                    }
                    startActivity(nextIntent)
                    true
                }

                R.id.nav_chat -> {
                    Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun setupListeners() {

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnEnviarSolicitud.setOnClickListener {

            val origen = binding.actvOrigen.text.toString().trim()
            val destino = binding.actvDestino.text.toString().trim()
            val cantidadStr = binding.etCantidad.text.toString().trim()
            val tipoMercancia = binding.etTipoMercancia.text.toString().trim()
            val descripcionDetallada = binding.etDescripcion.text.toString().trim()

            val portOrigenId = portsList.find { it.nom == origen }?.id
            val portDestiId = portsList.find { it.nom == destino }?.id

            val errorMessage = when {
                origen.isEmpty() || destino.isEmpty() || cantidadStr.isEmpty() ->
                    "Completa todos los campos"

                cantidadStr.toIntOrNull() == null ->
                    "Cantidad inválida"

                portOrigenId == null || portDestiId == null ->
                    "Selecciona un origen y destino válidos"

                else -> null
            }

            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val incotermMap = mapOf(
                R.id.chip_exw to 1017,
                R.id.chip_fob to 1018,
                R.id.chip_cif to 1019,
                R.id.chip_ddp to 1020,
                R.id.chip_dap to 1021
            )

            val incotermId = incotermMap[binding.cgIncoterm.checkedChipId]
                ?: run {
                    Toast.makeText(this, "Selecciona un incoterm", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val request = OferteRequest(
                incotermId = incotermId,
                clientId = userId,
                tipusValidacioId = 1,
                estatOfertaId = 1,
                operadorId = 13,
                descripMercancia = descripcionDetallada.ifEmpty {
                    "Mercancía de $origen a $destino"
                },
                portOrigenId = portOrigenId,
                portDestiId = portDestiId,
                aeroportOrigenId = null,
                aeroportDestiId = null,
                tipusContenidorId = null,
                concepto = tipoMercancia.ifEmpty {
                    "$origen → $destino"
                },
                bultos = cantidadStr,
                dataCreacio = date
            )

            lifecycleScope.launch {
                when (val result = repository.createOferte(request)) {
                    is Resource.Success -> {
                        Toast.makeText(
                            this@SolicitarPresupuestoActivity,
                            "Solicitud enviada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            this@SolicitarPresupuestoActivity,
                            result.message ?: "Error al enviar la solicitud",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun loadPorts() {
        lifecycleScope.launch {
            when (val result = repository.getPorts()) {

                is Resource.Success -> {
                    portsList = result.data ?: emptyList()
                    val nombres = portsList.map { it.nom }

                    val adapterPorts = ArrayAdapter(
                        this@SolicitarPresupuestoActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        nombres
                    )

                    binding.actvOrigen.setAdapter(adapterPorts)
                    binding.actvDestino.setAdapter(adapterPorts)
                }

                is Resource.Error -> {
                    Toast.makeText(
                        this@SolicitarPresupuestoActivity,
                        result.message ?: "Error al cargar puertos",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> Unit
            }
        }
    }
}
