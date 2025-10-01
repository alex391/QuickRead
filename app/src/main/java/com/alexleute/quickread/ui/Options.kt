/*
    QuickRead
    Copyright (C) 2024, 2025 Alex Leute
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 ONLY.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.alexleute.quickread.OptionsStorage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
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
            val minWpm = 50.0
            val maxWpm = 1200.0
            var wpm: Double =
                usToWpm(optionsStorage.delay.inWholeMicroseconds) // Need to this with more precision than ms because for large WPMs the duration difference is less than 1ms
            var sliderPosition: Float by remember { mutableFloatStateOf(0.0f) }
            sliderPosition = scaleRangeToSlider(wpm, minWpm, maxWpm)
            sliderPosition =
                min(max(sliderPosition, 0f), 1f) // For sanity, but probably does nothing
            val roundedWpm: Int = round(wpm).toInt()

            var fontSizeText: String? by remember { mutableStateOf(null) }
            if (fontSizeText == null) {
                fontSizeText = optionsStorage.fontSize.toString()
            }

            Text("Words Per Minute: $roundedWpm")
            Slider(
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .align(alignment = Alignment.CenterHorizontally),
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    wpm = scaleSliderToRange(sliderPosition, minWpm, maxWpm)
                    val ms: Double = wpmToMs(wpm)
                    val delay: Duration = ms.milliseconds

                    val newOptionsStorage = optionsStorage.copy(delay = delay)
                    save(newOptionsStorage)
                },
                valueRange = 0f..1f
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Font Size: ")
                TextField(
                    value = fontSizeText ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        try {
                            fontSizeText = it
                            val newOptionsStorage =
                                optionsStorage.copy(fontSize = max(5, min(it.toInt(), 120)))
                            save(newOptionsStorage)
                        } catch (_: NumberFormatException) {
                            fontSizeText = it
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

fun usToWpm(us: Long): Double {
    return (1.0 / us) * 6e7
}

fun wpmToMs(wpm: Double): Double {
    return (1.0 / (wpm * (1.0 / 60.0) * (1.0 / 1000.0)))
}

/**
 * sliderPosition 0 maps to min, sliderPosition 1 maps to max
 */
fun scaleSliderToRange(sliderPosition: Float, min: Double, max: Double): Double {
    return (sliderPosition * (max - min)) + min
}

/**
 * Scale a number in a range, to float between 0 and 1
 */
fun scaleRangeToSlider(valueInRange: Double, min: Double, max: Double): Float {
    val movedDown = valueInRange - min
    return (movedDown / (max - min)).toFloat()
}

