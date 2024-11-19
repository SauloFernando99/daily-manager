package saulo.fernando.daily_manager.manager

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun addEvent(userId: String, event: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val eventMap = mapOf("name" to event)
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .add(eventMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getEvents(userId: String, onComplete: (Result<List<String>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("events")
            .get()
            .addOnSuccessListener { snapshot ->
                val events = snapshot.documents.mapNotNull { it.getString("name") }
                onComplete(Result.success(events))
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }
}
