package com.example.myapplication.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "Trip")
data class TripEntity(

    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "departure")
    @NotNull
    var departure: String,

    @ColumnInfo(name = "departureLatitude")
    @NotNull
    var departureLatitude: String? = "",

    @ColumnInfo(name = "departureLongitude")
    @NotNull
    var departureLongitude: String? = "",

    @ColumnInfo(name = "destination")
    @NotNull
    var destination: String,

    @ColumnInfo(name = "destinationLatitude")
    @NotNull
    var destinationLatitude: String? = "",

    @ColumnInfo(name = "destinationLongitude")
    @NotNull
    var destinationLongitude: String? = "",

    @ColumnInfo(name = "price")
    @NotNull
    var price: String,

    @ColumnInfo(name = "avaiableSeats")
    @NotNull
    var avaiableSeats: String,

    )