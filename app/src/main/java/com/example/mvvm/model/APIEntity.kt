package com.example.mvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class APIEntity (
    @PrimaryKey(autoGenerate = true) var id: Int,
    var firstName: String,
    var lastName: String,
    var gender: String,
    var dob: String,
    var email: String,
    var phone: String,
    var website: String,
    var address: String,
    var status: String
)