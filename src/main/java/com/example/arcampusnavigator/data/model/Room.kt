package com.example.arcampusnavigator.data.model

import com.google.firebase.firestore.GeoPoint

data class Room(
    val id: String = "",
    val name: String = "",
    val floorId: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val type: RoomType = RoomType.OTHER,
    val amenities: List<String> = listOf()
)

enum class RoomType {
    CLASSROOM, OFFICE, BATHROOM, CAFETERIA, LIBRARY, LAB, PRINTER, STUDY_SPACE, OTHER
}