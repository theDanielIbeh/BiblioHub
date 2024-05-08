package com.example.bibliohub.fragments.adminHome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val orderDetailsRepository: OrderDetailsRepository,
    private val productRepository: ProductRepository,
    val biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    internal var selectedCategory: String? = ""

    private var filterText: MutableLiveData<String> = MutableLiveData("%%")
    var mFilterText: String? = "%%"

    internal val products: LiveData<PagingData<Product>> = filterText.switchMap {
        productRepository.getAllProducts(
            10,
            it,
        ).cachedIn(viewModelScope)
    }

    internal fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.delete(product = product)
        }
    }

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        filterText.value = query
    }

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application =
//                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BiblioHubApplication)
//                val productRepository = application.container.productRepository
//                val orderRepository = application.container.orderRepository
//                val orderDetailsRepository = application.container.orderDetailsRepository
//                val biblioHubPreferencesRepository = application.biblioHubPreferencesRepository
//                AdminHomeViewModel(
//                    orderRepository = orderRepository,
//                    orderDetailsRepository = orderDetailsRepository,
//                    productRepository = productRepository,
//                    biblioHubPreferencesRepository = biblioHubPreferencesRepository
//                )
//            }
//        }
//    }
}