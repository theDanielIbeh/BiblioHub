package com.example.bibliohub.data.entities.product

interface ProductRepository {
    suspend fun insert(product: Product)
}

class OfflineProductRepository(
    private val productDao: ProductDao
) : ProductRepository {
    override suspend fun insert(product: Product) {
        productDao.insert(product = product)
    }
}