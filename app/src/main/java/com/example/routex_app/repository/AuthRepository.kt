package com.example.routex_app.repository


import android.util.Log
import com.example.routex_app.models.LoginRequest
import com.example.routex_app.models.LoginResponse
import com.example.routex_app.utils.ApiEndpointsList
import com.example.routex_app.utils.Resource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import com.example.routex_app.network.ApiService


class AuthRepository(private val apiService: ApiService) {

    suspend fun login(usuario: String, clave: String): Resource<LoginResponse> {
        return try {
            // Llamamos a la función que definiste en ApiService
            val response = apiService.login(LoginRequest(usuario, clave))
            Resource.Success(response)
        } catch (e: Exception) {
            // Este Log es el que te dirá en consola si falla la conversión de JSON
            Log.e("AUTH_REPOSITORY", "Error en login: ${e.message}")
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Error de conexión")
        }
    }
}