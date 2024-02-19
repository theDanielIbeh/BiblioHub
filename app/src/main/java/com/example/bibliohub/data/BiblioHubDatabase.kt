package com.example.bibliohub.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bibliohub.data.entities.order.Order
import com.example.bibliohub.data.entities.order.OrderDao
import com.example.bibliohub.data.entities.orderDetails.OrderDetails
import com.example.bibliohub.data.entities.orderDetails.OrderDetailsDao
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.data.entities.product.ProductDao
import com.example.bibliohub.data.entities.user.User
import com.example.bibliohub.data.entities.user.UserDao

@Database(
    entities = [
        User::class,
        Product::class,
        Order::class,
        OrderDetails::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BiblioHubDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun orderDetailsDao(): OrderDetailsDao

    companion object {
        @Volatile
        private var INSTANCE: BiblioHubDatabase? = null

        fun getInstance(context: Context): BiblioHubDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BiblioHubDatabase::class.java,
                        "biblio_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}