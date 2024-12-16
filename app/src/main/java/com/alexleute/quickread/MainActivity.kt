package com.alexleute.quickread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexleute.quickread.ui.ImportText
import com.alexleute.quickread.ui.Options
import com.alexleute.quickread.ui.Read
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
object ImportText

@Serializable
object Options

@Serializable
object Read

@Serializable
data class OptionsStorage(
    var delay: Duration = 0.2.seconds,
    var fontSize: Int = 60,
    var longerWordsMoreTime: Boolean = false
)

suspend fun readOptionsFromFlow(flow: Flow<String>): OptionsStorage {
    return Json.decodeFromString(flow.first())
}


class MainActivity : ComponentActivity() {
    private val dataStore by preferencesDataStore(name = "options")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var tempOptionsStorage: OptionsStorage
        val optionsFlow: Flow<String> = dataStore.data.map {
            it[stringPreferencesKey("options")] ?: ""
        }
        runBlocking {
            tempOptionsStorage = readOptionsFromFlow(optionsFlow)
        }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            var optionsStorage: OptionsStorage by remember { mutableStateOf(tempOptionsStorage) }
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
                    val coroutineScope = rememberCoroutineScope()
                    Options(back = {
                        navController.popBackStack()
                    }, optionsStorage, save = {
                        optionsStorage = it
                        coroutineScope.launch {
                            dataStore.edit { preferences ->
                                val optionsKey = stringPreferencesKey("options")
                                preferences[optionsKey] = Json.encodeToString(optionsStorage)
                            }
                        }
                    })
                }
            }
        }
    }
}
