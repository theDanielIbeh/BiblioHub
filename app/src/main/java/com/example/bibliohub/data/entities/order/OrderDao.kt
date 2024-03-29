package com.example.bibliohub.data.entities.order

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bibliohub.utils.Constants

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(order: Order)

    @Update
    suspend fun update(order: Order)

    @Query("SELECT * FROM `order` WHERE status = 'PENDING' AND user_id = :userId")
    suspend fun getActiveOrderByUserId(userId: Int): Order?

    @Query("SELECT * FROM `order` WHERE status = 'PENDING' AND user_id = :userId")
    suspend fun getStaticActiveOrderByUserId(userId: Int): Order?

    @Query("UPDATE `order` SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: Constants.Status)

    @Query("SELECT * FROM `order` WHERE status != 'PENDING' ORDER BY date DESC")
    fun getAllOrders(): PagingSource<Int, Order>
}