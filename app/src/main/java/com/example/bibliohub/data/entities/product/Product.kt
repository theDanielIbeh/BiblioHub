package com.example.bibliohub.data.entities.product

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "author")
    var author: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "isbn")
    var isbn: String,

    @ColumnInfo(name = "quantity")
    var quantity: Int,

    @ColumnInfo(name = "img_src")
    var imgSrc: String,

    @ColumnInfo(name = "pub_date")
    var pubDate: String,

    @ColumnInfo(name = "price")
    var price: String,

    @ColumnInfo(name = "category")
    var category: String
)
