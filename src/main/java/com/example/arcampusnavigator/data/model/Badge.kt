package com.example.arcampusnavigator.data.model

data class Badge(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val requirements: List<String> = listOf()
)