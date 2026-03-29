package com.example.chtulucard.ui

import androidx.lifecycle.ViewModel
import com.example.chtulucard.data.CharacterEntity
import com.example.chtulucard.data.SessionDao
import kotlinx.coroutines.flow.Flow

class CharacterDetailViewModel(
    dao: SessionDao,
    sessionId: Int,
    characterId: Int
) : ViewModel() {
    val character: Flow<CharacterEntity?> = dao.getCharacterById(sessionId, characterId)
}
