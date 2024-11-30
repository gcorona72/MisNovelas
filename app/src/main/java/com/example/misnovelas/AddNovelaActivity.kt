package com.example.misnovelas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.misnovelas.ui.theme.misnovelasTheme

class AddNovelaActivity : ComponentActivity() {
    private lateinit var dbHelper: NovelaStorage
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = NovelaStorage(this)
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

        setContent {
            misnovelasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddNovelaScreen(modifier = Modifier.padding(innerPadding), dbHelper = dbHelper)
                }
            }
        }
    }

    @Composable
    fun AddNovelaScreen(modifier: Modifier = Modifier, dbHelper: NovelaStorage) {
        var nombre by remember { mutableStateOf("") }
        var año by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var valoracion by remember { mutableStateOf("") }
        var latitud by remember { mutableStateOf("") }
        var longitud by remember { mutableStateOf("") }
        val context = LocalContext.current

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = año,
                onValueChange = { año = it },
                label = { Text("Año") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = valoracion,
                onValueChange = { valoracion = it },
                label = { Text("Valoración") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = latitud,
                onValueChange = { latitud = it },
                label = { Text("Latitud") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = longitud,
                onValueChange = { longitud = it },
                label = { Text("Longitud") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (nombre.isNotBlank() && año.isNotBlank() && descripcion.isNotBlank() && valoracion.isNotBlank() && latitud.isNotBlank() && longitud.isNotBlank()) {
                    val nuevaNovela = Novela(
                        nombre = nombre,
                        año = año.toInt(),
                        descripcion = descripcion,
                        valoracion = valoracion.toDouble(),
                        isFavorite = false,
                        latitud = latitud.toDouble(),
                        longitud = longitud.toDouble()
                    )
                    dbHelper.saveNovela(nuevaNovela)
                    Toast.makeText(context, "Novela añadida", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    Toast.makeText(context, "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Añadir Novela")
            }
        }
    }
}