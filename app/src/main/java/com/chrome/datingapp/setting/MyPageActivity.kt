

package com.chrome.datingapp.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chrome.datingapp.R
import com.chrome.datingapp.auth.UserInfo
import com.chrome.datingapp.utils.FirebaseAuthUtils
import com.chrome.datingapp.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyPageActivity : AppCompatActivity() {

    private val uid = FirebaseAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        getMyData()
    }

    private fun getMyData() {

        val myProfile = findViewById<ImageView>(R.id.myProfile)
        val myUid = findViewById<TextView>(R.id.myUid)
        val myNickname = findViewById<TextView>(R.id.myNickname)
        val myAge = findViewById<TextView>(R.id.myAge)
        val myRegion = findViewById<TextView>(R.id.myRegion)
        val myGender = findViewById<TextView>(R.id.myGender)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserInfo::class.java)
                myUid.text = data!!.uid
                myNickname.text = data!!.nickname
                myAge.text = data!!.age
                myRegion.text = data!!.region
                myGender.text = data!!.gender

                val storageRef = Firebase.storage.reference.child(data.uid + ".jpeg")
                storageRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Log.d("Glide", task.toString())
                        Glide.with(baseContext)
                            .load(task.result)
                            .into(myProfile)
                    }
                })
            }
            override fun onCancelled(databseError: DatabaseError) {
                Log.w("MainActivity", "onCancelled", databseError.toException())
            }
        }
        FirebaseRef.userInfo.child(uid).addValueEventListener(postListener)
    }
}