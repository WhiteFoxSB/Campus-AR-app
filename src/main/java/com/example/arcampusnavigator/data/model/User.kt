package com.example.arcampusnavigator.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val points: Int = 0,
    val badges: List<String> = listOf(),
    val notificationUnlocked: Boolean = false,
    val selectedCampus: String? = null
)