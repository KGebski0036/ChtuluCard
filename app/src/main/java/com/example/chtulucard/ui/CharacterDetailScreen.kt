package com.example.chtulucard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.chtulucard.data.CharacterEntity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

enum class CharacterDetailTab {
    Info,
    Skills,
    Inventory,
    Notes,
    History
}

@Composable
fun CharacterDetailScreen(
    sessionName: String,
    character: CharacterEntity?,
    onBackClick: () -> Unit,
    onSaveStats: (
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int,
        Int
    ) -> Unit,
    onSaveSkills: (String, String) -> Unit,
    onSaveHistory: (String, String, String, String, String, String, String, String, String, String) -> Unit,
    onTryAgainClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(CharacterDetailTab.Info) }
    val context = LocalContext.current
    val avatarBitmap = remember(character?.avatarKey) {
        character?.avatarKey?.let { CharacterAvatarCatalog.loadBitmap(context, it)?.asImageBitmap() }
    }
    var currentHp by rememberSaveable(character?.id) {
        mutableStateOf(character?.hp ?: 1)
    }
    var isDead by rememberSaveable(character?.id) { mutableStateOf(false) }
    var showDeathVideo by rememberSaveable(character?.id) { mutableStateOf(false) }
    var deathVideoFinished by rememberSaveable(character?.id) { mutableStateOf(false) }
    var suppressDeathUntilHpPositive by rememberSaveable(character?.id) { mutableStateOf(false) }

    LaunchedEffect(currentHp) {
        if (currentHp > 0) {
            suppressDeathUntilHpPositive = false
            if (isDead && !showDeathVideo) {
                isDead = false
                deathVideoFinished = false
            }
            return@LaunchedEffect
        }

        if (currentHp <= 0 && !isDead && !suppressDeathUntilHpPositive) {
            isDead = true
            showDeathVideo = true
            deathVideoFinished = false
        }
    }

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
                NavigationBarItem(
                    selected = selectedTab == CharacterDetailTab.History,
                    onClick = { selectedTab = CharacterDetailTab.History },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = "History") },
                    label = { Text("History") }
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                if (isDead && deathVideoFinished) {
                    DeathStateBanner(
                        avatarBitmap = avatarBitmap,
                        onReviveClick = {
                            isDead = false
                            showDeathVideo = false
                            deathVideoFinished = false
                            suppressDeathUntilHpPositive = true
                            currentHp = 1
                        },
                        onTryAgainClick = onTryAgainClick
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                when (selectedTab) {
                    CharacterDetailTab.Info -> CharacterInfoTab(
                        character = character,
                        sessionName = sessionName,
                        avatarBitmap = avatarBitmap,
                        onSaveStats = onSaveStats,
                        isDead = isDead,
                        onHpChanged = { hp -> currentHp = hp }
                    )
                    CharacterDetailTab.Skills -> CharacterSkillsTab(
                        character = character,
                        onSaveSkills = onSaveSkills,
                        isDead = isDead
                    )
                    CharacterDetailTab.Inventory -> CharacterInventoryTab(character = character)
                    CharacterDetailTab.Notes -> CharacterNotesTab(character = character)
                    CharacterDetailTab.History -> CharacterHistoryTab(
                        character = character,
                        onSaveHistory = onSaveHistory,
                        isDead = isDead
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            if (showDeathVideo) {
                DeathVideoOverlay(
                    onFinished = {
                        showDeathVideo = false
                        deathVideoFinished = true
                    }
                )
            }
        }
    }
}

@Composable
private fun CharacterInfoTab(
    character: CharacterEntity,
    sessionName: String,
    avatarBitmap: androidx.compose.ui.graphics.ImageBitmap?,
    onSaveStats: (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int) -> Unit,
    isDead: Boolean,
    onHpChanged: (Int) -> Unit
) {
    var strengthText by rememberSaveable(character.id) { mutableStateOf(character.strength.toString()) }
    var constitutionText by rememberSaveable(character.id) { mutableStateOf(character.constitution.toString()) }
    var sizeText by rememberSaveable(character.id) { mutableStateOf(character.size.toString()) }
    var dexterityText by rememberSaveable(character.id) { mutableStateOf(character.dexterity.toString()) }
    var appearanceText by rememberSaveable(character.id) { mutableStateOf(character.appearance.toString()) }
    var educationText by rememberSaveable(character.id) { mutableStateOf(character.education.toString()) }
    var powerText by rememberSaveable(character.id) { mutableStateOf(character.power.toString()) }
    var intelligenceText by rememberSaveable(character.id) { mutableStateOf(character.intelligence.toString()) }
    var moveText by rememberSaveable(character.id) { mutableStateOf(character.move.toString()) }

    var sanityText by rememberSaveable(character.id) { mutableStateOf(character.sanity.toString()) }
    var hpText by rememberSaveable(character.id) { mutableStateOf(character.hp.toString()) }
    var mpText by rememberSaveable(character.id) { mutableStateOf(character.mp.toString()) }
    var luckText by rememberSaveable(character.id) { mutableStateOf(character.luck.toString()) }

    LaunchedEffect(hpText) {
        onHpChanged(hpText.toIntOrNull() ?: 0)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8DDF5)),
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Avatar",
                    tint = Color(0xFF5A418A),
                    modifier = Modifier.size(72.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = character.name,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            color = Color.Black
        )

        Text(
            text = character.occupationName.ifBlank { "Investigator" },
            fontSize = 17.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF5A418A)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SectionTitle("User information")
        Spacer(modifier = Modifier.height(8.dp))
        ProfileInfoRow(label = "Age", value = character.age.toString())
        ProfileInfoRow(label = "Sex", value = character.sex)
        ProfileInfoRow(label = "Domicile", value = character.domicile)
        ProfileInfoRow(label = "Session", value = sessionName)

        Spacer(modifier = Modifier.height(20.dp))

        SectionTitle("Derived values")
        Spacer(modifier = Modifier.height(8.dp))
        SimpleEditRow("Sanity", sanityText, enabled = !isDead) { sanityText = it }
        SimpleEditRow("HP", hpText, enabled = !isDead) {
            hpText = it
            onHpChanged(it.toIntOrNull() ?: 0)
        }
        SimpleEditRow("MP", mpText, enabled = !isDead) { mpText = it }
        SimpleEditRow("Luck", luckText, enabled = !isDead) { luckText = it }

        Spacer(modifier = Modifier.height(20.dp))

        SectionTitle("Editable statistics")
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Stat",
                modifier = Modifier.width(58.dp),
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = "Value",
                modifier = Modifier.weight(1f),
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = "1/2",
                modifier = Modifier.width(56.dp),
                fontSize = 11.sp,
                color = Color(0xFF5A418A),
                textAlign = TextAlign.Center
            )
            Text(
                text = "1/5",
                modifier = Modifier.width(56.dp),
                fontSize = 11.sp,
                color = Color(0xFF8A5A41),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        StatEditRow("STR", strengthText, enabled = !isDead) { strengthText = it }
        StatEditRow("CON", constitutionText, enabled = !isDead) { constitutionText = it }
        StatEditRow("SIZ", sizeText, enabled = !isDead) { sizeText = it }
        StatEditRow("DEX", dexterityText, enabled = !isDead) { dexterityText = it }
        StatEditRow("APP", appearanceText, enabled = !isDead) { appearanceText = it }
        StatEditRow("EDU", educationText, enabled = !isDead) { educationText = it }
        StatEditRow("POW", powerText, enabled = !isDead) { powerText = it }
        StatEditRow("INT", intelligenceText, enabled = !isDead) { intelligenceText = it }
        StatEditRow("MOVE", moveText, enabled = !isDead) { moveText = it }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                onSaveStats(
                    strengthText.toIntOrNull() ?: character.strength,
                    constitutionText.toIntOrNull() ?: character.constitution,
                    sizeText.toIntOrNull() ?: character.size,
                    dexterityText.toIntOrNull() ?: character.dexterity,
                    appearanceText.toIntOrNull() ?: character.appearance,
                    educationText.toIntOrNull() ?: character.education,
                    powerText.toIntOrNull() ?: character.power,
                    intelligenceText.toIntOrNull() ?: character.intelligence,
                    moveText.toIntOrNull() ?: character.move,
                    sanityText.toIntOrNull() ?: character.sanity,
                    hpText.toIntOrNull() ?: character.hp,
                    mpText.toIntOrNull() ?: character.mp,
                    luckText.toIntOrNull() ?: character.luck
                )
            },
            enabled = !isDead,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A418A))
        ) {
            Text(text = "Save stats", color = Color.White)
        }

        if (isDead) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Character is dead. Editing is disabled.",
                color = Color(0xFF8A1C1C),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD0C8E8))
        Text(
            text = "  $title  ",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5A418A)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD0C8E8))
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF2F0F7))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontStyle = FontStyle.Italic,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun StatEditRow(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    val parsedValue = value.toIntOrNull() ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.width(58.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Black
        )

        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() }) {
                    onValueChange(input)
                }
            },
            enabled = enabled,
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Column(
            modifier = Modifier
                .width(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEDE8F5))
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = (parsedValue / 2).toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF2E1F5E)
            )
            Text(
                text = "1/2",
                fontSize = 10.sp,
                color = Color(0xFF5A418A)
            )
        }

        Column(
            modifier = Modifier
                .width(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5EDE8))
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = (parsedValue / 5).toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF5E3020)
            )
            Text(
                text = "1/5",
                fontSize = 10.sp,
                color = Color(0xFF8A5A41)
            )
        }
    }
}

@Composable
private fun SimpleEditRow(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.width(58.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Black
        )

        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() }) {
                    onValueChange(input)
                }
            },
            enabled = enabled,
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
private fun CharacterSkillsTab(
    character: CharacterEntity,
    onSaveSkills: (String, String) -> Unit,
    isDead: Boolean
) {
    val context = LocalContext.current
    val defaultValues = remember {
        CharacterSkillDataRepository.loadSkills(context)
            .associate { it.name to it.defaultValue }
    }

    var occupationValues by remember(character.id, character.occupationSkillsJson) {
        mutableStateOf(
            CharacterSkillDataRepository.decodeAllocation(character.occupationSkillsJson)
                .mapValues { (skill, value) ->
                    value.ifBlank { (defaultValues[skill] ?: 0).toString() }
                }
        )
    }

    var personalValues by remember(character.id, character.personalSkillsJson) {
        mutableStateOf(
            CharacterSkillDataRepository.decodeAllocation(character.personalSkillsJson)
                .mapValues { (skill, value) ->
                    value.ifBlank { (defaultValues[skill] ?: 0).toString() }
                }
        )
    }

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
        SkillsHeaderRow()
        if (occupationValues.isEmpty()) {
            Text("No occupation skills assigned.")
        } else {
            occupationValues.toSortedMap().forEach { (skill, points) ->
                SkillEditRow(
                    label = skill,
                    value = points,
                    enabled = !isDead
                ) { updated ->
                    occupationValues = occupationValues.toMutableMap().apply {
                        this[skill] = updated
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Personal skills",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        SkillsHeaderRow()
        if (personalValues.isEmpty()) {
            Text("No personal skills assigned.")
        } else {
            personalValues.toSortedMap().forEach { (skill, points) ->
                SkillEditRow(
                    label = skill,
                    value = points,
                    enabled = !isDead
                ) { updated ->
                    personalValues = personalValues.toMutableMap().apply {
                        this[skill] = updated
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val normalizedOccupation = occupationValues.mapValues { (skill, points) ->
                    points.ifBlank { (defaultValues[skill] ?: 0).toString() }
                }
                val normalizedPersonal = personalValues.mapValues { (skill, points) ->
                    points.ifBlank { (defaultValues[skill] ?: 0).toString() }
                }
                onSaveSkills(
                    CharacterSkillDataRepository.encodeAllocation(normalizedOccupation),
                    CharacterSkillDataRepository.encodeAllocation(normalizedPersonal)
                )
            },
            enabled = !isDead,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A418A))
        ) {
            Text(text = "Save skills", color = Color.White)
        }

        if (isDead) {
            Text(
                text = "Character is dead. Editing is disabled.",
                color = Color(0xFF8A1C1C),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SkillsHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Skill",
            modifier = Modifier.width(120.dp),
            fontSize = 11.sp,
            color = Color.Gray
        )
        Text(
            text = "Value",
            modifier = Modifier.weight(1f),
            fontSize = 11.sp,
            color = Color.Gray
        )
        Text(
            text = "1/2",
            modifier = Modifier.width(56.dp),
            fontSize = 11.sp,
            color = Color(0xFF5A418A),
            textAlign = TextAlign.Center
        )
        Text(
            text = "1/5",
            modifier = Modifier.width(56.dp),
            fontSize = 11.sp,
            color = Color(0xFF8A5A41),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SkillEditRow(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    val parsedValue = value.toIntOrNull() ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.width(120.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = Color.Black
        )

        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() }) {
                    onValueChange(input)
                }
            },
            enabled = enabled,
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Column(
            modifier = Modifier
                .width(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEDE8F5))
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = (parsedValue / 2).toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF2E1F5E)
            )
            Text(
                text = "1/2",
                fontSize = 10.sp,
                color = Color(0xFF5A418A)
            )
        }

        Column(
            modifier = Modifier
                .width(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5EDE8))
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = (parsedValue / 5).toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF5E3020)
            )
            Text(
                text = "1/5",
                fontSize = 10.sp,
                color = Color(0xFF8A5A41)
            )
        }
    }
}

@Composable
private fun DeathVideoOverlay(onFinished: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri("asset:///youdied.mp4"))
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    onFinished()
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DeathStateBanner(
    avatarBitmap: androidx.compose.ui.graphics.ImageBitmap?,
    onReviveClick: () -> Unit,
    onTryAgainClick: () -> Unit
) {
    val pulse = rememberInfiniteTransition(label = "deadMessage")
    val alpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "deadMessageAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF7E9EA))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Character has died",
            color = Color(0xFF8A1C1C),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            modifier = Modifier.alpha(alpha)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEDEDF)),
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap,
                    contentDescription = "Dead avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Dead avatar",
                    tint = Color(0xFF8A1C1C),
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onReviveClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A418A))
            ) {
                Text("Revive", color = Color.White)
            }
            Button(
                onClick = onTryAgainClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A1C1C))
            ) {
                Text("Try again", color = Color.White)
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
private fun CharacterHistoryTab(
    character: CharacterEntity,
    onSaveHistory: (String, String, String, String, String, String, String, String, String, String) -> Unit,
    isDead: Boolean
) {
    var description by rememberSaveable(character.id) { mutableStateOf(character.description) }
    var ideologyBeliefs by rememberSaveable(character.id) { mutableStateOf(character.ideologyBeliefs) }
    var significantPeople by rememberSaveable(character.id) { mutableStateOf(character.significantPeople) }
    var meaningfulLocations by rememberSaveable(character.id) { mutableStateOf(character.meaningfulLocations) }
    var phobiasManias by rememberSaveable(character.id) { mutableStateOf(character.phobiasManias) }
    var arcaneTomesSpells by rememberSaveable(character.id) { mutableStateOf(character.arcaneTomesSpells) }
    var characterAssets by rememberSaveable(character.id) { mutableStateOf(character.characterAssets) }
    var injuries by rememberSaveable(character.id) { mutableStateOf(character.injuries) }
    var strangeEncounters by rememberSaveable(character.id) { mutableStateOf(character.strangeEncounters) }
    var equipment by rememberSaveable(character.id) { mutableStateOf(character.equipment) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SectionTitle("Description")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = description, label = "Description", enabled = !isDead) { description = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Ideology & Beliefs")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = ideologyBeliefs, label = "Ideology & beliefs", enabled = !isDead) { ideologyBeliefs = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Significant People")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = significantPeople, label = "Significant people", enabled = !isDead) { significantPeople = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Meaningful Locations")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = meaningfulLocations, label = "Meaningful locations", enabled = !isDead) { meaningfulLocations = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Phobias & Manias")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = phobiasManias, label = "Phobias & manias", enabled = !isDead) { phobiasManias = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Arcane Tomes & Spells")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = arcaneTomesSpells, label = "Arcane tomes & spells", enabled = !isDead) { arcaneTomesSpells = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Assets")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = characterAssets, label = "Assets", enabled = !isDead) { characterAssets = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Injuries")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = injuries, label = "Injuries", enabled = !isDead) { injuries = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Strange Encounters")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = strangeEncounters, label = "Strange encounters", enabled = !isDead) { strangeEncounters = it }

        Spacer(modifier = Modifier.height(6.dp))
        SectionTitle("Equipment")
        Spacer(modifier = Modifier.height(2.dp))
        HistoryTextField(value = equipment, label = "Equipment", enabled = !isDead) { equipment = it }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                onSaveHistory(
                    description, ideologyBeliefs, significantPeople, meaningfulLocations,
                    phobiasManias, arcaneTomesSpells, characterAssets, injuries,
                    strangeEncounters, equipment
                )
            },
            enabled = !isDead,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A418A))
        ) {
            Text(text = "Save history", color = Color.White)
        }

        if (isDead) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Character is dead. Editing is disabled.",
                color = Color(0xFF8A1C1C),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun HistoryTextField(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label, fontSize = 13.sp, color = Color(0xFF5A418A)) },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        maxLines = 6,
        shape = RoundedCornerShape(12.dp)
    )
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
