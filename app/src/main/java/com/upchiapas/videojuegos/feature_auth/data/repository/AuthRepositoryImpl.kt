package com.upchiapas.videojuegos.feature_auth.data.repository

import com.upchiapas.videojuegos.core.result.AppResult
import com.upchiapas.videojuegos.core.result.safeApiCall
import com.upchiapas.videojuegos.core.session.SessionManager
import com.upchiapas.videojuegos.feature_auth.data.datasource.AuthApiService
import com.upchiapas.videojuegos.feature_auth.data.dto.LoginRequestDto
import com.upchiapas.videojuegos.feature_auth.data.dto.RegisterRequestDto
import com.upchiapas.videojuegos.feature_auth.domain.model.User
import com.upchiapas.videojuegos.feature_auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): AppResult<User> {
        val result = safeApiCall {
            val response = apiService.register(RegisterRequestDto(name.trim(), email.trim(), password))
            sessionManager.saveToken(response.token)
            sessionManager.saveUser(response.user.id, response.user.name, response.user.email)
            User(response.user.id, response.user.name, response.user.email)
        }
        return result
    }

    override suspend fun login(email: String, password: String): AppResult<User> {
        val result = safeApiCall {
            val response = apiService.login(LoginRequestDto(email.trim(), password))
            sessionManager.saveToken(response.token)
            sessionManager.saveUser(response.user.id, response.user.name, response.user.email)
            User(response.user.id, response.user.name, response.user.email)
        }
        return result
    }

    override fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    override fun logout() {
        sessionManager.clearSession()
    }

    override fun getCurrentUserName(): String {
        return sessionManager.getUserName() ?: "Gamer"
    }
}
