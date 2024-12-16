package com.alexleute.quickread.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.alexleute.quickread.OptionsStorage
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Options(back: () -> Unit, optionsStorage: OptionsStorage, save: (OptionsStorage) -> Unit) {

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
            },
            actions = {
            },
            navigationIcon = {
                IconButton(onClick = {
                    back()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Settings"
                    )
                }

            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            var wpm: Float =
                msToWpm(optionsStorage.delay.inWholeMilliseconds.toFloat())
            Text("Words Per Minute: $wpm")
            Slider(
                value = wpm,
                onValueChange = {
                    wpm = it
                    val delay: Duration = (wpmToMs(wpm)).toDouble().milliseconds
                    val newOptionsStorage = optionsStorage.copy(delay = delay)
                    save(newOptionsStorage)
                },
                valueRange = 50f..1200f
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Font Size: ")
                TextField(
                    value = optionsStorage.fontSize.value.toInt().toString(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        try {
                            val newOptionsStorage =
                                optionsStorage.copy(fontSize = min(it.toInt(), 120).sp)
                            save(newOptionsStorage)
                        } catch (n: NumberFormatException) {
                            /* no-op */ // Just don't crash
                        }
                    })
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Longer words stay on the screen longer?")
                Checkbox(
                    checked = optionsStorage.longerWordsMoreTime,
                    onCheckedChange = {
                        val newOptionsStorage = optionsStorage.copy(longerWordsMoreTime = it)
                        save(newOptionsStorage)
                    },
                )
            }
        }
    }
}

fun msToWpm(ms: Float): Float {
    return 1f / (ms * (1f / 1000f) * (1f / 60f))
}

fun wpmToMs(wpm: Float): Float {
    return 1f / (wpm * (1f / 60f) * (1f / 1000f))
}