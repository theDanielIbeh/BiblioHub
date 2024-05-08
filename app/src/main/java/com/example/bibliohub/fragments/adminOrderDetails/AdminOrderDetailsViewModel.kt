package com.example.bibliohub.fragments.adminOrderDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.entities.order.Order
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.orderDetails.OrderDetailsRepository
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.data.entities.product.ProductRepository
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.data.entities.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminOrderDetailsViewModel  @Inject constructor(
    val userRepository: UserRepository,
    val orderRepository: OrderRepository,
    val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    lateinit var user: User
    lateinit var order: Order
    lateinit var name: String
    var total: String? = null
    lateinit var status: String


    lateinit var orderDetails: LiveData<PagingData<OrderDetails>>
    fun getOrderDetailsByOrderId(): LiveData<PagingData<OrderDetails>> =
        orderDetailsRepository.getAllOrderDetailsByIdPaging(
            10,
            orderId = order.id
        ).cachedIn(viewModelScope)

    suspend fun getProductById(productId: Int): Product? = productRepository.getProductById(productId = productId)

    suspend fun getUserById() =
        userRepository.getUserById(userId = order.customerId)
}