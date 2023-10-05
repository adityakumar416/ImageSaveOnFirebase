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

class ShowGridViewImageAdapter(
    private val imageList: ArrayList<ImageModel>,
    private val context: Context
): RecyclerView.Adapter<ShowGridViewImageAdapter.ViewHolder>() {

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val image :ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.grid_view_list_item,parent,false)

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


        holder.itemView.setOnLongClickListener(OnLongClickListener {
            showDialog(imageModel)
            true
        })


    }

    private fun showDialog(imageModel: ImageModel) {
        val firebaseStorage = FirebaseStorage.getInstance().getReference("images")
        val databaseRef = FirebaseDatabase.getInstance().getReference("images")

        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Banner")
            .setMessage("Do you want to delete this banner ?")
            .setNegativeButton("No", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            })
            .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    firebaseStorage.storage.getReferenceFromUrl(imageModel.url!!).delete().addOnSuccessListener(object : OnSuccessListener<Void>{
                        override fun onSuccess(p0: Void?) {
                            Toast.makeText(context, "Banner deleted", Toast.LENGTH_SHORT).show()

                            databaseRef.child(imageModel.imageId.toString()).removeValue()
                            imageList.remove(imageModel)

                        }
                    })
                        .addOnFailureListener(object :OnFailureListener{
                            override fun onFailure(p0: Exception) {

                            }

                        })
                }

            }).show()
    }



    override fun getItemCount(): Int {

        return imageList.size
    }
}
