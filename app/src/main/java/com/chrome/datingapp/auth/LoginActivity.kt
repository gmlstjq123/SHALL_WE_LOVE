package com.chrome.datingapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.chrome.datingapp.MainActivity
import com.chrome.datingapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val loginBtn = findViewById<Button>(R.id.login)
        loginBtn.setOnClickListener {
            val email = findViewById<TextInputEditText>(R.id.email)
            val password = findViewById<TextInputEditText>(R.id.password)
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }

                    else {
                        Toast.makeText(this, "로그인 정보를 확인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}