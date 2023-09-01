package com.chrome.datingapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.chrome.datingapp.auth.IntroActivity
import com.chrome.datingapp.auth.UserInfo
import com.chrome.datingapp.setting.MyPageActivity
import com.chrome.datingapp.setting.SettingActivity
import com.chrome.datingapp.utils.FirebaseAuthUtils
import com.chrome.datingapp.utils.FirebaseRef
import com.chrome.datingapp.utils.MyInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter : CardStackAdapter
    lateinit var manager : CardStackLayoutManager

    private val userInfoList = mutableListOf<UserInfo>()
    private var userCount = 0
    private val uid = FirebaseAuthUtils.getUid()
    private lateinit var currentUserGender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myPageBtn = findViewById<ImageView>(R.id.setting)
        val logoutBtn = findViewById<ImageView>(R.id.logout)

        myPageBtn.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        logoutBtn.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)
        manager = CardStackLayoutManager(baseContext, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                if(direction == Direction.Right) {
                    saveUserLike(uid, userInfoList[userCount].uid.toString())
                }
                userCount++
                if(userCount == userInfoList.count()) {
                    getUserInfoList(currentUserGender)
                    Toast.makeText(baseContext, "유저 정보를 다시 가져옵니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })

        cardStackAdapter = CardStackAdapter(baseContext, userInfoList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter

        getMyData()
    }

    private fun getMyData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserInfo::class.java)
                currentUserGender = data?.gender.toString()
                MyInfo.nickname = data?.nickname.toString()
                getUserInfoList(currentUserGender)
            }

            override fun onCancelled(databseError: DatabaseError) {
                Log.w("MainActivity", "onCancelled", databseError.toException())
            }
        }
        FirebaseRef.userInfo.child(uid).addValueEventListener(postListener)
    }

    private fun getUserInfoList(currentUserGender : String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(dataModel in dataSnapshot.children) {
                    val userInfo = dataModel.getValue(UserInfo::class.java)
                    if(userInfo!!.gender.toString().equals(currentUserGender)) {

                    } else {
                        userInfoList.add(userInfo!!)
                    }
                }
                cardStackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databseError: DatabaseError) {
                Log.w("MainActivity", "onCancelled", databseError.toException())
            }
        }
        FirebaseRef.userInfo.addValueEventListener(postListener)
    }

    private fun saveUserLike(uid : String, otherUid : String) {
        FirebaseRef.userLike.child(uid).child(otherUid).setValue("like")
        getOtherUserLike(uid, otherUid)
    }

    private fun getOtherUserLike(uid : String, otherUid : String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(dataModel in dataSnapshot.children) {
                    if(dataModel.key.toString() == uid) {
                        Toast.makeText(this@MainActivity, "매칭되었습니다", Toast.LENGTH_SHORT).show()
                        createNotificationChannel()
                        sendNotification()
                    }
                }
            }
            override fun onCancelled(databseError: DatabaseError) {
                Log.w("MainActivity", "onCancelled", databseError.toException())
            }
        }
        FirebaseRef.userLike.child(otherUid).addValueEventListener(postListener)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("test", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        if(NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            var builder = NotificationCompat.Builder(this, "test")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("매칭 완료")
                .setContentText("내가 좋아요 표시한 사람이 나를 좋아요 표시하였습니다.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            with(NotificationManagerCompat.from(this)) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notify(1, builder.build())
            }
        }
        else {
            Log.w("notification", "알림 수신이 차단된 상태입니다.")
        }
    }
}