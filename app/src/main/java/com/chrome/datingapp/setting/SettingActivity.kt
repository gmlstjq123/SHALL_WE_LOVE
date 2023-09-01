package com.chrome.datingapp.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.chrome.datingapp.R
import com.chrome.datingapp.chat.MatchingListActivity
import com.chrome.datingapp.chat.MyMessageActivity

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val myPageBtn = findViewById<Button>(R.id.myPageBtn)
        val likeListBtn = findViewById<Button>(R.id.myMatchingList)
        val myMessageBtn = findViewById<Button>(R.id.myMessageList)

        myPageBtn.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }

        likeListBtn.setOnClickListener {
            startActivity(Intent(this, MatchingListActivity::class.java))
        }

        myMessageBtn.setOnClickListener {
            startActivity(Intent(this, MyMessageActivity::class.java))
        }
    }
}