package com.campuslinker.app.chatting

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface into_chatting_room_APIS {@POST("room")
@Headers("accept: application/json",
    "content-type: application/json")
fun into_chatting_room_info(
    @Body json: into_chatting_room_model
): Call<into_chatting_result_model>
companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
    private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

    fun create(): into_chatting_room_APIS {

        val gson : Gson =   GsonBuilder().setLenient().create();

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
//                .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(into_chatting_room_APIS::class.java)
    }
}
}

data class into_chatting_result_model(
    @SerializedName("result")
    var result : Int,
    @SerializedName("message")
    var message : String,
    @SerializedName("room_num")
    var room_num : String
)
data class into_chatting_room_model(
    @SerializedName("accToken")
    var accToken:String? = null,
    @SerializedName("purpose")
    var purpose : String? =null,
    @SerializedName("board_num")
    var board_num : Int? =null
)
