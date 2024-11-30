package com.example.misnovelas

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.misnovelas.Novela
import com.example.misnovelas.UserDatabaseHelper

@Composable
fun AddNovelScreen(navController: NavController, dbHelper: UserDatabaseHelper, sharedPreferences: SharedPreferences) {
    var nombre by remember { mutableStateOf("") }
    var año by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la novela") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = año,
            onValueChange = { año = it },
            label = { Text("Año") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nombre.isNotBlank() && año.isNotBlank() && descripcion.isNotBlank() && año.all { it.isDigit() }) {
                val userId = dbHelper.getCurrentUserId(sharedPreferences)
                dbHelper.addNovelaForUser(
                    userId,
                    Novela(
                        nombre = nombre,
                        año = año.toInt(),
                        descripcion = descripcion,
                        valoracion = 0.0,
                        isFavorite = false
                    )
                )
                navController.navigate("novelList")
            } else {
                errorMessage = "Por favor, completa todos los campos correctamente."
            }
        }) {
            Text("Añadir Novela")
        }
        if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}