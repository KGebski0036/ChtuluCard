package com.example.chtulucard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CharacterScreen(
    sessionName: String,
    viewModel: CharacterViewModel,
    onBackClick: () -> Unit,
    onCreateCharacterClick: () -> Unit,
    onCharacterClick: (Int) -> Unit
) {
    val characters by viewModel.characters.collectAsState(initial = emptyList())

    val lightPurple = Color(0xFFE8DDF5)
    val darkPurple = Color(0xFF5A418A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 32.dp) // Zmniejszony margines, aby zrobić miejsce na przycisk
    ) {
        // --- NOWY ELEMENT: Przycisk Wstecz ---
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go Back",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = sessionName,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose your Cthulu\ncharacter",
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 48.dp, end = 48.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(characters) { character ->
                    CharacterRow(
                        text = character.name,
                        iconBgColor = lightPurple,
                        iconColor = darkPurple,
                        avatarResId = CharacterAvatarCatalog.drawableResIdForKey(character.avatarKey),
                        onClick = { onCharacterClick(character.id) }
                    )
                }

                item {
                    CharacterRow(
                        text = "Add character",
                        iconBgColor = lightPurple,
                        iconColor = darkPurple,
                        onClick = onCreateCharacterClick
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterRow(
    text: String,
    iconBgColor: Color,
    iconColor: Color,
    avatarResId: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            if (avatarResId != null) {
                Image(
                    painter = painterResource(id = avatarResId),
                    contentDescription = "Character Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Character Avatar",
                    tint = iconColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = text,
            fontSize = 20.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Black
        )
    }
}