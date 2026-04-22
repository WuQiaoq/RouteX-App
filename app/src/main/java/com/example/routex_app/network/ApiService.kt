package com.example.routex_app.network

import com.example.routex_app.models.CommercialDashboardResponse
import com.example.routex_app.models.CurrencyModel
import com.example.routex_app.models.IndustryModel
import com.example.routex_app.models.LoginRequest
import com.example.routex_app.models.LoginResponse
import com.example.routex_app.models.Oferta
import com.example.routex_app.models.Presupuesto
import com.example.routex_app.models.RegisterClientRequest
import com.example.routex_app.utils.ApiEndpointsList
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.HttpClient


import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*

class ApiService(private val client: HttpClient) {

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

    suspend fun getRejectedQuotes(token: String, userId: Int): List<Presupuesto> {
        return client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.REJECTED_QUOTES_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun getSentQuotes(token: String, userId: Int): List<Presupuesto> {
        return client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.SENT_QUOTES_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun getAcceptedQuotes(token: String, userId: Int): List<Presupuesto> {
        return client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.ACCEPTED_QUOTES_ENDPOINT + userId) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

    // Cambiamos el tipo de retorno a : HttpResponse
    suspend fun getIndustries(): HttpResponse {
        return client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.LIST_INDUSTRY)
    }

    // Cambiamos el tipo de retorno a : HttpResponse
    suspend fun getCurrencies(): HttpResponse {
        return client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.LIST_CURRENCY)
    }

    suspend fun registerClient(request: RegisterClientRequest): HttpResponse {
        return client.post(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.RESGISTER_NEW_CLIENT) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun getOfertas(token: String): List<Oferta> {
        return client.get(ApiEndpointsList.BASE_URL_PHP + ApiEndpointsList.LISTADO_OFERRTAS) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
        }.body()
    }

}
