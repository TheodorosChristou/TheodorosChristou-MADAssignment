package com.example.theodoroschristou_assignment

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Restaurant::class), version = 1, exportSchema = false)
public abstract class RestaurantDatabase: RoomDatabase() {
    abstract fun RestaurantDao(): RestaurantDao

    companion object {
        private var instance: RestaurantDatabase? = null

        fun getDatabase(ctx:Context) : RestaurantDatabase {
            var tmpInstance = instance
            if(tmpInstance == null) {
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    RestaurantDatabase::class.java,
                    "RestaurantDatabase"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }
}