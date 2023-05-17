package com.example.myapplication.trips


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.Utils.SEARCH_RESULT_CODE
import com.example.myapplication.databinding.ActivityTestBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlin.random.Random


class SearchActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Places.initialize(applicationContext, Utils.API_KEY)

        val fieldList : List<Place.Field> = mutableListOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
        val intent : Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
            .build(this@SearchActivity)
        startActivityForResult(intent, SEARCH_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SEARCH_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                getAddressFromApiAndSetResult(data)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(
                    this,
                    getString(R.string.error_search_view),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getAddressFromApiAndSetResult(data: Intent?) {
        val place: Place = Autocomplete.getPlaceFromIntent(data)
        val intent = Intent()
        intent.putExtra("address", place.address)
        intent.putExtra("lat", place.latLng?.latitude ?: "")
        intent.putExtra("long", place.latLng?.longitude ?: "")
        setResult(SEARCH_RESULT_CODE, intent)
        finish()
    }

}
