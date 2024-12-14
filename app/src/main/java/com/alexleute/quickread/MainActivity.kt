package com.alexleute.quickread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.alexleute.quickread.ui.theme.QuickReadTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickReadTheme {
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
                                TODO()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = null/* TODO */
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
                            "",
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .fillMaxWidth(1f), // TODO: actually center
                            onValueChange = {/* TODO: change value */ },
                            label = {
                                Text("Paste your text here")
                            }
                        )
                        TextField(
                            "",
                            modifier = Modifier
                                .fillMaxWidth(1f), // TODO: actually center
                            onValueChange = {/* TODO: Store state for later */ },
                            label = {
                                Text("Or paste a URL here")
                            }
                        )
                        Button(
                            onClick = {},
                            content = {
                                Text("Start reading!")
                            }
                        )
                    }
                }
            }
        }
    }
}
