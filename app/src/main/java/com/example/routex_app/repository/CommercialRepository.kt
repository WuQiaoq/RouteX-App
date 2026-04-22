package com.example.routex_app.repository


import com.example.routex_app.network.ApiService
import com.example.routex_app.models.CommercialDashboardResponse
import com.example.routex_app.models.Oferta
import com.example.routex_app.models.Presupuesto
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

    suspend fun getRejectedQuotes(userId: Int, token: String): Resource<List<Presupuesto>> {
        return try {
            // Asumiendo que tu ApiService usa Ktor y devuelve la respuesta directamente
            val response = apiService.getRejectedQuotes(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido")
        }
    }

    suspend fun getSentQuotes(userId: Int, token: String): Resource<List<Presupuesto>> {
        return try {
            // Llama al endpoint: commercial/ofertes/sent/{userId}
            val response = apiService.getSentQuotes(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener presupuestos enviados")
        }
    }

    suspend fun getAcceptedQuotes(userId: Int, token: String): Resource<List<Presupuesto>> {
        return try {
            // Llama al endpoint: commercial/ofertes/accepted/{userId}
            val response = apiService.getAcceptedQuotes(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener presupuestos aceptados")
        }
    }

    suspend fun getOfertasResource(token: String): Resource<List<Oferta>> {
        return try {
            val response = apiService.getOfertas(token)
            Resource.Success(response) // Aquí response DEBE ser List<Oferta>
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al conectar con el servidor")
        }
    }

}
