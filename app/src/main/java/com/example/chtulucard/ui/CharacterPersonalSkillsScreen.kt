package com.example.chtulucard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CharacterPersonalSkillsScreen(
    statsData: CharacterStatsData,
    initialAllocationJson: String,
    onBackClick: () -> Unit,
    onSaveClick: (personalSkillsJson: String) -> Unit
) {
    val context = LocalContext.current
    val skills = remember { CharacterSkillDataRepository.loadSkills(context) }
    val initialAllocation = remember(initialAllocationJson) {
        CharacterSkillDataRepository.decodeAllocation(initialAllocationJson)
    }
    val skillValues = remember {
        mutableStateMapOf<String, String>().apply {
            skills.forEach { skill ->
                put(skill.name, initialAllocation[skill.name].orEmpty())
            }
        }
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val personalPoints = statsData.education * 4

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .bottomBarInsets()
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DDF5), contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Back")
                }

                Button(
                    onClick = {
                        val invalid = skills.firstOrNull {
                            val value = skillValues[it.name].orEmpty().trim()
                            value.isNotEmpty() && value.toIntOrNull() == null
                        }
                        if (invalid != null) {
                            errorMessage = "Invalid points for ${invalid.name}."
                            return@Button
                        }

                        errorMessage = null
                        onSaveClick(
                            CharacterSkillDataRepository.encodeAllocation(
                                skills.associate { skill ->
                                    skill.name to skillValues[skill.name].orEmpty().trim()
                                }
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DDF5), contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Save")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Create character",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Personal points: $personalPoints",
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(18.dp))

            skills.forEach { skill ->
                OutlinedTextField(
                    value = skillValues[skill.name].orEmpty(),
                    onValueChange = {
                        skillValues[skill.name] = it
                        errorMessage = null
                    },
                    label = { Text(skill.name) },
                    placeholder = { Text(skill.defaultValue.toString()) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
