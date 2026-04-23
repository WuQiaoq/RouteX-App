package com.example.routex_app.ui.commercial.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routex_app.models.UserProfileModel
import com.example.routex_app.repository.CommercialRepository
import com.example.routex_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: CommercialRepository) : ViewModel() {

    private val _profile = MutableStateFlow<Resource<UserProfileModel>>(Resource.Loading())
    val profile: StateFlow<Resource<UserProfileModel>> = _profile

    fun fetchUserProfile(userId: Int, token: String) {
        viewModelScope.launch {
            _profile.value = Resource.Loading()
            val result = repository.getUserProfile(token, userId)
            _profile.value = result
        }
    }
}