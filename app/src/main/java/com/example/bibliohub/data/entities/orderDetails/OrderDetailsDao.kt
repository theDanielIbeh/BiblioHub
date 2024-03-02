package com.example.bibliohub.data.entities.orderDetails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDetailsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(orderDetails: OrderDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(orderDetails: OrderDetails)

    @Query("SELECT * FROM order_details WHERE order_id = :orderId")
    fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>>

    @Query("DELETE FROM order_details WHERE order_id = :orderId AND " +
            "product_id = :productId")
    suspend fun deleteOrderDetailsByOrderAndProductID(orderId: Int, productId:Int)
}