package saulo.fernando.daily_manager.manager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import saulo.fernando.daily_manager.account.AuthRepository
import saulo.fernando.daily_manager.account.LoginScreen
import saulo.fernando.daily_manager.account.SignUpScreen

@Composable
fun MyApp() {
    val authRepository = remember { AuthRepository() }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(authRepository) {
                navController.navigate("main")
            }
        }
        composable("signup") {
            SignUpScreen(authRepository) {
                navController.popBackStack() // Voltar para o login
            }
        }
        composable("main") {
            MainScreen(authRepository) {
                navController.navigate("login") {
                    popUpTo("main") { inclusive = true }
                }
            }
        }
    }
}

