package com.example.misnovelas

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun MapScreen(dbHelper: NovelaStorage, navController: NavController) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Solicitar permisos en tiempo de ejecución
    val permissionGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!permissionGranted) {
        val activity = context as? ComponentActivity
        activity?.let {
            ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    LaunchedEffect(mapView) {
        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync { map ->
            googleMap = map
            if (permissionGranted) {
                map.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    }
                }
            }
            // Agregar marcadores para las novelas
            val novelas = dbHelper.getNovelas()
            novelas.forEach { novela ->
                novela.latitud?.let { lat ->
                    novela.longitud?.let { lng ->
                        val location = LatLng(lat, lng)
                        map.addMarker(MarkerOptions().position(location).title(novela.nombre))
                    }
                }
            }
        }
    }

    DisposableEffect(mapView) {
        onDispose { mapView.onDestroy() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Mapa ocupa la mayor parte del espacio
        AndroidView({ mapView }, modifier = Modifier.weight(1f))

        // Botón para regresar
        Button(
            onClick = { navController.navigate("main") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Volver al Menú Principal")
        }
    }
}
