package com.upchiapas.videojuegos.feature_videojuegos.data.repository

import com.upchiapas.videojuegos.core.result.AppResult
import com.upchiapas.videojuegos.core.result.safeApiCall
import com.upchiapas.videojuegos.feature_videojuegos.data.datasource.VideojuegosApiService
import com.upchiapas.videojuegos.feature_videojuegos.data.mapper.toDomain
import com.upchiapas.videojuegos.feature_videojuegos.data.mapper.toRequestDto
import com.upchiapas.videojuegos.feature_videojuegos.domain.model.Videojuego
import com.upchiapas.videojuegos.feature_videojuegos.domain.repository.VideojuegosRepository

class VideojuegosRepositoryImpl(
    private val apiService: VideojuegosApiService
) : VideojuegosRepository {

    override suspend fun getVideojuegos(): AppResult<List<Videojuego>> {
        return safeApiCall {
            apiService.getVideojuegos().map { it.toDomain() }
        }
    }

    override suspend fun getVideojuegoById(id: Int): AppResult<Videojuego> {
        return safeApiCall {
            apiService.getVideojuegoById(id).toDomain()
        }
    }

    override suspend fun createVideojuego(videojuego: Videojuego): AppResult<Videojuego> {
        return safeApiCall {
            apiService.createVideojuego(videojuego.toRequestDto()).toDomain()
        }
    }

    override suspend fun updateVideojuego(id: Int, videojuego: Videojuego): AppResult<Videojuego> {
        return safeApiCall {
            apiService.updateVideojuego(id, videojuego.toRequestDto()).toDomain()
        }
    }

    override suspend fun deleteVideojuego(id: Int): AppResult<Unit> {
        return safeApiCall {
            apiService.deleteVideojuego(id)
        }
    }
}
