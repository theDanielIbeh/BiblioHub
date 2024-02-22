package com.example.bibliohub.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class BiblioHubPreferencesRepository(
    private val datastore: DataStore<Preferences>
) {
    private val gson: Gson = Gson()

    private companion object {
        const val TAG = "BiblioHubPreferencesRepo"
        private const val SUCCESS = 0
        private const val FAIL = -1
    }

    suspend fun <T> savePreference(key: String, value: T): Int {
        when (value) {
            is String -> {
                datastore.edit { preferences ->
                    val preferenceKey = stringPreferencesKey(key)
                    preferences[preferenceKey] = value
                }
            }

            is Int -> {
                datastore.edit { preferences ->
                    val preferenceKey = intPreferencesKey(key)
                    preferences[preferenceKey] = value
                }
            }

            is Float -> {
                datastore.edit { preferences ->
                    val preferenceKey = floatPreferencesKey(key)
                    preferences[preferenceKey] = value
                }
            }

            is Long -> {
                datastore.edit { preferences ->
                    val preferenceKey = longPreferencesKey(key)
                    preferences[preferenceKey] = value
                }
            }

            is Boolean -> {
                datastore.edit { preferences ->
                    val preferenceKey = booleanPreferencesKey(key)
                    preferences[preferenceKey] = value
                }
            }

            else -> {
                try {
                    datastore.edit { preferences ->
                        val preferenceKey = stringPreferencesKey(key)
                        preferences[preferenceKey] = gson.toJson(value)
                    }
                } catch (e: Exception) {
                    try {
                        datastore.edit { preferences ->
                            val preferenceKey = stringPreferencesKey(key)
                            preferences[preferenceKey] = value.toString()
                        }
                    } catch (e: Exception) {
                        return FAIL
                    }
                }
            }
        }
        return SUCCESS
    }


    @Suppress("UNCHECKED_CAST")
    fun <T: Any?> getPreference(keyClassType: Class<T>, key: String): Flow<T?> =
        datastore.data.catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
            .map { preferences ->
                return@map when (keyClassType) {
                    String::class.java -> {
                        val preferenceKey = stringPreferencesKey(key)
                        (preferences[preferenceKey] as? T)!!
                    }

                    Int::class.java -> {
                        val preferenceKey = intPreferencesKey(key)
                        (preferences[preferenceKey] ?: 0) as T
                    }

                    Float::class.java -> {
                        val preferenceKey = floatPreferencesKey(key)
                        (preferences[preferenceKey] ?: 0) as T
                    }

                    Long::class.java -> {
                        val preferenceKey = longPreferencesKey(key)
                        (preferences[preferenceKey] ?: 0) as T
                    }

                    Boolean::class.java -> {
                        val preferenceKey = booleanPreferencesKey(key)
                        (preferences[preferenceKey] ?: false) as T
                    }

                    else -> {
                        try {
                            val preferenceKey = stringPreferencesKey(key)
                            val value = preferences[preferenceKey]
                            if (value.isNullOrBlank()) {
                                null
                            } else {
                                gson.fromJson(value, keyClassType)
                            }
                        } catch (e: Exception) {
                            Log.i(TAG, "getValue: Exception occurred in POJO Xform " + e.message)
                            null
                        }
                    }
                }

            }
}