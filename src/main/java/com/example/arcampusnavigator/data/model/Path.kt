package com.example.arcampusnavigator.data.model

data class Path(
    val id: String = "",
    val points: List<GeoPoint> = listOf(),
    val isAccessible: Boolean = false,
    val isIndoor: Boolean = false
)