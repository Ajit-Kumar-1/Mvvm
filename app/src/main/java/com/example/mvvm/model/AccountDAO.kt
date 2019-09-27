package com.example.mvvm.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccountDAO {

    @Query("SELECT * FROM data")
    fun getAll(): LiveData<MutableList<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: AccountEntity)

}
