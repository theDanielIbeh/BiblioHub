package com.example.bibliohub.data.entities.order

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bibliohub.utils.Constants

@Entity(tableName = "order")
data class Order(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "customer_id")
    var customerId: Int,

    @ColumnInfo(name = "status")
    var status: Constants.Status,

    @ColumnInfo(name = "date")
    var date: String
)
