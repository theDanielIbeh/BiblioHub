package com.example.bibliohub.data.entities.orderDetails

interface OrderDetailsRepository {
    suspend fun insert(orderDetails: OrderDetails)
}

class OfflineOrderDetailsRepository(
    private val orderDetailsDao: OrderDetailsDao
) : OrderDetailsRepository {
    override suspend fun insert(orderDetails: OrderDetails) {
        orderDetailsDao.insert(orderDetails = orderDetails)
    }
}