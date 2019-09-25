package com.example.mvvm.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AccountDAO {

    @Query("SELECT * FROM data")
    fun getAll(): LiveData<MutableList<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: AccountEntity)

}
