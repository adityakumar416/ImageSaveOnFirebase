package com.example.firebaseimagesave

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseimagesave.databinding.ActivityShowImageBinding
import com.google.firebase.database.*

class ShowImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowImageBinding
    private lateinit var imageList: ArrayList<ImageModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        imageList = arrayListOf()

        val databaseReference = FirebaseDatabase.getInstance().getReference("images")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                imageList.clear()
                Log.i(TAG, "User Image $snapshot")
                    for (dataSnapshot in snapshot.children) {

                        val image: ImageModel? = dataSnapshot.getValue(ImageModel::class.java)
                        if (image != null) {
                            imageList.add(image)
                        }

                    }


                binding.recyclerview.layoutManager = LinearLayoutManager(this@ShowImageActivity,RecyclerView.VERTICAL,false)

                binding.recyclerview.adapter = ShowImageAdapter(imageList,this@ShowImageActivity)
                }

            override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ShowImageActivity,error.toString(),Toast.LENGTH_SHORT).show()
            }


        })



    }
}