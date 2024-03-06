package com.example.bibliohub.data.entities.product

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "author")
    var author: String?,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "isbn")
    var isbn: String?,

    @ColumnInfo(name = "quantity")
    var quantity: Int,

    @ColumnInfo(name = "img_src")
    var imgSrc: String?,

    @ColumnInfo(name = "pub_date")
    var pubDate: String?,

    @ColumnInfo(name = "price")
    var price: String,

    @ColumnInfo(name = "category")
    var category: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(description)
        parcel.writeString(isbn)
        parcel.writeInt(quantity)
        parcel.writeString(imgSrc)
        parcel.writeString(pubDate)
        parcel.writeString(price)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
