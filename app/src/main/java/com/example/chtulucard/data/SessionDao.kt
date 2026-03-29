package com.example.chtulucard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sessionId"),
            onDelete = ForeignKey.CASCADE // If a session is deleted, delete its characters too!
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
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
    val inventoryJson: String,
    val notesText: String
)

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY id DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)


    @Query("SELECT * FROM characters WHERE sessionId = :sessionId")
    fun getCharactersForSession(sessionId: Int): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :characterId AND sessionId = :sessionId LIMIT 1")
    fun getCharacterById(sessionId: Int, characterId: Int): Flow<CharacterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)
}

@Database(
    entities = [SessionEntity::class, CharacterEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}