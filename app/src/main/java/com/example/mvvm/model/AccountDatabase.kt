package com.example.mvvm.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AccountEntity::class], version = 1)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDAO(): AccountDAO

    companion object {

        private var instance: AccountDatabase? = null
        fun getInstance(context: Context): AccountDatabase? {
            if (instance == null)
                synchronized(AccountDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AccountDatabase::class.java, "data-list.db"
                    ).fallbackToDestructiveMigration().build()
                }
            return instance
        }

    }

}
