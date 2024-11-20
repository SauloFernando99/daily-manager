package saulo.fernando.daily_manager.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import saulo.fernando.daily_manager.account.AuthRepository
import saulo.fernando.daily_manager.composables.MyTopBar

@Composable
fun MainScreen(
    authRepository: AuthRepository,
    navController: NavController,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            MyTopBar(
                title = "My Agenda",
                onLogout = {
                    authRepository.logoutUser()
                    onLogout()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("agenda") }) {
                Text("Ir para Agenda")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("notepad") }) {
                Text("Bloco de Notas")
            }
        }
    }
}


