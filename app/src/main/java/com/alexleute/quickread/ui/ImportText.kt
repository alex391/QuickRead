package com.alexleute.quickread.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.alexleute.quickread.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportText(
    startReading: () -> Unit,
    options: () -> Unit,
    text: String,
    updateText: (text: String) -> Unit
) {
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
            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                text,
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth(1f),
                onValueChange = updateText,
                label = {
                    Text("Paste your text here")
                }
            )
            Button(
                onClick = startReading,
                content = {
                    Text("Start reading!")
                }
            )
        }
    }
}