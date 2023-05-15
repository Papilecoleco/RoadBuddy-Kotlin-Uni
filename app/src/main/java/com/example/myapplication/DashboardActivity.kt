package com.example.myapplication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.myapplication.Utils.DB_URL
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.db.dao.UserDao
import com.example.myapplication.db.entities.UserEntity
import com.example.myapplication.login.LoginActivity
import com.example.myapplication.trips.AddTripActivity
import com.example.myapplication.trips.ViewTripsActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class DashboardActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var database: DatabaseReference
    private lateinit var userRemote: User
    private lateinit var userLocal: UserEntity
    private lateinit var userUid: String
    private lateinit var roomDatabase: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var profileImage: CircleImageView
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("FILE_1", Context.MODE_PRIVATE)
        userUid = Utils.getUserId(sharedPreferences)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        //ALL NAV things
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //decide what to do when clicked
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this, DashboardActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.nav_logout -> logout()
            }
            true
        }


        //DATABASE
        //get url as reference
        database = Firebase.database(DB_URL).reference
        roomDatabase = AppDatabase(this)

        //get dao
        userDao = roomDatabase.userDao()

        //specify the child (where it is stored on firebaseDB, then what to use as child name
        database.child("Users").child(userUid).get().addOnSuccessListener { data ->
            userRemote = data.getValue(User::class.java)!!
            //map the entity and put them into room, so that there is no need to get them from firebase
            userDao.insertOrReplace(
                UserEntity(
                    id = userUid,
                    username = userRemote.username,
                    name = userRemote.name,
                    email = userRemote.email
                )
            )
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.message.toString(), Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            userLocal = userDao.getUserById(userUid)

            //SHOW THE VARIABLES
            binding.lblUsername.text = userLocal.name

            //Variables into nav_header
            findViewById<TextView>(R.id.tvUsername).text = userLocal.name
            findViewById<TextView>(R.id.tvEmail).text = userLocal.email
        }

        //GET IMAGE TO NAV HEADER
        //get Reference to where image is
        val storageRef = FirebaseStorage.getInstance().reference.child("myImages/$userUid")

        //Store in tempfile
        val localFile = File.createTempFile("tempImage", "jpeg")
        //wait for image to load

        val progressDialog = ProgressDialog(this).apply {
            setMessage("Fetching image...")
            setCancelable(false)
            show()
        }

        //Get the right id, not using this gets null pointer
        val header = navView.getHeaderView(0) as LinearLayout
        profileImage = header.findViewById(R.id.profileImage)

        //actual code for gettin image
        storageRef.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            profileImage.setImageBitmap(bitmap)

        }.addOnFailureListener {
            Toast.makeText(this, "Failed ! Cant get image", Toast.LENGTH_SHORT).show()
        }
            .addOnCompleteListener {
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
            }


        //ADD TRIP
        binding.btnAddTrip.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddTripActivity::class.java
                )
            )
        }
        //VIEW TRIPS
        binding.btnViewTrips.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ViewTripsActivity::class.java
                )
            )
        }

    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        Utils.logoutUser(sharedPreferences)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}

//startactivity começa uma atividade nova mas nao fecha a anterior, quando carrega no back volta para a anterior
//finish() fecha a atividade a correr
//finisAffinity fecha tudo