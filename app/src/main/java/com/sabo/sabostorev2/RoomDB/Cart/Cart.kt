package com.sabo.sabostorev2.RoomDB.Cart

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "Cart", primaryKeys = ["itemId", "uid"])
class Cart {

    @NonNull
    @ColumnInfo(name = "itemId")
    var itemId: String? = null

    @NonNull
    @ColumnInfo(name = "uid")
    var uid: String? = null

    @ColumnInfo(name = "itemName")
    var itemName: String? = null

    @ColumnInfo(name = "itemImage")
    var itemImage: String? = null

    @ColumnInfo(name = "itemQuantity")
    var itemQuantity: Int = 0

    @ColumnInfo(name = "itemPrice")
    var itemPrice: Double = 0.0
}