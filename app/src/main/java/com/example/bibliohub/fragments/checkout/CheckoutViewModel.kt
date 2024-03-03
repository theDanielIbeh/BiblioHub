package com.example.bibliohub.fragments.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val orderRepository: OrderRepository
) : ViewModel() {
    private var _checkoutModel: MutableStateFlow<CheckoutModel> = MutableStateFlow(CheckoutModel())
    val checkoutModel: StateFlow<CheckoutModel> = _checkoutModel.asStateFlow()

    fun resetCheckoutModel() {
        _checkoutModel.value = CheckoutModel()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
                val userRepository = application.container.userRepository
                val orderRepository = application.container.orderRepository
                CheckoutViewModel(
                    userRepository = userRepository,
                    orderRepository = orderRepository
                )
            }
        }
    }
}