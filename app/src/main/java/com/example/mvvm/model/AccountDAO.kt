package com.example.mvvm.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AccountDAO {

    @Query("SELECT * FROM data")
    fun getAll(): LiveData<MutableList<AccountEntity>>

    @Query("SELECT * FROM data WHERE id LIKE :id")
    suspend fun findById(id: Int): AccountEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: AccountEntity)

    @Update
    suspend fun updateData(vararg data: AccountEntity)

    @Query("SELECT * FROM data WHERE lastName LIKE :name")
    suspend fun findByLastName(name: String): AccountEntity

    @Query("DELETE FROM data")
    suspend fun deleteAll()

}
