package com.example.routex_app.repository

import com.example.routex_app.models.OferteRequest
import com.example.routex_app.models.CurrencyModel
import com.example.routex_app.models.DashboardResponse
import com.example.routex_app.models.IndustryModel
import com.example.routex_app.models.PortModel
import com.example.routex_app.models.RegisterClientRequest
import com.example.routex_app.network.ApiService
import com.example.routex_app.utils.Resource
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

class ClientRepository(private val apiService: ApiService) {

    // 1. Obtener lista de Industrias
    suspend fun getIndustries(): Resource<List<IndustryModel>> {
        return try {
            val response = apiService.getIndustries()
            if (response.status == HttpStatusCode.OK) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Error al cargar industrias")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Fallo de conexión")
        }
    }

    // 2. Obtener lista de Monedas
    suspend fun getCurrencies(): Resource<List<CurrencyModel>> {
        return try {
            val response = apiService.getCurrencies()
            if (response.status == HttpStatusCode.OK) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Error al cargar monedas")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Fallo de conexión")
        }
    }

    // 3. Registrar el cliente completo
    suspend fun registerClient(request: RegisterClientRequest): Resource<String> {
        return try {
            val response = apiService.registerClient(request)
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                Resource.Success("Cliente registrado correctamente")
            } else {
                // Intentamos leer el mensaje de error del JSON de C# {"error": "..."}
                val errorBody: Map<String, String> = response.body()
                Resource.Error(errorBody["error"] ?: "Error en el registro")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.localizedMessage}")
        }
    }

    //
    suspend fun createOferte(request: OferteRequest): Resource<String> {
        return try {
            val response = apiService.createOferte(request)
            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                Resource.Success("Solicitud enviada correctamente")
            } else {
                Resource.Error("Error al enviar la solicitud")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Fallo de conexión")
        }
    }

    suspend fun getPorts(): Resource<List<PortModel>> {
        return try {
            val response = apiService.getPorts()
            if (response.status == HttpStatusCode.OK) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Error al cargar puertos")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Fallo de conexión")
        }
    }
}