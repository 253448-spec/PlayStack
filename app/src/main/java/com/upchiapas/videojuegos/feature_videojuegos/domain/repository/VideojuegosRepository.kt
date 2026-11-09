package com.upchiapas.videojuegos.feature_videojuegos.domain.repository

import com.upchiapas.videojuegos.core.result.AppResult
import com.upchiapas.videojuegos.feature_videojuegos.domain.model.Videojuego

interface VideojuegosRepository {
    suspend fun getVideojuegos(): AppResult<List<Videojuego>>
    suspend fun getVideojuegoById(id: Int): AppResult<Videojuego>
    suspend fun createVideojuego(videojuego: Videojuego): AppResult<Videojuego>
    suspend fun updateVideojuego(id: Int, videojuego: Videojuego): AppResult<Videojuego>
    suspend fun deleteVideojuego(id: Int): AppResult<Unit>
}
