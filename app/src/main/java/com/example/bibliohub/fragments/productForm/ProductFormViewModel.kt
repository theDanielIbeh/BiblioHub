package com.example.bibliohub.fragments.productForm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliohub.BiblioHubApplication
import com.example.bibliohub.data.BiblioHubPreferencesRepository
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.data.entities.product.ProductRepository
import com.example.bibliohub.data.entities.user.UserRepository
import com.example.bibliohub.utils.Constants
import com.example.bibliohub.utils.HelperFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class ProductModel(
    var title: String = "",
    var author: String = "",
    var description: String = "",
    var isbn: String = "",
    var quantity: String = "",
    var imgSrc: String? = null,
    var pubDate: String = "",
    var price: String = "",
    var category: String = ""
)

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    val biblioHubPreferencesRepository: BiblioHubPreferencesRepository
) : ViewModel() {
    private var _productModel: MutableStateFlow<ProductModel> = MutableStateFlow(ProductModel())
    val productModel: StateFlow<ProductModel> = _productModel.asStateFlow()
    var product: Product? = null
    val categoryList = listOf("Fiction", "Non-Fiction")

    fun resetProductModel() {
        _productModel.value = ProductModel()
    }

    fun productModelToProduct(productModel: ProductModel, img: String?): Product {
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
        val date = sdf.parse(productModel.pubDate)
        val dateStr =
            date?.let { HelperFunctions.getDateString(Constants.DATE_FORMAT_HYPHEN_DMY, it) }
        return Product(
            title = productModel.title,
            author = productModel.author,
            description = productModel.description,
            isbn = productModel.isbn,
            quantity = productModel.quantity.toInt(),
            imgSrc = img,
            pubDate = dateStr.toString(),
            price = productModel.price,
            category = productModel.category
        )
    }

    private fun productToProductModel(product: Product): ProductModel {
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
        val date = product.pubDate?.let { sdf.parse(it) }
        val dateStr = date?.let { HelperFunctions.getDateString(Constants.DATE_FORMAT_FULL, it) }
        return ProductModel(
            title = product.title,
            author = product.author ?: "",
            description = product.description ?: "",
            isbn = product.isbn ?: "",
            quantity = product.quantity.toString(),
            imgSrc = product.imgSrc,
            pubDate = dateStr.toString(),
            price = product.price,
            category = product.category ?: ""
        )
    }

    fun initializeProductModel(product: Product) {
        _productModel.value = productToProductModel(product)
    }

    suspend fun insertProduct(product: Product) {
        productRepository.insert(product = product)
    }

    suspend fun updateProduct(product: Product) {
        productRepository.update(product = product)
    }

    suspend fun getProductByImageSrc(imgSrc: String): Product? =
        productRepository.getProductByImageSrc(imgSrc = imgSrc)

    suspend fun update(product: Product) {
        productRepository.update(product = product)
    }
}