package com.example.theodoroschristou_assignment

import androidx.room.*

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurants WHERE id=:id")
    fun getRestaurantById(id: Long): Restaurant?

    @Query("SELECT * FROM restaurants")
    fun getAllSongs(): List<Restaurant>

    @Insert
    fun insert(restaurants: Restaurant) : Long

    @Update
    fun update(restaurants: Restaurant) : Int

    @Delete
    fun delete(restaurants: Restaurant) : Int
}