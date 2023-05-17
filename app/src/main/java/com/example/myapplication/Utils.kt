package com.example.myapplication

import android.content.SharedPreferences
import com.google.firebase.database.FirebaseDatabase

object Utils {
    var USER_ID: String = "userId"
    const val DB_URL = "https://roadbuddy-80319-default-rtdb.europe-west1.firebasedatabase.app"
    const val API_KEY = "AIzaSyCuZBgNJaCdscOY97ktuaqqmEW_6zX5T88"
    const val SEARCH_RESULT_CODE = 102
    const val PICK_IMAGE_REQUEST = 71
    const val TRIP_INFO = "TRIP_INFO"

    fun writeNewUser(user: User, userUid: String) {
        val database = FirebaseDatabase.getInstance(DB_URL).getReference("Users")
        database.child(userUid).setValue(user)
    }

    fun getUserId(sharedPreferences: SharedPreferences): String {
        return sharedPreferences.getString(USER_ID, "").toString()
    }

    fun saveLoginUser(sharedPreferences: SharedPreferences, userId: String) {
        sharedPreferences.edit().putString(USER_ID, userId).apply()
    }

    fun logoutUser(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit().putString(USER_ID, "").apply()
    }
}

enum class TripsType {
    DEPARTURE, DESTINATION
}