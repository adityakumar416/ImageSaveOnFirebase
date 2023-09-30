package com.example.firebaseimagesave

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseimagesave.databinding.ActivityShowImageBinding
import com.google.firebase.database.*

class ShowImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowImageBinding
    private lateinit var imageList: ArrayList<ImageModel>
    private var isGridView = true
    private lateinit var adapter: ShowImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ShowImageAdapter(imageList,this@ShowImageActivity) // Replace getData() with your data source
        binding.recyclerview.adapter = adapter

        binding.toggleIcon.setOnClickListener {
            // Toggle between grid and list view
            isGridView = !isGridView

            if (isGridView) {
                // Switch to GridLayoutManager
                val gridLayoutManager = GridLayoutManager(this, 2) // 2 columns in the grid
                binding.recyclerview.layoutManager = gridLayoutManager
            } else {
                // Switch to LinearLayoutManager (List View)
                val linearLayoutManager = LinearLayoutManager(this)
                binding.recyclerview.layoutManager = linearLayoutManager
            }

            // Notify the adapter about the layout change
            adapter.notifyDataSetChanged()
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        imageList = arrayListOf()

        val gridLayoutManager = GridLayoutManager(this, 2) // 2 columns in the grid
        binding.recyclerview.layoutManager = gridLayoutManager

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