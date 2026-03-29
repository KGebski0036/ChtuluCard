package com.example.chtulucard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chtulucard.data.CharacterEntity
import com.example.chtulucard.data.SessionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val dao: SessionDao,
    sessionId: Int,
    characterId: Int
) : ViewModel() {
    val character: Flow<CharacterEntity?> = dao.getCharacterById(sessionId, characterId)

    fun saveStats(
        character: CharacterEntity,
        strength: Int,
        constitution: Int,
        size: Int,
        dexterity: Int,
        appearance: Int,
        education: Int,
        power: Int,
        intelligence: Int,
        move: Int,
        sanity: Int,
        hp: Int,
        mp: Int,
        luck: Int
    ) {
        viewModelScope.launch {
            dao.updateCharacter(
                character.copy(
                    strength = strength,
                    constitution = constitution,
                    size = size,
                    dexterity = dexterity,
                    appearance = appearance,
                    education = education,
                    power = power,
                    intelligence = intelligence,
                    move = move,
                    sanity = sanity,
                    hp = hp,
                    mp = mp,
                    luck = luck
                )
            )
        }
    }

    fun saveSkills(
        character: CharacterEntity,
        occupationSkillsJson: String,
        personalSkillsJson: String
    ) {
        viewModelScope.launch {
            dao.updateCharacter(
                character.copy(
                    occupationSkillsJson = occupationSkillsJson,
                    personalSkillsJson = personalSkillsJson
                )
            )
        }
    }

    fun saveHistory(
        character: CharacterEntity,
        description: String,
        ideologyBeliefs: String,
        significantPeople: String,
        meaningfulLocations: String,
        phobiasManias: String,
        arcaneTomesSpells: String,
        characterAssets: String,
        injuries: String,
        strangeEncounters: String,
        equipment: String
    ) {
        viewModelScope.launch {
            dao.updateCharacter(
                character.copy(
                    description = description,
                    ideologyBeliefs = ideologyBeliefs,
                    significantPeople = significantPeople,
                    meaningfulLocations = meaningfulLocations,
                    phobiasManias = phobiasManias,
                    arcaneTomesSpells = arcaneTomesSpells,
                    characterAssets = characterAssets,
                    injuries = injuries,
                    strangeEncounters = strangeEncounters,
                    equipment = equipment
                )
            )
        }
    }
}
