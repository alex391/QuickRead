package com.alexleute.quickread.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexleute.quickread.OptionsStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.toJavaDuration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Read(text: String, back: () -> Unit, options: () -> Unit, optionsStorage: OptionsStorage) {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
            },
            actions = {
                IconButton(onClick = {
                    options()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings"
                    )
                }
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
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Remove control characters, they're jarring to have on the screen:
            val filteredText = text.replace(regex = Regex("\\p{Cc}"), replacement = " ")
            val split: MutableList<String> =
                mutableListOf(
                    "3",
                    "2",
                    "1"
                ) // TODO this could be inserted also on resume (or done some other way, this is a bit temporary)
            split.addAll(filteredText.split(" "))

            var index: Int by remember { mutableIntStateOf(0) }
            var pause: Boolean by remember { mutableStateOf(false) }
            var scroll: Boolean by remember { mutableStateOf(false) }
            val scrollPosition: LazyListState by remember { mutableStateOf(LazyListState(0)) }
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(index, pause) {
                if (index >= split.size - 1) {
                    return@LaunchedEffect
                }
                if (pause) {
                    return@LaunchedEffect
                }
                if (optionsStorage.longerWordsMoreTime) {
                    val scale: Double =
                        split[index].length.toDouble() / 6.0 // Average word length is around 6
                    val scaledDelay: Duration = optionsStorage.delay.times(scale = scale)
                    delay(scaledDelay.toJavaDuration())
                } else {
                    delay(optionsStorage.delay.toJavaDuration())
                }
                index++
            }
            if (scroll) {
                LazyRow(state = scrollPosition) {
                    items(split.size) { i ->
                        Text(
                            split[i],
                            Modifier.padding(16.dp),
                            fontSize = optionsStorage.fontSize.sp
                        )
                    }
                }
            } else {
                Text(
                    split[index],
                    Modifier.padding(16.dp),
                    fontSize = optionsStorage.fontSize.sp
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        index = max(0, index - 1)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Pause",
                    )
                }
                Button(
                    onClick = {
                        if (!scroll) {
                            pause = !pause
                        }
                    },
                ) {
                    if (pause) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = "Pause",
                        )
                    }
                }
                Button(
                    onClick = {
                        index = min(split.size - 1, index + 1)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Pause",
                    )
                }
            }
            Button(
                onClick = {
                    scroll = !scroll
                    if (scroll) {
                        pause = true
                        coroutineScope.launch {
                            scrollPosition.scrollToItem(index)
                        }
                    } else {
                        index = scrollPosition.firstVisibleItemIndex
                    }
                },
            ) {
                if (scroll) {
                    Text("Normal mode")
                } else {
                    Text("Scroll mode")
                }
            }

        }
    }
}