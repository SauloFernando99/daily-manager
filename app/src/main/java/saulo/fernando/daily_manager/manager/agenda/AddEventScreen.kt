package saulo.fernando.daily_manager.manager.agenda

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import saulo.fernando.daily_manager.account.AuthRepository
import java.util.Calendar
import java.util.Date

@Composable
fun AddEventScreen(
    agendaRepository: AgendaRepository,
    authRepository: AuthRepository,
    navController: NavController,
    userId: String,
    onEventAdded: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }  // Inicializando com timestamp atual
    var time by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Configuração do DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            dateMillis = calendar.timeInMillis // Atualiza a data selecionada
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Configuração do TimePickerDialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            dateMillis = calendar.timeInMillis // Atualiza com hora selecionada
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
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

            // Botão para escolher a data
            Button(onClick = { datePickerDialog.show() }) {
                Text("Escolher Data")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão para escolher a hora
            Button(onClick = { timePickerDialog.show() }) {
                Text("Escolher Hora")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                if (title.isNotEmpty() && description.isNotEmpty() && place.isNotEmpty()) {
                    val event = Event(
                        title = title,
                        description = description,
                        place = place,
                        date = dateMillis
                    )
                    agendaRepository.addEvent(
                        userId,
                        event,
                        onSuccess = {
                            onEventAdded()  // Notificar que o evento foi adicionado
                        },
                        onFailure = { error ->
                            errorMessage = error.localizedMessage ?: "Erro ao adicionar evento"
                        }
                    )
                } else {
                    errorMessage = "Por favor, preencha todos os campos."
                }
            }) {
                Text("Salvar Evento")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            Row {
                Button(onClick = onNavigateBack) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Composable
fun AddEventTopBar(
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