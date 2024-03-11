package com.example.bibliohub.data.entities.order

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.utils.Constants

interface OrderRepository {
    suspend fun insert(order: Order)
    suspend fun update(order: Order)
    suspend fun getActiveOrderByUserId(userId: Int): Order?
    suspend fun getStaticActiveOrderByUserId(userId: Int): Order?
    suspend fun updateOrderStatus(orderId: Int, status: Constants.Status)
    fun getAllOrders(pageSize: Int): LiveData<PagingData<Order>>
}

class OfflineOrderRepository(
    private val orderDao: OrderDao
) : OrderRepository {
    override suspend fun insert(order: Order) {
        orderDao.insert(order = order)
    }

    override suspend fun update(order: Order) {
        orderDao.update(order = order)
    }

    override suspend fun getActiveOrderByUserId(userId: Int): Order? =
        orderDao.getActiveOrderByUserId(userId = userId)

    override suspend fun getStaticActiveOrderByUserId(userId: Int): Order? {
       return orderDao.getStaticActiveOrderByUserId(userId = userId)
    }

    override suspend fun updateOrderStatus(orderId: Int, status: Constants.Status) {
        orderDao.updateOrderStatus(orderId = orderId, status = status)
    }

    override fun getAllOrders(
        pageSize: Int,
    ): LiveData<PagingData<Order>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            orderDao.getAllOrders()
        }
    ).liveData
}