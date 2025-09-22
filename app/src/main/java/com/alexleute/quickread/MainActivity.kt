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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexleute.quickread.ui.ImportText
import com.alexleute.quickread.ui.Options
import com.alexleute.quickread.ui.Read
import kotlinx.coroutines.CoroutineScope
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
    val delay: Duration = 0.2.seconds,
    val fontSize: Int = 60,
    val longerWordsMoreTime: Boolean = false,
    val text: String = "", // When this is cleared, set index to 0 also
    val index: Int = 0,
)

suspend fun readOptionsFromFlow(flow: Flow<String>): OptionsStorage {
    return try {
        Json.decodeFromString(flow.first())
    } catch (_: Exception) {
        OptionsStorage()
    }
}

fun save(
    optionsStorage: OptionsStorage,
    coroutineScope: CoroutineScope,
    dataStore: DataStore<Preferences>
) {
    coroutineScope.launch {
        dataStore.edit { preferences ->
            val optionsKey = stringPreferencesKey("options")
            preferences[optionsKey] = Json.encodeToString(optionsStorage)
        }
    }
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
            var readText by remember { mutableStateOf(optionsStorage.text) }
            if (BuildConfig.DEBUG && readText == "") {
                readText =
                    "Aliquam commodo aliquet ultrices. Nulla varius elit tincidunt mi feugiat molestie. Suspendisse at dui id enim bibendum mattis. Sed eu. "
            }
            NavHost(
                navController = navController,
                startDestination = ImportText,
            ) {
                composable<ImportText> {
                    val coroutineScope = rememberCoroutineScope()
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
                            if (optionsStorage.text != readText) {
                                optionsStorage = optionsStorage.copy(text = readText, index = 0)
                                save(optionsStorage, coroutineScope, dataStore)
                            }
                        }
                    )
                }
                composable<Read> {
                    val coroutineScope = rememberCoroutineScope()
                    Read(
                        text = readText,
                        back = { navController.popBackStack() },
                        options = { navController.navigate(Options) },
                        optionsStorage = optionsStorage
                    ) {
                        optionsStorage = it
                        save(optionsStorage, coroutineScope, dataStore)
                    }
                }
                composable<Options> {
                    val coroutineScope = rememberCoroutineScope()
                    Options(back = {
                        navController.popBackStack()
                    }, optionsStorage) {
                        optionsStorage = it
                        save(optionsStorage, coroutineScope, dataStore)
                    }
                }
            }
        }
    }
}
