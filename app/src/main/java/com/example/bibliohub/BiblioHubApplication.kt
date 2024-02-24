package com.example.bibliohub

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.bibliohub.data.AppContainer
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.DefaultAppContainer

private const val BIBLIO_HUB_PREFERENCE_NAME = "biblioHub_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BIBLIO_HUB_PREFERENCE_NAME
)

class BiblioHubApplication : Application() {
    lateinit var container: AppContainer
    lateinit var biblioHubPreferencesRepository: BiblioHubPreferencesRepository
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        biblioHubPreferencesRepository = BiblioHubPreferencesRepository(dataStore)
    }
}