package com.example.routex_app.commercial

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        val cardDocumentoExistente: MaterialCardView = view.findViewById(R.id.cardDocumentoExistente)
        val layoutSubirDocumento: View = view.findViewById(R.id.layoutSubirDocumento)
        val ivStatusIcon: ImageView = view.findViewById(R.id.ivStatusIcon)
        val viewLine: View = view.findViewById(R.id.viewLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tracking_step, parent, false)
        return TrackingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val step = steps[position]

        holder.tvStepTitle.text = step.titol
        holder.tvStepTime.text = step.dataHora

        val colorOrange = Color.parseColor("#F97316")
        val colorGray = Color.parseColor("#E2E8F0")

        if (step.teDocument) {
            holder.ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
            holder.ivStatusIcon.imageTintList = ColorStateList.valueOf(colorOrange)
            holder.viewLine.backgroundTintList = ColorStateList.valueOf(colorOrange)

            holder.cardDocumentoExistente.visibility = View.VISIBLE
            holder.layoutSubirDocumento.visibility = View.GONE
            holder.tvNombreDoc.text = step.nomFitxer ?: "Documento cargado"
            holder.btnVerDoc.setOnClickListener {
                onVerClick(step.nomFitxer ?: "")
            }
        } else {
            holder.ivStatusIcon.setImageResource(R.drawable.ic_circle_outline)
            holder.ivStatusIcon.imageTintList = ColorStateList.valueOf(colorGray)
            holder.viewLine.backgroundTintList = ColorStateList.valueOf(colorGray)

            holder.cardDocumentoExistente.visibility = View.GONE
            holder.layoutSubirDocumento.visibility = View.VISIBLE
            holder.btnSubir.setOnClickListener {
                onSubirClick(step)
            }
        }

        holder.viewLine.visibility = if (position == steps.size - 1) View.GONE else View.VISIBLE
    }

    override fun getItemCount() = steps.size
}