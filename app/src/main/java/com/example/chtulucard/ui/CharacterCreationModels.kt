package com.example.chtulucard.ui

import androidx.annotation.DrawableRes
import com.example.chtulucard.R

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

data class CharacterAvatarOption(
    val key: String,
    @param:DrawableRes val drawableResId: Int
)

object CharacterAvatarCatalog {
    const val AVATAR_1 = "avatar1"
    const val AVATAR_2 = "avatar2"

    val options = listOf(
        CharacterAvatarOption(AVATAR_1, R.drawable.avatar1),
        CharacterAvatarOption(AVATAR_2, R.drawable.avatar2)
    )

    @DrawableRes
    fun drawableResIdForKey(key: String): Int {
        return options.firstOrNull { it.key == key }?.drawableResId ?: R.drawable.avatar1
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
