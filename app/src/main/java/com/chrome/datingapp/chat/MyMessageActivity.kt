package com.chrome.datingapp.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.chrome.datingapp.R
import com.chrome.datingapp.auth.UserInfo
import com.chrome.datingapp.utils.FirebaseAuthUtils
import com.chrome.datingapp.utils.FirebaseRef
import com.chrome.datingapp.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyMessageActivity : AppCompatActivity() {

    lateinit var listViewAdapter : MessageAdapter
    val messageList = mutableListOf<MessageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_message)

        val listView = findViewById<ListView>(R.id.LVmessage)
        listViewAdapter = MessageAdapter(this, messageList)
        listView.adapter = listViewAdapter

        getMyMessage()
    }

    private fun getMyMessage() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear()

                for (datamModel in dataSnapshot.children) {
                    val message = datamModel.getValue(MessageModel::class.java)
                    Log.d("MyMessage", message.toString())
                    messageList.add(message!!)
                }
                messageList.reverse()
                listViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databseError: DatabaseError) {
                Log.w("MyMessage", "onCancelled", databseError.toException())
            }
        }
        FirebaseRef.userMessage.child(FirebaseAuthUtils.getUid()).addValueEventListener(postListener)
    }
}