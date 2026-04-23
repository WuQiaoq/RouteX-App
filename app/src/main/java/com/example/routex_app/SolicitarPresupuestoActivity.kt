package com.example.routex_app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.routex_app.databinding.ActivitySolicitarPresupuestoBinding

class SolicitarPresupuestoActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySolicitarPresupuestoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySolicitarPresupuestoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupDropdowns()
        setupListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupDropdowns() {

        val ciudades = listOf("Shanghai", "Valencia", "Long Beach", "")
        val adapterCiudades = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ciudades)

        binding.actvOrigen.setAdapter(adapterCiudades)
        binding.actvDestino.setAdapter(adapterCiudades)
    }

    private fun setupListeners() {

        binding.btnEnviarSolicitud.setOnClickListener {
            val origen = binding.actvOrigen.text.toString()
            val destino = binding.actvDestino.text.toString()

            if (origen.isEmpty() || destino.isEmpty()) {
                Toast.makeText(this, "Por favor completa los campos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}