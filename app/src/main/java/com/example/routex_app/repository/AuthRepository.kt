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
            val response = apiService.login(LoginRequest(usuario, clave))

            if (response.status == HttpStatusCode.OK) {
                val data = response.body<LoginResponse>()
                Resource.Success(data)
            } else {
                // Aquí evitamos el crash parseando el error manualmente
                val errorMsg: Map<String, String> = response.body()
                Resource.Error(errorMsg["error"] ?: "Error desconocido")
            }
        } catch (e: Exception) {
            Log.e("AUTH", "Error: ${e.message}")
            Resource.Error("Error de red o formato")
        }
    }
}