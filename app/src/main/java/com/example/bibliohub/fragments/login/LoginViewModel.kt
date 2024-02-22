package com.example.bibliohub.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.data.entities.user.UserRepository
import com.example.bibliohub.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginModel(
    var email: String = "",
    var password: String = "",
)

class LoginViewModel(
    private val userRepository: UserRepository,
    val biblioHubPreferencesRepository: BiblioHubPreferencesRepository,
) : ViewModel() {
    private var _loginModel: MutableStateFlow<LoginModel> = MutableStateFlow(LoginModel())
    val loginModel: StateFlow<LoginModel> = _loginModel.asStateFlow()

    suspend fun getUserDetails(email: String): User? {
        return userRepository.getUser(email = email)
    }

    fun resetLoginModel() {
        _loginModel.value = LoginModel()
    }
    suspend fun savePreferences(it: User) {
        biblioHubPreferencesRepository.savePreference(Constants.USER, it)
        biblioHubPreferencesRepository.savePreference(Constants.IS_LOGGED_IN, true)
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
                val userRepository = application.container.userRepository
                val biblioHubPreferencesRepository = application.biblioHubPreferencesRepository
                LoginViewModel(userRepository = userRepository, biblioHubPreferencesRepository = biblioHubPreferencesRepository)
            }
        }
    }
}