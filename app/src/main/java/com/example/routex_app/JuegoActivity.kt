package com.example.routex_app


import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.example.routex_app.game.MotorJuegoRouteX

class JuegoActivity : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tipo = intent.getStringExtra("TIPO_VEHICULO") ?: "CAMION"
        initialize(MotorJuegoRouteX(tipo) { finish() })
    }
}