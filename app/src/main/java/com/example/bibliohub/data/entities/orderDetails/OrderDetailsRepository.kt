package com.example.bibliohub.data.entities.orderDetails

import kotlinx.coroutines.flow.Flow

interface OrderDetailsRepository {
    suspend fun insert(orderDetails: OrderDetails)

    suspend fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>>
    suspend fun getStaticOrderDetailsByOrderId(orderId: Int): List<OrderDetails>

    suspend fun insertOrUpdate(orderDetails: OrderDetails)

    suspend fun deleteOrderDetails(orderId: Int,productId:Int)



}

class OfflineOrderDetailsRepository(
    private val orderDetailsDao: OrderDetailsDao
) : OrderDetailsRepository {
    override suspend fun insert(orderDetails: OrderDetails) {
        orderDetailsDao.insert(orderDetails = orderDetails)
    }

    override suspend fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>> =
        orderDetailsDao.getOrderDetailsByOrderId(orderId)

    override suspend fun getStaticOrderDetailsByOrderId(orderId: Int): List<OrderDetails> {
        return  orderDetailsDao.getStaticOrderDetailsByOrderId(orderId)
    }

    override suspend fun insertOrUpdate(orderDetails: OrderDetails) {
        orderDetailsDao.insertOrUpdate(orderDetails)
    }

    override suspend fun deleteOrderDetails(orderId: Int, productId: Int) {
        orderDetailsDao.deleteOrderDetailsByOrderAndProductID(orderId, productId)
    }
}