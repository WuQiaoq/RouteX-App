package com.example.routex_app.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileModel(
    val userId: Int,
    val fullName: String,
    val email: String,
    val phone: String,
    val roleName: String,
    val companyId: Int?,
    val companyName: String,
    val industryName: String,
    val colaboradorId: String
)