package com.upchiapas.videojuegos.feature_videojuegos.presentation.home

import com.upchiapas.videojuegos.feature_videojuegos.domain.model.Videojuego

data class VideojuegosUiState(
    val videojuegos: List<Videojuego> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedVideojuego: Videojuego? = null,
    val isFormOpen: Boolean = false,
    val editingVideojuego: Videojuego? = null,
    val deleteCandidate: Videojuego? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val userFeedbackMessage: String? = null
)
