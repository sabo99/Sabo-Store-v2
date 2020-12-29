package com.sabo.sabostorev2.RoomDB.User

import io.reactivex.Completable

import io.reactivex.Flowable


class LocalUserDataSource(private val userDAO: UserDAO) : UserDataSource {
    override fun getUser(uid: String): Flowable<User> {
        return userDAO.getUser(uid)
    }

    override fun insertOrUpdateUser(vararg users: User): Completable {
        return userDAO.insertOrUpdateUser(*users)
    }

    override fun clearAccount(uid: String) {
        return userDAO.clearAccount(uid)
    }

    override fun removeAccount(uid: String): Completable {
        return userDAO.removeAccount(uid)
    }
}