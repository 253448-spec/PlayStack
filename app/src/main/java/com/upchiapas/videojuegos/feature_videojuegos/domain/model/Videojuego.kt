package com.upchiapas.videojuegos.feature_videojuegos.domain.model

data class Videojuego(
    val id: Int = 0,
    val titulo: String,
    val plataforma: String,
    val precio: Double,
    val horasJugadas: Int = 0,
    val completado: Boolean = false,
    val genero: String = "Acción"
)
