package com.example.arcampusnavigator.data.repository

import com.example.arcampusnavigator.data.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

interface NotificationRepository {
    suspend fun getNotifications(placeId: String): List<Notification>
    suspend fun addNotification(notification: Notification): Result<Notification>
    suspend fun upvoteNotification(notificationId: String): Result<Unit>
    suspend fun downvoteNotification(notificationId: String): Result<Unit>
    suspend fun reportNotification(notificationId: String, reason: String): Result<Unit>
    suspend fun getUserNotifications(userId: String): List<Notification>
}

class NotificationRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NotificationRepository {

    override suspend fun getNotifications(placeId: String): List<Notification> {
        return try {
            val snapshot = firestore.collection("notifications")
                .whereEqualTo("placeId", placeId)
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", Date())
                .get()
                .await()
            snapshot.toObjects(Notification::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addNotification(notification: Notification): Result<Notification> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

            val newNotification = notification.copy(
                userId = userId,
                createdAt = Date(),
                expiresAt = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000) // 24 hours from now
            )

            val notificationDoc = firestore.collection("notifications").document()
            val notificationWithId = newNotification.copy(id = notificationDoc.id)

            notificationDoc.set(notificationWithId).await()
            Result.success(notificationWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun upvoteNotification(notificationId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val notificationRef = firestore.collection("notifications").document(notificationId)
                val snapshot = transaction.get(notificationRef)
                val notification = snapshot.toObject(Notification::class.java)
                    ?: throw Exception("Notification not found")

                transaction.update(notificationRef, "upvotes", notification.upvotes + 1)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downvoteNotification(notificationId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val notificationRef = firestore.collection("notifications").document(notificationId)
                val snapshot = transaction.get(notificationRef)
                val notification = snapshot.toObject(Notification::class.java)
                    ?: throw Exception("Notification not found")

                transaction.update(notificationRef, "downvotes", notification.downvotes + 1)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportNotification(notificationId: String, reason: String): Result<Unit> {
        return try {
            val reportDoc = hashMapOf(
                "notificationId" to notificationId,
                "reason" to reason,
                "reportedAt" to Date(),
                "reportedBy" to (auth.currentUser?.uid ?: "anonymous")
            )

            firestore.collection("reports").add(reportDoc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserNotifications(userId: String): List<Notification> {
        return try {
            val snapshot = firestore.collection("notifications")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.toObjects(Notification::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}