package com.example.bibliohub.fragments.productDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    val orderRepository: OrderRepository,
    val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
    biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    lateinit var product: Product
    var loggedInUser: LiveData<User?>

    //Variables to hold order information
    lateinit var currentOrder: Order
    internal lateinit var userOrderDetails: List<OrderDetails>


    init {
        loggedInUser =
            biblioHubPreferencesRepository.getPreference(User::class.java, Constants.USER)
    }

    fun createOrUpdateOrderDetails(product: Product, quantity: Int) {
        viewModelScope.launch{//check if order exist in order details list
            val existingOrderDetail =
                userOrderDetails.firstOrNull {
                    it.orderId == currentOrder.id && it.productId == product.id
                }

            if (existingOrderDetail == null) {
                //if order details not exist create new
                orderDetailsRepository.insert(
                    OrderDetails(
                        orderId = currentOrder.id,
                        productId = product.id,
                        quantity = quantity,
                        price = product.price
                    )
                )
                return@launch
            }
            //Update order detail if exist
            if (quantity==0){
                //delete order information if user updates quantity to zero
                orderDetailsRepository.deleteOrderDetails(
                    orderId = existingOrderDetail.orderId,
                    productId = existingOrderDetail.productId
                )
                return@launch
            }
            //update order quantity when user updates order
            orderDetailsRepository.insertOrUpdate(existingOrderDetail.copy(quantity = quantity))
        }
    }

    suspend fun createNewOrder(userId: Int): Order {
        val newOrder = Order(customerId = userId, status = Constants.Status.PENDING, date = "")
        orderRepository.insert(newOrder)
        return newOrder
    }
}