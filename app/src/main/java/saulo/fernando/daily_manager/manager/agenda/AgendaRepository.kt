package saulo.fernando.daily_manager.manager.agenda

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class AgendaRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllEvents(userId: String, onComplete: (Result<List<Event>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .get()
            .addOnSuccessListener { snapshot ->
                val events = snapshot.documents.mapNotNull { document ->
                    try {
                        val title = document.getString("title") ?: ""
                        val description = document.getString("description") ?: ""
                        val place = document.getString("place") ?: ""
                        val date = document.getLong("date") ?: 0L
                        val id = document.id
                        Event(id = id, title = title, description = description, place = place, date = date)
                    } catch (e: Exception) {
                        null // Ignorar eventos mal formatados
                    }
                }
                onComplete(Result.success(events.sortedBy { it.date }))  // Ordenando por data
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    fun getEventById(
        userId: String,
        eventId: String,
        onComplete: (Result<Event?>) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .document(eventId)
            .get()
            .addOnSuccessListener { document ->
                val event = try {
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val place = document.getString("place") ?: ""
                    val date = document.getLong("date") ?: 0L
                    Event(id = document.id, title = title, description = description, place = place, date = date)
                } catch (e: Exception) {
                    null // Retorna null se o evento estiver mal formatado
                }
                onComplete(Result.success(event))
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }


    fun getFutureEvents(userId: String, onComplete: (Result<List<Event>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .whereGreaterThan("date", System.currentTimeMillis())  // Filtrando eventos futuros
            .get()
            .addOnSuccessListener { snapshot ->
                val events = snapshot.documents.mapNotNull { document ->
                    try {
                        val title = document.getString("title") ?: ""
                        val description = document.getString("description") ?: ""
                        val place = document.getString("place") ?: ""
                        val date = document.getLong("date") ?: 0L
                        val id = document.id
                        Event(id = id, title = title, description = description, place = place, date = date)
                    } catch (e: Exception) {
                        null // Ignorar eventos mal formatados
                    }
                }
                onComplete(Result.success(events.sortedBy { it.date }))  // Ordenando por data
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }


    fun getPastEvents(userId: String, onComplete: (Result<List<Event>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .whereLessThan("date", System.currentTimeMillis())  // Filtrando eventos passados
            .get()
            .addOnSuccessListener { snapshot ->
                val events = snapshot.documents.mapNotNull { document ->
                    try {
                        val title = document.getString("title") ?: ""
                        val description = document.getString("description") ?: ""
                        val place = document.getString("place") ?: ""
                        val date = document.getLong("date") ?: 0L
                        val id = document.id
                        Event(id = id, title = title, description = description, place = place, date = date)
                    } catch (e: Exception) {
                        null // Ignorar eventos mal formatados
                    }
                }
                onComplete(Result.success(events.sortedByDescending { it.date }))  // Ordenando por data
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
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

    fun updateEvent(
        userId: String,
        eventId: String,
        updatedEvent: Event,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .document(eventId)
            .set(updatedEvent)  // Substitui o documento pelo evento atualizado
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteEvent(
        userId: String,
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .document(eventId)
            .delete()  // Remove o documento do Firestore
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

}

