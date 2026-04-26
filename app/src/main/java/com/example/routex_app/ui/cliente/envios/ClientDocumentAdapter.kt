package com.example.routex_app.ui.cliente.envios

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.R
import com.example.routex_app.databinding.ItemTrackingStepBinding
import com.example.routex_app.ui.commercial.envios.TrackingStep

class ClientDocumentAdapter(
    private val steps: List<TrackingStep>,
    private val onVerClick: (String) -> Unit
) : RecyclerView.Adapter<ClientDocumentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTrackingStepBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackingStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val step = steps[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvStepTitle.text = step.titol
            tvStepTime.text = if (step.estaCompletado == 1) step.dataHora else "Pendiente"

            // --- GESTIÓN PARA EL CLIENTE ---

            // 1. Siempre ocultamos el botón de subir (esto es solo para el comercial)
            layoutSubirDocumento.visibility = View.GONE
            btnSubir.visibility = View.GONE

            // 2. Si el paso tiene documento, mostramos la CARD gris que lo contiene
            if (step.teDocument && !step.nomFitxer.isNullOrEmpty()) {
                cardDocumentoExistente.visibility = View.VISIBLE
                tvNombreDoc.text = step.nomFitxer // Mostramos el nombre del archivo

                // Configuramos el click del botón VER que está dentro de esa Card
                btnVerDoc.setOnClickListener {
                    onVerClick(step.nomFitxer!!)
                }
            } else {
                cardDocumentoExistente.visibility = View.GONE
            }

            // 3. Estética del Icono y la Línea (Naranja primario)
            val colorOrange = Color.parseColor("#F97316")
            val colorGray = Color.parseColor("#E2E8F0")

            if (step.estaCompletado == 1) {
                ivStepIcon.setImageResource(R.drawable.ic_check_circle)
                ivStepIcon.imageTintList = ColorStateList.valueOf(colorOrange)
                lineVertical.backgroundTintList = ColorStateList.valueOf(colorOrange)
            } else {
                ivStepIcon.setImageResource(R.drawable.ic_circle_outline)
                ivStepIcon.imageTintList = ColorStateList.valueOf(colorGray)
                lineVertical.backgroundTintList = ColorStateList.valueOf(colorGray)
            }

            // Ocultar la línea si es el último de la lista
            lineVertical.visibility = if (position == steps.size - 1) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount(): Int = steps.size
}