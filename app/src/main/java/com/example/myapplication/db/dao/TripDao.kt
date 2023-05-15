package com.example.myapplication.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.db.entities.TripEntity

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(trip: TripEntity)

    @Query("SELECT * FROM Trip")
    fun getAllTrips(): List<TripEntity>
}