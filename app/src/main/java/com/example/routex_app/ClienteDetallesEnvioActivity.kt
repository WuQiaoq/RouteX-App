package com.example.routex_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.adapters.DocumentoEnvio
import com.example.routex_app.adapters.DocumentosEnvioAdapter
import com.example.routex_app.databinding.ActivityDetalleEnviosBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.launch
import java.io.File

class ClienteDetallesEnvioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleEnviosBinding

    private var token: String = ""
    private var userId: Int = -1
    private var userName: String = "Cliente"
    private var envioId: Int = -1
    private val repository by lazy {
        ClientRepository(ApiService(KtorClient.httpClient))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEnviosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra("USER_TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)
        userName = intent.getStringExtra("USER_NAME") ?: "Cliente"
        envioId = intent.getIntExtra("ENVIO_ID", -1)

        setupListeners()
        setupBottomNavigation()
        mostrarDetalles()
        cargarTracking()
        mostrarDocumentos()
    }

    private fun setupListeners() {
        binding.btnBackDetalle.setOnClickListener {
            finish()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_shipping

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

                R.id.nav_shipping -> true

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

    private fun mostrarDetalles() {
        val code = intent.getStringExtra("CODE") ?: "#SHP-0000"
        val tipo = intent.getStringExtra("TIPO") ?: "Envio"
        val origen = intent.getStringExtra("ORIGEN") ?: "Origen"
        val destino = intent.getStringExtra("DESTINO") ?: "Destino"
        val estado = intent.getStringExtra("ESTADO") ?: "Pendiente"
        val fecha = intent.getStringExtra("FECHA") ?: "Pendiente de confirmar"
        val progreso = calcularProgreso(estado)

        binding.tvDetalleTitulo.text = "Detalle de Envio $code"
        binding.tvHeroTitle.text = tipo
        binding.tvHeroSubtitle.text = "$origen -> $destino"
        binding.tvEstadoActual.text = estado
        binding.tvLlegadaEstimada.text = fecha
        binding.progressBar.progress = progreso
        binding.tvProgressPercent.text = "$progreso% Completado"
        binding.tvHitoActual.text = estado
        binding.tvHitoRuta.text = "$origen - $destino"
    }

    private fun cargarTracking() {
        if (token.isEmpty() || envioId == -1) {
            return
        }

        lifecycleScope.launch {
            when (val result = repository.obtenerTrackingEnvioCliente(token, envioId)) {
                is Resource.Success -> {
                    val tracking = result.data ?: return@launch
                    binding.tvHeroSubtitle.text = tracking.ruta
                    binding.tvEstadoActual.text = tracking.estado
                    binding.tvLlegadaEstimada.text = tracking.llegadaEstimada
                    binding.progressBar.progress = tracking.progreso
                    binding.tvProgressPercent.text = "${tracking.progreso}% Completado"

                    val hitosVisibles = tracking.hitos.filter { it.completado }
                        .ifEmpty { tracking.hitos }
                        .takeLast(2)
                    val hitoPrincipal = hitosVisibles.lastOrNull()
                    val hitoSecundario = hitosVisibles.dropLast(1).lastOrNull()
                    binding.tvHitoActual.text = hitoPrincipal?.titulo ?: tracking.estado
                    binding.tvHitoRuta.text = hitoPrincipal?.descripcion ?: tracking.ruta
                    binding.tvHitoSecundarioTitulo.visibility =
                        if (hitoSecundario == null) View.GONE else View.VISIBLE
                    binding.tvHitoSecundarioDescripcion.visibility =
                        if (hitoSecundario == null) View.GONE else View.VISIBLE
                    binding.tvHitoSecundarioTitulo.text = hitoSecundario?.titulo.orEmpty()
                    binding.tvHitoSecundarioDescripcion.text = hitoSecundario?.descripcion.orEmpty()
                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }
    }

    private fun mostrarDocumentos() {
        val documentos = listOf(
            DocumentoEnvio(
                titulo = "Factura proforma",
                subtitulo = "PDF",
                nombreArchivo = "factura.pdf",
                disponible = true
            ),
            DocumentoEnvio(
                titulo = "Albaran de entrega",
                subtitulo = "PDF",
                nombreArchivo = "albaran.pdf",
                disponible = true
            ),
            DocumentoEnvio(
                titulo = "Albaran firmado",
                subtitulo = "PDF",
                nombreArchivo = "albaran_firmado.pdf",
                disponible = true
            )
        )

        binding.rvDocuments.apply {
            layoutManager = LinearLayoutManager(this@ClienteDetallesEnvioActivity)
            adapter = DocumentosEnvioAdapter(documentos) { documento ->
                if (!documento.disponible) {
                    Toast.makeText(
                        this@ClienteDetallesEnvioActivity,
                        "Documento no disponible todavia",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@DocumentosEnvioAdapter
                }

                descargarDocumento(documento)
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun descargarDocumento(documento: DocumentoEnvio) {
        if (token.isEmpty() || envioId == -1) {
            Toast.makeText(this, "Sesion no valida", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            when (val result = repository.descargarDocumentoCliente(token, envioId, documento.nombreArchivo)) {
                is Resource.Success -> {
                    val bytes = result.data ?: ByteArray(0)
                    if (bytes.isEmpty()) {
                        Toast.makeText(
                            this@ClienteDetallesEnvioActivity,
                            "Documento no disponible todavia",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    val downloadsDir = getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                        ?: filesDir
                    val outputFile = File(downloadsDir, documento.nombreArchivo)
                    outputFile.writeBytes(bytes)

                    Toast.makeText(
                        this@ClienteDetallesEnvioActivity,
                        "Documento guardado: ${outputFile.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Error -> {
                    Toast.makeText(
                        this@ClienteDetallesEnvioActivity,
                        "Documento no disponible todavia",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> Unit
            }
        }
    }

    private fun calcularProgreso(estado: String): Int {
        return when (estado.trim().uppercase()) {
            "ACEPTADA", "ACEPTADO", "COMPLETADO", "COMPLETADA", "ENTREGADO", "ENTREGADA" -> 100
            "EN TRANSITO", "EN TRÁNSITO" -> 72
            "ADUANA" -> 85
            "PENDIENTE" -> 35
            "RETRASADO", "RETRASADA" -> 55
            else -> 50
        }
    }
}
