package com.example.arcampusnavigator.data.model

data class Floor(
    val id: String = "",
    val buildingId: String = "",
    val level: Int = 0,
    val mapImageUrl: String = "",
    val rooms: List<Room> = listOf(),
    val paths: List<Path> = listOf()
)
