package com.example.arcampusnavigator.data.model

import java.util.Date

data class Notification(
    val id: String = "",
    val userId: String = "",
    val placeId: String = "",
    val title: String = "",
    val description: String = "",
    val type: NotificationType = NotificationType.OTHER,
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val createdAt: Date = Date(),
    val expiresAt: Date? = null,
    val isActive: Boolean = true
)

enum class NotificationType {
    WIFI_ISSUE, PRINTER_ISSUE, CROWD_ALERT, EVENT, OTHER
}