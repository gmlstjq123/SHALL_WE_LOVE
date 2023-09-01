package com.chrome.datingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.chrome.datingapp.auth.IntroActivity
import com.chrome.datingapp.utils.FirebaseAuthUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {

    // private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // auth = Firebase.auth
        // val uid = auth.currentUser?.uid.toString()
        val uid = FirebaseAuthUtils.getUid()

        if(uid == null) {
            Handler().postDelayed({
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }, 3000)
        }

        else {
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 3000)
        }

    }
}