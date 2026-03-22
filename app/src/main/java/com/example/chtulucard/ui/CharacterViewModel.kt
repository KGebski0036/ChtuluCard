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

    fun addCharacter(name: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                dao.insertCharacter(CharacterEntity(sessionId = sessionId, name = name.trim()))
            }
        }
    }
}