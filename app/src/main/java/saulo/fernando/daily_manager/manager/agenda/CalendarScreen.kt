package saulo.fernando.daily_manager.manager.agenda

import android.widget.CalendarView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import saulo.fernando.daily_manager.account.AuthRepository
import java.util.Calendar

@Composable
fun CalendarScreen(
    navController: NavController,
    agendaRepository: AgendaRepository,
    authRepository: AuthRepository
) {
    val currentUser = authRepository.getCurrentUser()
    var markedDates by remember { mutableStateOf(mapOf<Long, List<Event>>()) } // Mapeia datas para eventos
    var selectedEvents by remember { mutableStateOf(listOf<Event>()) } // Eventos da data selecionada
    var selectedDate by remember { mutableStateOf<Long?>(null) } // Data atualmente selecionada

    // Carregar eventos futuros do Firestore
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userId ->
            agendaRepository.getFutureEvents(userId) { result ->
                result.onSuccess { events ->
                    markedDates = events.groupBy {
                        Calendar.getInstance().apply {
                            timeInMillis = it.date
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    }
                }
            }
        }
    }
    Scaffold(
        topBar = {
            SimpleAgendaTopBar(
                navController = navController,
                onLogout = {authRepository.logoutUser()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    ) { paddingValues ->

        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Spacer(modifier = Modifier.height(16.dp))

            AndroidView(
                factory = { context ->
                    CalendarView(context).apply {
                        selectedDate?.let { this.date = it }

                        setOnDateChangeListener { _, year, month, dayOfMonth ->
                            val selected = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth, 0, 0, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis

                            selectedDate = selected
                            selectedEvents = markedDates[selected] ?: emptyList()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                if (selectedEvents.isEmpty()) {
                    item {
                        Text(
                            "Nenhum evento para esta data.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(selectedEvents) { event ->
                        //EventItem(event = event)
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleAgendaTopBar(
    navController: NavController,
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            androidx.compose.material3.Text(
                text = "DAILY MANAGER",
                style = TextStyle(
                    fontSize = 24.sp, // Tamanho do texto
                    color = Color.White // Cor do texto
                )
            )
        },
        backgroundColor = MaterialTheme.colorScheme.primary, // Cor de fundo
        navigationIcon = {
            androidx.compose.material3.IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    onLogout()
                }) {
                    Text("Logout")
                }
            }
        }
    )
}

