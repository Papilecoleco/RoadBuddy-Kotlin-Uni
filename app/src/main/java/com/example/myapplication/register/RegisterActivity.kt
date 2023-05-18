package com.example.myapplication.register

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.DashboardActivity
import com.example.myapplication.R
import com.example.myapplication.User
import com.example.myapplication.Utils
import com.example.myapplication.Utils.writeNewUser
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.db.entities.UserEntity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var name: String
    private lateinit var sharedPreferences: SharedPreferences


    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("FILE_1", Context.MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        database = AppDatabase(this)

        binding.btnRegister.setOnClickListener {
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()
            username = binding.etUsername.text.toString()
            name = binding.etName.text.toString()
            if (email.isEmpty() || password.isEmpty() || username.isEmpty() || name.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.enter_credentials),
                    Toast.LENGTH_SHORT
                ).show()
            } else registerUser()
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun registerUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userUid: String? = task.result.user?.uid
                    Toast.makeText(
                        this,
                        "Registado Com Sucesso !",
                        Toast.LENGTH_SHORT
                    ).show()

                    writeNewUser(User(username, email, name), userUid!!)
                    Utils.saveLoginUser(sharedPreferences, userUid)
                    database.userDao().insertOrReplace(
                        UserEntity(userUid, username, email, name)
                    )
                    startActivity(Intent(this, DashboardActivity::class.java))
                } else {
                    Toast.makeText(
                        this,
                        task.exception?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}