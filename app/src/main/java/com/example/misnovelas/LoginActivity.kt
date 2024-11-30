package com.example.misnovelas

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.misnovelas.ui.theme.misnovelasTheme

class LoginActivity : ComponentActivity() {
    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = UserDatabaseHelper(this)
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

        setContent {
            misnovelasTheme(darkTheme = false) { // Always light mode for login
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun LoginScreen(modifier: Modifier = Modifier) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val context = LocalContext.current

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val user = loginUser(context, username, password)
                if (user != null) {
                    with(sharedPreferences.edit()) {
                        putBoolean("dark_mode", user.darkMode)
                        putString("current_user", user.username)
                        apply()
                    }
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Iniciar Sesión")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val success = registerUser(context, username, password)
                if (success) {
                    Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Registrarse")
            }
        }
    }

    private fun loginUser(context: Context, username: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserDatabaseHelper.TABLE_USERS,
            arrayOf(UserDatabaseHelper.COLUMN_ID, UserDatabaseHelper.COLUMN_DARK_MODE),
            "${UserDatabaseHelper.COLUMN_USERNAME} = ? AND ${UserDatabaseHelper.COLUMN_PASSWORD} = ?",
            arrayOf(username, password),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ID))
            val darkMode = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_DARK_MODE)) == 1
            cursor.close()
            User(id, username, password, darkMode)
        } else {
            cursor.close()
            null
        }
    }

    private fun registerUser(context: Context, username: String, password: String): Boolean {
        val db = dbHelper.writableDatabase
        val cursor = db.query(
            UserDatabaseHelper.TABLE_USERS,
            arrayOf(UserDatabaseHelper.COLUMN_ID),
            "${UserDatabaseHelper.COLUMN_USERNAME} = ?",
            arrayOf(username),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            cursor.close()
            false // Username already exists
        } else {
            cursor.close()
            val values = ContentValues().apply {
                put(UserDatabaseHelper.COLUMN_USERNAME, username)
                put(UserDatabaseHelper.COLUMN_PASSWORD, password)
                put(UserDatabaseHelper.COLUMN_DARK_MODE, false) // Default to light mode
            }
            val newRowId = db.insert(UserDatabaseHelper.TABLE_USERS, null, values)
            newRowId != -1L
        }
    }
}