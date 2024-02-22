package com.example.bibliohub.data.entities.order

interface OrderRepository {
    suspend fun insert(order: Order)
}

class OfflineOrderRepository(
    private val orderDao: OrderDao
) : OrderRepository {
    override suspend fun insert(order: Order) {
        orderDao.insert(order = order)
    }
}