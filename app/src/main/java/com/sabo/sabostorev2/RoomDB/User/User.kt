package com.sabo.sabostorev2.RoomDB.User

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "User", primaryKeys = ["uid"])
class User {

    @NonNull
    @ColumnInfo(name = "uid")
    var uid: String? = null

    @ColumnInfo(name = "email")
    var email: String? = null

    @ColumnInfo(name = "username")
    var username: String? = null

    @ColumnInfo(name = "nickname")
    var nickname: String? = null

    @ColumnInfo(name = "image")
    var image: String? = null

    @ColumnInfo(name = "phone")
    var phone: String? = null

    @ColumnInfo(name = "countryCode")
    var countryCode: String?= null

    @ColumnInfo(name = "gender")
    var gender: Int? = null
}