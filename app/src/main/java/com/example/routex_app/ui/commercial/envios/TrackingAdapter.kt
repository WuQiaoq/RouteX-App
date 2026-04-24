package com.example.routex_app.commercial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.R
import com.example.routex_app.ui.commercial.envios.TrackingStep

import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class TrackingAdapter(
    private val steps: List<TrackingStep>,
    private val onSubirClick: (TrackingStep) -> Unit,
    private val onVerClick: (String) -> Unit
) : RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {

    class TrackingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStepTitle: TextView = view.findViewById(R.id.tvStepTitle)
        val tvStepTime: TextView = view.findViewById(R.id.tvStepTime)
        val tvNombreDoc: TextView = view.findViewById(R.id.tvNombreDoc)
        val btnVerDoc: TextView = view.findViewById(R.id.btnVerDoc)
        val btnSubir: MaterialButton = view.findViewById(R.id.btnSubir)

        // Contenedores de visibilidad
        val cardDocumentoExistente: MaterialCardView = view.findViewById(R.id.cardDocumentoExistente)
        val layoutSubirDocumento: View = view.findViewById(R.id.layoutSubirDocumento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tracking_step, parent, false)
        return TrackingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val step = steps[position]

        holder.tvStepTitle.text = step.titol
        holder.tvStepTime.text = step.dataHora

        // --- LÓGICA DE VISIBILIDAD DE DOCUMENTOS ---
        if (step.teDocument) {
            // 1. Si hay documento: mostramos el Card de "VER" y ocultamos el de "SUBIR"
            holder.cardDocumentoExistente.visibility = View.VISIBLE
            holder.layoutSubirDocumento.visibility = View.GONE

            holder.tvNombreDoc.text = step.nomFitxer ?: "Documento cargado"

            holder.btnVerDoc.setOnClickListener {
                onVerClick(step.nomFitxer ?: "")
            }
        } else {
            // 2. Si no hay documento: mostramos el botón de "SUBIR" y ocultamos el de "VER"
            holder.cardDocumentoExistente.visibility = View.GONE
            holder.layoutSubirDocumento.visibility = View.VISIBLE

            holder.btnSubir.setOnClickListener {
                onSubirClick(step)
            }
        }
    }

    override fun getItemCount() = steps.size
}