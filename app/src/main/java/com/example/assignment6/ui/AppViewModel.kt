/*  Assignment 6
    AppViewModel.kt

    Nicholas Herman / hermnich@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.example.assignment6.data.DataSource
import com.example.assignment6.data.UiState
import com.example.assignment6.model.Clue
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class AppViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication()

    private val _uiState = MutableStateFlow(
        UiState(
            hasLocationAccess = ContextCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onPermissionChange(permission: String, isGranted: Boolean) {
        when (permission) {
            ACCESS_FINE_LOCATION -> {
                _uiState.update { currentState ->
                    currentState.copy(hasLocationAccess = isGranted)
                }
            }
            else -> {
                Log.e("Permission change", "Unexpected permission: $permission")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun checkLocation(clue: Clue) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false
        }).addOnSuccessListener { location: Location? ->
            location ?: return@addOnSuccessListener

            _uiState.update { currentState ->
                currentState.copy(
                    distance = haversine(location.latitude, location.longitude, clue)
                )
            }
        }
    }

    private fun haversine(latitude: Double, longitude: Double, clue: Clue): Double {
        val earthRadiusKm = 6372.8

        val dLat = Math.toRadians(latitude - clue.latitude)
        val dLon = Math.toRadians(longitude - clue.longitude)
        val originLat = Math.toRadians(clue.latitude)
        val destinationLat = Math.toRadians(latitude)

        val a = sin(dLat / 2).pow(2.toDouble()) + sin(dLon / 2).pow(2.toDouble()) * cos(originLat) * cos(destinationLat)
        val c = 2 * asin(sqrt(a))
        return earthRadiusKm * c
    }

    fun clearDistance() {
        _uiState.update { currentState ->
            currentState.copy(
                distance = null
            )
        }
    }

    fun updateClue() {
        _uiState.update { currentState ->
            val newIndex = currentState.clueIndex + 1
            if (newIndex == DataSource.lastClueIndex) {
                currentState.copy(
                    clueIndex = newIndex,
                    clue = DataSource.clues[newIndex],
                    isLastClue = true
                )
            } else {
                currentState.copy(
                    clueIndex = newIndex,
                    clue = DataSource.clues[newIndex],
                )
            }
        }
    }

    fun reset() {
        pauseTimer()
        clearTimer()
        _uiState.update { currentState ->
            currentState.copy(
                clueIndex = 0,
                clue = DataSource.clues.first()
            )
        }
    }

    fun pauseTimer() {
        _uiState.update { currentState ->
            currentState.copy(
                timerPaused = true
            )
        }
    }

    fun startTimer() {
        _uiState.update { currentState ->
            currentState.copy(
                timerPaused = false
            )
        }
    }

    private fun clearTimer() {
        _uiState.update { currentState ->
            currentState.copy(
                time = 0L
            )
        }
    }

    fun updateTimer() {
        _uiState.update { currentState ->
            if (!currentState.timerPaused) {
                currentState.copy(
                    time = currentState.time + 1
                )
            } else {
                currentState.copy()
            }
        }
    }
}