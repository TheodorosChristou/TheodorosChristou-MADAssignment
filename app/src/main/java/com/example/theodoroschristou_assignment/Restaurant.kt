package com.example.theodoroschristou_assignment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
class Restaurant(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String = "",
    val address: String = "",
    val cuisine: String = "",
    val rating: Int = 0,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)