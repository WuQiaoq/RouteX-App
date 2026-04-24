package com.example.routex_app.ui.commercial.presupuesto


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.R
import com.example.routex_app.models.Presupuesto

class PresupuestoAdapter(
    private var lista: List<Presupuesto>,
    private val tipo: String, // "SENT", "ACCEPTED", "REJECTED"
    private val onItemClick: (Presupuesto) -> Unit
) : RecyclerView.Adapter<PresupuestoAdapter.PresupuestoViewHolder>() {

    class PresupuestoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val ivIcono: ImageView = v.findViewById(R.id.ivPresupuestoIcono)
        val tvPrecio: TextView = v.findViewById(R.id.tvPresupuestoPrecio)
        val tvEstado: TextView = v.findViewById(R.id.tvPresupuestoEstadoBadge)
        val tvRuta: TextView = v.findViewById(R.id.tvPresupuestoRuta)
        val tvMotivo: TextView = v.findViewById(R.id.tvPresupuestoMotivo)
        val layoutMotivo: View = v.findViewById(R.id.layoutPresupuestoMotivo)
        val layoutAcciones: View = v.findViewById(R.id.layoutPresupuestoAcciones)
        val btnEditar: View = v.findViewById(R.id.btnPresupuestoEditar)
        val btnBorrar: View = v.findViewById(R.id.btnPresupuestoBorrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresupuestoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_presupuesto, parent, false)
        return PresupuestoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PresupuestoViewHolder, position: Int) {
        val presupuesto = lista[position]

        holder.tvPrecio.text = presupuesto.Valor
        holder.tvRuta.text = presupuesto.Ruta

        // 1. Configurar el clic en todo el ítem
        holder.itemView.setOnClickListener {
            onItemClick(presupuesto)
        }

        // --- Lógica visual según el tipo ---
        when (tipo) {
            "SENT" -> {
                holder.itemView.alpha = 1.0f // Totalmente opaco
                holder.tvEstado.text = "ENVIADO"
                holder.tvEstado.setBackgroundColor(0xFF64748B.toInt())
                holder.layoutMotivo.visibility = View.GONE
                holder.layoutAcciones.visibility = View.GONE
            }
            "ACCEPTED" -> {
                holder.itemView.alpha = 1.0f // Totalmente opaco
                holder.tvEstado.text = "ACEPTADO"
                holder.tvEstado.setBackgroundColor(0xFF10B981.toInt())
                holder.layoutMotivo.visibility = View.GONE
                holder.layoutAcciones.visibility = View.GONE
            }
            "REJECTED" -> {
                // MODIFICACIÓN: Efecto visual de "deshabilitado"
                holder.itemView.alpha = 0.6f

                holder.tvEstado.text = "RECHAZADO"
                holder.tvEstado.setBackgroundColor(0xFFEF4444.toInt())
                holder.layoutMotivo.visibility = View.VISIBLE
                holder.layoutAcciones.visibility = View.VISIBLE
                holder.tvMotivo.text = presupuesto.RaoRebuig ?: "Sin motivo especificado"

                // Opcional: Si quieres quitar el feedback visual del clic (el ripple) en rechazados
                // holder.itemView.isClickable = true // El listener sigue vivo para mostrar el Toast del Fragment
            }
        }

        // Configuración de Iconos
        if (presupuesto.TipusTransportId == 1) {
            holder.ivIcono.setImageResource(R.drawable.placeholder_ship)
        } else {
            holder.ivIcono.setImageResource(android.R.drawable.ic_menu_send)
        }
    }
    override fun getItemCount(): Int = lista.size

    fun actualizarDatos(nuevaLista: List<Presupuesto>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }
}