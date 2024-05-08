package com.example.bibliohub.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.ProductList
import com.example.bibliohub.data.entities.product.ProductRepository
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.data.entities.user.UserRepository
import com.example.bibliohub.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginModel(
    var email: String = "",
    var password: String = "",
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
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

    suspend fun saveAdminPreferences() {
        biblioHubPreferencesRepository.savePreference(Constants.IS_ADMIN, true)
        biblioHubPreferencesRepository.savePreference(Constants.IS_LOGGED_IN, true)
    }

    fun insertProducts() {
        val products = ProductList.list()
        products.forEach { product ->
            viewModelScope.launch {
                productRepository.insert(product = product)
            }
        }
    }
//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application =
//                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
//                val userRepository = application.container.userRepository
//                val productRepository = application.container.productRepository
//                val biblioHubPreferencesRepository = application.biblioHubPreferencesRepository
//                LoginViewModel(
//                    userRepository = userRepository,
//                    productRepository = productRepository,
//                    biblioHubPreferencesRepository = biblioHubPreferencesRepository)
//            }
//        }
//    }
}