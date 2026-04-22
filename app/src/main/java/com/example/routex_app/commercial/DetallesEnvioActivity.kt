package com.example.routex_app.commercial

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityCommercialGestionEnviosBinding
import com.example.routex_app.network.NetworkClient
import com.example.routex_app.NavigationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetallesEnvioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommercialGestionEnviosBinding
    private var pedidoId: String = "0000"
    private var archivoContratoSubido: String? = null

    // Selector de archivos (Contratos/Imágenes)
    private val contractPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { subirContratoAlServidor(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommercialGestionEnviosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoId = intent.getStringExtra("PEDIDO_ID") ?: "0000"

        binding.btnBack.setOnClickListener { finish() }
        NavigationUtils.setupBottomNavigation(this, binding.bottomNav, R.id.nav_ofertas)

        // BOTÓN SUBIR: Al hacer clic en el botón de texto dentro del FrameLayout
        binding.btnSeleccionarArchivo.setOnClickListener {
            contractPicker.launch("image/*") // O "application/pdf" si el servidor lo soporta
        }

        // BOTÓN VER FACTURA: Ejemplo de descarga
        binding.btnVerFactura.setOnClickListener {
            descargarYMostrarDocumento("factura_proforma.jpg") // Nombre ejemplo
        }

        mostrarDetalles()
    }

    private fun subirContratoAlServidor(uri: Uri) {
        // Mostramos un aviso de carga (opcional)
        Toast.makeText(this, "Subiendo contrato...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@launch
                val fileName = "contrato_firmado.jpg"

                // Usamos el pedidoId como identificador de carpeta en el servidor
                val resultado = NetworkClient.enviarDni(pedidoId, bytes, fileName)

                withContext(Dispatchers.Main) {
                    archivoContratoSubido = fileName
                    // Cambiamos el diseño para indicar éxito
                    binding.btnSubirContrato.alpha = 0.5f
                    Toast.makeText(this@DetallesEnvioActivity, "¡Contrato subido con éxito!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetallesEnvioActivity, "Error al subir: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun descargarYMostrarDocumento(filename: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val imageBytes = NetworkClient.baixarDni(pedidoId, filename)

            withContext(Dispatchers.Main) {
                if (imageBytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    mostrarPreview(bitmap)
                } else {
                    Toast.makeText(this@DetallesEnvioActivity, "No se encontró el archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarPreview(bitmap: android.graphics.Bitmap) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_preview, null)
        val ivPreview = dialogView.findViewById<ImageView>(R.id.ivFullPreview)
        ivPreview.setImageBitmap(bitmap)

        AlertDialog.Builder(this)
            .setTitle("Vista previa del documento")
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun mostrarDetalles() {
        val cliente = intent.getStringExtra("CLIENTE") ?: "Empresa Genérica"
        val origen = intent.getStringExtra("ORIGEN") ?: "N/A"
        val destino = intent.getStringExtra("DESTINO") ?: "N/A"
        val estado = intent.getStringExtra("ESTADO") ?: "EN REVISIÓN"

        binding.apply {
            tvOrderNumber.text = "Pedido #ORD-$pedidoId"
            tvClientDetail.text = "Cliente: $cliente"
            tvEstadoBadge.text = estado.uppercase()
            tvDireccionDetalle.text = "📍 $origen -> $destino"

            when(estado.uppercase()) {
                "ACEPTADA" -> {
                    pbProgresoEnvio.progress = 100
                    tvPorcentajeCompletado.text = "100% Completado"
                }
                else -> {
                    pbProgresoEnvio.progress = 60
                    tvPorcentajeCompletado.text = "60% Completado"
                }
            }
        }
    }
}