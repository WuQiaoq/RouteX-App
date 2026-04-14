package com.example.routex_app.models

import kotlinx.serialization.Serializable

// Cada campo debe coincidir EXACTAMENTE con el JSON que devuelve tu PHP
@Serializable
data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String
                  )