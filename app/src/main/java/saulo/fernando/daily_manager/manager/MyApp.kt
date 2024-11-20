package saulo.fernando.daily_manager.manager

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import saulo.fernando.daily_manager.account.AuthRepository
import saulo.fernando.daily_manager.account.LoginScreen
import saulo.fernando.daily_manager.account.SignUpScreen
import saulo.fernando.daily_manager.manager.agenda.AddEventScreen
import saulo.fernando.daily_manager.manager.agenda.AgendaRepository
import saulo.fernando.daily_manager.manager.agenda.AgendaScreen
import saulo.fernando.daily_manager.manager.agenda.CalendarScreen
import saulo.fernando.daily_manager.manager.notes.AddNoteScreen
import saulo.fernando.daily_manager.manager.notes.NotepadScreen
import saulo.fernando.daily_manager.manager.notes.NotesRepository

@Composable
fun MyApp(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val firestoreRepository = FirestoreRepository()
    val notesRepository = NotesRepository()
    val agendaRepository = AgendaRepository()

    // Navegação principal
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Tela de login
        composable("login") {
            LoginScreen(
                authRepository = authRepository,
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Tela de cadastro
        composable("signup") {
            SignUpScreen(
                authRepository = authRepository,
                navController = navController,
                onSignUpSuccess = {
                    navController.navigate("main") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // Tela principal (onde o usuário pode acessar a agenda e notas)
        composable("main") {
            MainScreen(
                authRepository = authRepository,
                navController = navController,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        // Tela de agenda
        composable("agenda") {
            AgendaScreen(
                navController = navController,
                agendaRepository = agendaRepository,
                authRepository = authRepository
            )
        }

        // Tela de calendário
        composable("calendar") {
            CalendarScreen(
                navController = navController,
                agendaRepository = agendaRepository,
                authRepository = authRepository
            )
        }

        // Tela de bloco de notas
        composable("notepad") {
            NotepadScreen(
                navController = navController,
                notesRepository = notesRepository,
                authRepository = authRepository
            )
        }

        // Tela de adicionar nota
        composable("addNote") {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId != null) {
                AddNoteScreen(
                    notesRepository = notesRepository,
                    userId = userId,
                    onNoteAdded = {
                        navController.navigate("notepad") {
                            popUpTo("notepad") { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                // Redirecionar para login se o usuário não estiver autenticado
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        // Tela de adicionar evento
        composable("addEvent") {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId != null) {
                AddEventScreen(
                    agendaRepository = agendaRepository,
                    userId = userId,
                    onEventAdded = {
                        navController.navigate("agenda") {
                            popUpTo("agenda") { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }
}
