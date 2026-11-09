package com.upchiapas.videojuegos

import android.app.Application
import com.upchiapas.videojuegos.core.di.AppContainer
import com.upchiapas.videojuegos.core.di.DefaultAppContainer

class KtApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
