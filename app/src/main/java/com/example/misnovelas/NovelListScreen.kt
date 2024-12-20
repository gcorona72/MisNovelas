package com.example.misnovelas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun NovelListScreen(navController: NavHostController, dbHelper: NovelaStorage) {
    var novelas by remember { mutableStateOf(listOf<Novela>()) }

    // Cargar datos de la base de datos
    LaunchedEffect(Unit) {
        novelas = dbHelper.getNovelas()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lista de Novelas", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(novelas) { novela ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Título: ${novela.nombre}")
                        Text("Año: ${novela.año}")
                        Text("Descripción: ${novela.descripcion}")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Volver al Menú Principal")
        }
    }
}