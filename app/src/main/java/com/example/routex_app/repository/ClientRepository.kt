package com.example.routex_app.repository


import com.example.routex_app.models.CommercialDashboardResponse
import com.example.routex_app.models.ClientDashboardDto
import com.example.routex_app.models.OferteRequest
import com.example.routex_app.models.CurrencyModel
import com.example.routex_app.models.DashboardResponse
import com.example.routex_app.models.IndustryModel
import com.example.routex_app.models.PortModel
import com.example.routex_app.models.Presupuesto
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

    //crear oferte
    suspend fun createOferte(token: String, request: OferteRequest): Resource<String> {
        return try {
            // Cridem a la funció de l'ApiService que fa el POST
            val response = apiService.createOferte(token, request)

            // Ktor HttpResponse: comprovem el codi d'estat
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                Resource.Success("Oferta enviada correctament")
            } else {
                Resource.Error("Error del servidor: ${response.status.value}")
            }
        } catch (e: Exception) {
            // Captura errors de xarxa o de serialització JSON
            Resource.Error(e.localizedMessage ?: "Error de connexió al crear l'oferta")
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

    suspend fun getClientDashboard(token: String, userId: Int): Resource<ClientDashboardDto> {
        return try {
            val response = apiService.getClientDashboard(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar dashboard")
        }
    }

    suspend fun getEnvios(token: String, userId: Int): Resource<List<Presupuesto>> {
        return try {
            val data = apiService.getAcceptedQuotes(token, userId)
            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar envíos")
        }
    }
}