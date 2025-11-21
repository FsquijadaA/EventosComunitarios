package com.example.eventoscomunitarios.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.eventoscomunitarios.data.repository.AuthRepository
import com.google.firebase.auth.AuthCredential

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun register(name: String, community: String, email: String, password: String) {
        if (name.isBlank() || community.isBlank() || email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Todos los campos son obligatorios")
            return
        }

        uiState = AuthUiState(isLoading = true)

        repository.registerWithEmail(name, community, email, password) { success, error ->
            uiState = if (success) {
                AuthUiState(isSuccess = true)
            } else {
                AuthUiState(errorMessage = error ?: "Error al registrar")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Correo y contraseña son obligatorios")
            return
        }

        uiState = AuthUiState(isLoading = true)

        repository.loginWithEmail(email, password) { success, error ->
            uiState = if (success) {
                AuthUiState(isSuccess = true)
            } else {
                AuthUiState(errorMessage = error ?: "Error al iniciar sesión")
            }
        }
    }

    fun loginWithGoogle(credential: AuthCredential) {
        uiState = AuthUiState(isLoading = true)

        repository.loginWithCredential(credential) { success, error ->
            uiState = if (success) {
                AuthUiState(isSuccess = true)
            } else {
                AuthUiState(errorMessage = error ?: "Error al iniciar con Google")
            }
        }
    }

    fun resetState() {
        uiState = AuthUiState()
    }

    fun setError(message: String) {
        uiState = AuthUiState(errorMessage = message)
    }

    fun logout() {
        repository.logout()
        uiState = AuthUiState()
    }
}
