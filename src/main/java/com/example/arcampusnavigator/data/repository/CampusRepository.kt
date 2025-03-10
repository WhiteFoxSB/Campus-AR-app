package com.example.arcampusnavigator.data.repository

import com.example.arcampusnavigator.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface CampusRepository {
    suspend fun getAllCampuses(): List<Campus>
    suspend fun getCampusById(campusId: String): Campus?
    suspend fun getBuildingsForCampus(campusId: String): List<Building>
    suspend fun getBuildingById(buildingId: String): Building?
    suspend fun getRoomById(roomId: String): Room?
    suspend fun searchPlaces(query: String, campusId: String): List<Any>
    suspend fun getRoute(startPointId: String, endPointId: String, isAccessible: Boolean): Route?
}

class CampusRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val locationRepository: LocationRepository
) : CampusRepository {

    override suspend fun getAllCampuses(): List<Campus> {
        return try {
            val snapshot = firestore.collection("campuses").get().await()
            snapshot.toObjects(Campus::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getCampusById(campusId: String): Campus? {
        return try {
            val document = firestore.collection("campuses").document(campusId).get().await()
            document.toObject(Campus::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getBuildingsForCampus(campusId: String): List<Building> {
        return try {
            val snapshot = firestore.collection("buildings")
                .whereEqualTo("campusId", campusId)
                .get()
                .await()
            snapshot.toObjects(Building::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBuildingById(buildingId: String): Building? {
        return try {
            val document = firestore.collection("buildings").document(buildingId).get().await()
            document.toObject(Building::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getRoomById(roomId: String): Room? {
        return try {
            val document = firestore.collection("rooms").document(roomId).get().await()
            document.toObject(Room::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchPlaces(query: String, campusId: String): List<Any> {
        val results = mutableListOf<Any>()

        try {
            // Search buildings
            val buildingSnapshot = firestore.collection("buildings")
                .whereEqualTo("campusId", campusId)
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .await()
            results.addAll(buildingSnapshot.toObjects(Building::class.java))

            // Search rooms
            val roomSnapshot = firestore.collection("rooms")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .await()

            // Filter rooms to only include those in buildings of this campus
            val roomList = roomSnapshot.toObjects(Room::class.java)
            val buildingIds = getBuildingsForCampus(campusId).map { it.id }

            for (room in roomList) {
                val floorDoc = firestore.collection("floors").document(room.floorId).get().await()
                val floor = floorDoc.toObject(Floor::class.java)
                if (floor != null && buildingIds.contains(floor.buildingId)) {
                    results.add(room)
                }
            }
        } catch (e: Exception) {
            // Log error
        }

        return results
    }

    override suspend fun getRoute(startPointId: String, endPointId: String, isAccessible: Boolean): Route? {
        // This would involve complex routing algorithm in a real app
        // For MVP, we'll generate a simple route between points

        try {
            // Get start and end locations
            val startLocation = getLocationById(startPointId)
            val endLocation = getLocationById(endPointId)

            if (startLocation != null && endLocation != null) {
                // Calculate straight-line distance for MVP
                val distance = calculateDistance(startLocation, endLocation)

                // Estimate walking time (average walking speed ~1.4 m/s)
                val timeInSeconds = (distance / 1.4).toInt()

                // Create a simple route for MVP with a single step
                return Route(
                    id = "route_${startPointId}_${endPointId}",
                    startPoint = startLocation,
                    endPoint = endLocation,
                    distance = distance,
                    estimatedTime = timeInSeconds,
                    isAccessible = isAccessible,
                    steps = listOf(
                        NavigationStep(
                            id = "step_1",
                            instruction = "Head toward ${getNameById(endPointId)}",
                            distance = distance,
                            startPoint = startLocation,
                            endPoint = endLocation
                        )
                    )
                )
            }
        } catch (e: Exception) {
            // Log error
        }

        return null
    }

    private suspend fun getLocationById(id: String): com.google.firebase.firestore.GeoPoint? {
        // This is a simplified version for the MVP
        // In a full implementation, we would check if the ID is for a building, room, etc.
        try {
            // Try buildings first
            val buildingDoc = firestore.collection("buildings").document(id).get().await()
            val building = buildingDoc.toObject(Building::class.java)
            if (building != null) {
                return building.location
            }

            // Try rooms
            val roomDoc = firestore.collection("rooms").document(id).get().await()
            val room = roomDoc.toObject(Room::class.java)
            if (room != null) {
                return room.location
            }
        } catch (e: Exception) {
            // Log error
        }

        return null
    }

    private suspend fun getNameById(id: String): String {
        try {
            // Try buildings first
            val buildingDoc = firestore.collection("buildings").document(id).get().await()
            val building = buildingDoc.toObject(Building::class.java)
            if (building != null) {
                return building.name
            }

            // Try rooms
            val roomDoc = firestore.collection("rooms").document(id).get().await()
            val room = roomDoc.toObject(Room::class.java)
            if (room != null) {
                return room.name
            }
        } catch (e: Exception) {
            // Log error
        }

        return "Destination"
    }

    private fun calculateDistance(point1: GeoPoint?, point2: GeoPoint?): Double {
        // Haversine formula for calculating distance between two points on the Earth
        val R = 6371e3 // Earth radius in meters
        val lat1 = Math.toRadians(point1.latitude)
        val lat2 = Math.toRadians(point2.latitude)
        val deltaLat = Math.toRadians(point2.latitude - point1.latitude)
        val deltaLon = Math.toRadians(point2.longitude - point1.longitude)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c // Distance in meters
    }
}
