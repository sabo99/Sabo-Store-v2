package com.sabo.sabostorev2.RoomDB.Cart

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CartDAO {

    @Query("SELECT * FROM Cart WHERE uid=:uid")
    fun getAllCart(uid: String): Flowable<List<Cart>>

    @Query("SELECT * FROM Cart WHERE itemId=:itemId AND uid=:uid")
    fun checkItemInCart(itemId: String, uid: String): Single<Cart>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceAll(vararg cart: Cart): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateQtyItemInCart(cart: Cart): Single<Integer>

    @Delete
    fun deleteItemInCart(cart: Cart): Single<Integer>

    @Query("SELECT SUM(itemQuantity) FROM Cart WHERE uid=:uid")
    fun countItemInCart(uid: String): Single<Integer>

    @Query("SELECT SUM(itemPrice * itemQuantity) FROM Cart WHERE uid=:uid")
    fun getTotalPrice(uid: String): Single<Double>

    @Query("DELETE FROM Cart WHERE uid=:uid")
    fun clearAllCart(uid: String): Single<Integer>
}