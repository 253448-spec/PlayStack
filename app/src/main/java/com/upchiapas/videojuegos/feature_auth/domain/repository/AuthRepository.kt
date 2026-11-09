package com.upchiapas.videojuegos.feature_auth.domain.repository

import com.upchiapas.videojuegos.core.result.AppResult
import com.upchiapas.videojuegos.feature_auth.domain.model.User

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): AppResult<User>
    suspend fun login(email: String, password: String): AppResult<User>
    fun isLoggedIn(): Boolean
    fun logout()
    fun getCurrentUserName(): String
}
