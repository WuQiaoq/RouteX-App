package com.example.routex_app.repository

import com.example.routex_app.models.ClientDashboardDto
import com.example.routex_app.models.OferteRequest
import com.example.routex_app.models.CurrencyModel
import com.example.routex_app.models.DashboardResponse
import com.example.routex_app.models.IndustryModel
import com.example.routex_app.models.PortModel
import com.example.routex_app.models.Presupuesto
import com.example.routex_app.models.RegisterClientRequest
import com.example.routex_app.models.TrackingEnvioResponse
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

    suspend fun getClientEnvios(token: String, userId: Int): Resource<List<Presupuesto>> {
        return try {
            val data = apiService.getClientAcceptedQuotes(token, userId)
            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar envios")
        }
    }

    suspend fun obtenerPresupuestosCliente(token: String, userId: Int): Resource<List<Presupuesto>> {
        return try {
            val data = apiService.obtenerPresupuestosCliente(token, userId)
            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar presupuestos")
        }
    }

    suspend fun aceptarPresupuestoCliente(token: String, userId: Int, presupuestoId: Int): Resource<String> {
        return try {
            val response = apiService.aceptarPresupuestoCliente(token, userId, presupuestoId)
            if (response.status == HttpStatusCode.OK) {
                Resource.Success("Presupuesto aceptado correctamente")
            } else {
                Resource.Error("Error al aceptar presupuesto")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al aceptar presupuesto")
        }
    }

    suspend fun rechazarPresupuestoCliente(
        token: String,
        userId: Int,
        presupuestoId: Int,
        motivoRechazo: String
    ): Resource<String> {
        return try {
            val response = apiService.rechazarPresupuestoCliente(token, userId, presupuestoId, motivoRechazo)
            if (response.status == HttpStatusCode.OK) {
                Resource.Success("Presupuesto rechazado correctamente")
            } else {
                Resource.Error("Error al rechazar presupuesto")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al rechazar presupuesto")
        }
    }

    suspend fun descargarDocumentoCliente(token: String, ofertaId: Int, nombreArchivo: String): Resource<ByteArray> {
        return try {
            val data = apiService.descargarDocumentoCliente(token, ofertaId, nombreArchivo)
            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Documento no disponible")
        }
    }

    suspend fun obtenerTrackingEnvioCliente(token: String, ofertaId: Int): Resource<TrackingEnvioResponse> {
        return try {
            val data = apiService.obtenerTrackingEnvioCliente(token, ofertaId)
            Resource.Success(data)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar tracking")
        }
    }
}
