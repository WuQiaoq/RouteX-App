package com.example.routex_app

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.routex_app.cliente.EnviosTotalActivity
import com.example.routex_app.cliente.MainActivity
import com.example.routex_app.cliente.PerfilActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationUtilsClient {

    fun setupBottomNavigation(
        activity: AppCompatActivity,
        bottomNav: BottomNavigationView,
        currentItemId: Int
    ) {
        bottomNav.selectedItemId = currentItemId

        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == currentItemId) return@setOnItemSelectedListener true

            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(activity, MainActivity::class.java)
                R.id.nav_budgets -> { null }
                R.id.nav_shipping -> Intent(activity, EnviosTotalActivity::class.java)
                R.id.nav_chat -> { null }
                R.id.nav_profile -> Intent(activity, PerfilActivity::class.java)
                else -> null
            }

            if (intent != null) {
                // 1. RECUPERAMOS de la actividad actual
                val token = activity.intent.getStringExtra("USER_TOKEN")
                val userId = activity.intent.getIntExtra("USER_ID", -1)
                val userName = activity.intent.getStringExtra("USER_NAME")

                // 2. VERIFICACIÓN DE SEGURIDAD
                if (token.isNullOrEmpty() || userId == -1) {
                    android.util.Log.e("NAV_ERROR", "Se perdió la sesión al navegar a ${item.title}")
                }

                // 3. PASAMOS los datos al siguiente Intent
                intent.putExtra("USER_TOKEN", token)
                intent.putExtra("USER_ID", userId)
                intent.putExtra("USER_NAME", userName)

                // Evita que las actividades se acumulen en el stack
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

                activity.startActivity(intent)

                // Transición suave para que parezca una sola App (SPA style)
                activity.overridePendingTransition(0, 0)
                true
            } else {
                Toast.makeText(activity, "Sección en desarrollo", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}
