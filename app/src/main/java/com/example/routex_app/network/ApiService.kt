package com.example.routex_app.network

import com.example.routex_app.models.CommercialDashboardResponse
import com.example.routex_app.models.LoginRequest
import com.example.routex_app.models.LoginResponse
import com.example.routex_app.models.Presupuesto
import com.example.routex_app.utils.ApiEndpointsList
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.HttpClient


import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ApiService(private val client: HttpClient) {

    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        // Ktor ya sabe que debe usar la BASE_URL, así que solo pones el final
        return client.post(ApiEndpointsList.LOGIN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }.body()
    }

    suspend fun getCommercialDashboard(token: String, userId: Int): CommercialDashboardResponse {
        return client.get(ApiEndpointsList.BASE_URL_CS + ApiEndpointsList.COMMERCIAL_DASHBOARD_ENDPOINT + userId) {
            header("Authorization", "Bearer $token")
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

}
