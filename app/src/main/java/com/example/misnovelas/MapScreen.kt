package com.example.misnovelas

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun MapScreen(dbHelper: NovelaStorage) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(mapView) {
        mapView.onCreate(null)
        mapView.getMapAsync { map ->
            googleMap = map
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    }
                }
            } else {
                ActivityCompat.requestPermissions(context as ComponentActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }

            val novelas = dbHelper.getNovelas()
            novelas.forEach { novela ->
                if (novela.latitud != null && novela.longitud != null) {
                    val location = LatLng(novela.latitud, novela.longitud)
                    map.addMarker(MarkerOptions().position(location).title(novela.nombre))
                }
            }
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDestroy()
        }
    }

    AndroidView({ mapView }, modifier = Modifier.fillMaxSize())
}