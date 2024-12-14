package com.example.misnovelas

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.misnovelas.ui.theme.misnovelasTheme

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        setContent {
            misnovelasTheme {
                val dbHelper = NovelaStorage(this)
                MainScreen(dbHelper = dbHelper, sharedPreferences = sharedPreferences)
            }
        }
    }
}

@Composable
fun MainScreen(dbHelper: NovelaStorage, sharedPreferences: SharedPreferences) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") { MainMenu(navController) }
        composable("novelList") { NovelListScreen(navController, dbHelper) }
        composable("addNovel") { AddNovelaScreen(dbHelper, navController) }
        composable("map") { MapScreen(dbHelper) }
    }
}

@Composable
fun MainMenu(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("novelList") }) {
            Text("Ver Lista de Novelas")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("addNovel") }) {
            Text("Añadir Novela")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("map") }) { // Botón para navegar a MapScreen
            Text("Ver Mapa de Novelas")
        }
    }
}