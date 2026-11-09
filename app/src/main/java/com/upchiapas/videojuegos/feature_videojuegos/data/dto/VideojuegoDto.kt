package com.upchiapas.videojuegos.feature_videojuegos.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideojuegoDto(
    val id: Int = 0,
    val titulo: String,
    val plataforma: String,
    val precio: Double,
    val horasJugadas: Int = 0,
    val completado: Boolean = false,
    val genero: String = "Acción"
)

@Serializable
data class VideojuegoRequestDto(
    val titulo: String,
    val plataforma: String,
    val precio: Double,
    val horasJugadas: Int = 0,
    val completado: Boolean = false,
    val genero: String = "Acción"
)
