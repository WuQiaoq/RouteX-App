package com.example.routex_app.network

import com.example.routex_app.models.*
import com.example.routex_app.utils.ApiEndpointsList
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
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

    suspend fun getOfertas(token: String): List<Oferta> =
        client.get(ApiEndpointsList.BASE_URL_PHP + ApiEndpointsList.LISTADO_OFERRTAS) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    // Esta es la que usa tu ClientesActivosActivity
    suspend fun getActiveClients(token: String, userId: Int): List<ClienteActivo> =
        client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.LSITADO_CLIENTES + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    suspend fun getUserProfile(token: String, userId: Int): UserProfileModel {
        return client.get( ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.PERFIL_COMMERCIAL + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    //Enviar el solicitud de presupuesto
    suspend fun createOferte(request: OferteRequest) =
        client.post(ApiEndpointsList.BASE_URL_CS+ ApiEndpointsList.CLIENTE_OFERTAS) {
            contentType(ContentType.Application.Json)
            setBody(request)
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

    suspend fun getClientAcceptedQuotes(token: String, userId: Int): List<Presupuesto> =
        client.get(ApiEndpointsList.BASE_URL_CS_LOCAL + ApiEndpointsList.CLIENT_ACCEPTED_QUOTES_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    suspend fun obtenerPresupuestosCliente(token: String, userId: Int): List<Presupuesto> =
        client.get(ApiEndpointsList.BASE_URL_CS_LOCAL + ApiEndpointsList.CLIENT_PRESUPUESTOS_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    suspend fun aceptarPresupuestoCliente(token: String, userId: Int, presupuestoId: Int): HttpResponse =
        client.put(ApiEndpointsList.BASE_URL_CS_LOCAL + ApiEndpointsList.CLIENT_PRESUPUESTOS_ENDPOINT + presupuestoId + "/accept") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(DecisionPresupuestoRequest(clientId = userId))
        }

    suspend fun rechazarPresupuestoCliente(
        token: String,
        userId: Int,
        presupuestoId: Int,
        motivoRechazo: String
    ): HttpResponse =
        client.put(ApiEndpointsList.BASE_URL_CS_LOCAL + ApiEndpointsList.CLIENT_PRESUPUESTOS_ENDPOINT + presupuestoId + "/reject") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(DecisionPresupuestoRequest(clientId = userId, rejectionReason = motivoRechazo))
        }

    suspend fun descargarDocumentoCliente(token: String, ofertaId: Int, nombreArchivo: String): ByteArray =
        client.get(ApiEndpointsList.BASE_URL_CS_LOCAL + ApiEndpointsList.CLIENT_OFERTA_DOCUMENTS_ENDPOINT + ofertaId + "/documents/" + nombreArchivo) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.readBytes()

    suspend fun obtenerTrackingEnvioCliente(token: String, ofertaId: Int): TrackingEnvioResponse =
        client.get(ApiEndpointsList.BASE_URL_CS_LOCAL + ApiEndpointsList.CLIENT_ENVIO_TRACKING_ENDPOINT + ofertaId + "/tracking") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

}
