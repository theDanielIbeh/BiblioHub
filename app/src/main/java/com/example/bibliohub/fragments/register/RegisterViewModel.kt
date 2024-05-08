package com.example.bibliohub.fragments.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.data.entities.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class RegisterModel(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
)

@HiltViewModel
class RegisterViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private var _registerModel: MutableStateFlow<RegisterModel> = MutableStateFlow(RegisterModel())
    val registerModel: StateFlow<RegisterModel> = _registerModel.asStateFlow()

    suspend fun getUserDetails(email: String): User? {
        return userRepository.getUser(email = email)
    }

    suspend fun insertUser() {
        val (
            firstName,
            lastName,
            email,
            password
        ) = registerModel.value
        userRepository.insert(
            user = User(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
        )
    }

    fun resetRegisterModel() {
        _registerModel.value = RegisterModel()
    }
}