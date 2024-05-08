package com.example.bibliohub.fragments.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.entities.order.Order
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.orderDetails.OrderDetailsRepository
import com.example.bibliohub.data.entities.product.ProductRepository
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.data.entities.user.UserRepository
import com.example.bibliohub.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class CheckoutModel(
    var address: String = "",
    var postcode: String = "",
    var cardNumber: String = "",
    var expiry: String = "",
    var cvv: String = "",
    var pin: String = "",
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    val orderRepository: OrderRepository,
    val orderDetailsRepository: OrderDetailsRepository,
    val productRepository: ProductRepository,
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

    suspend fun updateOrder() {
        currentOrder?.status = Constants.Status.COMPLETED
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_SPREAD, Locale.getDefault())
        val currentDate = Date()
        currentOrder?.date = formatter.format(currentDate)
        currentOrder?.let { orderRepository.update(order = it) }

        updateProducts()
    }

    private suspend fun updateProducts() {
        val orderDetails = currentOrder?.id?.let {
            orderDetailsRepository.getAllOrderDetailsByOrderId(
                it
            )
        }
        orderDetails?.forEach { details ->
            val product = productRepository.getProductById(details.productId)

            product?.let {
                it.quantity -= details.quantity
                productRepository.update(product = it)
            }
        }
    }
}