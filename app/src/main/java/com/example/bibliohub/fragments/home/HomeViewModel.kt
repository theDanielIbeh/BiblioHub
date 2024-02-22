package com.example.bibliohub.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.entities.user.UserRepository

class HomeViewModel(
    userRepository: UserRepository,
    biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    // TODO: Implement the ViewModel

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
                val userRepository = application.container.userRepository
                val biblioHubPreferencesRepository = application.biblioHubPreferencesRepository
                HomeViewModel(
                    userRepository = userRepository,
                    biblioHubPreferencesRepository = biblioHubPreferencesRepository
                )
            }
        }
    }
}