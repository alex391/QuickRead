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
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.time.delay
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Read(text: String, back: () -> Unit, options: () -> Unit) {
    // TODO: This is very placeholder
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
                ) // TODO this could be inserted also on resume
            split.addAll(filteredText.split(" "))
            // TODO: what to split on could come from settings

            var index: Int by remember { mutableIntStateOf(0) }
            val delay = .2.seconds.toJavaDuration() // TODO: This also should come from settings
            LaunchedEffect(index) {
                if (index >= split.size - 1) {
                    return@LaunchedEffect
                }
                delay(delay)
                index++
            }
            Text(
                split[index],
                Modifier.padding(16.dp),
                fontSize = 60.sp
            ) // TODO: Font size could be from settings

        }
    }
}