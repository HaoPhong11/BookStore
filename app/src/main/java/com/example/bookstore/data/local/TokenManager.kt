package com.example.bookstore.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class TokenManager(private val context: Context) {
    companion object {
        val TOKEN_KEY    = stringPreferencesKey("jwt_token")
        val USER_ID_KEY  = longPreferencesKey("user_id")
        val USERNAME_KEY = stringPreferencesKey("username")

        /** Decode JWT payload (phần giữa) để lấy "sub" = username */
        fun decodeUsernameFromJwt(jwt: String): String? = try {
            val payload = jwt.split(".")[1]
            val decoded = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING)
            JSONObject(String(decoded)).optString("sub").ifBlank { null }
        } catch (e: Exception) { null }
    }

    val token:    Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userId:   Flow<Long?>   = context.dataStore.data.map { it[USER_ID_KEY] }
    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { it[USER_ID_KEY] = userId }
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { it[USERNAME_KEY] = username }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { it.remove(USER_ID_KEY) }
    }

    suspend fun clearAll() {
        context.dataStore.edit {
            it.remove(TOKEN_KEY)
            it.remove(USER_ID_KEY)
            it.remove(USERNAME_KEY)
        }
    }
}