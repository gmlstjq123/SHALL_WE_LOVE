package com.chrome.datingapp.chat.retrofit

import com.chrome.datingapp.chat.retrofit.Repository.Companion.CONTENT_TYPE
import com.chrome.datingapp.chat.retrofit.Repository.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NoticeAPI {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotice) : retrofit2.Response<ResponseBody>
}