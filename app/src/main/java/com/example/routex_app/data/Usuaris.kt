package com.example.routex_app.data

data class Usuaris (
    val id: Int,
    val correu: String,
    val nom: String,
    val cognoms: String,
    val tlfn: String?,
    val rol_id: Int,
    val company_id: Int,
    val status: Int,
    val ultima_conex: String?
)