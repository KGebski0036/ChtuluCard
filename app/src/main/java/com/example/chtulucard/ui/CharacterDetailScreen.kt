package com.example.chtulucard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chtulucard.data.CharacterEntity

enum class CharacterDetailTab {
    Info,
    Skills,
    Inventory,
    Notes
}

@Composable
fun CharacterDetailScreen(
    sessionName: String,
    character: CharacterEntity?,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(CharacterDetailTab.Info) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFEFEAF7)) {
                NavigationBarItem(
                    selected = selectedTab == CharacterDetailTab.Info,
                    onClick = { selectedTab = CharacterDetailTab.Info },
                    icon = { Icon(Icons.Filled.Info, contentDescription = "Info") },
                    label = { Text("Info") }
                )
                NavigationBarItem(
                    selected = selectedTab == CharacterDetailTab.Skills,
                    onClick = { selectedTab = CharacterDetailTab.Skills },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Skills") },
                    label = { Text("Skills") }
                )
                NavigationBarItem(
                    selected = selectedTab == CharacterDetailTab.Inventory,
                    onClick = { selectedTab = CharacterDetailTab.Inventory },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Inventory") },
                    label = { Text("Inventory") }
                )
                NavigationBarItem(
                    selected = selectedTab == CharacterDetailTab.Notes,
                    onClick = { selectedTab = CharacterDetailTab.Notes },
                    icon = { Icon(Icons.Filled.Edit, contentDescription = "Notes") },
                    label = { Text("Notes") }
                )
            }
        }
    ) { paddingValues ->
        if (character == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Character not found",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8DDF5)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(
                            id = CharacterAvatarCatalog.drawableResIdForKey(character.avatarKey)
                        ),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = character.name,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        color = Color.Black
                    )
                    Text(
                        text = "${character.occupationName.ifBlank { "Investigator" }}",
                        fontSize = 32.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF2F2F2F)
                    )
                    Text(
                        text = "${character.sex} ${character.age}",
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF2F2F2F)
                    )
                    Text(
                        text = "From: ${character.placeOfBirth}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF3C3C3C)
                    )
                    Text(
                        text = "Session: $sessionName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5A418A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                CharacterDetailTab.Info -> CharacterInfoTab(character = character)
                CharacterDetailTab.Skills -> CharacterSkillsTab(character = character)
                CharacterDetailTab.Inventory -> CharacterInventoryTab(character = character)
                CharacterDetailTab.Notes -> CharacterNotesTab(character = character)
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun CharacterInfoTab(character: CharacterEntity) {
    val rows = listOf(
        "Sanity" to character.power,
        "HP" to ((character.constitution + character.size) / 10),
        "MP" to (character.power / 5),
        "Luck" to ((character.power + character.intelligence) / 2),
        "STR" to character.strength,
        "CON" to character.constitution,
        "SIZ" to character.size,
        "DEX" to character.dexterity,
        "APP" to character.appearance,
        "EDU" to character.education,
        "POW" to character.power,
        "INT" to character.intelligence,
        "MOVE" to character.move
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { (label, value) ->
            DetailStatRow(label = label, value = value.toString())
        }
        DetailStatRow(label = "Domicile", value = character.domicile)
    }
}

@Composable
private fun CharacterSkillsTab(character: CharacterEntity) {
    val occupation = remember(character.occupationSkillsJson) {
        CharacterSkillDataRepository.decodeAllocation(character.occupationSkillsJson)
    }
    val personal = remember(character.personalSkillsJson) {
        CharacterSkillDataRepository.decodeAllocation(character.personalSkillsJson)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Occupation skills",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        if (occupation.isEmpty()) {
            Text("No occupation skills assigned.")
        } else {
            occupation.entries.sortedBy { it.key }.forEach { (skill, points) ->
                DetailStatRow(label = skill, value = points.ifBlank { "-" })
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Personal skills",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        if (personal.isEmpty()) {
            Text("No personal skills assigned.")
        } else {
            personal.entries.sortedBy { it.key }.forEach { (skill, points) ->
                DetailStatRow(label = skill, value = points.ifBlank { "-" })
            }
        }
    }
}

@Composable
private fun CharacterInventoryTab(character: CharacterEntity) {
    val inventoryMap = remember(character.inventoryJson) {
        CharacterSkillDataRepository.decodeAllocation(character.inventoryJson)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (inventoryMap.isEmpty()) {
            Text("Inventory is empty.")
        } else {
            inventoryMap.entries.sortedBy { it.key }.forEach { (item, amount) ->
                DetailStatRow(label = item, value = amount.ifBlank { "1" })
            }
        }
    }
}

@Composable
private fun CharacterNotesTab(character: CharacterEntity) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (character.notesText.isBlank()) {
            Text("No notes yet.")
        } else {
            Text(
                text = character.notesText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun DetailStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F0F7))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontStyle = FontStyle.Italic,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF4E4E4E)
        )
    }
}
