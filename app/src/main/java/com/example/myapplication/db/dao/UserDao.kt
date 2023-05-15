package com.example.myapplication.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.db.entities.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(user: UserEntity)

    @Query("SELECT * FROM User")
    fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM User WHERE userId IN(:uid)")
    fun getUserById(uid: String): UserEntity
}