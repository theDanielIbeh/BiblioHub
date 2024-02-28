package com.example.bibliohub.data.entities.order

import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun insert(order: Order)
    suspend fun getActiveOrderByUserId(userId: Int): Flow<Order?>
}

class OfflineOrderRepository(
    private val orderDao: OrderDao
) : OrderRepository {
    override suspend fun insert(order: Order) {
        orderDao.insert(order = order)
    }

    override suspend fun getActiveOrderByUserId(userId: Int): Flow<Order?> =
        orderDao.getActiveOrderByUserId(userId = userId)
}