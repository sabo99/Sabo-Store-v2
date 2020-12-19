package com.sabo.sabostorev2.RoomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sabo.sabostorev2.RoomDB.User.User;
import com.sabo.sabostorev2.RoomDB.User.UserDAO;

@Database(version = 1, entities = {User.class}, exportSchema = false)
public abstract class RoomDBHost extends RoomDatabase {

    public abstract UserDAO userDAO();
    private static RoomDBHost instance;

    public static RoomDBHost getInstance(Context context){
        if (instance == null)
            instance = Room.databaseBuilder(context, RoomDBHost.class, "SaboStoreV2")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}
