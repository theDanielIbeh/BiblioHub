package com.example.bibliohub.data.entities.orderDetails

import kotlinx.coroutines.flow.Flow

interface OrderDetailsRepository {
    suspend fun insert(orderDetails: OrderDetails)

    suspend fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>?>

    fun getOpenOrderDetails(orderID: Int): Flow<List<OrderDetails>>

    suspend fun insertOrUpdate(orderDetails: OrderDetails)

    suspend fun deleteOrderDetails(orderID: Int,productID:Int)



}

class OfflineOrderDetailsRepository(
    private val orderDetailsDao: OrderDetailsDao
) : OrderDetailsRepository {
    override suspend fun insert(orderDetails: OrderDetails) {
        orderDetailsDao.insert(orderDetails = orderDetails)
    }

    override suspend fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>?> =
        orderDetailsDao.getOrderDetailsByOrderId(orderId)

    override fun getOpenOrderDetails(orderID: Int): Flow<List<OrderDetails>> {
        return orderDetailsDao.getOpenOrderDetails(orderID)
    }

    override suspend fun insertOrUpdate(orderDetails: OrderDetails) {
        orderDetailsDao.insertOrUpdate(orderDetails)
    }

    override suspend fun deleteOrderDetails(orderID: Int, productID: Int) {
        orderDetailsDao.deleteOrderDetailsByOrderAndProductID(orderID, productID)
    }
}