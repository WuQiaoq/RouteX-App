package com.example.routex_app.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.databinding.ItemPresupuestoClienteBinding

data class PresupuestoClienteItem(
    val id: Int,
    val codigo: String,
    val titulo: String,
    val ruta: String,
    val fecha: String,
    val precio: String,
    val estado: String
)

class PresupuestoClienteAdapter(
    private var presupuestos: List<PresupuestoClienteItem>,
    private val onItemClick: (PresupuestoClienteItem) -> Unit
) : RecyclerView.Adapter<PresupuestoClienteAdapter.PresupuestoViewHolder>() {

    class PresupuestoViewHolder(val binding: ItemPresupuestoClienteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresupuestoViewHolder {
        val binding = ItemPresupuestoClienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PresupuestoViewHolder(binding)
    }

    override fun getItemCount(): Int = presupuestos.size

    override fun onBindViewHolder(holder: PresupuestoViewHolder, position: Int) {
        val presupuesto = presupuestos[position]

        holder.binding.tvId.text = presupuesto.codigo
        holder.binding.tvTitle.text = presupuesto.titulo
        holder.binding.tvExtraInfo.text = presupuesto.ruta
        holder.binding.tvDate.text = presupuesto.fecha
        holder.binding.tvPrice.text = presupuesto.precio
        holder.binding.tvStatus.text = presupuesto.estado.uppercase()
        holder.binding.tvAction.text = "Ver Detalles >"

        val (backgroundColor, textColor) = when (presupuesto.estado.uppercase()) {
            "ACEPTADO" -> "#DCFCE7" to "#15803D"
            "RECHAZADO" -> "#FEE2E2" to "#B91C1C"
            else -> "#FEF3C7" to "#D97706"
        }

        holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor(backgroundColor))
        holder.binding.tvStatus.setTextColor(Color.parseColor(textColor))
        holder.binding.root.setOnClickListener { onItemClick(presupuesto) }
        holder.binding.tvAction.setOnClickListener { onItemClick(presupuesto) }
    }

    fun actualizarDatos(nuevosPresupuestos: List<PresupuestoClienteItem>) {
        presupuestos = nuevosPresupuestos
        notifyDataSetChanged()
    }
}
