package com.chrome.datingapp.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRef {
    companion object {
        val database = Firebase.database
        val userInfo = database.getReference("userInfo")
        val userLike = database.getReference("userLike")
        val userMessage = database.getReference("userMessage")
    }
}