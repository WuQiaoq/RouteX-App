package com.example.routex_app.repository


import com.example.routex_app.network.ApiService
import com.example.routex_app.models.CommercialDashboardResponse
import com.example.routex_app.utils.Resource

class CommercialRepository(private val apiService: ApiService) {

    suspend fun getDashboardData(token: String, userId: Int): Resource<CommercialDashboardResponse> {
        return try {
            // Ahora pasamos el token para el Header y el userId para la URL
            val response = apiService.getCommercialDashboard(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar datos")
        }
    }
}