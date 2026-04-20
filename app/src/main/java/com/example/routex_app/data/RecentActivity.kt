package com.example.routex_app.data

data class RecentActivity(
    val title: String,
    val description: String,
    val status: String,
    val date: String,
    val iconResId: Int // 存储图片资源的 ID，如 R.drawable.ic_shipment
)