package com.campuslinker.app.chatting

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT

interface chatting_clear_APIS {@PUT("room")
@Headers("accept: application/json",
    "content-type: application/json")
fun chatting_clear_info(
    @Body json: chatting_clear_model
): Call<clear_chatting_result_model>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): chatting_clear_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(chatting_clear_APIS::class.java)
        }
    }
}

data class clear_chatting_result_model(
    @SerializedName("result")
    var result : Int,
    @SerializedName("message")
    var message : String
)
data class chatting_clear_model(
    @SerializedName("room_num")
    var room_num : Int? =null,
    @SerializedName("accToken")
    var accToken:String? = null

)