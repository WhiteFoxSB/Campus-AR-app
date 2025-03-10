package com.example.arcampusnavigator.data.model

data class Route(
    val id: String = "",
    val startPoint: com.google.firebase.firestore.GeoPoint? = GeoPoint(0.0, 0.0),
    val endPoint: com.google.firebase.firestore.GeoPoint? = GeoPoint(0.0, 0.0),
    val waypoints: List<GeoPoint> = listOf(),
    val distance: Double = 0.0,
    val estimatedTime: Int = 0, // in seconds
    val isAccessible: Boolean = false,
    val steps: List<NavigationStep> = listOf()
)