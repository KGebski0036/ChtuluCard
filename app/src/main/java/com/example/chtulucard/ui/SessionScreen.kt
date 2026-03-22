package com.example.chtulucard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chtulucard.ui.SessionViewModel

@Composable
fun SessionScreen(
    viewModel: SessionViewModel,
    onSessionClick: (Int, String) -> Unit
    ) {
    // Collect the database state safely
    val sessions by viewModel.sessions.collectAsState()

    // State for the "New Session" popup
    var showDialog by remember { mutableStateOf(false) }
    var newSessionName by remember { mutableStateOf("") }

    val darkRedColor = Color(0xFF4A2A2A) // Approximate color from your design

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = darkRedColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Session")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "ChtuluCard",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Choose your Chtulu\nsession",
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Session List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(sessions) { session ->
                    SessionCard(name = session.name, darkRedColor = darkRedColor) {
                        onSessionClick(session.id, session.name)
                    }
                }
            }
        }

        // Popup Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("New Session") },
                text = {
                    OutlinedTextField(
                        value = newSessionName,
                        onValueChange = { newSessionName = it },
                        label = { Text("Session Name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addSession(newSessionName)
                        showDialog = false
                        newSessionName = "" // reset
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SessionCard(name: String, darkRedColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(250.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(darkRedColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = Color.White,
            fontStyle = FontStyle.Italic,
            fontSize = 18.sp
        )
    }
}