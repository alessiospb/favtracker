package com.example.productcardsapp  // Update this if your package name is different

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.productcardsapp.ui.theme.ProductCardsAppTheme
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "main") {
                composable("listCards") {
                    ListCardsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }


                composable("main") {
                    MainScreen(
                        onAddCard = { navController.navigate("addCard") },
                        onListCards = { navController.navigate("listCards") },
                        onSearch = { query -> println("Searching: $query") },
                        onFilterChange = { type -> println("Filter type: $type") }
                    )
                }

                composable("addCard") {
                    AddCardScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
