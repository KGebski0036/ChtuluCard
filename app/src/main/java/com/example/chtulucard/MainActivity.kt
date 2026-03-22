package com.example.chtulucard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.chtulucard.data.AppDatabase
import com.example.chtulucard.ui.SessionScreen
import com.example.chtulucard.ui.SessionViewModel
import com.example.chtulucard.ui.theme.ChtuluCardTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chtulucard.ui.CharacterScreen
import com.example.chtulucard.ui.CharacterViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "chtulucard-database"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    private val sessionViewModel by viewModels<SessionViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SessionViewModel(db.sessionDao()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChtuluCardTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "sessions") {

                    composable("sessions") {
                        SessionScreen(
                            viewModel = sessionViewModel,
                            onSessionClick = { sessionId, sessionName ->
                                navController.navigate("characters/$sessionId/$sessionName")
                            }
                        )
                    }

                    composable(
                        route = "characters/{sessionId}/{sessionName}",
                        arguments = listOf(
                            navArgument("sessionId") { type = NavType.IntType },
                            navArgument("sessionName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                        val sessionName = backStackEntry.arguments?.getString("sessionName") ?: "Unknown Session"

                        val characterViewModel: CharacterViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    @Suppress("UNCHECKED_CAST")
                                    return CharacterViewModel(db.sessionDao(), sessionId) as T
                                }
                            }
                        )

                        CharacterScreen(
                            sessionName = sessionName,
                            viewModel = characterViewModel,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}