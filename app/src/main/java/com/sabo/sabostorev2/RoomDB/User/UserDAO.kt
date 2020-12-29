package com.sabo.sabostorev2.RoomDB.User

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface UserDAO {
    @Query("SELECT * FROM User WHERE uid=:uid")
    fun getUser(uid: String): Flowable<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateUser(vararg users: User): Completable

    @Query("DELETE FROM User WHERE uid=:uid")
    fun clearAccount(uid: String)

    @Query("DELETE FROM User WHERE uid=:uid")
    fun removeAccount(uid: String): Completable
}