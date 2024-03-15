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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
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
            product?.quantity?.minus(details.quantity)
            product?.let { productRepository.update(product = it) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
                val userRepository = application.container.userRepository
                val orderRepository = application.container.orderRepository
                val orderDetailsRepository = application.container.orderDetailsRepository
                val productRepository = application.container.productRepository
                val biblioHubPreferencesRepository = application.biblioHubPreferencesRepository
                CheckoutViewModel(
                    userRepository = userRepository,
                    orderRepository = orderRepository,
                    orderDetailsRepository = orderDetailsRepository,
                    productRepository = productRepository,
                    biblioHubPreferencesRepository = biblioHubPreferencesRepository
                )
            }
        }
    }
}