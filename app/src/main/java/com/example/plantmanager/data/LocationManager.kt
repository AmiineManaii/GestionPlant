package com.example.plantmanager.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double> {
        if (!hasLocationPermission()) {

            return DEFAULT_LOCATION
        }

        return try {
            val location = fusedLocationClient.lastLocation.await()

            if (location != null) {
                Pair(location.latitude, location.longitude)
            } else {
                DEFAULT_LOCATION
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DEFAULT_LOCATION
        }
    }

    companion object {

        val DEFAULT_LOCATION = Pair(36.8065, 10.1815)
    }
}