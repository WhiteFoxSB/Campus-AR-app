package com.example.arcampusnavigator.data.model

data class NavigationStep(
    val id: String = "",
    val instruction: String = "",
    val distance: Double = 0.0,
    val startPoint: com.google.firebase.firestore.GeoPoint? = GeoPoint(0.0, 0.0),
    val endPoint: com.google.firebase.firestore.GeoPoint? = GeoPoint(0.0, 0.0),
    val isIndoor: Boolean = false,
    val floorChange: Int? = null // null if no floor change
)