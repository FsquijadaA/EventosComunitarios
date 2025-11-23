package com.example.eventoscomunitarios.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.eventoscomunitarios.data.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val userEmail: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    // -------- Helpers de estado --------
    fun resetState() {
        uiState = AuthUiState()
    }

    fun setError(message: String) {
        uiState = uiState.copy(
            isLoading = false,
            isSuccess = false,
            errorMessage = message
        )
    }

    // -------- Registro con correo --------
    fun register(name: String, community: String, email: String, password: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        repository.registerWithEmail(name, community, email, password) { success, error ->
            uiState = if (success) {
                uiState.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = error ?: "Error al crear la cuenta"
                )
            }
        }
    }

    // -------- Login con correo --------
    fun login(email: String, password: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        repository.loginWithEmail(email, password) { success, error ->
            uiState = if (success) {
                uiState.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = error ?: "Error al iniciar sesión"
                )
            }
        }
    }

    // -------- Login con Google --------
    fun loginWithGoogle(credential: AuthCredential) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        FirebaseAuth.getInstance()
            .signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                uiState = uiState.copy(
                    isLoading = false,
                    isSuccess = true,
                    isAuthenticated = user != null,
                    userEmail = user?.email,
                    errorMessage = null
                )
            }
            .addOnFailureListener { e ->
                uiState = uiState.copy(
                    isLoading = false,
                    isSuccess = false,
                    isAuthenticated = false,
                    errorMessage = e.message ?: "Error al iniciar con Google"
                )
            }
    }

    // -------- Logout --------
    fun logout() {
        repository.logout()
        uiState = AuthUiState()
    }
}
