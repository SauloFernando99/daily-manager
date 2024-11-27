package saulo.fernando.daily_manager.manager.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import java.util.Date
import java.util.Locale

@Composable
fun EditEventScreen(
    agendaRepository: AgendaRepository,
    userId: String,
    eventId: String,
    onEventUpdated: () -> Unit,
    onNavigateBack: () -> Unit,
    authRepository: AuthRepository,
    navController: NavController
) {
    var event by remember { mutableStateOf<Event?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    // Buscar o evento do repositório
    LaunchedEffect(eventId) {
        agendaRepository.getAllEvents(userId) { result ->
            result.onSuccess { events ->
                event = events.find { it.id == eventId }
            }.onFailure {
                errorMessage = it.localizedMessage ?: "Erro ao carregar evento"
            }
        }
    }

    Scaffold(
        topBar = {
            EditEventTopBar(
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
        if (event == null) {
            Text(
                text = if (errorMessage.isNotEmpty()) errorMessage else "Carregando evento...",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            var title by remember { mutableStateOf(event!!.title) }
            var description by remember { mutableStateOf(event!!.description) }
            var place by remember { mutableStateOf(event!!.place) }
            var date by remember { mutableStateOf(Date(event!!.date)) }

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
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("Local") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo para editar a data (pode ser substituído por um DatePicker)
                TextField(
                    value = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date),
                    onValueChange = {
                        try {
                            date = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(it) ?: date
                        } catch (e: Exception) {
                            errorMessage = "Formato inválido de data"
                        }
                    },
                    label = { Text("Data e Hora (dd/MM/yyyy HH:mm)") },
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
                        agendaRepository.updateEvent(
                            userId = userId,
                            eventId = eventId,
                            updatedEvent = event!!.copy(
                                title = title,
                                description = description,
                                place = place,
                                date = date.time
                            ),
                            onSuccess = onEventUpdated,
                            onFailure = {
                                errorMessage = it.localizedMessage ?: "Erro ao atualizar evento"
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
fun EditEventTopBar(
    onLogout: () -> Unit,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "Editar Evento",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.White
                )
            )
        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
        },
        actions = {
            androidx.compose.material.IconButton(onClick = { expanded = true }) {
                androidx.compose.material.Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
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

