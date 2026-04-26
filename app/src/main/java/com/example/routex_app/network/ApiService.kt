package com.example.routex_app.network

import android.util.Log
import com.example.routex_app.models.*
import com.example.routex_app.ui.commercial.envios.DetalleEnvio
import com.example.routex_app.ui.commercial.envios.EnvioActivo
import com.example.routex_app.utils.ApiEndpointsList
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*

class ApiService(private val client: HttpClient) {

    // Login: Normalmente querrás el body (token/user) directamente o la respuesta completa
    suspend fun login(email: String): HttpResponse {
        return client.post(ApiEndpointsList.LOGIN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email))
        }
    }

    suspend fun getCommercialDashboard(token: String, userId: Int): CommercialDashboardResponse {
        return client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.COMMERCIAL_DASHBOARD_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    // Usamos .body<List<Presupuesto>>() para consistencia
    suspend fun getRejectedQuotes(token: String, userId: Int): List<Presupuesto> =
        client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.REJECTED_QUOTES_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    suspend fun getSentQuotes(token: String, userId: Int): List<Presupuesto> =
        client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.SENT_QUOTES_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    suspend fun getAcceptedQuotes(token: String, userId: Int): List<Presupuesto> =
        client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.ACCEPTED_QUOTES_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    // Corregido: Para que el Repository pueda usarlos fácilmente, mejor devolver la lista directamente
    suspend fun getIndustries(): List<IndustryModel> =
        client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.LIST_INDUSTRY).body()

    suspend fun getCurrencies(): List<CurrencyModel> =
        client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.LIST_CURRENCY).body()

    suspend fun registerClient(request: RegisterClientRequest): HttpResponse {
        return client.post(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.RESGISTER_NEW_CLIENT) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    // Esta es la que usa tu ClientesActivosActivity
    suspend fun getActiveClients(token: String, userId: Int): List<ClienteActivo> =
        client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.LSITADO_CLIENTES + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    suspend fun getUserProfile(token: String, userId: Int): UserProfileModel {
        return client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.PERFIL_COMMERCIAL + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    // Cambia DetalleEnvio por DetalleEnvioDto si ese es el nombre de tu data class
    suspend fun getEnviosActivos(token: String, userId: Int): List<EnvioActivo> {
        return client.get(ApiEndpointsList.BASE_URL_CS + "commercial/envios/activos/$userId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun getDetalleEnvio(token: String, envioId: Int): DetalleEnvio {
        return client.get(ApiEndpointsList.BASE_URL_CS + "commercial/envios/detalle/$envioId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun confirmarSubidaEnDB(token: String, stepId: Int, fileName: String): Boolean {
        return try {
            val response: HttpResponse = client.post(ApiEndpointsList.BASE_URL_CS + "commercial/confirmar-subida") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(ConfirmarSubidaRequest(stepId, fileName))
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("API_SERVICE", "Error confirmando subida: ${e.message}")
            false
        }
    }

    suspend fun subirDocumento(stepId: Int, fileBytes: ByteArray, fileName: String): Boolean {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "api/commercial/envios/subir", // Ajusta a tu URL de C#
                formData = formData {
                    append("stepId", stepId.toString())
                    append("fichero", fileBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                }
            )
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    suspend fun descargarArchivo(filename: String): ByteArray? {
        return try {
            client.get("api/commercial/envios/descargar/$filename").body<ByteArray>()
        } catch (e: Exception) {
            null
        }
    }

    // Dins de class ApiService
    suspend fun createOferte(token: String, request: OferteRequest): HttpResponse {
        return client.post(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.CLIENTE_OFERTAS) {
            header(HttpHeaders.Authorization, "Bearer $token") // Important: Afegeix el token
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
    suspend fun getClientDashboard(
        token: String,
        userId: Int
    ): ClientDashboardDto {
        return client.get(ApiEndpointsList.BASE_URL_CS+ ApiEndpointsList.CLIENTE_DASHBOARD + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    // puertos de solicitud presupuesto
    suspend fun getPorts() =
        client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.PORT)

    // --- NUEVAS FUNCIONES PARA CLIENTE ---

    // Obtener lista de envíos activos del cliente (Aceptados/Tránsito)
    suspend fun getClientEnviosActivos(token: String, userId: Int): List<EnvioActivo> {
        return client.get(ApiEndpointsList.BASE_URL_CS + "client/envios/activos/$userId") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    // Obtener el detalle con tracking de un envío para el cliente
    // Pasamos envioId y userId para la validación de seguridad en C#
    suspend fun getClientDetalleEnvio(token: String, envioId: Int, userId: Int): DetalleEnvio {
        return client.get(ApiEndpointsList.BASE_URL_CS + "client/envios/detalle/$envioId/$userId") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

}