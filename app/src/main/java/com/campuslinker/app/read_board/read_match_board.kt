package com.campuslinker.app.read_board

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
import java.util.*

interface read_match_board {
    @GET("{board_num}")
    @Headers("accept: application/json",
    "content-type: application/json"
)
fun get_read_match_board_Info(
    @Path("board_num", encoded = true) boardnum: String,
    @Query("accToken", encoded = true) accToken: String
): Call<read_match_board_response_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/board/match/" // 주소

        fun create(): read_match_board {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(read_match_board::class.java)
        }
    }
}
data class read_match_board_response_model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null,
    @SerializedName("board")
    var board : ArrayList<match_board>? =null
)
data class match_board(
    @SerializedName("reaction_count")
    var reaction_count : Int?=null,
    @SerializedName("gender")
    var gender : String?=null,
    @SerializedName("num")
    var num : Int?=null,
    @SerializedName("student_id")
    var student_id : String?=null,
    @SerializedName("title")
    var title : String?=null,
    @SerializedName("hidden_name")
    var hidden_name : String?=null,
    @SerializedName("room_num")
    var room_num : Int?=null,
    @SerializedName("school")
    var school : String?=null,
    @SerializedName("contents")
    var contents : String?=null,
    @SerializedName("delete_date")
    var delete_date : String?=null,
    @SerializedName("format_create_date")
    var format_create_date : String?=null,
    @SerializedName("format_delete_date")
    var format_delete_date : String?=null,
    @SerializedName("location")
    var location : String?=null,
    @SerializedName("comment")
    var comment : Int?=null,
    @SerializedName("create_date")
    var create_date : String?=null,
    @SerializedName("format_date")
    var format_date : String?=null,
    @SerializedName("category")
    var category : String?=null,
    @SerializedName("write_user")
    var write_user : String?=null,
)