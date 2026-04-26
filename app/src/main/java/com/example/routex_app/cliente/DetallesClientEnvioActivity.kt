package com.example.routex_app.cliente

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.NavigationUtilsClient
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityClienteDetalleEnvioBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.network.NetworkClient
import com.example.routex_app.ui.cliente.envios.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DetallesClientEnvioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClienteDetalleEnvioBinding
    private var token: String = ""
    private var userId: Int = -1
    private var envioId: Int = -1

    private val viewModel: ClientDetalleEnvioViewModel by viewModels {
        ClientDetalleViewModelFactory(ClientRepository(ApiService(KtorClient.httpClient)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClienteDetalleEnvioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra("USER_TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)
        envioId = intent.getIntExtra("PEDIDO_ID", -1)

        NavigationUtilsClient().setupBottomNavigation(this, binding.bottomNav, R.id.nav_shipping)

        setupListeners()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                irAListadoEnvios()
            }
        })
        observarEstado()

        if (envioId != -1) {
            viewModel.loadDetalle(token, envioId, userId)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { irAListadoEnvios() }
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.loadingSpinner.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    state.data?.let { detalle ->
                        binding.tvHeaderTitle.text = "Envío #${detalle.orderNumber}"
                        binding.tvHeroTitle.text = detalle.concepto
                        binding.tvHeroSubtitle.text = detalle.rutaCompleta
                        binding.tvStatus.text = detalle.estadoActual

                        binding.tvLastUpdate.text = detalle.trackingSteps
                            .filter { it.estaCompletado == 1 }
                            .lastOrNull()?.dataHora ?: "--/--/--"

                        val pasosCompletados = detalle.trackingSteps.count { it.estaCompletado == 1 }
                        if (detalle.trackingSteps.isNotEmpty()) {
                            binding.progressBarHorizontal.progress = (pasosCompletados * 100) / detalle.trackingSteps.size
                        }

                        binding.rvTracking.apply {
                            layoutManager = LinearLayoutManager(this@DetallesClientEnvioActivity)
                            adapter = ClientDocumentAdapter(
                                steps = detalle.trackingSteps,
                                onVerClick = { fileName -> descargarYVerDocumento(fileName) }
                            )
                        }
                    }
                }
            }
        }
    }

    // --- MÉTODOS DE DESCARGA COPIADOS Y ADAPTADOS DEL COMERCIAL ---

    private fun descargarYVerDocumento(fullPath: String) {
        val cleanFileName = fullPath.substringAfterLast("/") // solo para extensión y nombre del archivo temp
        val folderId = envioId.toString()

        Log.d("BAIXAR", "fullPath: $fullPath → cleanFileName: $cleanFileName → folderId: $folderId")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { binding.loadingSpinner.visibility = View.VISIBLE }

                val bytesDescargados = NetworkClient.baixarDni(folderId, fullPath.trimStart('/'))

                withContext(Dispatchers.Main) {
                    binding.loadingSpinner.visibility = View.GONE
                    if (bytesDescargados != null && bytesDescargados.isNotEmpty()) {
                        Log.d("APP_SUCCESS", "Fitxer llest: $cleanFileName (${bytesDescargados.size} bytes)")
                        if (cleanFileName.lowercase().endsWith(".pdf")) {
                            abrirPdfExterno(bytesDescargados, cleanFileName)
                        } else {
                            mostrarImagenDialog(bytesDescargados)
                        }
                    } else {
                        Toast.makeText(this@DetallesClientEnvioActivity, "No s'ha pogut obtenir el fitxer", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.loadingSpinner.visibility = View.GONE
                    Log.e("APP_ERROR", "Error: ${e.message}")
                }
            }
        }
    }

    private fun mostrarImagenDialog(bytes: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        if (bitmap == null) {
            Log.e("IMG_ERROR", "Bytes no vàlids per a imatge. Mida: ${bytes.size}")
            Toast.makeText(this, "Error: El fitxer no és una imatge vàlida", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = android.app.Dialog(this)
        val imageView = ImageView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adjustViewBounds = true
            setImageBitmap(bitmap)
        }

        dialog.setContentView(imageView)
        dialog.show()
    }

    private fun abrirPdfExterno(bytes: ByteArray, fileName: String) {
        try {
            val tempFile = File(cacheDir, fileName)
            tempFile.writeBytes(bytes)

            val contentUri: Uri = FileProvider.getUriForFile(
                this, "${packageName}.provider", tempFile // ← .provider
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Ver PDF"))
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun irAListadoEnvios() {
        val intent = Intent(this, EnviosTotalActivity::class.java).apply {
            putExtra("USER_TOKEN", token)
            putExtra("USER_ID", userId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

}