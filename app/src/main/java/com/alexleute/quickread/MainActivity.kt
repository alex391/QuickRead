package com.alexleute.quickread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alexleute.quickread.ui.ImportText
import com.alexleute.quickread.ui.Options
import com.alexleute.quickread.ui.Read
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
object ImportText

@Serializable
object Options

@Serializable
data class Read(val text: String)

data class OptionsStorage(var delay: Duration = 0.2.seconds)

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
                    var readText by remember { mutableStateOf("") }
                    ImportText(
                        startReading = {
                            navController.navigate(Read(readText)) // TODO should be the text from the textbox instead
                        },
                        options = {
                            navController.navigate(Options)
                        },
                        text = readText,
                        updateText = {
                            readText = it
                        }
                    )
                }
                composable<Read> {
                    val args = it.toRoute<Read>()
                    Read(
                        text = args.text,
                        back = { navController.popBackStack() },
                        options = { navController.navigate(Options) },
                        optionsStorage = OptionsStorage() // TODO: placeholder
                    )
                }
                composable<Options> {
                    var optionsStorage: OptionsStorage by remember { mutableStateOf(OptionsStorage()) }
                    Options(back = {

                    }, optionsStorage, save = {
                        optionsStorage = it

                    })
                }
            }
        }
    }
}
