package com.example.routex_app.repository

import OferteRequest
import com.example.routex_app.models.CommercialDashboardResponse
import com.example.routex_app.models.CurrencyModel
import com.example.routex_app.models.DashboardResponse
import com.example.routex_app.models.IndustryModel
import com.example.routex_app.models.RegisterClientRequest
import com.example.routex_app.network.ApiService
import com.example.routex_app.utils.Resource
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

class ClientRepository(private val apiService: ApiService) {

    // 1. Obtener lista de Industrias
    suspend fun getIndustries(): Resource<List<IndustryModel>> {
        return try {
            // Ahora apiService.getIndustries() devuelve List<IndustryModel> directamente
            val industries = apiService.getIndustries()
            Resource.Success(industries)
        } catch (e: Exception) {
            // Ktor lanza excepciones si el status no es 2xx al usar .body()
            Resource.Error(e.localizedMessage ?: "Error al cargar industrias")
        }
    }

    // 2. Obtener lista de Monedas
    suspend fun getCurrencies(): Resource<List<CurrencyModel>> {
        return try {
            val currencies = apiService.getCurrencies()
            Resource.Success(currencies)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar monedas")
        }
    }

    // 3. Registrar el cliente completo
    suspend fun registerClient(request: RegisterClientRequest): Resource<String> {
        return try {
            val response = apiService.registerClient(request)

            // Aquí SI usamos .status porque registerClient devuelve HttpResponse
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                Resource.Success("Cliente registrado correctamente")
            } else {
                // Intentamos capturar el error del body si el status no es exitoso
                val errorMsg = try {
                    val errorBody: Map<String, String> = response.body()
                    errorBody["error"] ?: "Error en el registro"
                } catch (e: Exception) {
                    "Error del servidor (${response.status.value})"
                }
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.localizedMessage}")
        }
    }


    // Dins de la classe ClientRepository
    suspend fun getDashboard(token: String, userId: Int): Resource<CommercialDashboardResponse> {
        return try {
            // Crida al mètode correcte de l'ApiService
            val response = apiService.getCommercialDashboard(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al carregar el dashboard comercial")
        }
    }

}