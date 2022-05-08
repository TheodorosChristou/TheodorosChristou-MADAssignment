package com.example.theodoroschristou_assignment

import androidx.room.*

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<Restaurant>

    @Insert
    fun insert(restaurants: Restaurant) : Long

}