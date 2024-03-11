package com.example.bibliohub.fragments.cart

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.entities.order.Order
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.orderDetails.OrderDetailsRepository
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.data.entities.product.ProductRepository
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.utils.Constants
import kotlinx.coroutines.launch


class CartViewModel(
    val orderRepository: OrderRepository,
    val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
    biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    var loggedInUser: LiveData<User?>

    //Variables to hold order information
    var currentOrder: Order? = null
    var orderDetails: List<OrderDetails>? = null
    var orderSum = ObservableField<String>()

    init {
        loggedInUser =
            biblioHubPreferencesRepository.getPreference(User::class.java, Constants.USER)
    }

    fun createOrUpdateOrderDetails(orderDetails: OrderDetails, quantity: Int) {
        viewModelScope.launch {
            if (quantity > 0) {
                //if order details not exist create new
                currentOrder?.id?.let {
                    OrderDetails(
                        orderId = it,
                        productId = orderDetails.productId,
                        quantity = quantity,
                        price = orderDetails.price
                    )
                }?.let {
                    orderDetailsRepository.insertOrUpdate(
                        it
                    )
                }
            } else
            //delete order information if user updates quantity to zero
                orderDetailsRepository.deleteOrderDetails(
                    orderId = orderDetails.orderId,
                    productId = orderDetails.productId
                )
        }
    }


    suspend fun createNewOrder(userId: Int): Order? {
        val newOrder = Order(customerId = userId, status = Constants.Status.PENDING, date = "")
        orderRepository.insert(newOrder)
        return orderRepository.getActiveOrderByUserId(userId)
    }

    fun deleteFromCart(productId: Int) {
        viewModelScope.launch {
            currentOrder?.id?.let { orderDetailsRepository.deleteOrderDetails(it, productId) }
        }
    }

    fun updateOrderSum(value: Double) {
        orderSum.set(value.toString())
    }

    internal fun getOrderDetailsByIds(orderId: Int): LiveData<PagingData<OrderDetails>> =
        orderDetailsRepository.getAllOrderDetailsByIdPaging(
            10,
            orderId = orderId
        )

    suspend fun getProduct(productId: Int): Product? =
        productRepository.getProductById(productId)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
                val productRepository = application.container.productRepository
                val orderRepository = application.container.orderRepository
                val orderDetailsRepository = application.container.orderDetailsRepository
                val biblioHubPreferencesRepository = application.biblioHubPreferencesRepository
                CartViewModel(
                    orderRepository = orderRepository,
                    orderDetailsRepository = orderDetailsRepository,
                    productRepository = productRepository,
                    biblioHubPreferencesRepository = biblioHubPreferencesRepository
                )
            }
        }
    }
}