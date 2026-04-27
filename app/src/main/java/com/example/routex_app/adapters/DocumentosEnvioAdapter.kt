package com.example.routex_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.databinding.ItemDocumentBinding

data class DocumentoEnvio(
    val titulo: String,
    val subtitulo: String,
    val nombreArchivo: String,
    val disponible: Boolean = false
)

class DocumentosEnvioAdapter(
    private val documentos: List<DocumentoEnvio>,
    private val alDescargarClick: (DocumentoEnvio) -> Unit
) : RecyclerView.Adapter<DocumentosEnvioAdapter.DocumentoViewHolder>() {

    class DocumentoViewHolder(val binding: ItemDocumentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentoViewHolder {
        val binding = ItemDocumentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DocumentoViewHolder(binding)
    }

    override fun getItemCount(): Int = documentos.size

    override fun onBindViewHolder(holder: DocumentoViewHolder, position: Int) {
        val documento = documentos[position]
        holder.binding.tvDocumentTitle.text = documento.titulo
        holder.binding.tvDocumentSubtitle.text = documento.subtitulo
        holder.binding.btnDownloadDocument.alpha = if (documento.disponible) 1f else 0.45f
        holder.binding.btnDownloadDocument.setOnClickListener {
            alDescargarClick(documento)
        }
    }
}
