package saulo.fernando.daily_manager.manager

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import saulo.fernando.daily_manager.account.AuthRepository
import saulo.fernando.daily_manager.account.LoginScreen
import saulo.fernando.daily_manager.account.SignUpScreen

@Composable
fun MyApp(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val firestoreRepository = FirestoreRepository()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
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
        composable("agenda") {
            AgendaScreen(
                navController = navController,
                firestoreRepository = firestoreRepository,
                authRepository = authRepository
            )
        }
        composable("addEvent") {
            AddEventScreen(
                firestoreRepository = firestoreRepository,
                userId = authRepository.getCurrentUser()?.uid,
                onEventAdded = {
                    // Refresh events after adding
                    navController.navigate("agenda") {
                        popUpTo("agenda") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}


