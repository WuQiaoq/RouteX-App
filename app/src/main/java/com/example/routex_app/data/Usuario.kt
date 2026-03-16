package com.example.routex_app.data

data class Usuario (
    val id: String ,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val telefono: String,
    val rol: RolUsuario,
    val perfilModificable: Boolean
)

enum class RolUsuario {
    CLIENTE, COMERCIAL
}
