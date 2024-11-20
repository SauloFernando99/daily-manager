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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date

@Composable
fun AddEventScreen(
    agendaRepository: AgendaRepository,
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

    // Funções para abrir DatePicker e TimePicker
    val context = LocalContext.current
    val datePickerDialog = remember { DatePickerDialog(context) }
    val timePickerDialog = remember { TimePickerDialog(context, { _, hourOfDay, minute ->
        time = "$hourOfDay:$minute"
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateMillis  // Usando o timestamp atual
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        dateMillis = calendar.timeInMillis // Atualizando a data com a hora escolhida
    }, 0, 0, true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
        Button(onClick = {
            val calendar = Calendar.getInstance()
            datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }) {
            Text("Escolher Data")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botão para escolher a hora
        Button(onClick = {
            timePickerDialog.show()
        }) {
            Text("Escolher Hora")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (title.isNotEmpty() && description.isNotEmpty() && place.isNotEmpty()) {
                val event = Event(
                    title = title,
                    description = description,
                    place = place,
                    date = dateMillis, // Usando o timestamp calculado
                )
                agendaRepository.addEvent(userId, event, onSuccess = {
                    onEventAdded()  // Notificar que o evento foi adicionado
                }, onFailure = { error ->
                    errorMessage = error.localizedMessage ?: "Erro ao adicionar evento"
                })
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
