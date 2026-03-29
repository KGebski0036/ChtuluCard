package com.example.chtulucard

import android.net.Uri

object AppRoutes {
    const val SESSIONS = "sessions"
    const val CHARACTERS = "characters/{sessionId}/{sessionName}"
    const val CHARACTER_DETAIL = "character/{sessionId}/{sessionName}/{characterId}"
    const val CREATE_CHARACTER_INFO = "character-create/info/{sessionId}/{sessionName}"
    const val CREATE_CHARACTER_STATS = "character-create/stats/{sessionId}/{sessionName}"
    const val CREATE_CHARACTER_OCCUPATION_SKILLS = "character-create/occupation-skills/{sessionId}/{sessionName}"
    const val CREATE_CHARACTER_PERSONAL_SKILLS = "character-create/personal-skills/{sessionId}/{sessionName}"

    fun characters(sessionId: Int, sessionName: String): String {
        return "characters/$sessionId/${Uri.encode(sessionName)}"
    }

    fun characterDetail(sessionId: Int, sessionName: String, characterId: Int): String {
        return "character/$sessionId/${Uri.encode(sessionName)}/$characterId"
    }

    fun createCharacterInfo(sessionId: Int, sessionName: String): String {
        return "character-create/info/$sessionId/${Uri.encode(sessionName)}"
    }

    fun createCharacterStats(sessionId: Int, sessionName: String): String {
        return "character-create/stats/$sessionId/${Uri.encode(sessionName)}"
    }

    fun createCharacterOccupationSkills(sessionId: Int, sessionName: String): String {
        return "character-create/occupation-skills/$sessionId/${Uri.encode(sessionName)}"
    }

    fun createCharacterPersonalSkills(sessionId: Int, sessionName: String): String {
        return "character-create/personal-skills/$sessionId/${Uri.encode(sessionName)}"
    }
}
