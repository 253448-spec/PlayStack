package com.upchiapas.videojuegos.feature_auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

@Serializable
data class AuthResponseDto(
    val token: String,
    val user: UserDto
)
