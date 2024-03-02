package com.example.bibliohub.data.entities.product

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun insert(product: Product)
    suspend fun getAvailableProducts(pageSize: Int, filterText: String?): Flow<PagingData<Product>>
    fun getProductsInCart(pageSize: Int, cartIds: List<Int>): Flow<PagingData<Product>>
}

class OfflineProductRepository(
    private val productDao: ProductDao
) : ProductRepository {
    override suspend fun insert(product: Product) {
        productDao.insert(product = product)
    }

    override suspend fun getAvailableProducts(
        pageSize: Int,
        filterText: String?
    ): Flow<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            productDao.getAvailableProducts(filterText)
        }
    ).flow

    override fun getProductsInCart(
        pageSize: Int,
        cartIds: List<Int>
    ): Flow<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            productDao.getProductsInCart(cartIds)
        }
    ).flow
}