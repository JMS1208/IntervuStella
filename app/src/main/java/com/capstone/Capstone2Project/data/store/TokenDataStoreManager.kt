package com.capstone.Capstone2Project.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.Capstone2Project.data.preferences.MobileToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class TokenDataStoreManager(
    private val context: Context
): DataStoreManager() {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Token_Data")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val getCurrentMobileToken: Flow<MobileToken> = context.dataStore.data
        .catch {exception->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs->
            val accessToken = prefs[ACCESS_TOKEN] ?: MobileToken.EMPTY_TOKEN.accessToken
            val refreshToken = prefs[REFRESH_TOKEN] ?: MobileToken.EMPTY_TOKEN.refreshToken

            MobileToken(accessToken, refreshToken)
        }

    suspend fun saveMobileToken(mobileToken: MobileToken) {
        context.dataStore.edit {prefs->
            prefs[ACCESS_TOKEN] = mobileToken.accessToken
            prefs[REFRESH_TOKEN] = mobileToken.refreshToken
        }
    }

}