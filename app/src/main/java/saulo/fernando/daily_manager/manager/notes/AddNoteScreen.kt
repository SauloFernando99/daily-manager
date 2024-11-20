package saulo.fernando.daily_manager.manager.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
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
import saulo.fernando.daily_manager.composables.MyTopBar
import java.util.Date

@Composable
fun AddNoteScreen(
    notesRepository: NotesRepository,
    userId: String?,
    onNoteAdded: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MyTopBar(
                title = "Adicionar Nota",
                onLogout = { onNavigateBack() } // Navegar para tela anterior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onNavigateBack) {
                    Text("Cancelar")
                }
                Button(onClick = {
                    if (userId != null) {
                        // Criar o objeto Note
                        val note = Note(
                            title = title,
                            description = description,
                            date = Date()
                        )
                        notesRepository.addNote(
                            userId = userId,
                            note = note,
                            onSuccess = onNoteAdded,
                            onFailure = { errorMessage = it.localizedMessage ?: "Erro ao adicionar nota" }
                        )
                    } else {
                        errorMessage = "Usuário não autenticado"
                    }
                }) {
                    Text("Salvar")
                }
            }
        }
    }
}
