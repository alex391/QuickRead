package com.alexleute.quickread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexleute.quickread.ui.ImportText
import com.alexleute.quickread.ui.Read
import kotlinx.serialization.Serializable

@Serializable
object ImportText // TODO: This should probably also be a data class

@Serializable
object Options

@Serializable
object Read // TODO: This should be a data class instead

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = ImportText,
            ) {
                composable<ImportText> {
                    ImportText(startReading = {
                        navController.navigate(Read)
                    }, options = {
                        navController.navigate(Options)
                    })
                }
                composable<Read> {
                    Read(
                        text = TODO(),
                        back = { navController.popBackStack() },
                        options = { navController.navigate(Options) }
                    )
                }
                composable<Options> {
                    Text("This is placeholder text for Options")
                }
            }
        }
    }
}
