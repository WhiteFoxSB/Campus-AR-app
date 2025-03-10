package com.example.arcampusnavigator.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.arcampusnavigator.data.model.GeoPoint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.indooratlas.android.sdk.IALocationManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface LocationRepository {
    fun getLocationUpdates(): Flow<GeoPoint>
    suspend fun getLastKnownLocation(): GeoPoint?
    suspend fun startIndoorLocationTracking()
    suspend fun stopIndoorLocationTracking()
}

class LocationRepositoryImpl(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) : LocationRepository {

    private var indoorLocationManager: IALocationManager? = null

    override fun getLocationUpdates(): Flow<GeoPoint> = callbackFlow {
        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val geoPoint = GeoPoint(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = location.altitude
                    )
                    trySend(geoPoint)
                }
            }
        }

        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override suspend fun getLastKnownLocation(): GeoPoint? {
        return try {
            val location = if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
                fusedLocationClient.lastLocation.await()
            location?.let {
                GeoPoint(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    altitude = it.altitude
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun startIndoorLocationTracking() {
        // Initialize IndoorAtlas (requires API key in a real implementation)
        if (indoorLocationManager == null) {
            indoorLocationManager = IALocationManager.create(context)
        }

        // Start positioning
        indoorLocationManager?.requestLocationUpdates(null, object : com.indooratlas.android.sdk.IALocationListener {
            override fun onLocationChanged(location: com.indooratlas.android.sdk.IALocation?) {
                // In a real app, we would combine this with the outdoor position
                // and update our location service
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {
                // Handle status changes
            }
        })
    }

    override suspend fun stopIndoorLocationTracking() {
        indoorLocationManager?.destroy()
        indoorLocationManager = null
    }
}

// Helper function for dependency injection module
fun provideLocationClient(context: Context): FusedLocationProviderClient {
    return LocationServices.getFusedLocationProviderClient(context)
}