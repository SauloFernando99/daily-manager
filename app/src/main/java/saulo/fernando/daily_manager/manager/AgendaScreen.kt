package saulo.fernando.daily_manager.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import saulo.fernando.daily_manager.account.AuthRepository

@Composable
fun AgendaScreen(
    navController: NavController,
    firestoreRepository: FirestoreRepository,
    authRepository: AuthRepository
) {
    val currentUser = authRepository.getCurrentUser()
    var events by remember { mutableStateOf(listOf<String>()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (currentUser != null) {
            firestoreRepository.getEvents(currentUser.uid) { result ->
                result.onSuccess { events = it }
                    .onFailure { errorMessage = it.localizedMessage ?: "Erro ao carregar eventos" }
            }
        } else {
            errorMessage = "Usuário não autenticado"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Minha Agenda", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn {
            items(events) { event ->
                Text(text = event, modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("addEvent") // Chama a tela de adicionar evento
        }) {
            Text("Adicionar Evento")
        }
    }
}
