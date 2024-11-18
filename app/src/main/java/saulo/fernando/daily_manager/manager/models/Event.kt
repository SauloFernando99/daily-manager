package saulo.fernando.daily_manager.manager.models

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class Event(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = ""
)

fun addEvent(userId: String, event: Event, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = Firebase.firestore
    val userEventsRef = db.collection("users").document(userId).collection("events")

    userEventsRef.add(event)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
}

fun fetchEvents(
    userId: String,
    onSuccess: (List<Event>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = Firebase.firestore
    val userEventsRef = db.collection("users").document(userId).collection("events")

    userEventsRef.get()
        .addOnSuccessListener { result ->
            val events = result.documents.mapNotNull { it.toObject(Event::class.java) }
            onSuccess(events)
        }
        .addOnFailureListener { onFailure(it) }
}