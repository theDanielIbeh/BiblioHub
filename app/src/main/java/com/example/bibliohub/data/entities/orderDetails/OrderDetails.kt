package com.example.bibliohub.data.entities.orderDetails

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "order_details", primaryKeys = ["order_id", "product_id"])
data class OrderDetails(
    @ColumnInfo(name = "order_id")
    var orderId: Int,

    @ColumnInfo(name = "product_id")
    var productId: Int,

    @ColumnInfo(name = "quantity")
    var quantity: Int,

    @ColumnInfo(name = "price")
    var price: String
)
