package com.campuslinker.app.chatting

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import java.util.ArrayList

interface chatting_user_APIS {
    @GET("{roomNum}/{accToken}")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun get_chatting_room_list_info(
        @Path("roomNum", encoded = true) roomNum: String,
        @Path("accToken", encoded = true) accToken: String
    ): Call<chatting_user_read_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/roominfo/" // 주소

        fun create(): chatting_user_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(chatting_user_APIS::class.java)
        }
    }
}
data class chatting_user_read_model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null,
    @SerializedName("roomInfo")
    var roomInfo  : ArrayList<chatting_user_list_model>? =null
)
data class chatting_user_list_model(
    @SerializedName("userCount")
    var userCount : Int?=null,
    @SerializedName("maxUser")
    var maxUser : String?=null
)