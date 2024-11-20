package saulo.fernando.daily_manager.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SignUpScreen(
    authRepository: AuthRepository,
    navController: NavController,
    onSignUpSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // Controlar exibição do Dialog

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DAILY MANAGER",
                        style = TextStyle(
                            fontSize = 24.sp, // Tamanho do texto
                            color = Color.White, // Cor do texto
                        )
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary, // Cor de fundo
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar",
                            tint = Color.White)
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Campo de e-mail com validação
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = if (it.isBlank()) "Campo de e-mail é obrigatório" else ""
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
                        passwordError = if (it.isBlank()) "Campo de senha é obrigatório" else ""
                    },
                    label = { Text("Senha") },
                    isError = passwordError.isNotBlank(),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (passwordError.isNotBlank()) {
                    Text(text = passwordError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de confirmação de senha com validação
                TextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = if (it.isBlank()) "Campo de confirmação de senha é obrigatório" else ""
                        if (it != password) {
                            confirmPasswordError = "As senhas não correspondem"
                        }
                    },
                    label = { Text("Confirme a Senha") },
                    isError = confirmPasswordError.isNotBlank(),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (confirmPasswordError.isNotBlank()) {
                    Text(text = confirmPasswordError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão de cadastro
                Button(onClick = {
                    isLoading = true
                    // Verifica se os campos estão vazios ou inválidos
                    if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || password != confirmPassword) {
                        isLoading = false
                        if (email.isBlank()) emailError = "Campo de e-mail é obrigatório"
                        if (password.isBlank()) passwordError = "Campo de senha é obrigatório"
                        if (confirmPassword.isBlank()) confirmPasswordError = "Campo de confirmação de senha é obrigatório"
                        if (password != confirmPassword) confirmPasswordError = "As senhas não correspondem"
                    } else {
                        // Se tudo estiver correto, realiza o cadastro
                        authRepository.registerUser(
                            email = email,
                            password = password,
                            onSuccess = {
                                isLoading = false
                                onSignUpSuccess()
                            },
                            onFailure = { error ->
                                isLoading = false
                                errorMessage = error.localizedMessage ?: "Erro desconhecido"
                                showDialog = true // Exibe o dialog de erro
                            }
                        )
                    }
                }) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Cadastrar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    )

    // AlertDialog para erro de cadastro
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Erro de Cadastro") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
