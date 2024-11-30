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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.misnovelas.ui.theme.misnovelasTheme

class UserListActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = UserDatabaseHelper(this)
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        setContent {
            misnovelasTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserListScreen(modifier = Modifier.padding(innerPadding), dbHelper = dbHelper)
                }
            }
        }
    }

    @Composable
    fun UserListScreen(modifier: Modifier = Modifier, dbHelper: UserDatabaseHelper) {
        val users by remember { mutableStateOf(dbHelper.getAllUsers()) }
        val context = LocalContext.current
        var showDeleteDialog by remember { mutableStateOf(false) }
        var usernameToDelete by remember { mutableStateOf("") }
        var passwordToDelete by remember { mutableStateOf("") }
        var deleteError by remember { mutableStateOf("") }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Lista de Usuarios Registrados")
            Spacer(modifier = Modifier.height(16.dp))
            users.forEach { user ->
                Text(text = "ID: ${user.id}, Usuario: ${user.username}, Contraseña: ${user.password}, Modo Oscuro: ${user.darkMode}")
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showDeleteDialog = true }) {
                Text(text = "Eliminar Usuario")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            }) {
                Text(text = "Volver a Main")
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = "Eliminar Usuario") },
                text = {
                    Column {
                        TextField(
                            value = usernameToDelete,
                            onValueChange = { usernameToDelete = it },
                            label = { Text("Username") }
                        )
                        TextField(
                            value = passwordToDelete,
                            onValueChange = { passwordToDelete = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                        if (deleteError.isNotEmpty()) {
                            Text(text = deleteError, color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val success = dbHelper.deleteUser(usernameToDelete, passwordToDelete)
                        if (success) {
                            deleteError = ""
                            showDeleteDialog = false
                            context.startActivity(Intent(context, UserListActivity::class.java))
                        } else {
                            deleteError = "Usuario o contraseña incorrectos"
                        }
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
    }
}