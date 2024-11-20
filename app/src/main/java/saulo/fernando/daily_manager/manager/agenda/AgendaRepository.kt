package saulo.fernando.daily_manager.manager.agenda

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class AgendaRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getFutureEvents(userId: String, onComplete: (Result<List<Event>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .whereGreaterThan("date", System.currentTimeMillis())  // Filtrando eventos futuros
            .get()
            .addOnSuccessListener { snapshot ->
                val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
                onComplete(Result.success(events.sortedBy { it.date }))  // Ordenando por data
            }
            .addOnFailureListener { exception ->
                Log.e("AgendaRepository", "Erro ao carregar eventos futuros", exception)
                onComplete(Result.failure(exception))
            }
    }

    fun getPastEvents(userId: String, onComplete: (Result<List<Event>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .whereLessThan("date", System.currentTimeMillis())  // Filtrando eventos passados
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d("AgendaRepository", "Nenhum evento passado encontrado")
                } else {
                    Log.d("AgendaRepository", "Eventos encontrados: ${snapshot.documents.size}")
                }

                val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
                onComplete(Result.success(events.sortedByDescending { it.date }))  // Ordenando por data
            }
            .addOnFailureListener { exception ->
                Log.e("AgendaRepository", "Erro ao carregar eventos passados", exception)
                onComplete(Result.failure(exception))
            }
    }

    fun addEvent(
        userId: String,
        event: Event,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .whereEqualTo("date", event.date)  // Verificando se já existe um evento na mesma data e hora
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    firestore.collection("users")
                        .document(userId)
                        .collection("events")
                        .add(event)  // Adicionando o evento
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("Já existe um evento nesta data e hora."))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }
}

