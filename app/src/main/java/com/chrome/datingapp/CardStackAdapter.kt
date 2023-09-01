package com.chrome.datingapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chrome.datingapp.auth.UserInfo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CardStackAdapter(val context : Context, val items: MutableList<UserInfo>) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardStackAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val nickname = itemView.findViewById<TextView>(R.id.itemNickname)
        val age = itemView.findViewById<TextView>(R.id.itemAge)
        val region = itemView.findViewById<TextView>(R.id.itemRegion)
        val image = itemView.findViewById<ImageView>(R.id.profileImage)

        fun bindItems(item : UserInfo) {
            val storageRef = Firebase.storage.reference.child(item.uid + ".jpeg")
            storageRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d("Glide", task.toString())
                    Glide.with(context)
                        .load(task.result)
                        .into(image)
                }
            })

            nickname.text = item.nickname
            age.text = item.age
            region.text = item.region
        }
    }
}