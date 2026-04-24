package com.example.routex_app.ui.commercial.envios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.R

class EnvioAdapter(
    private var envios: List<EnvioActivo>,
    private val onItemClick: (EnvioActivo) -> Unit
) : RecyclerView.Adapter<EnvioAdapter.EnvioViewHolder>() {

    class EnvioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrder: TextView = view.findViewById(R.id.tvOrderNumber)
        val tvOrigin: TextView = view.findViewById(R.id.tvOrigin)
        val tvDest: TextView = view.findViewById(R.id.tvDest)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val card: View = view.findViewById(R.id.cardOferta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_envio_card, parent, false)
        return EnvioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnvioViewHolder, position: Int) {
        val envio = envios[position]

        holder.tvOrder.text = "#SHP-${envio.id}"

        // Gestión de N/A: Si quieres que se vea más limpio si el dato es "N/A"
        holder.tvOrigin.text = if (envio.rutaOrigen == "N/A") "Origen Pendiente" else envio.rutaOrigen
        holder.tvDest.text = if (envio.rutaDestino == "N/A") "Destino Pendiente" else envio.rutaDestino

        holder.tvStatus.text = envio.estado

        // IMPORTANTE: Tienes que asignar la fecha, si no siempre dirá "Pendiente"
        // Buscamos el ID tvArrivalDate que está en tu XML
        val tvDate = holder.itemView.findViewById<TextView>(R.id.tvArrivalDate)
        tvDate.text = "🕒 Creado: ${envio.fechaCreacion}"

        holder.card.setOnClickListener { onItemClick(envio) }
    }
    override fun getItemCount() = envios.size

    fun updateData(newList: List<EnvioActivo>) {
        this.envios = newList
        notifyDataSetChanged()
    }
}