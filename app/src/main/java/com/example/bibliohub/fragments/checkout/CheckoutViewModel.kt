package com.example.bibliohub.fragments.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.entities.order.Order
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.data.entities.user.UserRepository
import com.example.bibliohub.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckoutModel(
    var address: String = "",
    var postcode: String = "",
    var cardNumber: String = "",
    var expiry: String = "",
    var cvv: String = "",
    var pin: String = "",
)

class CheckoutViewModel(
    private val userRepository: UserRepository,
    val orderRepository: OrderRepository,
    biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    private var _checkoutModel: MutableStateFlow<CheckoutModel> = MutableStateFlow(CheckoutModel())
    val checkoutModel: StateFlow<CheckoutModel> = _checkoutModel.asStateFlow()
    var loggedInUser: LiveData<User?>

    //Variables to hold order information
    var currentOrder: Order? = null

    init {
        loggedInUser =
            biblioHubPreferencesRepository.getPreference(User::class.java, Constants.USER)
    }


    fun resetCheckoutModel() {
        _checkoutModel.value = CheckoutModel()
    }

    suspend fun updateOrderStatus(status: Constants.Status) {
        currentOrder?.id?.let { orderRepository.updateOrderStatus(orderId = it, status = status) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
                val userRepository = application.container.userRepository
                val orderRepository = application.container.orderRepository
                val biblioHubPreferencesRepository = application.biblioHubPreferencesRepository
                CheckoutViewModel(
                    userRepository = userRepository,
                    orderRepository = orderRepository,
                    biblioHubPreferencesRepository = biblioHubPreferencesRepository
                )
            }
        }
    }
}