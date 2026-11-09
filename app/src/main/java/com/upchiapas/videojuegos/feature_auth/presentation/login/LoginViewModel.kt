package com.upchiapas.videojuegos.feature_auth.presentation.login

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

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    private fun validate(): Boolean {
        var isValid = true
        val state = _uiState.value

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

        if (emailError != null || passwordError != null) {
            isValid = false
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
        }

        return isValid
    }

    fun login(onSuccess: () -> Unit) {
        if (!validate()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.login(_uiState.value.email, _uiState.value.password)
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
                LoginViewModel(application.container.authRepository)
            }
        }
    }
}
