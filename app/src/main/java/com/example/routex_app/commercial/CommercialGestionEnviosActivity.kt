package com.example.routex_app.commercial

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.routex_app.NavigationUtils
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityCommercialGestionEnviosBinding

class CommercialGestionEnviosActivity : AppCompatActivity() {

    // El nombre de la clase Binding se genera automáticamente del nombre del XML
    private lateinit var binding: ActivityCommercialGestionEnviosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializar ViewBinding
        binding = ActivityCommercialGestionEnviosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Configurar la barra de navegación inferior
        // Reutilizamos tu utilidad de navegación (asegúrate de que R.id.nav_home o el que corresponda exista)
        NavigationUtils.setupBottomNavigation(this, binding.bottomNav, R.id.nav_home)

        // 3. Configurar el botón de retroceso de la Toolbar
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 4. Configurar acciones de botones (Asumiendo que añades los IDs al XML)
        configurarAcciones()

        // 5. Carga de datos inicial (opcional)
        cargarDatosPedido()
    }

    private fun configurarAcciones() {
        // Ejemplo: Botón "Ver" factura proforma
        // binding.btnVerFactura.setOnClickListener {
        //    Toast.makeText(this, "Abriendo documento...", Toast.LENGTH_SHORT).show()
        // }

        // Ejemplo: Botón "Pasar a siguiente fase" (el botón verde)
        // binding.btnSiguienteFase.setOnClickListener {
        //    procesarCambioFase()
        // }
    }

    private fun cargarDatosPedido() {
        // 1. Recuperar los extras que enviamos desde el Fragment
        val pedidoId = intent.getStringExtra("PEDIDO_ID") ?: "0"
        val ruta = intent.getStringExtra("RUTA") ?: "Ruta no especificada"
        val concepto = intent.getStringExtra("CONCEPTO") ?: "Sin concepto"
        val precio = intent.getStringExtra("PRECIO") ?: "Consultar"

        // 2. Pintar los datos en el XML usando los IDs del ViewBinding
        binding.apply {
            // Título del pedido (donde decía #ORD-7729)
            // Asegúrate de que en el XML el ID sea tvOrderNumber o el que hayas puesto
            tvOrderNumber.text = "Pedido #ORD-$pedidoId"

            // El cliente o descripción de la ruta
            tvClientDetail.text = "Ruta: $ruta"

            // La fase o concepto actual
           // tvFaseActual.text = "📄 Fase actual: $concepto"

            // Si tienes un campo para el precio total
            // tvTotalAmount.text = "$precio €"
        }
    }

    private fun procesarCambioFase() {
        // Lógica para conectar con tu API de C# y actualizar el estado
        Toast.makeText(this, "Actualizando estado del envío...", Toast.LENGTH_SHORT).show()
    }
}