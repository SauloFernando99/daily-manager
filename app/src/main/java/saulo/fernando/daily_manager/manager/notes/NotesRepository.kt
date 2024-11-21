package saulo.fernando.daily_manager.manager.notes

import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

data class Note(
    val id: String = "",
    val title: String,
    val description: String,
    val date: Date
)

class NotesRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun addNote(
        userId: String,
        note: Note,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val noteMap = mapOf(
            "title" to note.title,
            "description" to note.description,
            "date" to note.date
        )
        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .add(noteMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getNotes(
        userId: String,
        onComplete: (Result<List<Note>>) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .get()
            .addOnSuccessListener { snapshot ->
                val notes = snapshot.documents.mapNotNull { document ->
                    try {
                        val title = document.getString("title") ?: ""
                        val description = document.getString("description") ?: ""
                        val date = document.getDate("date") ?: Date()
                        val id = document.id
                        Note(id = id, title = title, description = description, date = date)
                    } catch (e: Exception) {
                        null // Ignorar notas mal formatadas
                    }
                }
                onComplete(Result.success(notes))
            }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    // Editar uma nota
    fun editNote(
        userId: String,
        noteId: String,
        updatedNote: Note,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val noteMap = mapOf(
            "title" to updatedNote.title,
            "description" to updatedNote.description,
            "date" to updatedNote.date
        )
        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .document(noteId)
            .set(noteMap) // Substitui os dados existentes
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Excluir uma nota
    fun deleteNote(
        userId: String,
        noteId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .document(noteId)
            .delete() // Remove o documento
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
