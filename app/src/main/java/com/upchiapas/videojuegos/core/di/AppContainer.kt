package com.upchiapas.videojuegos.core.di

import android.content.Context
import com.upchiapas.videojuegos.core.network.NetworkModule
import com.upchiapas.videojuegos.core.session.SessionManager
import com.upchiapas.videojuegos.core.session.SharedPreferencesSessionManager
import com.upchiapas.videojuegos.feature_auth.data.datasource.AuthApiService
import com.upchiapas.videojuegos.feature_auth.data.repository.AuthRepositoryImpl
import com.upchiapas.videojuegos.feature_auth.domain.repository.AuthRepository
import com.upchiapas.videojuegos.feature_videojuegos.data.datasource.VideojuegosApiService
import com.upchiapas.videojuegos.feature_videojuegos.data.repository.VideojuegosRepositoryImpl
import com.upchiapas.videojuegos.feature_videojuegos.domain.repository.VideojuegosRepository
import retrofit2.Retrofit

interface AppContainer {
    val sessionManager: SessionManager
    val authRepository: AuthRepository
    val videojuegosRepository: VideojuegosRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val sessionManager: SessionManager by lazy {
        SharedPreferencesSessionManager(context)
    }

    private val json by lazy {
        NetworkModule.provideJson()
    }

    private val okHttpClient by lazy {
        NetworkModule.provideOkHttpClient(sessionManager)
    }

    private val retrofit: Retrofit by lazy {
        NetworkModule.provideRetrofit(okHttpClient, json)
    }

    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val videojuegosApiService: VideojuegosApiService by lazy {
        retrofit.create(VideojuegosApiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService, sessionManager)
    }

    override val videojuegosRepository: VideojuegosRepository by lazy {
        VideojuegosRepositoryImpl(videojuegosApiService)
    }
}
