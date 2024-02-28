package com.example.bibliohub.data.entities.orderDetails

import kotlinx.coroutines.flow.Flow

interface OrderDetailsRepository {
    suspend fun insert(orderDetails: OrderDetails)

    suspend fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>?>
}

class OfflineOrderDetailsRepository(
    private val orderDetailsDao: OrderDetailsDao
) : OrderDetailsRepository {
    override suspend fun insert(orderDetails: OrderDetails) {
        orderDetailsDao.insert(orderDetails = orderDetails)
    }

    override suspend fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>?> =
        orderDetailsDao.getOrderDetailsByOrderId(orderId)
}