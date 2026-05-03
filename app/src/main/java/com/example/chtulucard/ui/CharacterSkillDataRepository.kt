package com.example.chtulucard.ui

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object CharacterSkillDataRepository {

    fun loadOccupations(context: Context): List<OccupationDefinition> {
        val json = readAsset(context, "occupations.json")
        val array = JSONArray(json)
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    OccupationDefinition(
                        name = item.optString("name"),
                        pointsFormula = item.optString("pointsFormula"),
                        skills = readStringList(item, "skills")
                    )
                )
            }
        }
    }

    fun loadSkills(context: Context): List<SkillDefinition> {
        val json = readAsset(context, "skills.json")
        val array = JSONArray(json)
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    SkillDefinition(
                        name = item.optString("name"),
                        defaultValue = item.optInt("defaultValue", 0)
                    )
                )
            }
        }
    }

    fun evaluatePointsFormula(formula: String, stats: CharacterStatsData): Int {
        val statsMap = mapOf(
            "STR" to stats.strength,
            "CON" to stats.constitution,
            "SIZ" to stats.size,
            "DEX" to stats.dexterity,
            "APP" to stats.appearance,
            "EDU" to stats.education,
            "POW" to stats.power,
            "INT" to stats.intelligence,
            "MOVE" to stats.move
        )

        val termRegex = Regex("([A-Za-z]+)\\s*(?:\\*|x|X|\\s)?\\s*(\\d+)?")
        return formula
            .split("+")
            .sumOf { rawTerm ->
                val term = rawTerm.trim()
                val match = termRegex.find(term) ?: return@sumOf 0
                val statName = match.groupValues[1].uppercase()
                val multiplier = match.groupValues[2].toIntOrNull() ?: 1
                (statsMap[statName] ?: 0) * multiplier
            }
    }

    fun encodeAllocation(allocation: Map<String, String>): String {
        val jsonObject = JSONObject()
        allocation.forEach { (skillName, value) ->
            jsonObject.put(skillName, value)
        }
        return jsonObject.toString()
    }

    fun decodeAllocation(json: String): Map<String, String> {
        if (json.isBlank()) return emptyMap()
        val jsonObject = JSONObject(json)
        val keys = jsonObject.keys()
        val result = mutableMapOf<String, String>()
        while (keys.hasNext()) {
            val key = keys.next()
            result[key] = jsonObject.optString(key)
        }
        return result
    }

    fun encodeTextList(items: List<String>): String {
        return JSONArray(items).toString()
    }

    fun decodeTextList(json: String): List<String> {
        if (json.isBlank()) return emptyList()

        return try {
            val array = JSONArray(json)
            buildList {
                for (index in 0 until array.length()) {
                    val value = array.optString(index).trim()
                    if (value.isNotEmpty()) {
                        add(value)
                    }
                }
            }
        } catch (_: Exception) {
            runCatching {
                val objectJson = JSONObject(json)
                buildList {
                    val keys = objectJson.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = objectJson.optString(key).trim()
                        if (value.isNotEmpty()) {
                            add(key.takeIf { it.isNotBlank() }?.let { "$it x$value" } ?: value)
                        }
                    }
                }
            }.getOrDefault(emptyList())
        }
    }

    private fun readAsset(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun readStringList(jsonObject: JSONObject, key: String): List<String> {
        val jsonArray = jsonObject.optJSONArray(key) ?: JSONArray()
        return buildList {
            for (index in 0 until jsonArray.length()) {
                add(jsonArray.optString(index))
            }
        }
    }
}
