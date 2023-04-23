package com.example.firebaseimagesave

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ShowImageAdapter(
    private val imageList: ArrayList<ImageModel>,
    private val context: Context
): RecyclerView.Adapter<ShowImageAdapter.ViewHolder>() {

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val image :ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val imageModel:ImageModel = imageList[position]



        Picasso
            .get()
            .load(imageModel.url)
            .into(holder.image)
/*
            Glide
                .with(Context)
            .load(imageModel.url)
            .into(holder.image)*/



    }
    


    override fun getItemCount(): Int {

            return imageList.size
    }
}



