package com.example.routex_app.commercial


import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.routex_app.R
import com.example.routex_app.databinding.ActivityCommercialHomeBinding
import com.example.routex_app.databinding.ItemNotificationBinding

class CommercialHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommercialHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommercialHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Binding separado para cada item_notification incluido
        val notif1Binding = ItemNotificationBinding.bind(binding.notifItem1)
        val notif2Binding = ItemNotificationBinding.bind(binding.notifItem2)
        val notif3Binding = ItemNotificationBinding.bind(binding.notifItem3)

        // Ahora sí accedes a los IDs sin error
        notif1Binding.tvNotifTitle.text = "Shipment Delayed"
        notif1Binding.tvNotifBody.text = "Container #TR-8829 from Port Newark has been delayed."
        notif1Binding.tvNotifTime.text = "1m ago"
        notif1Binding.ivNotifIcon.setImageResource(R.drawable.ic_shipment)

        notif2Binding.tvNotifTitle.text = "New Client Message"
        notif2Binding.tvNotifBody.text = "Global Logistics Corp: Can we adjust the pickup time?"
        notif2Binding.tvNotifTime.text = "3m ago"
        notif2Binding.ivNotifIcon.setImageResource(R.drawable.ic_clients)

        notif3Binding.tvNotifTitle.text = "Quote Accepted"
        notif3Binding.tvNotifBody.text = "Your quote for Project NorthRoad v2.2 was accepted."
        notif3Binding.tvNotifTime.text = "5m ago"
        notif3Binding.ivNotifIcon.setImageResource(R.drawable.ic_budget)
    }
}