package saulo.fernando.daily_manager.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import saulo.fernando.daily_manager.account.AuthRepository

@Composable
fun MainScreen(authRepository: AuthRepository, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bem-vindo!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            authRepository.logoutUser()
            onLogout()
        }) {
            Text("Logout")
        }
    }
}
