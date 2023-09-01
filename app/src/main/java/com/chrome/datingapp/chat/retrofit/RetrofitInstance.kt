package com.chrome.datingapp.chat.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Repository.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api = retrofit.create(NoticeAPI::class.java)
    }
}