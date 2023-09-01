package com.chrome.datingapp.chat

import android.app.Notification
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.chrome.datingapp.R
import com.chrome.datingapp.auth.UserInfo
import com.chrome.datingapp.chat.retrofit.NoticeModel
import com.chrome.datingapp.chat.retrofit.PushNotice
import com.chrome.datingapp.chat.retrofit.RetrofitInstance
import com.chrome.datingapp.utils.FirebaseAuthUtils
import com.chrome.datingapp.utils.FirebaseRef
import com.chrome.datingapp.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MatchingListActivity : AppCompatActivity() {

    private val uid = FirebaseAuthUtils.getUid()
    private val myLikeList = mutableListOf<String>()
    private val myLikeUserInfo = mutableListOf<UserInfo>()
    lateinit var receiverUid : String
    lateinit var receiverToken : String
    lateinit var listViewAdapter : LVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_list)

        val listview = findViewById<ListView>(R.id.listViewItems)
        listViewAdapter = LVAdapter(this, myLikeUserInfo)

        listview.adapter = listViewAdapter

        getMyLikeList()

        listview.setOnItemClickListener { parent, view, position, id ->
            val noticeModel = NoticeModel("제가 당신을 좋아하나봐요!", "저.. 어떠세요?")
            val pushNotice = PushNotice(noticeModel, myLikeUserInfo[position].token.toString())
            pushNotification(pushNotice)
        }

        listview.setOnItemLongClickListener { parent, view, position, id ->
            matchingChk(myLikeUserInfo[position].uid.toString())
            receiverUid = myLikeUserInfo[position].uid.toString()
            receiverToken = myLikeUserInfo[position].token.toString()
            return@setOnItemLongClickListener(true)
        }
    }

    private fun matchingChk(otherUid : String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.children.count() == 0) {
                    Toast.makeText(this@MatchingListActivity, "매칭되지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
                }
                else {
                    for(dataModel in dataSnapshot.children) {
                        if(dataModel.key.toString().equals(uid)) {
                            Toast.makeText(this@MatchingListActivity, "매칭된 유저입니다.", Toast.LENGTH_SHORT).show()
                            showDialog()
                        }
                        else {
                            Toast.makeText(this@MatchingListActivity, "매칭되지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onCancelled(databseError: DatabaseError) {
                Log.w("MainActivity", "onCancelled", databseError.toException())
            }
        }
        FirebaseRef.userLike.child(otherUid).addValueEventListener(postListener)
    }

    private fun getMyLikeList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(dataModel in dataSnapshot.children) {
                    myLikeList.add(dataModel.key.toString())
                }
                getUserInfoList()
            }
            override fun onCancelled(databseError: DatabaseError) {
                Log.w("MatchingListActivity", "onCancelled", databseError.toException())
            }
        }
        FirebaseRef.userLike.child(uid).addValueEventListener(postListener)
    }

    private fun getUserInfoList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(dataModel in dataSnapshot.children) {
                    val userInfo = dataModel.getValue(UserInfo::class.java)
                    if(myLikeList.contains(userInfo?.uid)) {
                        myLikeUserInfo.add(userInfo!!)
                    }
                }
                listViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databseError: DatabaseError) {
                Log.w("MatchingListActivity", "onCancelled", databseError.toException())
            }
        }
        FirebaseRef.userInfo.addValueEventListener(postListener)
    }

    private fun pushNotification(notification: PushNotice) = CoroutineScope(Dispatchers.IO).launch {
        RetrofitInstance.api.postNotification(notification)
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("메시지 보내기")
        val alertDialog = builder.show()

        val sendBtn = alertDialog.findViewById<Button>(R.id.send)
        val message = alertDialog.findViewById<EditText>(R.id.message)
        sendBtn?.setOnClickListener {
            val messageText = message!!.text.toString()
            val messageModel = MessageModel(MyInfo.nickname, messageText)
            FirebaseRef.userMessage.child(receiverUid).push().setValue(messageModel)

            val noticeModel = NoticeModel(MyInfo.nickname, messageText)
            val pushModel = PushNotice(noticeModel, receiverToken)
            pushNotification(pushModel)

            alertDialog.dismiss()
        }
    }
}