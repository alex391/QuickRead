package com.alexleute.quickread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
object Read

data class OptionsStorage(
    var delay: Duration = 0.2.seconds,
    var fontSize: TextUnit = 60.sp,
    var longerWordsMoreTime: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var optionsStorage: OptionsStorage by remember { mutableStateOf(OptionsStorage()) } // TODO: persistent storage
            var readText by remember { mutableStateOf("") }
            // TODO: keep track of text position if you're going to the settings and back to the reading (so you don't loose your place)
            NavHost(
                navController = navController,
                startDestination = ImportText,
            ) {
                composable<ImportText> {
                    ImportText(
                        startReading = {
                            navController.navigate(Read)
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
                    Read(
                        text = readText,
                        back = { navController.popBackStack() },
                        options = { navController.navigate(Options) },
                        optionsStorage = optionsStorage
                    )
                }
                composable<Options> {
                    Options(back = {
                        navController.popBackStack()
                    }, optionsStorage, save = {
                        optionsStorage = it
                    })
                }
            }
        }
    }
}
