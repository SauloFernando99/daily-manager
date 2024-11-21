package saulo.fernando.daily_manager.manager.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import saulo.fernando.daily_manager.account.AuthRepository
import saulo.fernando.daily_manager.composables.MyTopBar

@Composable
fun NotepadScreen(
    navController: NavController,
    notesRepository: NotesRepository,
    authRepository: AuthRepository
) {
    val currentUser = authRepository.getCurrentUser()
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            notesRepository.getNotes(currentUser.uid) { result ->
                result.onSuccess { notes = it }
                    .onFailure { errorMessage = it.localizedMessage ?: "Erro ao carregar notas" }
            }
        } else {
            errorMessage = "Usuário não autenticado. Por favor, faça login."
        }
    }

    Scaffold(
        topBar = {
            NotePadTopBar(
                onLogout = {authRepository.logoutUser()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
                navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addNote") }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Nota")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else if (notes.isEmpty()) {
                Text(
                    text = "Nenhuma nota disponível. Adicione sua primeira nota!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onEdit = { selectedNote ->
                                navController.navigate("editNote/${selectedNote.id}")
                            },
                            onDelete = { selectedNote ->
                                notesRepository.deleteNote(
                                    userId = currentUser?.uid ?: "",
                                    noteId = selectedNote.id,
                                    onSuccess = {
                                        notes = notes.filter { it.id != selectedNote.id }
                                    },
                                    onFailure = {
                                        errorMessage = "Erro ao excluir a nota: ${it.localizedMessage}"
                                    }
                                )
                            },
                            navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onEdit: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Data: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(note.date)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Row para os botões Editar e Excluir
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate("editNote/${note.id}") },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Editar", color = Color.White)
                }

                Button(
                    onClick = { onDelete(note) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun NotePadTopBar(
    onLogout: () -> Unit,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            androidx.compose.material3.Text(
                text = "DAILY MANAGER",
                style = TextStyle(
                    fontSize = 24.sp, // Tamanho do texto
                    color = Color.White, // Cor do texto
                )
            )
        },
        backgroundColor = MaterialTheme.colorScheme.primary, // Cor de fundo
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
