package com.example.routex_app

import android.os.Bundle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

class SolicitarPresupuestoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySolicitarPresupuestoBinding
    private var portsList: List<PortModel> = emptyList()

    private val repository by lazy {
        ClientRepository(ApiService(KtorClient.httpClient))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySolicitarPresupuestoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
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

    private fun setupListeners() {
        binding.btnEnviarSolicitud.setOnClickListener {

            val origen = binding.actvOrigen.text.toString().trim()
            val destino = binding.actvDestino.text.toString().trim()

            val portOrigenId = portsList.find { it.nom == origen }?.id
            val portDestiId = portsList.find { it.nom == destino }?.id

            if (portOrigenId == null || portDestiId == null) {
                Toast.makeText(this, "Selecciona un origen y destino válidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cantidadStr = binding.etCantidad.text.toString().trim()
            val tipoMercancia = binding.etTipoMercancia.text.toString().trim()
            val descripcionDetallada = binding.etDescripcion.text.toString().trim()

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val incotermMap = mapOf(
                R.id.chip_exw to 1017,
                R.id.chip_fob to 1018,
                R.id.chip_cif to 1019,
                R.id.chip_ddp to 1020,
                R.id.chip_dap to 1021
            )

            val selectedIncotermChipId = binding.cgIncoterm.checkedChipId
            val incotermIdNullable = incotermMap[selectedIncotermChipId]

            val errorMessage = when {
                origen.isEmpty() || destino.isEmpty() || cantidadStr.isEmpty() ->
                    "Completa todos los campos"

                cantidadStr.toIntOrNull() == null ->
                    "Cantidad inválida"

                incotermIdNullable == null ->
                    "Selecciona un incoterm"

                else -> null
            }

            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // the Chip -> to find the incotermId in the map
            val incotermId = incotermMap[binding.cgIncoterm.checkedChipId]
                // if the user did not select the chip::
                ?: run {
                    Toast.makeText(this, "Selecciona un incoterm", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            val request = OferteRequest(
                incotermId = incotermId,
                clientId = 1008,
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