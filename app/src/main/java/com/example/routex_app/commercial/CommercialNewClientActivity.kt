package com.example.routex_app.commercial

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.routex_app.R
import com.example.routex_app.models.RegisterClientRequest
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class CommercialNewClientActivity : AppCompatActivity() {

    private lateinit var repository: ClientRepository
    private lateinit var autoIndustry: AutoCompleteTextView
    private lateinit var autoCurrency: AutoCompleteTextView
    private lateinit var txtIdStatus: TextView
    private var encodedIdImage: String? = null // Aquí guardamos el DNI codificado

    // Selector de imágenes para subir
    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { processAndEncodeImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commercial_new_client)

        repository = ClientRepository(ApiService(KtorClient.httpClient))

        // Vincular Vistas
        autoIndustry = findViewById(R.id.autoIndustry)
        autoCurrency = findViewById(R.id.autoCurrency)
        txtIdStatus = findViewById(R.id.txtIdStatus)
        val btnUploadId = findViewById<MaterialButton>(R.id.btnUploadId)
        val btnDownloadId = findViewById<MaterialButton>(R.id.btnDownloadId)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        loadDropdownData()

        // ACCIÓN SUBIR: Abrir galería
        btnUploadId.setOnClickListener {
            imagePicker.launch("image/*")
        }

        // ACCIÓN BAJAR: Mostrar lo que está en 'encodedIdImage' (Simulando bajada del servidor)
        btnDownloadId.setOnClickListener {
            if (!encodedIdImage.isNullOrEmpty()) {
                showDecodedImageDialog(encodedIdImage!!)
            } else {
                Toast.makeText(this, "No hay ningún DNI cargado aún", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegistrar.setOnClickListener {
            val request = collectData()
            if (request != null) executeRegistration(request)
        }
    }

    // --- PROCESO 1: De Imagen a Base64 (ENCRIPTAR PARA SUBIR) ---
    private fun processAndEncodeImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Compresión para no saturar el servidor (70%)
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val bytes = outputStream.toByteArray()

                // Codificación Base64 limpia
                val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)

                withContext(Dispatchers.Main) {
                    encodedIdImage = base64String
                    txtIdStatus.text = "✓ DNI preparado para envío"
                    txtIdStatus.setTextColor(getColor(android.R.color.holo_green_dark))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CommercialNewClientActivity, "Error al procesar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- PROCESO 2: De Base64 a Imagen (DESENCRIPTAR Y MOSTRAR) ---
    private fun showDecodedImageDialog(base64Str: String) {
        try {
            // Decodificar String a Bytes
            val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Inflar el layout del pop-up
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_preview, null)
            val ivPreview = dialogView.findViewById<ImageView>(R.id.ivFullPreview)
            ivPreview.setImageBitmap(decodedBitmap)

            AlertDialog.Builder(this)
                .setTitle("Vista previa del DNI")
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al desencriptar imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun collectData(): RegisterClientRequest? {
        val company = findViewById<TextInputEditText>(R.id.etCompanyName).text.toString()
        val email = findViewById<TextInputEditText>(R.id.etClientEmail).text.toString()

        if (company.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Faltan campos obligatorios", Toast.LENGTH_SHORT).show()
            return null
        }

        return RegisterClientRequest(
            companyName = company,
            industryName = autoIndustry.text.toString(),
            taxId = findViewById<TextInputEditText>(R.id.etTaxId).text.toString(),
            currencyId = autoCurrency.text.toString(),
            correu = email,
            nom = findViewById<TextInputEditText>(R.id.etFullName).text.toString(),
            cognoms = "",
            tlfn = findViewById<TextInputEditText>(R.id.etPhone).text.toString(),
            representativeIdImage = encodedIdImage // Aquí va el DNI codificado
                                    )
    }

    private fun executeRegistration(request: RegisterClientRequest) {
        lifecycleScope.launch {
            when (val result = repository.registerClient(request)) {
                is Resource.Success -> {
                    Toast.makeText(this@CommercialNewClientActivity, "Registrado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> Toast.makeText(this@CommercialNewClientActivity, result.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    private fun loadDropdownData() {
        lifecycleScope.launch {
            // Cargar Industrias
            when (val result = repository.getIndustries()) {
                is Resource.Success -> {
                    val names = result.data?.map { it.categoria } ?: emptyList()
                    autoIndustry.setAdapter(ArrayAdapter(this@CommercialNewClientActivity, android.R.layout.simple_dropdown_item_1line, names))
                }
                is Resource.Error -> Toast.makeText(this@CommercialNewClientActivity, "Error industrias", Toast.LENGTH_SHORT).show()
                else -> {}
            }

            // Cargar Monedas
            when (val result = repository.getCurrencies()) {
                is Resource.Success -> {
                    val codes = result.data?.map { it.id } ?: emptyList()
                    autoCurrency.setAdapter(ArrayAdapter(this@CommercialNewClientActivity, android.R.layout.simple_dropdown_item_1line, codes))
                }
                else -> {}
            }
        }
    }

}