package com.example.routex_app
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 适配器：负责将 RecentActivity 数据列表绑定到 item_recent_activity.xml 布局上
 */
class RecentActivityAdapter(private val activities: List<RecentActivity>) :
    RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    /**
     * ViewHolder：持有并缓存 item 布局中的所有控件引用
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    // 1. 创建新视图 (由 LayoutManager 调用)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return ViewHolder(view)
    }

    // 2. 绑定数据到视图 (由 LayoutManager 调用)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]

        // 设置文本内容
        holder.tvTitle.text = activity.title
        holder.tvDescription.text = activity.description
        holder.tvStatus.text = activity.status
        holder.tvDate.text = activity.date

        // 设置图标
        holder.ivIcon.setImageResource(activity.iconResId)

        // --- 动态样式处理 ---
        // 根据状态文字修改颜色逻辑
        when (activity.status.uppercase()) {
            "RETRASADO" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#EF4444")) // 红色
            }
            "A TIEMPO", "COMPLETADO", "ENTREGADO" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#10B981")) // 绿色
            }
            "PENDIENTE" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#F59E0B")) // 橙色
            }
            else -> {
                holder.tvStatus.setTextColor(Color.parseColor("#6B7280")) // 默认灰色
            }
        }
    }

    // 3. 返回数据总量
    override fun getItemCount(): Int = activities.size
}