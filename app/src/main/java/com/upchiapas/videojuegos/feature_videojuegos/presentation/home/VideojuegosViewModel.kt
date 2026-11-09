package com.upchiapas.videojuegos.feature_videojuegos.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.videojuegos.KtApplication
import com.upchiapas.videojuegos.core.result.AppResult
import com.upchiapas.videojuegos.feature_auth.domain.repository.AuthRepository
import com.upchiapas.videojuegos.feature_videojuegos.domain.model.Videojuego
import com.upchiapas.videojuegos.feature_videojuegos.domain.repository.VideojuegosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VideojuegosViewModel(
    private val videojuegosRepository: VideojuegosRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideojuegosUiState())
    val uiState: StateFlow<VideojuegosUiState> = _uiState.asStateFlow()

    init {
        loadVideojuegos()
    }

    fun loadVideojuegos() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = videojuegosRepository.getVideojuegos()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            videojuegos = result.data,
                            errorMessage = null
                        )
                    }
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

    fun openCreateForm() {
        _uiState.update {
            it.copy(
                isFormOpen = true,
                editingVideojuego = null
            )
        }
    }

    fun openEditForm(videojuego: Videojuego) {
        _uiState.update {
            it.copy(
                isFormOpen = true,
                editingVideojuego = videojuego,
                selectedVideojuego = null
            )
        }
    }

    fun closeForm() {
        _uiState.update { it.copy(isFormOpen = false, editingVideojuego = null) }
    }

    fun saveVideojuego(
        titulo: String,
        plataforma: String,
        precio: Double,
        horasJugadas: Int,
        completado: Boolean,
        genero: String
    ) {
        val currentEdit = _uiState.value.editingVideojuego
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            if (currentEdit == null) {
                // Crear nuevo
                val nuevo = Videojuego(
                    id = 0,
                    titulo = titulo.trim(),
                    plataforma = plataforma.trim(),
                    precio = precio,
                    horasJugadas = horasJugadas,
                    completado = completado,
                    genero = genero.trim()
                )
                when (val result = videojuegosRepository.createVideojuego(nuevo)) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                isFormOpen = false,
                                userFeedbackMessage = "¡Videojuego creado con éxito!"
                            )
                        }
                        loadVideojuegos()
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                userFeedbackMessage = "Error al crear: ${result.error.toUserMessage()}"
                            )
                        }
                    }
                }
            } else {
                // Editar existente
                val actualizado = currentEdit.copy(
                    titulo = titulo.trim(),
                    plataforma = plataforma.trim(),
                    precio = precio,
                    horasJugadas = horasJugadas,
                    completado = completado,
                    genero = genero.trim()
                )
                when (val result = videojuegosRepository.updateVideojuego(actualizado.id, actualizado)) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                isFormOpen = false,
                                editingVideojuego = null,
                                userFeedbackMessage = "¡Videojuego actualizado con éxito!"
                            )
                        }
                        loadVideojuegos()
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                userFeedbackMessage = "Error al actualizar: ${result.error.toUserMessage()}"
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectVideojuego(videojuego: Videojuego) {
        _uiState.update { it.copy(selectedVideojuego = videojuego) }
    }

    fun clearSelectedVideojuego() {
        _uiState.update { it.copy(selectedVideojuego = null) }
    }

    fun requestDeleteConfirmation(videojuego: Videojuego) {
        _uiState.update {
            it.copy(
                deleteCandidate = videojuego,
                selectedVideojuego = null
            )
        }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(deleteCandidate = null) }
    }

    fun executeDelete() {
        val candidate = _uiState.value.deleteCandidate ?: return
        _uiState.update { it.copy(isDeleting = true) }

        viewModelScope.launch {
            when (val result = videojuegosRepository.deleteVideojuego(candidate.id)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            deleteCandidate = null,
                            userFeedbackMessage = "'${candidate.titulo}' eliminado correctamente"
                        )
                    }
                    loadVideojuegos()
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            deleteCandidate = null,
                            userFeedbackMessage = "Error al eliminar: ${result.error.toUserMessage()}"
                        )
                    }
                }
            }
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(userFeedbackMessage = null) }
    }

    fun getUserName(): String {
        return authRepository.getCurrentUserName()
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        authRepository.logout()
        onLogoutSuccess()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KtApplication)
                VideojuegosViewModel(
                    videojuegosRepository = application.container.videojuegosRepository,
                    authRepository = application.container.authRepository
                )
            }
        }
    }
}
