package com.example.routex_app.ui.cliente.envios

import com.example.routex_app.network.ApiService
import com.example.routex_app.ui.commercial.envios.DetalleEnvio
import com.example.routex_app.ui.commercial.envios.EnvioActivo
import com.example.routex_app.utils.Resource

class ClientRepository(
    private val apiService: ApiService
) {

    // 1. Obtener la lista de envíos filtrados (Aceptados, Tránsito, etc.)
    suspend fun getEnviosActivos(token: String, userId: Int): Resource<List<EnvioActivo>> {
        return try {
            val response = apiService.getClientEnviosActivos(token, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar la lista de envíos")
        }
    }

    // 2. Obtener el detalle de un envío específico con su tracking
    suspend fun getDetalleEnvioCliente(token: String, id: Int, userId: Int): Resource<DetalleEnvio> {
        return try {
            val response = apiService.getClientDetalleEnvio(token, id, userId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cargar los detalles del envío")
        }
    }
}