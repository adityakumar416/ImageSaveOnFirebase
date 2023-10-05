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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageList = arrayListOf()

        binding.iconView.text = "List View"


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
              //  binding.recyclerview.layoutManager = LinearLayoutManager(this@ShowImageActivity)
                binding.recyclerview.layoutManager = GridLayoutManager(this@ShowImageActivity, 2)


                binding.recyclerview.adapter = ShowImageAdapter(imageList,this@ShowImageActivity)
                binding.recyclerview.adapter = ShowGridViewImageAdapter(imageList,this@ShowImageActivity)



                }



            override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ShowImageActivity,error.toString(),Toast.LENGTH_SHORT).show()
            }


        })

       /* adapter = ItemAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
*/
        // On icon click, switch between grid and list view
        binding.iconView.setOnClickListener {
            if (isGridView) {
                binding.recyclerview.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
             //   binding.iconView.setImageResource(R.drawable.ic_grid_view)
                binding.recyclerview.adapter = ShowImageAdapter(imageList,this@ShowImageActivity)
                binding.iconView.text = "Grid View"

            } else {
                binding.recyclerview.layoutManager = GridLayoutManager(this, 2)
              //  binding.iconView.setImageResource(R.drawable.ic_list_view)
                binding.recyclerview.adapter = ShowGridViewImageAdapter(imageList,this@ShowImageActivity)
                binding.iconView.text = "List View"

            }
            isGridView = !isGridView
        }

    }
}