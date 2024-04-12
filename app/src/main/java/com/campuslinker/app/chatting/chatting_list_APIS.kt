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
import retrofit2.http.Query
import java.util.ArrayList

interface chatting_list_APIS {
    @GET("{room_num}/{accToken}")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun get_chatting_list_info(
        @Path("room_num", encoded = true) room_num: String,
        @Path("accToken", encoded = true) accToken: String,
        @Query("page", encoded = true) page: String
    ): Call<chatting_room_read_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/room/" // 주소

        fun create(): chatting_list_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(chatting_list_APIS::class.java)
        }
    }
}
data class chatting_room_read_model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null,
    @SerializedName("list")
    var list  : ArrayList<chatting_room_model>? =null,
    @SerializedName("room_name")
    var room_name : String?=null
)
data class chatting_room_model(
    @SerializedName("num")
    var num : Int?=null,
    @SerializedName("room")
    var room : Int?=null,
    @SerializedName("user_id")
    var user_id : String?=null,
    @SerializedName("chat")
    var chat : String?=null,
    @SerializedName("create_date")
    var create_date : String?=null,
    @SerializedName("format_date")
    var format_date : String?=null
)