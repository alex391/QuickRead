package com.alexleute.quickread.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexleute.quickread.OptionsStorage
import kotlinx.coroutines.time.delay
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
            // TODO: what to split on could come from settings

            var index: Int by remember { mutableIntStateOf(0) }
            LaunchedEffect(index) {
                if (index >= split.size - 1) {
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
            Text(
                split[index],
                Modifier.padding(16.dp),
                fontSize = optionsStorage.fontSize
            ) // TODO: Font size could be from settings

        }
    }
}