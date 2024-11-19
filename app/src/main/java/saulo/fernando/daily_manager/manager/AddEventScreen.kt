package saulo.fernando.daily_manager.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddEventScreen(
    firestoreRepository: FirestoreRepository,
    userId: String?,
    onEventAdded: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Adicionar Evento", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Campo para inserir nome do evento
        TextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Nome do Evento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Botão para adicionar evento
        Button(onClick = {
            if (eventName.isNotBlank()) {
                if (userId != null) {
                    firestoreRepository.addEvent(
                        userId = userId,
                        event = eventName,
                        onSuccess = {
                            onEventAdded()
                            onNavigateBack()
                        },
                        onFailure = { errorMessage = it.localizedMessage ?: "Erro ao adicionar evento" }
                    )
                } else {
                    errorMessage = "Usuário não encontrado"
                }
            } else {
                errorMessage = "O nome do evento não pode ser vazio."
            }
        }) {
            Text("Adicionar Evento")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Mensagem de erro
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        // Botão para voltar para a tela de Agenda
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack) {
            Text("Voltar para Agenda")
        }
    }
}
