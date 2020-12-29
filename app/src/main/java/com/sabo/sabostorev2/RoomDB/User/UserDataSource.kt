package com.sabo.sabostorev2.RoomDB.User

import io.reactivex.Completable
import io.reactivex.Flowable

interface UserDataSource {
    fun getUser(uid: String): Flowable<User>

    fun insertOrUpdateUser(vararg users: User): Completable

    fun clearAccount(uid: String)

    fun removeAccount(uid: String): Completable
}