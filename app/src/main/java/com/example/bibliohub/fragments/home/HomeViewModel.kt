package com.example.bibliohub.fragments.home

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val orderRepository: OrderRepository,
    private val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
    biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    lateinit var activeOrder: Flow<Order?>
    private var loggedInUser: Flow<User?>

    //Variables to hold order information
    private lateinit var currentOrder: Order
    internal val userOrderDetails = mutableListOf<OrderDetails>()

    //    var cart: MutableStateFlow<List<OrderDetails>?> = MutableStateFlow(listOf())
//    var orderDetailsInOrder: Flow<List<OrderDetails>?> = flowOf(listOf())
    private var filterText: MutableStateFlow<String> = MutableStateFlow("%%")
    var mFilterText: String? = "%%"
    internal var isRecyclerInitialized = false

    init {
        loggedInUser =
            biblioHubPreferencesRepository.getPreference(User::class.java, Constants.USER)
//        viewModelScope.launch {
//            loggedInUser.collectLatest { user ->
//                activeOrder = user?.let { getActiveOrderByUserId(it.id) } as Flow<Order?>
//                activeOrder.collectLatest {
//                    if (it == null) {
//                        createNewOrder(userId = user.id)
//                    } else {
//                        Log.d("Order", it.toString())
//                        orderDetailsInOrder = getOrderDetailsByOrderId(it.id)
//                        orderDetailsInOrder.collectLatest { ca ->
//                            Log.d("Order", ca.toString())
//                            cart.update { ca }
//                            Log.d("Order", cart.value.toString())
//                        }
//                    }
//                }
//            }
//        }
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
                    updateOrderDetailsList(it) {
                        //run callback after order details initialized with check if recycler has
                        // already ben initialized
                        if (!isRecyclerInitialized) {
                            onOrderDetailsInitialized()
                        }
                    }

                }
            }
        }
    }


    private fun updateOrderDetailsList(
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
                    orderDetailsRepository.getStaticOrderDetailsByOrderId(
                        currentOrder.id
                    )
                }
            withContext(Dispatchers.Main) {
                updateOrderDetailsList(orderDetails) {
                    onCartUpdated()
                }
            }
        }
        viewModelScope.launch {//check if order exist in order details list
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


    private suspend fun getActiveOrderByUserId(userId: Int): Flow<Order?> =
        orderRepository.getActiveOrderByUserId(userId = userId)

    private suspend fun createNewOrder(userId: Int): Order {
        val newOrder = Order(customerId = userId, status = Constants.Status.PENDING, date = "")
        orderRepository.insert(newOrder)
        return newOrder
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

    fun deleteFromCart(productId: Int) {
        viewModelScope.launch {
            orderDetailsRepository.deleteOrderDetails(currentOrder.id, productId)
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
                imgSrc = "data:image/jpeg;base64,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",
                pubDate = "",
                price = "",
                category = "",
                id = 1
            ), Product(
                name = "Second",
                author = "author",
                description = "",
                isbn = "",
                quantity = 10,
                imgSrc = "data:image/jpeg;base64,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",
                pubDate = "",
                price = "",
                category = "",
                id = 2
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