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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CharacterStatsScreen(
    initialStats: CharacterStatsData?,
    onBackClick: () -> Unit,
    onNextClick: (CharacterStatsData) -> Unit
) {
    var strength by rememberSaveable { mutableStateOf(initialStats?.strength?.toString().orEmpty()) }
    var constitution by rememberSaveable { mutableStateOf(initialStats?.constitution?.toString().orEmpty()) }
    var size by rememberSaveable { mutableStateOf(initialStats?.size?.toString().orEmpty()) }
    var dexterity by rememberSaveable { mutableStateOf(initialStats?.dexterity?.toString().orEmpty()) }
    var appearance by rememberSaveable { mutableStateOf(initialStats?.appearance?.toString().orEmpty()) }
    var education by rememberSaveable { mutableStateOf(initialStats?.education?.toString().orEmpty()) }
    var power by rememberSaveable { mutableStateOf(initialStats?.power?.toString().orEmpty()) }
    var intelligence by rememberSaveable { mutableStateOf(initialStats?.intelligence?.toString().orEmpty()) }
    var move by rememberSaveable { mutableStateOf(initialStats?.move?.toString().orEmpty()) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                        val stats = linkedMapOf(
                            "STR" to strength.trim().toIntOrNull(),
                            "CON" to constitution.trim().toIntOrNull(),
                            "SIZ" to size.trim().toIntOrNull(),
                            "DEX" to dexterity.trim().toIntOrNull(),
                            "APP" to appearance.trim().toIntOrNull(),
                            "EDU" to education.trim().toIntOrNull(),
                            "POW" to power.trim().toIntOrNull(),
                            "INT" to intelligence.trim().toIntOrNull(),
                            "MOVE" to move.trim().toIntOrNull()
                        )
                        val invalidField = stats.entries.firstOrNull { it.value == null }?.key
                        if (invalidField != null) {
                            errorMessage = "$invalidField must be a number."
                            return@Button
                        }

                        errorMessage = null
                        onNextClick(
                            CharacterStatsData(
                                strength = stats.getValue("STR")!!,
                                constitution = stats.getValue("CON")!!,
                                size = stats.getValue("SIZ")!!,
                                dexterity = stats.getValue("DEX")!!,
                                appearance = stats.getValue("APP")!!,
                                education = stats.getValue("EDU")!!,
                                power = stats.getValue("POW")!!,
                                intelligence = stats.getValue("INT")!!,
                                move = stats.getValue("MOVE")!!
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DDF5), contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
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
                text = "Features",
                fontSize = 28.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(18.dp))

            CharacterStatField(label = "STR", value = strength, onValueChange = {
                strength = it
                errorMessage = null
            })
            CharacterStatField(label = "CON", value = constitution, onValueChange = {
                constitution = it
                errorMessage = null
            })
            CharacterStatField(label = "SIZ", value = size, onValueChange = {
                size = it
                errorMessage = null
            })
            CharacterStatField(label = "DEX", value = dexterity, onValueChange = {
                dexterity = it
                errorMessage = null
            })
            CharacterStatField(label = "APP", value = appearance, onValueChange = {
                appearance = it
                errorMessage = null
            })
            CharacterStatField(label = "EDU", value = education, onValueChange = {
                education = it
                errorMessage = null
            })
            CharacterStatField(label = "POW", value = power, onValueChange = {
                power = it
                errorMessage = null
            })
            CharacterStatField(label = "INT", value = intelligence, onValueChange = {
                intelligence = it
                errorMessage = null
            })
            CharacterStatField(label = "MOVE", value = move, onValueChange = {
                move = it
                errorMessage = null
            })

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
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

@Composable
private fun CharacterStatField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp)
    )
}
