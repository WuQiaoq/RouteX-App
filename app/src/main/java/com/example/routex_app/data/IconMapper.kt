package com.example.routex_app.data

import com.example.routex_app.R

fun mapIcon(icon: String): Int {
    return when (icon) {
        "shipment" -> R.drawable.ic_shipment
        "warning" -> R.drawable.ic_rejected
        else -> R.drawable.ic_shipment
    }
}