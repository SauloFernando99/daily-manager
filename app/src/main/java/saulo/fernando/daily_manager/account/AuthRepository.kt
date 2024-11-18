package saulo.fernando.daily_manager.account

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess() else onFailure(task.exception!!)
            }
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess() else onFailure(task.exception!!)
            }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
