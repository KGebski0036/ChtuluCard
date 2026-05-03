package com.example.chtulucard

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.chtulucard.data.AppDatabase
import com.example.chtulucard.ui.SessionScreen
import com.example.chtulucard.ui.SessionViewModel
import com.example.chtulucard.ui.theme.ChtuluCardTheme
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chtulucard.ui.CharacterScreen
import com.example.chtulucard.ui.CharacterCreationInput
import com.example.chtulucard.ui.CharacterCreationStateKeys
import com.example.chtulucard.ui.CharacterDetailScreen
import com.example.chtulucard.ui.CharacterDetailViewModel
import com.example.chtulucard.ui.CharacterStatsData
import com.example.chtulucard.ui.CharacterViewModel
import com.example.chtulucard.ui.CharacterIdentityData
import com.example.chtulucard.ui.CharacterIdentityScreen
import com.example.chtulucard.ui.CharacterOccupationSkillsScreen
import com.example.chtulucard.ui.CharacterPersonalSkillsScreen
import com.example.chtulucard.ui.CharacterStatsScreen

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "chtulucard-database"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    private val sessionViewModel by viewModels<SessionViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SessionViewModel(db.sessionDao()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChtuluCardTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = AppRoutes.SESSIONS) {

                    composable(AppRoutes.SESSIONS) {
                        SessionScreen(
                            viewModel = sessionViewModel,
                            onSessionClick = { sessionId, sessionName ->
                                navController.navigate(AppRoutes.characters(sessionId, sessionName))
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.CHARACTERS,
                        arguments = listOf(
                            navArgument("sessionId") { type = NavType.IntType },
                            navArgument("sessionName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                        val sessionName = Uri.decode(
                            backStackEntry.arguments?.getString("sessionName") ?: "Unknown Session"
                        )

                        val characterViewModel: CharacterViewModel = viewModel(
                            factory = characterViewModelFactory(sessionId)
                        )

                        CharacterScreen(
                            sessionName = sessionName,
                            viewModel = characterViewModel,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onCreateCharacterClick = {
                                navController.navigate(AppRoutes.createCharacterInfo(sessionId, sessionName))
                            },
                            onCharacterClick = { characterId ->
                                navController.navigate(
                                    AppRoutes.characterDetail(sessionId, sessionName, characterId)
                                )
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.CHARACTER_DETAIL,
                        arguments = listOf(
                            navArgument("sessionId") { type = NavType.IntType },
                            navArgument("sessionName") { type = NavType.StringType },
                            navArgument("characterId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                        val sessionName = Uri.decode(
                            backStackEntry.arguments?.getString("sessionName") ?: "Unknown Session"
                        )
                        val characterId = backStackEntry.arguments?.getInt("characterId") ?: 0

                        val detailViewModel: CharacterDetailViewModel = viewModel(
                            factory = characterDetailViewModelFactory(sessionId, characterId)
                        )
                        val character = detailViewModel.character.collectAsState(initial = null).value

                        CharacterDetailScreen(
                            sessionName = sessionName,
                            character = character,
                            onBackClick = { navController.popBackStack() },
                            onSaveStats = { strength, constitution, size, dexterity, appearance, education, power, intelligence, move, sanity, hp, mp, luck ->
                                character?.let {
                                    detailViewModel.saveStats(
                                        character = it,
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
                                }
                            },
                            onSaveSkills = { occupationSkillsJson, personalSkillsJson ->
                                character?.let {
                                    detailViewModel.saveSkills(
                                        character = it,
                                        occupationSkillsJson = occupationSkillsJson,
                                        personalSkillsJson = personalSkillsJson
                                    )
                                }
                            },
                            onSaveInventory = { inventoryJson ->
                                character?.let {
                                    detailViewModel.saveInventory(
                                        character = it,
                                        inventoryJson = inventoryJson
                                    )
                                }
                            },
                            onSaveNotes = { notesText ->
                                character?.let {
                                    detailViewModel.saveNotes(
                                        character = it,
                                        notesText = notesText
                                    )
                                }
                            },
                            onSaveHistory = { description, ideologyBeliefs, significantPeople, meaningfulLocations, phobiasManias, arcaneTomesSpells, characterAssets, injuries, strangeEncounters, equipment ->
                                character?.let {
                                    detailViewModel.saveHistory(
                                        character = it,
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
                                }
                            },
                            onTryAgainClick = {
                                navController.navigateToCharactersAfterCreation(sessionId, sessionName)
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.CREATE_CHARACTER_INFO,
                        arguments = listOf(
                            navArgument("sessionId") { type = NavType.IntType },
                            navArgument("sessionName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                        val sessionName = Uri.decode(
                            backStackEntry.arguments?.getString("sessionName") ?: "Unknown Session"
                        )

                        val savedStateHandle = backStackEntry.savedStateHandle
                        val initialData = CharacterIdentityData(
                            name = savedStateHandle.get<String>(CharacterCreationStateKeys.NAME).orEmpty(),
                            age = savedStateHandle.get<String>(CharacterCreationStateKeys.AGE).orEmpty(),
                            sex = savedStateHandle.get<String>(CharacterCreationStateKeys.SEX).orEmpty(),
                            placeOfBirth = savedStateHandle.get<String>(CharacterCreationStateKeys.PLACE_OF_BIRTH).orEmpty(),
                            domicile = savedStateHandle.get<String>(CharacterCreationStateKeys.DOMICILE).orEmpty(),
                            avatarKey = savedStateHandle.get<String>(CharacterCreationStateKeys.AVATAR_KEY)
                                ?: ""
                        )

                        CharacterIdentityScreen(
                            sessionName = sessionName,
                            initialData = initialData,
                            onBackClick = { navController.popBackStack() },
                            onNextClick = { identityData ->
                                savedStateHandle[CharacterCreationStateKeys.NAME] = identityData.name
                                savedStateHandle[CharacterCreationStateKeys.AGE] = identityData.age
                                savedStateHandle[CharacterCreationStateKeys.SEX] = identityData.sex
                                savedStateHandle[CharacterCreationStateKeys.PLACE_OF_BIRTH] = identityData.placeOfBirth
                                savedStateHandle[CharacterCreationStateKeys.DOMICILE] = identityData.domicile
                                savedStateHandle[CharacterCreationStateKeys.AVATAR_KEY] = identityData.avatarKey
                                navController.navigate(AppRoutes.createCharacterStats(sessionId, sessionName))
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.CREATE_CHARACTER_STATS,
                        arguments = listOf(
                            navArgument("sessionId") { type = NavType.IntType },
                            navArgument("sessionName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                        val sessionName = Uri.decode(
                            backStackEntry.arguments?.getString("sessionName") ?: "Unknown Session"
                        )
                        val initialStats = readStatsData(backStackEntry.savedStateHandle)

                        CharacterStatsScreen(
                            initialStats = initialStats,
                            onBackClick = { navController.popBackStack() },
                            onNextClick = { statsData ->
                                saveStatsData(backStackEntry.savedStateHandle, statsData)
                                navController.navigate(
                                    AppRoutes.createCharacterOccupationSkills(sessionId, sessionName)
                                )
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.CREATE_CHARACTER_OCCUPATION_SKILLS,
                        arguments = listOf(
                            navArgument("sessionId") { type = NavType.IntType },
                            navArgument("sessionName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                        val sessionName = Uri.decode(
                            backStackEntry.arguments?.getString("sessionName") ?: "Unknown Session"
                        )

                        val statsEntry = navController.getBackStackEntry(
                            AppRoutes.createCharacterStats(sessionId, sessionName)
                        )
                        val statsData = readStatsData(statsEntry.savedStateHandle) ?: CharacterStatsData(
                            strength = 0,
                            constitution = 0,
                            size = 0,
                            dexterity = 0,
                            appearance = 0,
                            education = 0,
                            power = 0,
                            intelligence = 0,
                            move = 0
                        )

                        CharacterOccupationSkillsScreen(
                            statsData = statsData,
                            initialOccupationName = backStackEntry.savedStateHandle
                                .get<String>(CharacterCreationStateKeys.OCCUPATION_NAME)
                                .orEmpty(),
                            initialAllocationJson = backStackEntry.savedStateHandle
                                .get<String>(CharacterCreationStateKeys.OCCUPATION_SKILLS_JSON)
                                .orEmpty(),
                            onBackClick = { navController.popBackStack() },
                            onNextClick = { occupationName, allocationJson ->
                                backStackEntry.savedStateHandle[CharacterCreationStateKeys.OCCUPATION_NAME] = occupationName
                                backStackEntry.savedStateHandle[CharacterCreationStateKeys.OCCUPATION_SKILLS_JSON] = allocationJson
                                navController.navigate(
                                    AppRoutes.createCharacterPersonalSkills(sessionId, sessionName)
                                )
                            }
                        )
                    }

                    composable(
                        route = AppRoutes.CREATE_CHARACTER_PERSONAL_SKILLS,
                        arguments = listOf(
                            navArgument("sessionId") { type = NavType.IntType },
                            navArgument("sessionName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                        val sessionName = Uri.decode(
                            backStackEntry.arguments?.getString("sessionName") ?: "Unknown Session"
                        )

                        val infoEntry = navController.getBackStackEntry(
                            AppRoutes.createCharacterInfo(sessionId, sessionName)
                        )
                        val statsEntry = navController.getBackStackEntry(
                            AppRoutes.createCharacterStats(sessionId, sessionName)
                        )
                        val occupationEntry = navController.getBackStackEntry(
                            AppRoutes.createCharacterOccupationSkills(sessionId, sessionName)
                        )

                        val statsData = readStatsData(statsEntry.savedStateHandle) ?: CharacterStatsData(
                            strength = 0,
                            constitution = 0,
                            size = 0,
                            dexterity = 0,
                            appearance = 0,
                            education = 0,
                            power = 0,
                            intelligence = 0,
                            move = 0
                        )

                        val characterViewModel: CharacterViewModel = viewModel(
                            factory = characterViewModelFactory(sessionId)
                        )

                        CharacterPersonalSkillsScreen(
                            statsData = statsData,
                            initialAllocationJson = backStackEntry.savedStateHandle
                                .get<String>(CharacterCreationStateKeys.PERSONAL_SKILLS_JSON)
                                .orEmpty(),
                            onBackClick = { navController.popBackStack() },
                            onSaveClick = { personalSkillsJson ->
                                backStackEntry.savedStateHandle[CharacterCreationStateKeys.PERSONAL_SKILLS_JSON] = personalSkillsJson

                                val creationInput = CharacterCreationInput(
                                    name = infoEntry.savedStateHandle
                                        .get<String>(CharacterCreationStateKeys.NAME)
                                        .orEmpty(),
                                    age = infoEntry.savedStateHandle
                                        .get<String>(CharacterCreationStateKeys.AGE)
                                        .orEmpty()
                                        .toIntOrNull() ?: 0,
                                    sex = infoEntry.savedStateHandle
                                        .get<String>(CharacterCreationStateKeys.SEX)
                                        .orEmpty(),
                                    placeOfBirth = infoEntry.savedStateHandle
                                        .get<String>(CharacterCreationStateKeys.PLACE_OF_BIRTH)
                                        .orEmpty(),
                                    domicile = infoEntry.savedStateHandle
                                        .get<String>(CharacterCreationStateKeys.DOMICILE)
                                        .orEmpty(),
                                    avatarKey = infoEntry.savedStateHandle
                                        .get<String>(CharacterCreationStateKeys.AVATAR_KEY)
                                        ?: "",
                                    strength = statsData.strength,
                                    constitution = statsData.constitution,
                                    size = statsData.size,
                                    dexterity = statsData.dexterity,
                                    appearance = statsData.appearance,
                                    education = statsData.education,
                                    power = statsData.power,
                                    intelligence = statsData.intelligence,
                                    move = statsData.move,
                                    sanity = statsData.power,
                                    hp = (statsData.constitution + statsData.size) / 10,
                                    mp = statsData.power / 5,
                                    luck = (statsData.power + statsData.intelligence) / 2,
                                    occupationName = occupationEntry.savedStateHandle
                                        .get<String>(CharacterCreationStateKeys.OCCUPATION_NAME)
                                        .orEmpty(),
                                    occupationSkillsJson = occupationEntry.savedStateHandle
                                        .get<String>(CharacterCreationStateKeys.OCCUPATION_SKILLS_JSON)
                                        .orEmpty(),
                                    personalSkillsJson = personalSkillsJson,
                                    inventoryJson = "{}",
                                    notesText = ""
                                )

                                characterViewModel.addCharacter(
                                    input = creationInput,
                                    onSaved = {
                                        navController.navigateToCharactersAfterCreation(sessionId, sessionName)
                                    },
                                    onError = {
                                        navController.navigateToCharactersAfterCreation(sessionId, sessionName)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun NavHostController.navigateToCharactersAfterCreation(sessionId: Int, sessionName: String) {
        val route = AppRoutes.characters(sessionId, sessionName)
        runCatching {
            navigate(route) {
                launchSingleTop = true
            }
        }.onFailure {
            // Last-resort fallback that keeps the app alive even if route state is unexpected.
            navigate(AppRoutes.SESSIONS) {
                launchSingleTop = true
            }
        }
    }

    private fun saveStatsData(savedStateHandle: androidx.lifecycle.SavedStateHandle, statsData: CharacterStatsData) {
        savedStateHandle[CharacterCreationStateKeys.STR] = statsData.strength
        savedStateHandle[CharacterCreationStateKeys.CON] = statsData.constitution
        savedStateHandle[CharacterCreationStateKeys.SIZ] = statsData.size
        savedStateHandle[CharacterCreationStateKeys.DEX] = statsData.dexterity
        savedStateHandle[CharacterCreationStateKeys.APP] = statsData.appearance
        savedStateHandle[CharacterCreationStateKeys.EDU] = statsData.education
        savedStateHandle[CharacterCreationStateKeys.POW] = statsData.power
        savedStateHandle[CharacterCreationStateKeys.INT] = statsData.intelligence
        savedStateHandle[CharacterCreationStateKeys.MOVE] = statsData.move
    }

    private fun readStatsData(savedStateHandle: androidx.lifecycle.SavedStateHandle): CharacterStatsData? {
        val strength = savedStateHandle.get<Int>(CharacterCreationStateKeys.STR)
        val constitution = savedStateHandle.get<Int>(CharacterCreationStateKeys.CON)
        val size = savedStateHandle.get<Int>(CharacterCreationStateKeys.SIZ)
        val dexterity = savedStateHandle.get<Int>(CharacterCreationStateKeys.DEX)
        val appearance = savedStateHandle.get<Int>(CharacterCreationStateKeys.APP)
        val education = savedStateHandle.get<Int>(CharacterCreationStateKeys.EDU)
        val power = savedStateHandle.get<Int>(CharacterCreationStateKeys.POW)
        val intelligence = savedStateHandle.get<Int>(CharacterCreationStateKeys.INT)
        val move = savedStateHandle.get<Int>(CharacterCreationStateKeys.MOVE)

        if (
            strength == null || constitution == null || size == null || dexterity == null ||
            appearance == null || education == null || power == null || intelligence == null || move == null
        ) {
            return null
        }

        return CharacterStatsData(
            strength = strength,
            constitution = constitution,
            size = size,
            dexterity = dexterity,
            appearance = appearance,
            education = education,
            power = power,
            intelligence = intelligence,
            move = move
        )
    }

    private fun characterViewModelFactory(sessionId: Int): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CharacterViewModel(db.sessionDao(), sessionId) as T
            }
        }
    }

    private fun characterDetailViewModelFactory(sessionId: Int, characterId: Int): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CharacterDetailViewModel(db.sessionDao(), sessionId, characterId) as T
            }
        }
    }
}