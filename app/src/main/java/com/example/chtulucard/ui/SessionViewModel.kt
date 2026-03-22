package com.example.chtulucard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chtulucard.data.SessionDao
import com.example.chtulucard.data.SessionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(private val dao: SessionDao) : ViewModel() {

    val sessions: StateFlow<List<SessionEntity>> = dao.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSession(name: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                dao.insertSession(SessionEntity(name = name.trim()))
            }
        }
    }
}