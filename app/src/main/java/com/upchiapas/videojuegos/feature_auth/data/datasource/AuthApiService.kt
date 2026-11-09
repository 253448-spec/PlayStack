package com.upchiapas.videojuegos.feature_auth.data.datasource

import com.upchiapas.videojuegos.feature_auth.data.dto.AuthResponseDto
import com.upchiapas.videojuegos.feature_auth.data.dto.LoginRequestDto
import com.upchiapas.videojuegos.feature_auth.data.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): AuthResponseDto

    @POST("login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): AuthResponseDto
}
