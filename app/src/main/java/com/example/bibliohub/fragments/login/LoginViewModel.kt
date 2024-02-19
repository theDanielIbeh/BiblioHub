package com.example.bibliohub.fragments.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bibliohub.data.BiblioHubDatabase
import com.example.bibliohub.data.entities.user.OfflineUserRepository
import com.example.bibliohub.data.entities.user.UserDao
import com.example.bibliohub.data.entities.user.UserRepository
import kotlinx.coroutines.launch

data class LoginModel(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = ""
)

class LoginViewModel(
    private val application: Application,
) : ViewModel() {
    var loginModel: LoginModel = LoginModel()
    private val userDao: UserDao = BiblioHubDatabase.getInstance(application).userDao()
    private val userRepository: UserRepository = OfflineUserRepository(userDao = userDao)

    fun getUserDetails(email: String) {
        viewModelScope.launch {
            userRepository.getUser(email = email)
        }
    }

    fun resetLoginModel() {
        loginModel = LoginModel()
    }

    class LoginViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(application = application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}