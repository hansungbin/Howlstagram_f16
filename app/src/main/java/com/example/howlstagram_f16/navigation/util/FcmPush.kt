package com.example.howlstagram_f16.navigation.util

import android.util.Log
import com.example.howlstagram_f16.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class FcmPush {

//    var JSON = MediaType.parse("application/json; charset=utf-8")
//    var url = "https://fcm.googleapis.com/fcm/send"
//    var serverKey = "AAAAolx4sV4:APA91bFHEnPUlJ-Vjl8EH7QWB7L381H7p8xzpG2uZra4FI71xzyAbcJmCB7gPw2DKc8yU5mZez8l12eEHP8nQixHKykKaZZmyktabnAh5OOcCm3Q-VIrgbCUatQWSV8QMOSvhQWQIGJK"
//
//    var gson : Gson? = null
//    var okHttpClient : OkHttpClient? = null
//
//    companion object{
//        var instance = FcmPush()
//    }
//
//    init {
//        gson = Gson()
//        okHttpClient = OkHttpClient()
//    }
//
//    fun sendMessage(destinationUid : String, title : String, message : String){
//        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
//            task ->
//            if (task.isSuccessful){
//                var token = task?.result?.get("pushToken").toString()
//
//                var pushDTO = PushDTO()
//                pushDTO.to = token
//                pushDTO.notification.title = title
//                pushDTO.notification.body = message
//
//                var body = RequestBody.create(JSON,gson?.toJson(pushDTO))
//                var request = Request.Builder()
//                    .addHeader("Content-Type","application/json")
//                    .addHeader("Authorization", "key=" + serverKey)
//                    .url(url)
//                    .post(body)
//                    .build()
//
//                okHttpClient?.newCall(request)?.enqueue(object : Callback{
//                    override fun onFailure(call: Call?, e: IOException?) {
//
//                    }
//
//                    override fun onResponse(call: Call?, response: Response?) {
//                        println(response?.body()?.string())
//                        Log.d("error", "FcmPush __ destinationUid.toString() = $destinationUid.toString()")
//                        Log.d("error", "FcmPush __ title.toString() = $title.toString()")
//                        Log.d("error", "FcmPush __ message.toString() = $message.toString()")
//                        Log.d("error", "FcmPush __ serverKey.toString() = $serverKey.toString()")
//                    }
//
//
//                })
//
//            }
//        }
//
//    }
}