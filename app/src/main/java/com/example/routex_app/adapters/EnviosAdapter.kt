package com.example.routex_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.data.Envio
import com.example.routex_app.databinding.ItemEnvioBinding


class EnviosAdapter(
    private val envios: List<Envio>,
    private val onClick: (Envio) -> Unit
) : RecyclerView.Adapter<EnviosAdapter.EnvioViewHolder>() {

    class EnvioViewHolder(val binding: ItemEnvioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvioViewHolder {
        val binding = ItemEnvioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EnvioViewHolder(binding)
    }

    override fun getItemCount(): Int = envios.size

    override fun onBindViewHolder(holder: EnvioViewHolder, position: Int) {
        val envio = envios[position]

        holder.binding.tvId.text = envio.code
        holder.binding.tvTransportDesc.text = envio.tipo
        holder.binding.tvOrigin.text = envio.origen
        holder.binding.tvDestination.text = envio.destino
        holder.binding.tvStatus.text = envio.estado
        holder.binding.tvTimeInfo.text = envio.fecha
        holder.binding.tvAction.text = "Ver Detalles"

        holder.binding.root.setOnClickListener {
            onClick(envio)
        }

        holder.binding.tvAction.setOnClickListener {
            onClick(envio)
        }
    }
}