package com.example.bibliohub.data.entities.product

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData

interface ProductRepository {
    suspend fun insert(product: Product)
    suspend fun update(product: Product)
    suspend fun delete(product: Product)
    suspend fun getProductById(productId: Int): Product?
    fun getAvailableProducts(pageSize: Int, filterText: String?): LiveData<PagingData<Product>>
    fun getProductsByIDs(pageSize: Int, productIDs: List<Int>): LiveData<PagingData<Product>>
    fun getProductsInCart(pageSize: Int, cartIds: List<Int>): LiveData<PagingData<Product>>
    fun getAllProducts(pageSize: Int, filterText: String?): LiveData<PagingData<Product>>
    suspend fun getProductByImageSrc(imgSrc: String): Product?
}

class OfflineProductRepository(
    private val productDao: ProductDao
) : ProductRepository {
    override suspend fun insert(product: Product) {
        productDao.insert(product = product)
    }

    override suspend fun update(product: Product) {
        productDao.update(product = product)
    }

    override suspend fun delete(product: Product) {
        productDao.delete(product = product)
    }

    override suspend fun getProductById(productId: Int): Product? =
        productDao.getProductById(productId = productId)

    override fun getAvailableProducts(
        pageSize: Int,
        filterText: String?
    ): LiveData<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            productDao.getAvailableProducts(filterText)
        }
    ).liveData

    override fun getProductsByIDs(
        pageSize: Int,
        productIDs: List<Int>
    ): LiveData<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            productDao.getProductsByIDs(productIDs)
        }
    ).liveData

    override fun getProductsInCart(pageSize: Int, cartIds: List<Int>): LiveData<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            productDao.getProductsInCart(cartIds)
        }
    ).liveData

    override fun getAllProducts(
        pageSize: Int,
        filterText: String?
    ): LiveData<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            productDao.getAllProducts(filterText)
        }
    ).liveData

    override suspend fun getProductByImageSrc(imgSrc: String): Product? =
        productDao.getProductByImageSrc(imgSrc = imgSrc)
}