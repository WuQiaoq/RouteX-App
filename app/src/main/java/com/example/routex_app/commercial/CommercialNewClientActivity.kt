package com.example.routex_app.commercial


import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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
import com.example.routex_app.network.NetworkClient
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommercialNewClientActivity : AppCompatActivity() {

    private lateinit var repository: ClientRepository
    private lateinit var autoIndustry: AutoCompleteTextView
    private lateinit var autoCurrency: AutoCompleteTextView
    private lateinit var txtIdStatus: TextView

    private var serverFileResponse: String? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { processAndUploadImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commercial_new_client)

        repository = ClientRepository(ApiService(KtorClient.httpClient))

        autoIndustry = findViewById(R.id.autoIndustry)
        autoCurrency = findViewById(R.id.autoCurrency)
        txtIdStatus = findViewById(R.id.txtIdStatus)
        val btnUploadId = findViewById<MaterialButton>(R.id.btnUploadId)
        val btnDownloadId = findViewById<MaterialButton>(R.id.btnDownloadId)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnBack = findViewById< ImageButton>(R.id.btnBack)

        loadDropdownData()

        btnUploadId.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnDownloadId.setOnClickListener {
            // Cridem a la funció unificada
            descarregarIMostrarDni()
        }

        btnRegistrar.setOnClickListener {
            val request = collectData()
            if (request != null) executeRegistration(request)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }


    // FUNCIÓ DE DESCÀRREGA UNIFICADA I CORREGIDA
    private fun descarregarIMostrarDni() {
        val usertlfn = findViewById<TextInputEditText>(R.id.etPhone).text.toString().trim()
        val filename = serverFileResponse

        if (usertlfn.isEmpty()) {
            Toast.makeText(this, "Cal el telefon per identificar la carpeta", Toast.LENGTH_SHORT).show()
            return
        }
        if (filename == null) {
            Toast.makeText(this, "Encara no has pujat cap DNI", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // Passem userId i filename al NetworkClient
            val imageBytes = NetworkClient.baixarDni(usertlfn, filename)

            withContext(Dispatchers.Main) {
                if (imageBytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    mostrarDialogPreview(bitmap) // Utilitzem el nom correcte de la funció
                } else {
                    Toast.makeText(this@CommercialNewClientActivity, "Error en baixar el fitxer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogPreview(bitmap: android.graphics.Bitmap) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_preview, null)
        val ivPreview = dialogView.findViewById<ImageView>(R.id.ivFullPreview)
        ivPreview.setImageBitmap(bitmap)

        AlertDialog.Builder(this)
            .setTitle("DNI recuperat del servidor")
            .setView(dialogView)
            .setPositiveButton("Tancar", null)
            .show()
    }

    private fun collectData(): RegisterClientRequest? {
        val company = findViewById<TextInputEditText>(R.id.etCompanyName).text.toString()
        val email = findViewById<TextInputEditText>(R.id.etClientEmail).text.toString()

        if (company.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Faltan camps obligatoris", Toast.LENGTH_SHORT).show()
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
            representativeIdImage = serverFileResponse // Enviem el nom del fitxer ja pujat
        )
    }

    private fun executeRegistration(request: RegisterClientRequest) {
        lifecycleScope.launch {
            when (val result = repository.registerClient(request)) {
                is Resource.Success -> {
                    Toast.makeText(this@CommercialNewClientActivity, "Client registrat!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> Toast.makeText(this@CommercialNewClientActivity, result.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    private fun loadDropdownData() {
        lifecycleScope.launch {
            val industryResult = repository.getIndustries()
            if (industryResult is Resource.Success) {
                val names = industryResult.data?.map { it.categoria } ?: emptyList()
                autoIndustry.setAdapter(ArrayAdapter(this@CommercialNewClientActivity, android.R.layout.simple_dropdown_item_1line, names))
            }

            val currencyResult = repository.getCurrencies()
            if (currencyResult is Resource.Success) {
                val codes = currencyResult.data?.map { it.id } ?: emptyList()
                autoCurrency.setAdapter(ArrayAdapter(this@CommercialNewClientActivity, android.R.layout.simple_dropdown_item_1line, codes))
            }
        }
    }

    private fun descarregarIMostrarDni(filename: String) {
        val usertlfn = findViewById<TextInputEditText>(R.id.etPhone).text.toString().trim()
        lifecycleScope.launch(Dispatchers.IO) {
            // 1. Baixem els bytes (ja venen desencriptats pel servidor)
            val imageBytes = NetworkClient.baixarDni(usertlfn, filename)

            withContext(Dispatchers.Main) {
                if (imageBytes != null) {
                    // 2. Convertim bytes a Bitmap
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    // 3. Reutilitzem la teva lògica de mostrar el Dialog
                    mostrarDialogPreview(bitmap)
                } else {
                    Toast.makeText(this@CommercialNewClientActivity, "Error en baixar el fitxer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processAndUploadImage(uri: Uri) {
        val usertlfn = findViewById<TextInputEditText>(R.id.etPhone).text.toString().trim()

        if (usertlfn.isEmpty()) {
            Toast.makeText(this, "Posa el telèfon primer!", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // Obtenim els bytes de la imatge seleccionada
            val inputStream = contentResolver.openInputStream(uri)
            val originalBytes = inputStream?.readBytes() ?: return@launch

            // Cridem al client de xarxa (aquí s'encripta i s'envia byte a byte)
            val resultat = NetworkClient.enviarDni(usertlfn, originalBytes, "dni.jpg")

            withContext(Dispatchers.Main) {
                Toast.makeText(this@CommercialNewClientActivity, resultat, Toast.LENGTH_LONG).show()
                if (resultat.contains("✅")) {
                    serverFileResponse = "dni.jpg"
                    txtIdStatus.text = "DNI guardat i encriptat al servidor"
                }
            }
        }
    }


}