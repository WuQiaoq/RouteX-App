package com.example.routex_app.ui.commercial.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.routex_app.repository.CommercialRepository

class ProfileViewModelFactory(private val repository: CommercialRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}