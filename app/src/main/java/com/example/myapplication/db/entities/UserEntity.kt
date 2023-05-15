package com.example.myapplication.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "User")
data class UserEntity(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "userId")
    var id: String,

    @ColumnInfo(name = "username")
    @NotNull
    var username: String,

    @ColumnInfo(name = "email")
    @NotNull
    var email: String,

    @ColumnInfo(name = "name")
    @NotNull
    var name: String,
)