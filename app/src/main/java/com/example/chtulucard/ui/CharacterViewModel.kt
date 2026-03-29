package com.example.chtulucard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chtulucard.data.CharacterEntity
import com.example.chtulucard.data.SessionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CharacterViewModel(private val dao: SessionDao, private val sessionId: Int) : ViewModel() {

    // Fetch only the characters for THIS specific session
    val characters: Flow<List<CharacterEntity>> = dao.getCharactersForSession(sessionId)

    fun addCharacter(input: CharacterCreationInput) {
        if (input.name.isNotBlank()) {
            viewModelScope.launch {
                dao.insertCharacter(
                    CharacterEntity(
                        sessionId = sessionId,
                        name = input.name.trim(),
                        age = input.age,
                        sex = input.sex.trim(),
                        placeOfBirth = input.placeOfBirth.trim(),
                        domicile = input.domicile.trim(),
                        avatarKey = input.avatarKey,
                        strength = input.strength,
                        constitution = input.constitution,
                        size = input.size,
                        dexterity = input.dexterity,
                        appearance = input.appearance,
                        education = input.education,
                        power = input.power,
                        intelligence = input.intelligence,
                        move = input.move,
                        sanity = input.sanity,
                        hp = input.hp,
                        mp = input.mp,
                        luck = input.luck,
                        occupationName = input.occupationName,
                        occupationSkillsJson = input.occupationSkillsJson,
                        personalSkillsJson = input.personalSkillsJson,
                        inventoryJson = input.inventoryJson,
                        notesText = input.notesText
                    )
                )
            }
        }
    }
}