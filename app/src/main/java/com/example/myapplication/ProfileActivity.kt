package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityProfileBinding
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.db.dao.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var currentFirebaseUser: String
    private lateinit var userDao: UserDao
    private lateinit var userUid: String
    private lateinit var binding: ActivityProfileBinding
    private lateinit var roomDatabase: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        binding.btnChooseImage.setOnClickListener { launchGallery() }
        //binding.btnUploadImage.setOnClickListener { uploadImage() }
        /*binding.editNomeBtn.setOnClickListener {
            binding.lleditName.visibility = View.VISIBLE
        }
        binding.editUsernameBtn.setOnClickListener {
            binding.llEditUsername.visibility = View.VISIBLE
        }
        binding.btnEditName.setOnClickListener {
            editNameOrUsername("name", binding.etNovoNome.text.toString())
        }
        binding.btnEditUsername.setOnClickListener {
            editNameOrUsername("username", binding.etNovoUsername.text.toString())
        }*/

        binding.btnEdit.setOnClickListener {
            editNameOrUsername("name", binding.etNovoNome.text.toString())
            editNameOrUsername("username", binding.etNovoNome.text.toString())
            uploadImage()
        }

        database = Firebase.database(Utils.DB_URL).reference

        //Get the UID from user
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!.uid

        roomDatabase = AppDatabase(this)

        //get dao
        userDao = roomDatabase.userDao()

        sharedPreferences = getSharedPreferences("FILE_1", Context.MODE_PRIVATE)
        userUid = Utils.getUserId(sharedPreferences)
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_picture)),
            Utils.PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utils.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.imagePreview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            Log.e("FILEPATH", filePath.toString())
            val ref = storageReference?.child("myImages/$currentFirebaseUser")
            ref?.putFile(filePath!!)
                ?.addOnSuccessListener {
                    // Image upload success, get the download URL
                    ref.downloadUrl
                        .addOnSuccessListener { downloadUrl ->
                            // Save the download URL to the Firebase Database
                            val imageUrl = downloadUrl.toString()
                            database.child("Users").child(currentFirebaseUser).child("imageURL").setValue(imageUrl)
                            Toast.makeText(this, "Image carregada com successo!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Falha a carregar imagem: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                ?.addOnFailureListener { exception ->
                    Toast.makeText(this, "Falha a carregar imagem: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Por favor, selecione uma imagem!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun editNameOrUsername(child: String, value: String) {
        if (value != ""){
            database.child("Users").child(currentFirebaseUser).child(child).setValue(value)
            Toast.makeText(this, "$child alterado com sucesso!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DashboardActivity::class.java).apply {}
            //startActivity(intent)
        }else{
            Toast.makeText(this, "$child não foi alterado", Toast.LENGTH_SHORT).show()
        }

    }
}


