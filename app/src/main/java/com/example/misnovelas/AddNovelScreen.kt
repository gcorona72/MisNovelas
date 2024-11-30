package com.example.misnovelas

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AddNovelaScreen(dbHelper: NovelaStorage) {
    var nombre by remember { mutableStateOf("") }
    var año by remember { mutableStateOf(0) }
    var descripcion by remember { mutableStateOf("") }
    var valoracion by remember { mutableStateOf(0.0) }
    var isFavorite by remember { mutableStateOf(false) }
    var latitud by remember { mutableStateOf(0.0) }
    var longitud by remember { mutableStateOf(0.0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Añadir Novela")
        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        TextField(value = año.toString(), onValueChange = { año = it.toIntOrNull() ?: 0 }, label = { Text("Año") })
        TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
        TextField(value = valoracion.toString(), onValueChange = { valoracion = it.toDoubleOrNull() ?: 0.0 }, label = { Text("Valoración") })

        // Agregar campos para latitud y longitud
        TextField(value = latitud.toString(), onValueChange = { latitud = it.toDoubleOrNull() ?: 0.0 }, label = { Text("Latitud") })
        TextField(value = longitud.toString(), onValueChange = { longitud = it.toDoubleOrNull() ?: 0.0 }, label = { Text("Longitud") })

        Button(onClick = {
            val novela = Novela(nombre, año, descripcion, valoracion, isFavorite, latitud, longitud)
            dbHelper.saveNovela(novela)
        }) {
            Text("Guardar Novela")
        }
    }


}
