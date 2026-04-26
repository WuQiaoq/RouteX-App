package com.example.routex_app.commercial

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.NavigationUtilsCommercial
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityCommercialGestionEnviosBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.network.NetworkClient
import com.example.routex_app.ui.commercial.envios.TrackingStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class DetallesEnvioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommercialGestionEnviosBinding
    private lateinit var apiService: ApiService
    private var pedidoId: String = "0"
    private var token: String = ""
    private var currentStepId: Int = -1

    // Lanzador corregido para aceptar imágenes y PDFs
    private val contractPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { subirArchivo(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommercialGestionEnviosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiService(KtorClient.httpClient)
        pedidoId = intent.getStringExtra("PEDIDO_ID") ?: "0"
        token = intent.getStringExtra("USER_TOKEN") ?: ""

        setupListeners()

        if (token.isNotEmpty() && pedidoId != "0") {
            obtenerDatosReales()
        } else {
            Toast.makeText(this, "Error: Datos de sesión no encontrados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        NavigationUtilsCommercial.setupBottomNavigation(this, binding.bottomNav, R.id.nav_ofertas)
    }

    private fun obtenerDatosReales() {
        lifecycleScope.launch {
            try {
                val detalle = withContext(Dispatchers.IO) {
                    apiService.getDetalleEnvio(token, pedidoId.toInt())
                }

                mostrarCabecera(detalle.cliente, detalle.orderNumber)
                setupTrackingList(detalle.trackingSteps)

                // ← AÑADE ESTO
                binding.tvStatus.text = detalle.estadoActual

                binding.tvLastUpdate.text = detalle.trackingSteps
                    .filter { it.teDocument }
                    .lastOrNull()?.dataHora ?: "--/--/--"

                val pasosCompletados = detalle.trackingSteps.count { it.teDocument }
                if (detalle.trackingSteps.isNotEmpty()) {
                    binding.progressBarHorizontal.progress =
                        (pasosCompletados * 100) / detalle.trackingSteps.size
                }

            } catch (e: Exception) {
                Log.e("COMMERCIAL", "Error: ${e.message}")
            }
        }
    }
    private fun setupTrackingList(pasos: List<TrackingStep>) {
        binding.rvTracking.apply {
            layoutManager = LinearLayoutManager(this@DetallesEnvioActivity)
            adapter = TrackingAdapter(
                steps = pasos,
                onSubirClick = { step ->
                    currentStepId = step.id
                    // Permitimos cualquier tipo de archivo (PDF/Imagen)
                    contractPicker.launch("*/*")
                },
                onVerClick = { filename -> descargarYVer(filename) } // Consistencia de nombre
            )
        }
    }

    private fun mostrarCabecera(cliente: String, orderNumber: String) {
        binding.tvOrderNumber.text = "Pedido $orderNumber"
        binding.tvClientDetail.text = "Cliente: $cliente"
    }

    private fun subirArchivo(uri: Uri) {
        val folderId = pedidoId
        val fileName = getFileName(uri) ?: "doc_${System.currentTimeMillis()}"
        val stepIdParaActualizar = currentStepId

        Log.d("SUBIDA", "Iniciando subida - folderId: $folderId, fileName: $fileName, stepId: $stepIdParaActualizar")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                }

                val inputStream = contentResolver.openInputStream(uri)
                Log.d("SUBIDA", "InputStream abierto: ${inputStream != null}")

                val bytes = inputStream?.readBytes() ?: run {
                    Log.e("SUBIDA", "InputStream es null, abortando")
                    return@launch
                }
                Log.d("SUBIDA", "Bytes leídos: ${bytes.size}")

                val resultadoSocket = NetworkClient.enviarDni(folderId, bytes, fileName)
                Log.d("SUBIDA", "Resultado socket: $resultadoSocket")

                // ESTE ES EL BUG: compara "con éxito" pero el mensaje es "Pujada finalitzada"
                val exito = !resultadoSocket.contains("Error", ignoreCase = true)
                Log.d("SUBIDA", "¿Éxito en socket?: $exito")

                if (exito) {
                    Log.d("SUBIDA", "Llamando a confirmarSubidaEnDB - stepId: $stepIdParaActualizar")
                    val exitoDB = apiService.confirmarSubidaEnDB(token, stepIdParaActualizar, fileName)
                    Log.d("SUBIDA", "Resultado DB: $exitoDB")

                    withContext(Dispatchers.Main) {
                        val msg = if (exitoDB) "¡Archivo guardado!" else "Subido, pero error en DB"
                        Toast.makeText(this@DetallesEnvioActivity, msg, Toast.LENGTH_SHORT).show()
                        obtenerDatosReales()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetallesEnvioActivity, "Error socket: $resultadoSocket", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("SUBIDA", "Excepción: ${e.javaClass.simpleName} - ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetallesEnvioActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                }
            }
        }
    }

    private fun descargarYVer(fullPath: String) {
        val cleanFileName = fullPath.substringAfterLast("/")
        val folderId = pedidoId

        Log.d("BAIXAR", "fullPath: $fullPath → cleanFileName: $cleanFileName → folderId: $folderId")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                }

                val bytesDescargados = NetworkClient.baixarDni(folderId, fullPath.trimStart('/'))

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    if (bytesDescargados != null) {
                        if (cleanFileName.lowercase().endsWith(".pdf")) {
                            abrirPdfExterno(bytesDescargados, cleanFileName)
                        } else {
                            mostrarImagenDialog(bytesDescargados)
                        }
                    } else {
                        Toast.makeText(this@DetallesEnvioActivity, "No se pudo descargar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@DetallesEnvioActivity, "Error en descarga", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun mostrarImagenDialog(bytes: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        if (bitmap == null) {
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = android.app.Dialog(this)
        val imageView = ImageView(this)
        imageView.adjustViewBounds = true
        imageView.setImageBitmap(bitmap)

        dialog.setContentView(imageView)
        // Opcional: Hacer que el diálogo ocupe buen espacio
        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) name = it.getString(nameIndex)
            }
        }
        return name
    }

    private fun abrirPdfExterno(bytes: ByteArray, fileName: String) {
        try {
            val tempFile = File(cacheDir, fileName)
            tempFile.writeBytes(bytes)

            val contentUri: Uri = androidx.core.content.FileProvider.getUriForFile(
                this, "${packageName}.provider", tempFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Ver PDF"))
        } catch (e: Exception) {
            Log.e("PDF", "Error al abrir PDF: ${e.message}")
            Toast.makeText(this, "Error al abrir PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


}