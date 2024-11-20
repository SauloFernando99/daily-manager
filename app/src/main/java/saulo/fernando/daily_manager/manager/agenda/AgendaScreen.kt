package saulo.fernando.daily_manager.manager.agenda

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import saulo.fernando.daily_manager.account.AuthRepository
import java.util.Date
import java.util.Locale

@Composable
fun AgendaScreen(
    navController: NavController,
    agendaRepository: AgendaRepository,
    authRepository: AuthRepository
) {
    val currentUser = authRepository.getCurrentUser()
    var events by remember { mutableStateOf(listOf<Event>()) }
    var showPastEvents by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(showPastEvents) {
        currentUser?.uid?.let { userId ->
            if (showPastEvents) {
                agendaRepository.getPastEvents(userId) { result ->
                    result.onSuccess { loadedEvents ->
                        events = loadedEvents // Atualizando a lista de eventos passados
                        Log.d("AgendaScreen", "Eventos passados carregados: ${events.size}")
                    }.onFailure { error ->
                        errorMessage = error.localizedMessage ?: "Erro ao carregar eventos"
                    }
                }
            } else {
                agendaRepository.getFutureEvents(userId) { result ->
                    result.onSuccess { loadedEvents ->
                        events = loadedEvents // Atualizando a lista de eventos futuros
                        Log.d("AgendaScreen", "Eventos futuros carregados: ${events.size}")
                    }.onFailure { error ->
                        errorMessage = error.localizedMessage ?: "Erro ao carregar eventos"
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AgendaTopBar(
                showPastEvents = showPastEvents,
                onToggleShowPast = { showPastEvents = !showPastEvents },
                onShowCalendar = { navController.navigate("calendar") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addEvent") }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Evento")
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
                Text(text = errorMessage, color = Color.Red)
            } else {
                LazyColumn {
                    if (events.isEmpty()) {
                        item {
                            Text("Nenhum evento encontrado.", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        items(events) { event ->
                            EventItem(event = event)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun AgendaTopBar(
    showPastEvents: Boolean,
    onToggleShowPast: () -> Unit,
    onShowCalendar: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Minha Agenda") },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    onToggleShowPast()
                }) {
                    Text(if (showPastEvents) "Ocultar Eventos Passados" else "Exibir Eventos Passados")
                }
                DropdownMenuItem(onClick = {
                    expanded = false
                    onShowCalendar()
                }) {
                    Text("Mostrar Calendário")
                }
            }
        }
    )
}

@Composable
fun EventItem(event: Event) {
    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())

    // Convertendo o timestamp de data para o formato de string
    val formattedDate = dateFormat.format(Date(event.date))
    // Convertendo o timestamp de hora para o formato de string
    val formattedTime = timeFormat.format(Date(event.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Local: ${event.place}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Data: $formattedDate", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Hora: $formattedTime", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
