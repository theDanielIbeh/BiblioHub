package com.example.bibliohub.data

import android.content.Context
import com.example.bibliohub.data.entities.order.OfflineOrderRepository
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.orderDetails.OfflineOrderDetailsRepository
import com.example.bibliohub.data.entities.orderDetails.OrderDetailsRepository
import com.example.bibliohub.data.entities.product.OfflineProductRepository
import com.example.bibliohub.data.entities.product.ProductRepository
import com.example.bibliohub.data.entities.user.OfflineUserRepository
import com.example.bibliohub.data.entities.user.UserRepository

interface AppContainer {
    val orderRepository: OrderRepository
    val orderDetailsRepository: OrderDetailsRepository
    val productRepository: ProductRepository
    val userRepository: UserRepository
}

class DefaultAppContainer(private val context: Context): AppContainer {
    override val orderRepository: OrderRepository by lazy {
        OfflineOrderRepository(BiblioHubDatabase.getInstance(context = context).orderDao())
    }
    override val orderDetailsRepository: OrderDetailsRepository by lazy {
        OfflineOrderDetailsRepository(BiblioHubDatabase.getInstance(context = context).orderDetailsDao())
    }
    override val productRepository: ProductRepository by lazy {
        OfflineProductRepository(BiblioHubDatabase.getInstance(context = context).productDao())
    }
    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(BiblioHubDatabase.getInstance(context = context).userDao())
    }
}