package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityIntroBinding
import com.example.myapplication.login.LoginActivity
import com.example.myapplication.register.RegisterActivity


class IntroActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityIntroBinding
    private lateinit var userUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("FILE_1", Context.MODE_PRIVATE)
        userUid = Utils.getUserId(sharedPreferences)

        if (Utils.getUserId(sharedPreferences).isNotEmpty()) startActivity(
            Intent(
                this,
                DashboardActivity::class.java
            )
        )

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}