package com.example.routex_app.network

import com.example.routex_app.utils.ApiEndpointsList
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.forms.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import java.io.File

object NetworkClient {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    android.util.Log.d("KTOR_JAVA_SERVER", message)
                }
            }
        }
    }

    // PUJADA: /dni/upload/{userId}
    suspend fun enviarDni(usertlfn: String, bytes: ByteArray, filename: String): String {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "${ApiEndpointsList.BASE_URL_SERVER}${ApiEndpointsList.DNI_UPLOAD}$usertlfn",
                formData = formData {
                    append("file", bytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                    })
                }
            )
            response.bodyAsText()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    // DESCÀRREGA: /dni/download/{userId}/{filename}
    suspend fun baixarDni(usertlfn: String, filename: String): ByteArray? {
        return try {
            val response = client.get("${ApiEndpointsList.BASE_URL_SERVER}${ApiEndpointsList.DNI_DOWNLOAD}$usertlfn/$filename")
            if (response.status == HttpStatusCode.OK) {
                response.readBytes()
            } else null
        } catch (e: Exception) {
            null
        }
    }
}