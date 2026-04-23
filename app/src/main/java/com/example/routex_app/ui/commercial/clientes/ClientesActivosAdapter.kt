package com.example.routex_app.ui.commercial.clientes

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.R
import com.example.routex_app.databinding.ItemClienteActivoBinding
import com.example.routex_app.models.ClienteActivo
import java.io.IOException

class ClientesActivosAdapter(
    private var clientes: List<ClienteActivo>,
    private val onItemClick: (ClienteActivo) -> Unit
) : RecyclerView.Adapter<ClientesActivosAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(val binding: ItemClienteActivoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ItemClienteActivoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]
        val context = holder.itemView.context

        // Usamos holder.binding para acceder a las vistas
        with(holder.binding) {
            // CORRECCIÓN DE IDs: Usamos los nombres exactos que están en tu XML original
            tvNombreEmpresa.text = cliente.companyName
            // Como en tu XML no hay 'tvIndustry', usamos los que sí existen:
            tvDetalleCarga.text = cliente.ultimaCargaResumen ?: "Sin detalles"
            tvEnviosActivos.text = "${cliente.enviosActivosCount} envíos activos"

            // Manejo de Assets para el icono
            val fileName = when (cliente.tipusTransportId) {
                1 -> "avion.jpg"
                2 -> "barco.jpg"
                else -> "camion.jpg"
            }

            try {
                val inputStream = context.assets.open(fileName)
                val drawable = Drawable.createFromStream(inputStream, null)
                // Usamos ivClienteIcono que es el ID de tu ImageView en el XML
                ivClienteIcono.setImageDrawable(drawable)
            } catch (e: IOException) {
                ivClienteIcono.setImageResource(R.drawable.ic_clients)
            }

            // Click listener
            root.setOnClickListener { onItemClick(cliente) }

            // Botón de chat si quieres darle funcionalidad
            btnChat.setOnClickListener {
                // Acción de chat
            }
        }
    }

    override fun getItemCount(): Int = clientes.size

    fun updateData(newList: List<ClienteActivo>) {
        clientes = newList
        notifyDataSetChanged()
    }
}