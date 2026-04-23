package com.example.routex_app.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.routex_app.R
import com.example.routex_app.models.RecentActivity

class RecentActivityAdapter(private var activities: List<RecentActivity>) :
    RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]

        holder.tvTitle.text = activity.title
        holder.tvDescription.text = activity.description
        holder.tvStatus.text = activity.status
        holder.tvDate.text = activity.date
        holder.ivIcon.setImageResource(activity.iconResId)

        when (activity.status.uppercase()) {
            "RETRASADO" -> holder.tvStatus.setTextColor(Color.parseColor("#EF4444"))
            "A TIEMPO", "COMPLETADO", "ENTREGADO" -> holder.tvStatus.setTextColor(Color.parseColor("#10B981"))
            "PENDIENTE" -> holder.tvStatus.setTextColor(Color.parseColor("#F59E0B"))
            else -> holder.tvStatus.setTextColor(Color.parseColor("#6B7280"))
        }
    }

    override fun getItemCount(): Int = activities.size

    fun updateData(newActivities: List<RecentActivity>) {
        this.activities = newActivities
        notifyDataSetChanged()
    }
}