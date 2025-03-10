package com.example.arcampusnavigator.data.model

import com.google.firebase.firestore.GeoPoint

data class Building(
    val id: String = "",
    val name: String = "",
    val campusId: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val entrances: List<GeoPoint> = listOf(),
    val floors: List<Floor> = listOf(),
    val amenities: List<String> = listOf()
)