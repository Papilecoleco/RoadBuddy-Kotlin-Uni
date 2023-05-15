package com.example.myapplication.trips

import java.io.Serializable

data class Trip(
    var id: String = "",
    var departure: String = "",
    var departureLatitude: String = "",
    var departureLongitude: String = "",
    var destination: String = "",
    var destinationLatitude: String = "",
    var destinationLongitude: String = "",
    var date: String = "",
    var time: String = "",
    var price: String = "",
    var availableSeats: String = "",
    var userUid: String = ""
) : Serializable