package com.example.bibliohub.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.ProductList.list
import com.example.bibliohub.data.entities.order.Order
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.orderDetails.OrderDetailsRepository
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.data.entities.product.ProductRepository
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    val orderRepository: OrderRepository,
    val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
    internal val biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    private lateinit var activeOrder: LiveData<Order?>
    var loggedInUser: LiveData<User?> =
        biblioHubPreferencesRepository.getPreference(User::class.java, Constants.USER)

    //Variables to hold order information
    var currentOrder: Order? = null
    internal lateinit var userOrderDetailsLive: LiveData<List<OrderDetails>>
    internal val userOrderDetails = mutableListOf<OrderDetails>()
    internal var selectedCategory: String? = ""

    //    var cart: MutableStateFlow<List<OrderDetails>?> = MutableStateFlow(listOf())
//    var orderDetailsInOrder: Flow<List<OrderDetails>?> = flowOf(listOf())
    private var filterText: MutableLiveData<String> = MutableLiveData("%%")
    var mFilterText: String? = "%%"


    fun updateOrderDetailsList(
        orderDetails: List<OrderDetails>,
        onOrderDetailsUpdated: () -> Unit
    ) {
        userOrderDetails.clear()
        userOrderDetails.addAll(orderDetails)
        onOrderDetailsUpdated()
    }


    fun createOrUpdateOrderDetails(
        product: Product,
        quantity: Int,
        onCartUpdated: () -> Unit
    ) {
        suspend fun updateOrderDetailsAndRecycler() {
            val orderDetails =
                withContext(Dispatchers.IO) {
                    currentOrder?.id?.let {
                        orderDetailsRepository.getStaticOrderDetailsByOrderId(
                            it
                        )
                    }
                }
            withContext(Dispatchers.Main) {
                if (orderDetails != null) {
                    updateOrderDetailsList(orderDetails) {
                        onCartUpdated()
                    }
                }
            }
        }
        viewModelScope.launch {//check if order exist in order details list
            val existingOrderDetail =
                userOrderDetails.firstOrNull {
                    it.orderId == currentOrder?.id && it.productId == product.id
                }

            if (existingOrderDetail == null) {
                //if order details not exist create new
                currentOrder?.id?.let {
                    OrderDetails(
                        orderId = it,
                        productId = product.id,
                        quantity = quantity,
                        price = product.price
                    )
                }?.let {
                    orderDetailsRepository.insert(
                        it
                    )
                }
                updateOrderDetailsAndRecycler()
                return@launch
            }
            //Update order detail if exist
            if (quantity == 0) {
                //delete order information if user updates quantity to zero
                orderDetailsRepository.deleteOrderDetails(
                    orderId = existingOrderDetail.orderId,
                    productId = existingOrderDetail.productId
                )
                updateOrderDetailsAndRecycler()
                return@launch
            }
            //update order quantity when user updates order
            orderDetailsRepository.insertOrUpdate(existingOrderDetail.copy(quantity = quantity))
            updateOrderDetailsAndRecycler()
        }
    }


    suspend fun createNewOrder(userId: Int): Order? {
        val newOrder = Order(customerId = userId, status = Constants.Status.PENDING, date = "")
        orderRepository.insert(newOrder)
        return orderRepository.getActiveOrderByUserId(userId = userId)
    }


    internal val products: LiveData<PagingData<Product>> = filterText.switchMap {
        productRepository.getAvailableProducts(
            10,
            it,
        ).cachedIn(viewModelScope)
    }

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        filterText.value = query
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