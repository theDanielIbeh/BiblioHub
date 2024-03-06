package com.example.bibliohub.data.entities.order

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bibliohub.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(order: Order)

    @Query("SELECT * FROM `order` WHERE status = 'PENDING' AND customer_id = :userId")
    fun getActiveOrderByUserId(userId: Int): Flow<Order?>

    @Query("SELECT * FROM `order` WHERE status = 'PENDING' AND customer_id = :userId")
    suspend fun getStaticActiveOrderByUserId(userId: Int): Order?

    @Query("UPDATE `order` SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: Constants.Status)
}