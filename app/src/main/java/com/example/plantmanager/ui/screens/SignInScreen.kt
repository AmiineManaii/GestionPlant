package com.example.plantmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    authViewModel: com.example.plantmanager.viewmodels.AuthViewModel,
    onSignedIn: () -> Unit,
    onGoToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val enabled = email.isNotBlank() && password.isNotBlank()
    val state by authViewModel.authState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Se connecter", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
            }
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state is com.example.plantmanager.viewmodels.AuthViewModel.AuthState.Error) {
                        Text(
                            (state as com.example.plantmanager.viewmodels.AuthViewModel.AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Button(
                        onClick = { authViewModel.signIn(email, password) },
                        enabled = enabled,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Connexion") }
                    TextButton(onClick = onGoToSignUp, modifier = Modifier.fillMaxWidth()) { Text("Créer un compte") }
                }
            }
        }
    }
    LaunchedEffect(state) {
        if (state is com.example.plantmanager.viewmodels.AuthViewModel.AuthState.SignedIn) {
            onSignedIn()
        }
    }
}
