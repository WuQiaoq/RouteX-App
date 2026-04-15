package com.example.routex_app.commercial

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.routex_app.R
import com.example.routex_app.models.RegisterClientRequest
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class CommercialNewClientActivity : AppCompatActivity() {

    private lateinit var repository: ClientRepository

    // Referencias a los componentes de la UI (Asegúrate de ponerle IDs en el XML)
    private lateinit var autoIndustry: AutoCompleteTextView
    private lateinit var autoCurrency: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commercial_new_client)

        // 1. Inicializar Repositorio (Asumiendo que tienes tu KtorClient configurado)
        val apiService = ApiService(KtorClient.httpClient)
        repository = ClientRepository(apiService)

        // 2. Vincular vistas
        autoIndustry = findViewById(R.id.autoIndustry)
        autoCurrency = findViewById(R.id.autoCurrency)

        // 3. Cargar datos para los desplegables
        loadDropdownData()

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            // 1. Recoger datos de la UI
            // Asegúrate de que estos IDs coincidan con los de tu XML
            val companyName = findViewById<TextInputEditText>(R.id.etCompanyName).text.toString()
            val taxId = findViewById<TextInputEditText>(R.id.etTaxId).text.toString()
            val industry = autoIndustry.text.toString()

            val fullName = findViewById<TextInputEditText>(R.id.etFullName).text.toString()
            val email = findViewById<TextInputEditText>(R.id.etClientEmail).text.toString()
            val phone = findViewById<TextInputEditText>(R.id.etPhone).text.toString()

            val currency = autoCurrency.text.toString()

            // 2. Validación básica
            if (companyName.isEmpty() || email.isEmpty() || industry.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Crear el objeto Request
            val request = RegisterClientRequest(
                companyName = companyName,
                industryName = industry,
                taxId = taxId,
                currencyId = currency,
                correu = email,
                nom = fullName.split(" ").firstOrNull() ?: fullName, // Separar nombre
                cognoms = fullName.split(" ").drop(1).joinToString(" "), // Separar apellidos
                tlfn = phone
            )

            // 4. Enviar al servidor
            executeRegistration(request)
        }
    }

    private fun loadDropdownData() {
        lifecycleScope.launch {
            // --- Cargar Industrias ---
            when (val result = repository.getIndustries()) {
                is Resource.Success -> {
                    val names = result.data?.map { it.categoria } ?: emptyList()
                    val adapter = ArrayAdapter(this@CommercialNewClientActivity, android.R.layout.simple_dropdown_item_1line, names)
                    autoIndustry.setAdapter(adapter)
                }
                is Resource.Error -> {
                    Toast.makeText(this@CommercialNewClientActivity, result.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {

                }
            }

            // --- Cargar currency ---
            when (val result = repository.getCurrencies()) {
                is Resource.Success -> {
                    val codes = result.data?.map { it.id } ?: emptyList()
                    val adapter = ArrayAdapter(this@CommercialNewClientActivity, android.R.layout.simple_dropdown_item_1line, codes)
                    autoCurrency.setAdapter(adapter)
                }
                is Resource.Error -> {
                    Toast.makeText(this@CommercialNewClientActivity, "Error monedas: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {

                }
            }
        }
    }

    private fun executeRegistration(request: RegisterClientRequest) {
        lifecycleScope.launch {
            when (val result = repository.registerClient(request)) {
                is Resource.Loading -> {
                    // Aquí podrías deshabilitar el botón para evitar doble clic
                }
                is Resource.Success -> {
                    Toast.makeText(this@CommercialNewClientActivity, result.data, Toast.LENGTH_LONG).show()
                    finish() // Cerramos la pantalla y volvemos al Dashboard
                }
                is Resource.Error -> {
                    // Si C# devuelve "Email ya existe", aparecerá aquí
                    Toast.makeText(this@CommercialNewClientActivity, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}