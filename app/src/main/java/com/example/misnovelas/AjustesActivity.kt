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
import com.example.misnovelas.ui.theme.misnovelasTheme

class AjustesActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        dbHelper = UserDatabaseHelper(this)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        setContent {
            misnovelasTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AjustesScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun AjustesScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("dark_mode", false)) }
        val currentUser = sharedPreferences.getString("current_user", "") ?: ""

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Ajustes")
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Modo Oscuro")
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = {
                        isDarkMode = it
                        with(sharedPreferences.edit()) {
                            putBoolean("dark_mode", it)
                            apply()
                        }
                        dbHelper.updateUserDarkMode(currentUser, it) // Actualiza la base de datos
                        recreate() // Recreate activity to apply theme change
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            }) {
                Text(text = "Volver a Main")
            }
        }
    }
}