package com.example.myapplication.trips

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.DashboardActivity
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.databinding.ActivityInfoBoleiaBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InfoTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBoleiaBinding
    private lateinit var tripInfo: Trip
    private lateinit var databaseTripReference: DatabaseReference
    private var trip = Trip()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBoleiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseTripReference = FirebaseDatabase.getInstance(Utils.DB_URL).getReference("Trip")
        sharedPreferences = getSharedPreferences("FILE_1", Context.MODE_PRIVATE)
        trip.userUid = Utils.getUserId(sharedPreferences)

        //no adapter tem um putextra onde mandamos o item[position] que é a trip em que clicamos,
        // serializable é para podermos passar um objeto desse tipo por intent
        tripInfo = intent.getSerializableExtra(Utils.TRIP_INFO) as Trip
        setCustomTrip()

        binding.btnConfirm.setOnClickListener {
            Toast.makeText(
                this,
                getString(R.string.reservation_confirmed),
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }


    private fun setCustomTrip() {
        binding.partida.text = buildDesignatedString(getString(R.string.from), tripInfo.departure)
        binding.destino.text = buildDesignatedString(getString(R.string.to), tripInfo.destination)
        binding.txtDate.text = buildDesignatedString(getString(R.string.day), tripInfo.date)
        binding.txtTime.text = buildDesignatedString(getString(R.string.time), tripInfo.time)
        (buildDesignatedString(getString(R.string.price_show), tripInfo.price) + "€").also { binding.txtPrice.text = it }
        binding.txtSeats.text =
            buildDesignatedString(getString(R.string.avaiable_seats), tripInfo.availableSeats)
    }

    private fun buildDesignatedString(str: String, value: String): String {
        return buildString {
            this.append(str)
            this.append(" ")
            this.append(value)
        }
    }

}