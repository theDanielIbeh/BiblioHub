package com.example.bibliohub.data.entities.orderDetails

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDetailsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(orderDetails: OrderDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(orderDetails: OrderDetails)

    @Query("SELECT * FROM order_details WHERE order_id = :orderId")
    fun getOrderDetailsByOrderId(orderId: Int): LiveData<List<OrderDetails>>

    @Query("SELECT * FROM order_details WHERE order_id = :orderId")
    suspend fun getStaticOrderDetailsByOrderId(orderId: Int): List<OrderDetails>

    @Query(
        "DELETE FROM order_details WHERE order_id = :orderId AND " +
                "product_id = :productId"
    )
    suspend fun deleteOrderDetailsByOrderAndProductID(orderId: Int, productId: Int)

    @Query("SELECT * FROM order_details WHERE order_id = :orderId")
    suspend fun getAllOrderDetailsByOrderId(orderId: Int): List<OrderDetails>?

    @Query("SELECT * FROM order_details WHERE order_id = :orderId")
    fun getAllOrderDetailsByIdPaging(orderId: Int): PagingSource<Int, OrderDetails>

    @Query("SELECT * FROM `order_details`")
    fun getAllOrderDetails(): PagingSource<Int, OrderDetails>
}