package com.example.mvvm.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [APIEntity::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDAO

    companion object{
        private var instance: MyDatabase? = null
        fun getInstance(context: Context): MyDatabase? {
            if (instance == null)
                synchronized(MyDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        MyDatabase::class.java, "data-list.db")
                        .fallbackToDestructiveMigration().build()
                }
            return instance
        }
    }
}
