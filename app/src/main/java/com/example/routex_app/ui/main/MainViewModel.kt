package com.example.routex_app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.data.MainState
import com.example.routex_app.data.RecentActivity
import com.example.routex_app.repository.ClientRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ClientRepository) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun loadDashboard(token: String, userId: Int, userName: String) {
        // 开始加载，先更新用户名并显示加载状态
        _state.update { it.copy(isLoading = true, userName = userName) }

        viewModelScope.launch {
            try {
                // 模拟网络延迟 (等你后端接口写好了，把这里替换成 repository.getClientData(token, userId))
                delay(1500)

                val mockActivities = listOf(
                    RecentActivity("50,000 Shoe Boxes from China", "En Tránsito • Port of Long Beach", "A TIEMPO", "Oct 24", com.example.routex_app.R.drawable.ic_shipment),
                    RecentActivity("Electronics Parts", "Completado • Madrid Terminal", "ENTREGADO", "Oct 22", com.example.routex_app.R.drawable.ic_shipment)
                )

                // actualizar todos los datos de estado
                _state.update {
                    it.copy(
                        isLoading = false,
                        activeCount = 12,
                        pendingCount = 48,
                        recentActivities = mockActivities
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Error al cargar datos")
                }
            }
        }
    }
}