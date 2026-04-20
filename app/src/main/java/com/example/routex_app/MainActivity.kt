package com.example.routex_app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routex_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 使用 ViewBinding 访问布局中的控件
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        setupBottomNavigation()
    }

    private fun setupUI() {
        // texto bienvenido
        binding.tvWelcomeName.text = "Bienvenido de nuevo, Alex"

        binding.btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
            // cuando hacer click esconder el punto
            binding.viewNotificationDot.visibility = View.GONE
        }

        binding.btnRequestQuote.setOnClickListener {
            Toast.makeText(this, "Solicitando presupuesto...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val activities = listOf(
            RecentActivity(
                "50,000 Shoe Boxes from China",
                "En Tránsito • Port of Long Beach",
                "A TIEMPO",
                "Oct 24",
                R.drawable.ic_shipment
            ),
            RecentActivity(
                "Electronics Parts",
                "Completado • Madrid Terminal",
                "ENTREGADO",
                "Oct 22",
                R.drawable.ic_shipment
            ),
            RecentActivity(
                "Raw Materials",
                "Retrasado • Port of Shanghai",
                "RETRASADO",
                "Oct 20",
                R.drawable.ic_shipment
            )
        )

        binding.rvRecentActivity.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecentActivityAdapter(activities)
            // 防止在 NestedScrollView 中出现滚动冲突或卡顿
            isNestedScrollingEnabled = false
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_shipping -> {
                    Toast.makeText(this, "Envíos seleccionados", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}