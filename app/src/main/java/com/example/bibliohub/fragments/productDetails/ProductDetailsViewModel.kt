package com.example.bibliohub.fragments.productDetails

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val orderRepository: OrderRepository,
    private val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
    biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    lateinit var product: Product
    private var loggedInUser: Flow<User?>

    //Variables to hold order information
    private lateinit var currentOrder: Order
    internal lateinit var userOrderDetails: List<OrderDetails>


    init {
        loggedInUser =
            biblioHubPreferencesRepository.getPreference(User::class.java, Constants.USER)
    }

    fun initOrderDetails(onOrderDetailsInitialized: () -> Unit) {
        viewModelScope.launch {
            //check if user has existing order details
            loggedInUser.collectLatest { userInfo ->
                if (userInfo == null) {
                    //throw null error when user null to stop app flow
                    throw NullPointerException()
                }
                //get current order info else create new order
                currentOrder =
                    orderRepository.getStaticActiveOrderByUserId(userInfo.id) ?: createNewOrder(
                        userInfo.id
                    )

                //check if user already has an order saved and assign to order details list
                orderDetailsRepository.getOrderDetailsByOrderId(currentOrder.id).collectLatest {
                    //clear list in the event list is holding other objects
                    userOrderDetails = it
                    //run callback after order details initialized
                    onOrderDetailsInitialized()
                }
            }
        }
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

    private suspend fun createNewOrder(userId: Int): Order {
        val newOrder = Order(customerId = userId, status = Constants.Status.PENDING, date = "")
        orderRepository.insert(newOrder)
        return newOrder
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
                val productRepository = application.container.productRepository
                val orderRepository = application.container.orderRepository
                val orderDetailsRepository = application.container.orderDetailsRepository
                val biblioHubPreferencesRepository = application.biblioHubPreferencesRepository
                ProductDetailsViewModel(
                    orderRepository = orderRepository,
                    orderDetailsRepository = orderDetailsRepository,
                    productRepository = productRepository,
                    biblioHubPreferencesRepository = biblioHubPreferencesRepository
                )
            }
        }
    }
}