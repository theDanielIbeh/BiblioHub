package com.example.bibliohub.fragments.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import okhttp3.internal.notifyAll

class HomeViewModel(
    private val orderRepository: OrderRepository,
    private val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
    biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    lateinit var activeOrder: Flow<Order?>
    private var loggedInUser: Flow<User?>
    var cart: MutableStateFlow<List<OrderDetails>?> = MutableStateFlow(listOf())
    var orderDetailsInOrder: Flow<List<OrderDetails>?> = flowOf(listOf())
    private var filterText: MutableStateFlow<String> = MutableStateFlow("%%")
    var mFilterText: String? = "%%"

    init {
        loggedInUser =
            biblioHubPreferencesRepository.getPreference(User::class.java, Constants.USER)
        viewModelScope.launch {
            loggedInUser.collectLatest { user ->
                activeOrder = user?.let { getActiveOrderByUserId(it.id) } as Flow<Order?>
                activeOrder.collectLatest {
                    if (it == null) {
                        createNewOrder(userId = user.id)
                    } else {
                        Log.d("Order", it.toString())
                        orderDetailsInOrder = getOrderDetailsByOrderId(it.id)
                        orderDetailsInOrder.collectLatest { ca ->
                            Log.d("Order", ca.toString())
                            cart.update { ca }
                            Log.d("Order", cart.value.toString())
                        }
                    }
                }
            }
        }
    }


    private suspend fun getActiveOrderByUserId(userId: Int): Flow<Order?> =
        orderRepository.getActiveOrderByUserId(userId = userId)

    private suspend fun createNewOrder(userId: Int) {
        val newOrder = Order(customerId = userId, status = Constants.Status.PENDING, date = "")
        orderRepository.insert(newOrder)
    }

    private suspend fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>?> =
        orderDetailsRepository.getOrderDetailsByOrderId(orderId)

    fun createNewOrderDetail(product: Product, quantity: String) {
        viewModelScope.launch {
            activeOrder.collectLatest {
                if (it != null) {
                    val newOrderDetail = OrderDetails(
                        orderId = it.id,
                        productId = product.id,
                        quantity = quantity.toIntOrNull() ?: 0,
                        price = product.price
                    )
                    orderDetailsRepository.insert(newOrderDetail)
                }
            }

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val products: Flow<PagingData<Product>> = filterText.flatMapLatest {
        productRepository.getAvailableProducts(
            10,
            it,
        ).cachedIn(viewModelScope)
    }

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        filterText.value = query
    }

    fun insertProducts() {
        val products = arrayListOf(
            Product(
                name = "First",
                author = "author",
                description = "",
                isbn = "",
                quantity = 10,
                imgSrc = "",
                pubDate = "",
                price = "",
                category = "",
            ), Product(
                name = "Second",
                author = "author",
                description = "",
                isbn = "",
                quantity = 10,
                imgSrc = "",
                pubDate = "",
                price = "",
                category = "",
            )
        )
        products.forEach { product ->
            viewModelScope.launch {
                productRepository.insert(product = product)
            }
        }
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
                HomeViewModel(
                    orderRepository = orderRepository,
                    orderDetailsRepository = orderDetailsRepository,
                    productRepository = productRepository,
                    biblioHubPreferencesRepository = biblioHubPreferencesRepository
                )
            }
        }
    }
}