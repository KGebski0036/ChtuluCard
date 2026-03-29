package com.example.chtulucard.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

data class CharacterIdentityData(
    val name: String,
    val age: String,
    val sex: String,
    val placeOfBirth: String,
    val domicile: String,
    val avatarKey: String
)

data class CharacterCreationInput(
    val name: String,
    val age: Int,
    val sex: String,
    val placeOfBirth: String,
    val domicile: String,
    val avatarKey: String,
    val strength: Int,
    val constitution: Int,
    val size: Int,
    val dexterity: Int,
    val appearance: Int,
    val education: Int,
    val power: Int,
    val intelligence: Int,
    val move: Int,
    val sanity: Int,
    val hp: Int,
    val mp: Int,
    val luck: Int,
    val occupationName: String,
    val occupationSkillsJson: String,
    val personalSkillsJson: String,
    val inventoryJson: String = "{}",
    val notesText: String = ""
)

data class CharacterStatsData(
    val strength: Int,
    val constitution: Int,
    val size: Int,
    val dexterity: Int,
    val appearance: Int,
    val education: Int,
    val power: Int,
    val intelligence: Int,
    val move: Int
)

data class OccupationDefinition(
    val name: String,
    val pointsFormula: String,
    val skills: List<String>
)

data class SkillDefinition(
    val name: String,
    val defaultValue: Int
)

object CharacterAvatarCatalog {
    fun listFilenames(context: Context): List<String> =
        context.assets.list("avatars")
            ?.filter { it.endsWith(".png", ignoreCase = true) }
            ?.sorted()
            ?: emptyList()

    fun loadBitmap(context: Context, filename: String): Bitmap? {
        if (filename.isBlank()) return null
        return try {
            context.assets.open("avatars/$filename").use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            null
        }
    }
}

object CharacterCreationStateKeys {
    const val NAME = "creation_name"
    const val AGE = "creation_age"
    const val SEX = "creation_sex"
    const val PLACE_OF_BIRTH = "creation_place_of_birth"
    const val DOMICILE = "creation_domicile"
    const val AVATAR_KEY = "creation_avatar_key"
    const val STR = "creation_str"
    const val CON = "creation_con"
    const val SIZ = "creation_siz"
    const val DEX = "creation_dex"
    const val APP = "creation_app"
    const val EDU = "creation_edu"
    const val POW = "creation_pow"
    const val INT = "creation_int"
    const val MOVE = "creation_move"
    const val OCCUPATION_NAME = "creation_occupation_name"
    const val OCCUPATION_SKILLS_JSON = "creation_occupation_skills_json"
    const val PERSONAL_SKILLS_JSON = "creation_personal_skills_json"
}
