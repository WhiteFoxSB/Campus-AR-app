package com.example.arcampusnavigator.data.model

import com.google.firebase.firestore.GeoPoint

data class Campus(
    val id: String = "",
    val name: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val boundaries: List<GeoPoint> = listOf(),
    val buildings: List<String> = listOf()
)