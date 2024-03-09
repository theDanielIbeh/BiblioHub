package com.example.bibliohub.data.entities.orderDetails

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData

interface OrderDetailsRepository {
    suspend fun insert(orderDetails: OrderDetails)

    fun getOrderDetailsByOrderId(orderId: Int): LiveData<List<OrderDetails>>
    suspend fun getStaticOrderDetailsByOrderId(orderId: Int): List<OrderDetails>

    suspend fun insertOrUpdate(orderDetails: OrderDetails)

    suspend fun deleteOrderDetails(orderId: Int, productId: Int)

    suspend fun getAllOrderDetailsByOrderId(orderId: Int): List<OrderDetails>?

    fun getAllOrderDetailsByIdPaging(pageSize: Int, orderId: Int): LiveData<PagingData<OrderDetails>>
    fun getAllOrderDetails(pageSize: Int): LiveData<PagingData<OrderDetails>>
}

class OfflineOrderDetailsRepository(
    private val orderDetailsDao: OrderDetailsDao
) : OrderDetailsRepository {
    override suspend fun insert(orderDetails: OrderDetails) {
        orderDetailsDao.insert(orderDetails = orderDetails)
    }

    override fun getOrderDetailsByOrderId(orderId: Int): LiveData<List<OrderDetails>> =
        orderDetailsDao.getOrderDetailsByOrderId(orderId)

    override suspend fun getStaticOrderDetailsByOrderId(orderId: Int): List<OrderDetails> {
        return orderDetailsDao.getStaticOrderDetailsByOrderId(orderId)
    }

    override suspend fun insertOrUpdate(orderDetails: OrderDetails) {
        orderDetailsDao.insertOrUpdate(orderDetails)
    }

    override suspend fun deleteOrderDetails(orderId: Int, productId: Int) {
        orderDetailsDao.deleteOrderDetailsByOrderAndProductID(orderId, productId)
    }

    override suspend fun getAllOrderDetailsByOrderId(orderId: Int): List<OrderDetails>? =
        orderDetailsDao.getAllOrderDetailsByOrderId(orderId = orderId)

    override fun getAllOrderDetailsByIdPaging(
        pageSize: Int,
        orderId: Int
    ): LiveData<PagingData<OrderDetails>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            orderDetailsDao.getAllOrderDetailsByIdPaging(orderId = orderId)
        }
    ).liveData
    override fun getAllOrderDetails(
        pageSize: Int,
    ): LiveData<PagingData<OrderDetails>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            orderDetailsDao.getAllOrderDetails()
        }
    ).liveData
}