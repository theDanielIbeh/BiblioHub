package com.example.bibliohub.data.entities.order

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bibliohub.utils.Constants

@Entity(tableName = "order")
data class Order(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    var customerId: Int,

    @ColumnInfo(name = "status")
    var status: Constants.Status?,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "address")
    var address: String? = null,

    @ColumnInfo(name = "postcode")
    var postcode: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()?.let { Constants.Status.valueOf(it) },
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(customerId)
        parcel.writeString(status?.name)
        parcel.writeString(date)
        parcel.writeString(address)
        parcel.writeString(postcode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}
