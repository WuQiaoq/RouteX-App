package com.example.routex_app.cliente

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.routex_app.R

class DetallesEnvioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_envios)

        val code = intent.getStringExtra("CODE")

        Toast.makeText(this, code, Toast.LENGTH_LONG).show()
    }
}