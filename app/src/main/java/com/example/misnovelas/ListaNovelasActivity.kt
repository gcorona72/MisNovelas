package com.example.misnovelas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.misnovelas.ui.theme.misnovelasTheme

class ListaNovelasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            misnovelasTheme {
                val dbHelper = NovelaStorage(this)
                ListaNovelasScreen(dbHelper = dbHelper)
            }
        }
    }
}

@Composable
fun ListaNovelasScreen(modifier: Modifier = Modifier, dbHelper: NovelaStorage) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var año by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var valoracion by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var novelas by remember { mutableStateOf(listOf<Novela>()) }
    var nombreABorrar by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var mostrarFavoritas by remember { mutableStateOf(false) }
    var novelaSeleccionada by remember { mutableStateOf<Novela?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        novelas = dbHelper.getNovelas()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { showDialog = true }) {
            Text(text = "Añadir novela")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showDeleteDialog = true }) {
            Text(text = "Eliminar novela")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { mostrarFavoritas = !mostrarFavoritas }) {
            Text(text = if (mostrarFavoritas) "Mostrar todas las novelas" else "Mostrar novelas favoritas")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Volver a Main")
        }

        val novelasAMostrar = if (mostrarFavoritas) novelas.filter { it.isFavorite } else novelas

        val recyclerView = remember { RecyclerView(context) }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = NovelaAdapter(context, novelasAMostrar, { novela ->
            val success = dbHelper.updateFavoriteStatus(novela)
            if (success) {
                novelas = dbHelper.getNovelas()
            } else {
                mensajeError = "Error al actualizar el estado de favorito en SQLite"
                showErrorDialog = true
            }
        }, { novela ->
            novelaSeleccionada = novela
            showDetailsDialog = true
        })

        AndroidView({ recyclerView }, modifier = Modifier.fillMaxSize())

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Añadir novela") },
                text = {
                    Column {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") }
                        )
                        TextField(
                            value = año,
                            onValueChange = { año = it },
                            label = { Text("Año") }
                        )
                        TextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") }
                        )
                        TextField(
                            value = valoracion,
                            onValueChange = { valoracion = it },
                            label = { Text("Valoración") }
                        )
                        TextField(
                            value = latitud,
                            onValueChange = { latitud = it },
                            label = { Text("Latitud") }
                        )
                        TextField(
                            value = longitud,
                            onValueChange = { longitud = it },
                            label = { Text("Longitud") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        when {
                            nombre.isBlank() || año.isBlank() || descripcion.isBlank() || valoracion.isBlank() || latitud.isBlank() || longitud.isBlank() -> {
                                mensajeError = "Todos los campos deben estar completos"
                                showErrorDialog = true
                            }
                            !año.all { it.isDigit() } -> {
                                mensajeError = "El año no debe contener letras"
                                showErrorDialog = true
                            }
                            !valoracion.all { it.isDigit() } -> {
                                mensajeError = "La valoración no debe contener letras"
                                showErrorDialog = true
                            }
                            else -> {
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
                                novelas = dbHelper.getNovelas()
                                nombre = ""
                                año = ""
                                descripcion = ""
                                valoracion = ""
                                latitud = ""
                                longitud = ""
                                showDialog = false
                            }
                        }
                    }) {
                        Text("Añadir")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = "Eliminar novela") },
                text = {
                    Column {
                        TextField(
                            value = nombreABorrar,
                            onValueChange = { nombreABorrar = it },
                            label = { Text("Nombre de la novela a borrar") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val novelaAEliminar = novelas.find { it.nombre == nombreABorrar }
                        if (novelaAEliminar != null) {
                            val success = dbHelper.deleteNovela(novelaAEliminar.nombre)
                            if (success) {
                                novelas = dbHelper.getNovelas()
                                mensajeError = ""
                            } else {
                                mensajeError = "Error al eliminar la novela de SQLite"
                                showErrorDialog = true
                            }
                        } else {
                            mensajeError = "No se ha encontrado ninguna novela con ese nombre"
                            showErrorDialog = true
                        }
                        nombreABorrar = ""
                        showDeleteDialog = false
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text(text = "Error") },
                text = { Text(text = mensajeError) },
                confirmButton = {
                    Button(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        if (showDetailsDialog && novelaSeleccionada != null) {
            AlertDialog(
                onDismissRequest = { showDetailsDialog = false },
                title = { Text(text = "Detalles de la novela") },
                text = {
                    Column {
                        Text(text = "Nombre: ${novelaSeleccionada?.nombre}")
                        Text(text = "Año: ${novelaSeleccionada?.año}")
                        Text(text = "Descripción: ${novelaSeleccionada?.descripcion}")
                        Text(text = "Valoración: ${novelaSeleccionada?.valoracion}")
                        Text(text = "Favorita: ${if (novelaSeleccionada?.isFavorite == true) "Sí" else "No"}")
                    }
                },
                confirmButton = {
                    Button(onClick = { showDetailsDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}