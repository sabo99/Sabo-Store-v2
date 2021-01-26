package com.sabo.sabostorev2.RoomDB.Cart

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class LocalCartDataSource(private val cartDAO: CartDAO): CartDataSource {
    override fun getAllCart(uid: String): Flowable<List<Cart>> {
        return cartDAO.getAllCart(uid)
    }

    override fun checkItemInCart(itemId: String, uid: String): Single<Cart> {
        return cartDAO.checkItemInCart(itemId, uid)
    }

    override fun insertOrReplaceAll(vararg cart: Cart): Completable {
        return cartDAO.insertOrReplaceAll(*cart)
    }

    override fun updateQtyItemInCart(cart: Cart): Single<Int> {
        return cartDAO.updateQtyItemInCart(cart)
    }

    override fun deleteItemInCart(cart: Cart): Single<Int> {
        return cartDAO.deleteItemInCart(cart)
    }

    override fun countItemInCart(uid: String): Single<Int> {
        return cartDAO.countItemInCart(uid)
    }

    override fun getTotalPrice(uid: String): Single<Double> {
        return cartDAO.getTotalPrice(uid)
    }

    override fun clearAllCart(uid: String): Single<Int>{
        return cartDAO.clearAllCart(uid)
    }

}