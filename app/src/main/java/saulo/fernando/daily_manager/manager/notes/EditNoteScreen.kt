package saulo.fernando.daily_manager.manager.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import saulo.fernando.daily_manager.account.AuthRepository

@Composable
fun EditNoteScreen(
    notesRepository: NotesRepository,
    userId: String,
    noteId: String,
    onNoteUpdated: () -> Unit,
    onNavigateBack: () -> Unit,
    authRepository: AuthRepository,
    navController: NavController
) {
    var note by remember { mutableStateOf<Note?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    // Buscar a nota do repositório
    LaunchedEffect(noteId) {
        notesRepository.getNotes(userId) { result ->
            result.onSuccess { notes ->
                note = notes.find { it.id == noteId }
            }.onFailure {
                errorMessage = it.localizedMessage ?: "Erro ao carregar nota"
            }
        }
    }

    Scaffold(
        topBar = {
            AddNoteTopBar(
                onLogout = {
                    authRepository.logoutUser()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navController = navController
            )
        }
    ) { paddingValues ->
        if (note == null) {
            Text(
                text = if (errorMessage.isNotEmpty()) errorMessage else "Carregando nota...",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            var title by remember { mutableStateOf(note!!.title) }
            var description by remember { mutableStateOf(note!!.description) }

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
                        notesRepository.editNote(
                            userId = userId,
                            noteId = noteId,
                            updatedNote = note!!.copy(title = title, description = description),
                            onSuccess = onNoteUpdated,
                            onFailure = {
                                errorMessage = it.localizedMessage ?: "Erro ao atualizar nota"
                            }
                        )
                    }) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}


@Composable
fun EditNoteTopBar(
    onLogout: () -> Unit,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            androidx.compose.material3.Text(
                text = "DAILY MANAGER",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.White
                )
            )
        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
        },
        actions = {
            androidx.compose.material.IconButton(onClick = { expanded = true }) {
                androidx.compose.material.Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    onLogout()
                }) {
                    androidx.compose.material.Text("Logout")
                }
            }
        }
    )
}
