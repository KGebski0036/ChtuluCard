package com.example.chtulucard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CharacterIdentityScreen(
    sessionName: String,
    initialData: CharacterIdentityData?,
    onBackClick: () -> Unit,
    onNextClick: (CharacterIdentityData) -> Unit
) {
    val avatarOptions = CharacterAvatarCatalog.options
    var name by rememberSaveable { mutableStateOf(initialData?.name.orEmpty()) }
    var age by rememberSaveable { mutableStateOf(initialData?.age.orEmpty()) }
    var sex by rememberSaveable { mutableStateOf(initialData?.sex.orEmpty()) }
    var placeOfBirth by rememberSaveable { mutableStateOf(initialData?.placeOfBirth.orEmpty()) }
    var domicile by rememberSaveable { mutableStateOf(initialData?.domicile.orEmpty()) }
    var selectedAvatarIndex by rememberSaveable {
        mutableIntStateOf(
            avatarOptions.indexOfFirst { it.key == initialData?.avatarKey }
                .takeIf { it >= 0 } ?: 0
        )
    }
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Back")
                }

                Button(
                    onClick = {
                        val identityData = CharacterIdentityData(
                            name = name.trim(),
                            age = age.trim(),
                            sex = sex.trim(),
                            placeOfBirth = placeOfBirth.trim(),
                            domicile = domicile.trim(),
                            avatarKey = avatarOptions[selectedAvatarIndex].key
                        )

                        errorMessage = validateIdentity(identityData)
                        if (errorMessage == null) {
                            onNextClick(identityData)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DDF5), contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sessionName,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF5A418A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CharacterIdentityField(
                label = "Character name",
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                }
            )
            CharacterIdentityField(
                label = "Age",
                value = age,
                onValueChange = {
                    age = it
                    errorMessage = null
                },
                keyboardType = KeyboardType.Number
            )
            CharacterIdentityField(
                label = "Sex",
                value = sex,
                onValueChange = {
                    sex = it
                    errorMessage = null
                }
            )
            CharacterIdentityField(
                label = "Place of birth",
                value = placeOfBirth,
                onValueChange = {
                    placeOfBirth = it
                    errorMessage = null
                }
            )
            CharacterIdentityField(
                label = "Domicile",
                value = domicile,
                onValueChange = {
                    domicile = it
                    errorMessage = null
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Choose your avatar",
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    selectedAvatarIndex = (selectedAvatarIndex - 1 + avatarOptions.size) % avatarOptions.size
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous avatar",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(width = 220.dp, height = 260.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .border(width = 3.dp, color = Color(0xFF2E2E2E), shape = RoundedCornerShape(30.dp))
                        .background(Color(0xFFF7F3FB)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = avatarOptions[selectedAvatarIndex].drawableResId),
                        contentDescription = "Selected avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                IconButton(onClick = {
                    selectedAvatarIndex = (selectedAvatarIndex + 1) % avatarOptions.size
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next avatar",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

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
private fun CharacterIdentityField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp)
    )
}

private fun validateIdentity(data: CharacterIdentityData): String? {
    return when {
        data.name.isBlank() -> "Character name is required."
        data.age.toIntOrNull() == null -> "Age must be a number."
        data.sex.isBlank() -> "Sex is required."
        data.placeOfBirth.isBlank() -> "Place of birth is required."
        data.domicile.isBlank() -> "Domicile is required."
        else -> null
    }
}
