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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterOccupationSkillsScreen(
    statsData: CharacterStatsData,
    initialOccupationName: String,
    initialAllocationJson: String,
    onBackClick: () -> Unit,
    onNextClick: (occupationName: String, allocationJson: String) -> Unit
) {
    val context = LocalContext.current
    val occupations = remember { CharacterSkillDataRepository.loadOccupations(context) }
    val skillsByName = remember {
        CharacterSkillDataRepository.loadSkills(context).associateBy { it.name }
    }

    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(initialOccupationName) }
    var selectedOccupation by remember {
        mutableStateOf(occupations.firstOrNull { it.name == initialOccupationName } ?: occupations.firstOrNull())
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val initialAllocation = remember(initialAllocationJson) {
        CharacterSkillDataRepository.decodeAllocation(initialAllocationJson)
    }
    val skillValues = remember(selectedOccupation?.name) {
        mutableStateMapOf<String, String>().apply {
            selectedOccupation?.skills.orEmpty().forEach { skillName ->
                put(skillName, initialAllocation[skillName].orEmpty())
            }
        }
    }

    val filteredOccupations = occupations.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    val occupationPoints = selectedOccupation?.let {
        CharacterSkillDataRepository.evaluatePointsFormula(it.pointsFormula, statsData)
    } ?: 0

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
                        val occupation = selectedOccupation
                        if (occupation == null) {
                            errorMessage = "Select an occupation."
                            return@Button
                        }

                        val invalid = occupation.skills.firstOrNull {
                            val value = skillValues[it].orEmpty().trim()
                            value.isNotEmpty() && value.toIntOrNull() == null
                        }
                        if (invalid != null) {
                            errorMessage = "Invalid points for $invalid."
                            return@Button
                        }

                        errorMessage = null
                        onNextClick(
                            occupation.name,
                            CharacterSkillDataRepository.encodeAllocation(occupation.skills.associateWith {
                                skillValues[it].orEmpty().trim()
                            })
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
                text = "Occupation points: $occupationPoints",
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        expanded = true
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Choose occupation") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filteredOccupations.forEach { occupation ->
                        DropdownMenuItem(
                            text = { Text(occupation.name) },
                            onClick = {
                                selectedOccupation = occupation
                                searchText = occupation.name
                                expanded = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            selectedOccupation?.let {
                Text(
                    text = "Formula: ${it.pointsFormula}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF5A418A)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            selectedOccupation?.skills.orEmpty().forEach { skillName ->
                val defaultValue = skillsByName[skillName]?.defaultValue ?: 0
                OutlinedTextField(
                    value = skillValues[skillName].orEmpty(),
                    onValueChange = {
                        skillValues[skillName] = it
                        errorMessage = null
                    },
                    label = { Text(skillName) },
                    placeholder = { Text(defaultValue.toString()) },
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
