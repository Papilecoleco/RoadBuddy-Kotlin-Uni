package com.example.myapplication.trips

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.myapplication.R
import com.example.myapplication.TripsType
import com.example.myapplication.Utils
import com.example.myapplication.Utils.SEARCH_RESULT_CODE
import com.example.myapplication.Utils.getUserId
import com.example.myapplication.databinding.ActivityAddTripBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class AddTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTripBinding
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.N)
    private var dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private lateinit var databaseTripReference: DatabaseReference
    private val dateCalendar = Calendar.getInstance()
    private val timeCalendar = Calendar.getInstance()
    private val style = AlertDialog.THEME_HOLO_DARK
    private var tripType = TripsType.DEPARTURE
    private var trip = Trip()


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseTripReference = FirebaseDatabase.getInstance(Utils.DB_URL).getReference("Trip")
        sharedPreferences = getSharedPreferences("FILE_1", Context.MODE_PRIVATE)
        trip.userUid = getUserId(sharedPreferences)

        binding.btnDeparture.setOnClickListener {
            tripType = TripsType.DEPARTURE
            startActivityForResult(Intent(this, SearchActivity::class.java), SEARCH_RESULT_CODE)
        }
        binding.btnDestination.setOnClickListener {
            tripType = TripsType.DESTINATION
            startActivityForResult(Intent(this, SearchActivity::class.java), SEARCH_RESULT_CODE)
        }

        binding.etPrice.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.etPrice.text?.clear()
        }
        binding.etPrice.addTextChangedListener {
            trip.price = it.toString()
        }

        binding.etSeats.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.etSeats.text?.clear()
        }
        binding.etSeats.addTextChangedListener {
            trip.availableSeats = it.toString()
        }

        binding.btnSubmit.setOnClickListener {
            submitTrip()
        }

        binding.btnDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    trip.date = dateFormat.format(selectedDate.time)
                    binding.btnDate.text = dateFormat.format(selectedDate.time)
                },
                dateCalendar.get(Calendar.YEAR),
                dateCalendar.get(Calendar.MONTH),
                dateCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        binding.btnTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                this, style, { _, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)
                    val time = timeFormat.format(selectedTime.time)
                    trip.time = time
                    binding.btnTime.text = time
                },

                timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }
    }

    private fun submitTrip() {
        if (trip.departure.isNotEmpty() &&
            trip.departureLatitude.isNotEmpty() &&
            trip.departureLongitude.isNotEmpty() &&
            trip.destination.isNotEmpty() &&
            trip.destinationLatitude.isNotEmpty() &&
            trip.destinationLongitude.isNotEmpty() &&
            trip.time.isNotEmpty() && trip.price.isNotEmpty() &&
            trip.availableSeats.isNotEmpty() && trip.userUid.isNotEmpty()
        ) {
            binding.progress.visibility = View.VISIBLE
            databaseTripReference.get()
                .addOnSuccessListener {
                    val id = it.childrenCount + 1
                    trip.id = id.toString()
                    databaseTripReference.child("$id").setValue(trip)
                    setViewAfterSubmitTrip(success = true)
                }
                .addOnFailureListener {
                    setViewAfterSubmitTrip(success = false)
                }
        } else {
            Toast.makeText(this, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* If API worked, we needed to verify both the result code and also if the resultCode was RESULT_OK,
         since the API isn't working, we don't check for RESULT_OK because we mocked the results
         if (resultCode == SEARCH_RESULT_CODE && resultCode == RESULT_OK) {
         */
        if (resultCode == SEARCH_RESULT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                val address = data.getStringExtra("address")
                val lat = data.getStringExtra("lat")
                val long = data.getStringExtra("long")
                if (tripType == TripsType.DEPARTURE) {
                    binding.btnDeparture.text = address
                    trip.departure = address.toString()
                    trip.departureLatitude = lat.toString()
                    trip.departureLongitude = long.toString()
                } else {
                    binding.btnDestination.text = address
                    trip.destination = address.toString()
                    trip.destinationLatitude = lat.toString()
                    trip.destinationLongitude = long.toString()
                }
            }
        }
    }

    private fun setViewAfterSubmitTrip(success: Boolean) {
        if (success) {
            binding.content.visibility = View.GONE
            binding.progress.visibility = View.GONE
            binding.imgSuccess.visibility = View.VISIBLE
            binding.imgSuccess.setImageResource(R.drawable.ic_success)
            binding.txtSuccess.visibility = View.VISIBLE
            binding.txtSuccess.text = getString(R.string.trip_created_success)
            binding.btnTripResult.visibility = View.VISIBLE
            binding.btnTripResult.text = getString(R.string.next)
            binding.btnTripResult.setOnClickListener { finish() }
        } else {
            binding.content.visibility = View.GONE
            binding.progress.visibility = View.GONE
            binding.imgSuccess.visibility = View.VISIBLE
            binding.imgSuccess.setImageResource(R.drawable.ic_error)
            binding.txtSuccess.visibility = View.VISIBLE
            binding.txtSuccess.text = getString(R.string.error)
            binding.btnTripResult.visibility = View.VISIBLE
            binding.btnTripResult.text = getString(R.string.try_again)
            binding.btnTripResult.setOnClickListener {
                submitTrip()
            }
        }

    }
}
