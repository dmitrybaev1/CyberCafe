package ru.shawarma.core.data.managers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.utils.dataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcher: CoroutineDispatcher
): TokenManager {

    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val TOKEN_TYPE = stringPreferencesKey("token_type")
    private val EXPIRES_IN = longPreferencesKey("expires_in")

    override suspend fun update(authData: AuthData) {
        withContext(dispatcher) {
            context.dataStore.edit { prefs ->
                authData.apply {
                    prefs[ACCESS_TOKEN] = accessToken
                    prefs[REFRESH_TOKEN] = refreshToken
                    prefs[TOKEN_TYPE] = tokenType
                    prefs[EXPIRES_IN] = expiresIn
                }
            }
        }
    }

    override suspend fun getAuthData(): AuthData =
        withContext(dispatcher){
            val prefs = context.dataStore.edit {  }
            val accessToken = prefs[ACCESS_TOKEN] ?: ""
            val refreshToken = prefs[REFRESH_TOKEN] ?: ""
            val tokenType = prefs[TOKEN_TYPE] ?: ""
            val expiresIn = prefs[EXPIRES_IN] ?: 0
            AuthData(accessToken, refreshToken, tokenType, expiresIn)
        }

}