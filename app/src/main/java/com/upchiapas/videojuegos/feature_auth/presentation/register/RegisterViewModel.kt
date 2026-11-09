package com.upchiapas.videojuegos.feature_auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.videojuegos.KtApplication
import com.upchiapas.videojuegos.core.result.AppResult
import com.upchiapas.videojuegos.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, nameError = null, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, errorMessage = null) }
    }

    private fun validate(): Boolean {
        var isValid = true
        val state = _uiState.value

        val nameError = when {
            state.name.isBlank() -> "El nombre es obligatorio"
            state.name.trim().length < 2 -> "El nombre debe tener al menos 2 letras"
            else -> null
        }

        val emailError = when {
            state.email.isBlank() -> "El correo es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches() -> "Formato de correo no válido"
            else -> null
        }

        val passwordError = when {
            state.password.isBlank() -> "La contraseña es obligatoria"
            state.password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            else -> null
        }

        val confirmPasswordError = when {
            state.confirmPassword.isBlank() -> "Confirma tu contraseña"
            state.confirmPassword != state.password -> "Las contraseñas no coinciden"
            else -> null
        }

        if (nameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            isValid = false
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
        }

        return isValid
    }

    fun register(onSuccess: () -> Unit) {
        if (!validate()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.register(
                name = _uiState.value.name,
                email = _uiState.value.email,
                password = _uiState.value.password
            )
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KtApplication)
                RegisterViewModel(application.container.authRepository)
            }
        }
    }
}
