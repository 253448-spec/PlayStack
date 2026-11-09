package com.upchiapas.videojuegos.core.session

import android.content.Context
import android.content.SharedPreferences

interface SessionManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveUser(id: Int, name: String, email: String)
    fun getUserName(): String?
    fun getUserEmail(): String?
    fun isLoggedIn(): Boolean
    fun clearSession()
}

class SharedPreferencesSessionManager(context: Context) : SessionManager {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    override fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    override fun saveUser(id: Int, name: String, email: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, id)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    override fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, "Gamer")
    }

    override fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    override fun isLoggedIn(): Boolean {
        val token = getToken()
        return !token.isNullOrBlank()
    }

    override fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "videojuegos_session_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }
}
