package com.example.routex_app

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.routex_app.MainActivity
import com.example.routex_app.R
import com.example.routex_app.commercial.CommercialHomeActivity
import com.example.routex_app.commercial.PresupuestosActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavigationUtils {

    fun setupBottomNavigation(
        activity: AppCompatActivity,
        bottomNav: BottomNavigationView,
        currentItemId: Int
    ) {
        // Marcamos el ítem actual para que se vea seleccionado
        bottomNav.selectedItemId = currentItemId

        bottomNav.setOnItemSelectedListener { item ->
            // Si el usuario presiona el icono de la pantalla donde ya está, no hacemos nada
            if (item.itemId == currentItemId) return@setOnItemSelectedListener true

            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(activity, CommercialHomeActivity::class.java)

                // CORREGIDO: Eliminamos el bloque con 'null'
                R.id.nav_budgets -> Intent(activity, PresupuestosActivity::class.java)

                R.id.nav_clients -> {
                    // Intent(activity, ClientsActivity::class.java)
                    null
                }
                else -> null
            }

            if (intent != null) {
                // MUY IMPORTANTE: Pasamos el Token e ID para no perder la sesión
                val token = activity.intent.getStringExtra("USER_TOKEN")
                val userId = activity.intent.getIntExtra("USER_ID", -1)

                intent.putExtra("USER_TOKEN", token)
                intent.putExtra("USER_ID", userId)

                // FLAG para no crear múltiples instancias de la misma Activity
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

                activity.startActivity(intent)
                activity.overridePendingTransition(0, 0) // Navegación instantánea sin parpadeo
                true
            } else {
                Toast.makeText(activity, "Sección en desarrollo", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}