package com.example.routex_app.repository


import com.example.routex_app.models.LoginResponse
import com.example.routex_app.models.UsuariModel

import com.example.routex_app.utils.Resource

import com.example.routex_app.network.ApiService

import org.mindrot.jbcrypt.BCrypt

import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode




class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, clavePlana: String): Resource<LoginResponse> {
        return try {
            val response = apiService.login(email)

            if (response.status == HttpStatusCode.OK) {
                // Recibimos la respuesta completa (Usuario + Token)
                val loginData = response.body<LoginResponse>()
                val usuario = loginData.usuari

                // Hack del prefijo para evitar "Invalid salt revision"
                val hashCorregido = usuario.contrasenya.replaceFirst("$2y$", "$2a$")

                if (BCrypt.checkpw(clavePlana, hashCorregido)) {
                    Resource.Success(loginData) // Retornamos el objeto completo
                } else {
                    Resource.Error("Contraseña incorrecta")
                }
            } else {
                Resource.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.localizedMessage}")
        }
    }
}