package saulo.fernando.daily_manager.manager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import saulo.fernando.daily_manager.account.AuthRepository
import saulo.fernando.daily_manager.account.LoginScreen

@Composable
fun MyApp() {
    val authRepository = remember { AuthRepository() }
    val isUserLoggedIn = remember { mutableStateOf(authRepository.isUserLoggedIn()) }

    if (isUserLoggedIn.value) {
        MainScreen(authRepository) {
            isUserLoggedIn.value = false
        }
    } else {
        LoginScreen(authRepository) {
            isUserLoggedIn.value = true
        }
    }
}
