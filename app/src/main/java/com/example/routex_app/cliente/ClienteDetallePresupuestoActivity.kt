package com.example.routex_app.cliente

import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.routex_app.databinding.ActivityDetallePresupuestoClienteBinding
import com.example.routex_app.network.ApiService
import com.example.routex_app.network.KtorClient
import com.example.routex_app.repository.ClientRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.launch

class ClienteDetallePresupuestoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallePresupuestoClienteBinding

    private var token: String = ""
    private var userId: Int = -1
    private var presupuestoId: Int = -1
    private var codigo: String = "#QUO-0000"
    private var titulo: String = "Presupuesto"
    private var ruta: String = "Ruta pendiente"
    private var fecha: String = "Fecha pendiente"
    private var precio: String = "Consultar"
    private var estado: String = "Pendiente"
    private val repository by lazy {
        ClientRepository(ApiService(KtorClient.httpClient))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallePresupuestoClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        leerExtras()
        mostrarDatos()
        setupListeners()
    }

    private fun leerExtras() {
        token = intent.getStringExtra("USER_TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)
        presupuestoId = intent.getIntExtra("PRESUPUESTO_ID", -1)
        codigo = intent.getStringExtra("CODIGO") ?: codigo
        titulo = intent.getStringExtra("TITULO") ?: titulo
        ruta = intent.getStringExtra("RUTA") ?: ruta
        fecha = intent.getStringExtra("FECHA") ?: fecha
        precio = intent.getStringExtra("PRECIO") ?: precio
        estado = intent.getStringExtra("ESTADO") ?: estado
    }

    private fun mostrarDatos() {
        binding.tvCodigo.text = codigo
        binding.tvTitulo.text = titulo
        binding.tvRuta.text = ruta.ifBlank { "Ruta pendiente" }
        binding.tvFecha.text = fecha
        binding.tvPrecio.text = precio
        binding.tvEstado.text = estado.uppercase()

        val (backgroundColor, textColor) = when (estado.uppercase()) {
            "ACEPTADO" -> "#DCFCE7" to "#15803D"
            "RECHAZADO" -> "#FEE2E2" to "#B91C1C"
            "EXPIRADO" -> "#E2E8F0" to "#64748B"
            else -> "#FEF3C7" to "#D97706"
        }

        binding.cardEstado.setCardBackgroundColor(Color.parseColor(backgroundColor))
        binding.tvEstado.setTextColor(Color.parseColor(textColor))

        val puedeDecidir = estado.equals("Pendiente", ignoreCase = true)
        binding.btnAceptar.isEnabled = puedeDecidir
        binding.btnRechazar.isEnabled = puedeDecidir
        binding.btnAceptar.alpha = if (puedeDecidir) 1f else 0.5f
        binding.btnRechazar.alpha = if (puedeDecidir) 1f else 0.5f
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAceptar.setOnClickListener {
            aceptarPresupuesto()
        }

        binding.btnRechazar.setOnClickListener {
            mostrarDialogoRechazo()
        }
    }

    private fun mostrarDialogoRechazo() {
        val input = EditText(this).apply {
            hint = "Motivo del rechazo"
            minLines = 3
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Rechazar propuesta")
            .setMessage("Indica el motivo para rechazar este presupuesto.")
            .setView(input)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Rechazar", null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val motivo = input.text.toString().trim()
                if (motivo.isEmpty()) {
                    Toast.makeText(this, "Debes indicar un motivo", Toast.LENGTH_SHORT).show()
                } else {
                    dialog.dismiss()
                    rechazarPresupuesto(motivo)
                }
        }
    }

    private fun aceptarPresupuesto() {
        //token , userid is null
        if (!sesionValida()) return

        //evitar clics consecutivos
        binding.btnAceptar.isEnabled = false
        binding.btnRechazar.isEnabled = false
        // ejecutar solicitudes de API
        lifecycleScope.launch {

            when (val result = repository.aceptarPresupuestoCliente(token, userId, presupuestoId)) {
                is Resource.Success -> {
                    Toast.makeText(
                        this@ClienteDetallePresupuestoActivity,
                        result.data ?: "Presupuesto aceptado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                is Resource.Error -> {
                    binding.btnAceptar.isEnabled = true
                    binding.btnRechazar.isEnabled = true
                    Toast.makeText(
                        this@ClienteDetallePresupuestoActivity,
                        result.message ?: "Error al aceptar presupuesto",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // do nothing
                is Resource.Loading -> Unit
            }
        }
    }

    private fun rechazarPresupuesto(motivo: String) {
        if (!sesionValida()) return

        binding.btnRechazar.isEnabled = false
        binding.btnAceptar.isEnabled = false
        lifecycleScope.launch {
            when (val result = repository.rechazarPresupuestoCliente(token, userId, presupuestoId, motivo)) {
                is Resource.Success -> {
                    Toast.makeText(
                        this@ClienteDetallePresupuestoActivity,
                        result.data ?: "Presupuesto rechazado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                is Resource.Error -> {
                    binding.btnRechazar.isEnabled = true
                    binding.btnAceptar.isEnabled = true
                    Toast.makeText(
                        this@ClienteDetallePresupuestoActivity,
                        result.message ?: "Error al rechazar presupuesto",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Loading -> Unit
            }
        }
    }

    private fun sesionValida(): Boolean {
        val valida = token.isNotEmpty() && userId != -1 && presupuestoId != -1
        if (!valida) {
            Toast.makeText(this, "Sesion no valida", Toast.LENGTH_SHORT).show()
        }
        return valida
    }
}
