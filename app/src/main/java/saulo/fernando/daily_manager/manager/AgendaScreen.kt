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
import saulo.fernando.daily_manager.account.AuthRepository
import saulo.fernando.daily_manager.manager.models.Event
import saulo.fernando.daily_manager.manager.models.fetchEvents

@Composable
fun AgendaScreen() {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Sua Agenda", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (events.isNotEmpty()) {
            LazyColumn {
                items(events) { event ->
                    Text(text = "Título: ${event.title}")
                    Text(text = "Descrição: ${event.description}")
                    Text(text = "Data: ${event.date}")
                    Text(text = "Hora: ${event.time}")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else if (errorMessage.isNotEmpty()) {
            Text("Erro: $errorMessage", color = Color.Red)
        } else {
            Text("Nenhum evento encontrado.")
        }

    }
}
