package com.example.routex_app.network

import com.example.routex_app.utils.ApiEndpointsList
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    val httpClient = HttpClient(Android) {
        // Esto permite que Ktor convierta JSON a clases de Kotlin automáticamente
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Por si PHP manda datos extra que no usamos
                isLenient = true         // Para ser flexible con el formato JSON
            })
        }

        // Esto imprimirá en el Logcat de Android Studio todas las peticiones (muy útil)
        install(Logging) {
            // Esto envía los logs directamente al Logcat de Android
            logger = object : Logger {
                override fun log(message: String) {
                    android.util.Log.d("KTOR_LOG", message)
                }
            }
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        defaultRequest {
            url(ApiEndpointsList.BASE_URL_CS)
        }

    }
}