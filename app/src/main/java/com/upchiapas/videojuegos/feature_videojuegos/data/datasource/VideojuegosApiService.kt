package com.upchiapas.videojuegos.feature_videojuegos.data.datasource

import com.upchiapas.videojuegos.feature_videojuegos.data.dto.VideojuegoDto
import com.upchiapas.videojuegos.feature_videojuegos.data.dto.VideojuegoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VideojuegosApiService {

    @GET("videojuegos")
    suspend fun getVideojuegos(): List<VideojuegoDto>

    @GET("videojuegos/{id}")
    suspend fun getVideojuegoById(
        @Path("id") id: Int
    ): VideojuegoDto

    @POST("videojuegos")
    suspend fun createVideojuego(
        @Body request: VideojuegoRequestDto
    ): VideojuegoDto

    @PUT("videojuegos/{id}")
    suspend fun updateVideojuego(
        @Path("id") id: Int,
        @Body request: VideojuegoRequestDto
    ): VideojuegoDto

    @DELETE("videojuegos/{id}")
    suspend fun deleteVideojuego(
        @Path("id") id: Int
    ): Unit
}
