package com.capstone.Capstone2Project.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.Capstone2Project.data.preferences.UIMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UIModeDataStoreManager(
    private val context: Context
): DataStoreManager() {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("System_theme")
        private val UI_MODE = stringPreferencesKey("theme_mode")
    }

    val getCurrentThemeMode: Flow<UIMode> = context.dataStore.data
        .catch { exception->
            if(exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs->
            when(prefs[UI_MODE]) {
                UIMode.DARK_MODE.value -> UIMode.DARK_MODE
                UIMode.LIGHT_MODE.value -> UIMode.LIGHT_MODE
                else -> UIMode.SYSTEM_UI_MODE
            }
        }

    suspend fun saveUIMode(mode: UIMode) {
        context.dataStore.edit {prefs->
            prefs[UI_MODE] = mode.value
        }
    }
}