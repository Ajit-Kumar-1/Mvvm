package com.example.mvvm.model

import androidx.room.*

@Dao
interface DataDAO {

    @Query("SELECT * FROM data")
    suspend fun getAll(): List<APIEntity>

    @Query("SELECT * FROM data WHERE id LIKE :id")
    suspend fun findById(id: Int): APIEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: APIEntity)

    @Delete
    suspend fun delete(data: APIEntity)

    @Update
    suspend fun updateData(vararg data: APIEntity)

    @Query("SELECT * FROM data WHERE lastName LIKE :name")
    suspend fun findByLastName(name:String): APIEntity

}
