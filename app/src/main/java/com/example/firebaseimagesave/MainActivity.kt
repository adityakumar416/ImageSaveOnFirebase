package com.example.firebaseimagesave

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseimagesave.NetworkReceiver.Companion.networkReceiverListener
import com.example.firebaseimagesave.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import kotlin.text.Typography.amp


class MainActivity : AppCompatActivity() {
    // creating variable for buttons, image view and Uri for file.
    private var firebaseStorage: FirebaseStorage? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var networkReceiver: NetworkReceiver? = null
    private lateinit var uri:Uri
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNetworkListener()
        // on below line initializing variables for buttons and image view.


        // on below line adding click listener for our choose image button.
        val firebaseStorage = FirebaseStorage.getInstance().getReference("images")
        val databaseRef = FirebaseDatabase.getInstance().getReference("images")

        binding.userImage.setOnClickListener {

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 71)


        }

        binding.showImage.setOnClickListener {
            val intent = Intent(this@MainActivity,ShowImageActivity::class.java)
            startActivity(intent)

        }



           binding.btnUploadImage.setOnClickListener(object : android.view.View.OnClickListener {

                @RequiresApi(Build.VERSION_CODES.R)
                override fun onClick(v: android.view.View?) {
                    val processDialog = ProgressDialog(this@MainActivity)
                    processDialog.setMessage("Image Uploading")
                    processDialog.setCancelable(false)
                    processDialog.show()


                    val storageRef = firebaseStorage.child(System.currentTimeMillis().toString()+"."+ getFileExtension(uri))
                    storageRef.putFile(uri)
                        .addOnSuccessListener {

                                Log.i(TAG, "onSuccess Main: $it")

                                Toast.makeText(this@MainActivity, "Upload Image Successfully", Toast.LENGTH_SHORT).show()
                                processDialog.dismiss()


                               val urlTask: Task<Uri> = it.storage.downloadUrl
                                 while (!urlTask.isSuccessful);
                                    val downloadUrl:Uri = urlTask.result
                                    Log.i(TAG, "onSuccess: $downloadUrl")

                                    val imageModel =ImageModel(databaseRef.push().key,"Aditya",downloadUrl.toString())
                                    val uploadId =imageModel.imageId

                            if (uploadId != null) {
                                databaseRef.child(uploadId).setValue(imageModel)
                            }


                        }

                        .addOnFailureListener {

                                Toast.makeText(this@MainActivity, "Failed to Upload Image", Toast.LENGTH_SHORT).show()
                                processDialog.dismiss()

                    }
                     .addOnProgressListener { taskSnapshot -> //displaying the upload progress
                val progress =
                    100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                         processDialog.setMessage("Uploaded " + progress.toInt() + "%...")
            }
                }
            })
       /* binding.btnUploadImage.setOnClickListener{
            uploadFile()
        }*/

    }
  /*  private fun uploadFile() {
        val firebaseStorage = FirebaseStorage.getInstance().getReference("images")
        val databaseRef = FirebaseDatabase.getInstance().getReference("images")
        //checking if file is available
        //displaying progress dialog while image is uploading
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading")
        progressDialog.show()

        val storageRef = firebaseStorage.child(System.currentTimeMillis().toString()+"."+ getFileExtension(uri))

        //adding the file to reference
        storageRef.putFile(uri)
            .addOnSuccessListener {
                progressDialog.dismiss()

                //displaying success toast
                Toast.makeText(applicationContext, "File Uploaded ", Toast.LENGTH_LONG).show()

                //creating the upload object to store uploaded image details
                val imageModel = ImageModel(
                    "Aditya",
                    it.storage.downloadUrl.toString()
                )

                //adding an upload to firebase database
                val uploadId: String = databaseRef.push().key.toString()
                databaseRef.child(uploadId).setValue(imageModel)
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, exception.message, Toast.LENGTH_LONG).show()
            }
            .addOnProgressListener { taskSnapshot -> //displaying the upload progress
                val progress =
                    100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
            }
    }*/

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = this.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 71 && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            uri = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                binding.userImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun setupNetworkListener() {
        NetworkReceiver.networkReceiverListener =
            object : NetworkReceiver.NetworkReceiverListener {
                override fun onNetworkConnectionChanged(isConnected: Boolean) {
                    toggleNoInternetBar(!isConnected)
                }
            }
    }

    private fun toggleNoInternetBar(display: Boolean) {
        if (display) {
            val enterAnim = AnimationUtils.loadAnimation(this, R.anim.enter_from_bottom)
            binding.noInternetBar.startAnimation(enterAnim)
        } else {
            val exitAnim = AnimationUtils.loadAnimation(this, R.anim.exit_to_bottom)
            binding.noInternetBar.startAnimation(exitAnim)
        }
        binding.noInternetBar.visibility = if (display) View.VISIBLE else View.GONE
    }

    override fun onStart() {
        super.onStart()
        networkReceiver = NetworkReceiver()
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        networkReceiver?.let {
            unregisterReceiver(it) }
    }



}