package saulo.fernando.daily_manager.account

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.format.TextStyle

@Composable
fun LoginScreen(authRepository: AuthRepository, navController: NavController, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf("") } // Mensagem de erro do email
    var passwordError by remember { mutableStateOf("") } // Mensagem de erro da senha
    var showDialog by remember { mutableStateOf(false) } // Controle para exibir o Dialog

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DAILY MANAGER",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 24.sp, // Aumentando o tamanho da fonte
                            color = Color.White, // Cor da fonte para branco
                        )
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary // Mantém a cor de fundo
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
            // Campo de email com validação
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    if (it.isBlank()) {
                        emailError = "Campo de email é obrigatório"
                    } else {
                        emailError = "" // Limpa o erro quando o campo é preenchido
                    }
                },
                label = { Text("Email") },
                isError = emailError.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            if (emailError.isNotBlank()) {
                Text(text = emailError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de senha com validação
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    if (it.isBlank()) {
                        passwordError = "Campo de senha é obrigatório"
                    } else {
                        passwordError = "" // Limpa o erro quando o campo é preenchido
                    }
                },
                label = { Text("Senha") },
                isError = passwordError.isNotBlank(),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (passwordError.isNotBlank()) {
                Text(text = passwordError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de login
            Button(onClick = {
                // Verifica se os campos estão vazios antes de chamar o login
                if (email.isBlank() || password.isBlank()) {
                    // Se algum campo estiver vazio, exibe uma mensagem de erro
                    if (email.isBlank()) {
                        emailError = "Campo de email é obrigatório"
                    }
                    if (password.isBlank()) {
                        passwordError = "Campo de senha é obrigatório"
                    }
                } else {
                    // Caso contrário, realiza o login
                    authRepository.loginUser(email, password,
                        onSuccess = {
                            onLoginSuccess()  // Chamando o sucesso do login
                        },
                        onFailure = { error ->
                            // Se o login falhar, exibe uma mensagem de erro no Dialog
                            errorMessage = error.localizedMessage ?: "Credenciais inválidas. Tente novamente."
                            showDialog = true // Exibe o dialog de erro
                        })
                }
            }) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão para navegar para a tela de cadastro
            Button(onClick = { navController.navigate("signup") }) {
                Text("Cadastrar-se")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // AlertDialog de erro de login
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Erro de Login") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
