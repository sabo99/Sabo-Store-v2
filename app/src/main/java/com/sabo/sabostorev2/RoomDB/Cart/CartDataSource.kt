package com.sabo.sabostorev2.RoomDB.Cart

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface CartDataSource {
    fun getAllCart(uid: String): Flowable<List<Cart>>

    fun checkItemInCart(itemId : String, uid: String): Single<Cart>

    fun insertOrReplaceAll(vararg cart: Cart): Completable

    fun updateQtyItemInCart(cart: Cart): Single<Int>

    fun deleteItemInCart(cart: Cart): Single<Int>

    fun countItemInCart(uid: String): Single<Int>

    fun getTotalPrice(uid: String): Single<Double>

    fun clearAllCart(uid: String): Single<Int>
}