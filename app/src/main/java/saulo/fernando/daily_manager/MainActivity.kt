package saulo.fernando.daily_manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import saulo.fernando.daily_manager.account.AuthRepository
import saulo.fernando.daily_manager.manager.MyApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authRepository = AuthRepository()
            MyApp(authRepository)
        }
    }
}
