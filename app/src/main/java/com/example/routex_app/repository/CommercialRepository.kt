package com.example.routex_app.repository

import com.example.routex_app.models.* // Simplificamos imports para incluir EnvioActivo y DetalleEnvio
import com.example.routex_app.network.ApiService
import com.example.routex_app.ui.commercial.envios.DetalleEnvio
import com.example.routex_app.ui.commercial.envios.EnvioActivo
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommercialRepository(private val apiService: ApiService) {

    // --- NUEVO: Obtener detalle de un envío específico ---
    suspend fun getDetalleEnvio(token: String, envioId: Int): Resource<DetalleEnvio> {
        return try {
            val response = apiService.getDetalleEnvio(token, envioId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al obtener el detalle del envío")
        }
    }

    // --- CORREGIDO: getEnviosActivos ---
    // Cambiamos List<Oferta> por List<EnvioActivo> para que coincida con tu ApiService
    suspend fun getEnviosActivos(token: String, userId: Int): Resource<List<EnvioActivo>> {
        return try {
            val response = apiService.getEnviosActivos(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al obtener envíos activos")
        }
    }

    // --- DASHBOARD ---
    suspend fun getDashboardData(token: String, userId: Int): Resource<CommercialDashboardResponse> {
        return try {
            val response = apiService.getCommercialDashboard(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar datos del dashboard")
        }
    }

    // --- PRESUPUESTOS (RECHAZADOS, ENVIADOS, ACEPTADOS) ---
    suspend fun getRejectedQuotes(userId: Int, token: String): Resource<List<Presupuesto>> {
        return try {
            val response = apiService.getRejectedQuotes(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener presupuestos rechazados")
        }
    }

    suspend fun getSentQuotes(userId: Int, token: String): Resource<List<Presupuesto>> {
        return try {
            val response = apiService.getSentQuotes(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener presupuestos enviados")
        }
    }

    suspend fun getAcceptedQuotes(userId: Int, token: String): Resource<List<Presupuesto>> {
        return try {
            val response = apiService.getAcceptedQuotes(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener presupuestos aceptados")
        }
    }

    // --- CLIENTES Y PERFIL ---
    suspend fun getActiveClients(userId: Int, token: String): Resource<List<ClienteActivo>> {
        return try {
            val response = apiService.getActiveClients(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener la lista de clientes activos")
        }
    }

    suspend fun getUserProfile(token: String, userId: Int): Resource<UserProfileModel> {
        return try {
            val response = apiService.getUserProfile(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al obtener perfil")
        }
    }
}