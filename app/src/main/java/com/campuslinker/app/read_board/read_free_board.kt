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

interface read_free_board {
    @GET("{board_num}")
    @Headers("accept: application/json",
    "content-type: application/json"
)
fun get_read_free_board_Info(
    @Path("board_num", encoded = true) boardnum: String,
    @Query("accToken", encoded = true) accToken: String
): Call<read_free_board_response_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/board/free/" // 주소

        fun create(): read_free_board {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(read_free_board::class.java)
        }
    }
}
data class read_free_board_response_model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null,
    @SerializedName("board")
    var board : ArrayList<board>? =null
)
data class board(
    @SerializedName("hidden_name")
    var hidden_name : String?=null,
    @SerializedName("reaction_count")
    var reaction_count : Int?=null,
    @SerializedName("board_type")
    var board_type : String?=null,
    @SerializedName("school")
    var school : String?=null,
    @SerializedName("contents")
    var contents : String?=null,
    @SerializedName("num")
    var num : Int?=null,
    @SerializedName("comment")
    var comment : Int?=null,
    @SerializedName("create_date")
    var create_date : String?=null,
    @SerializedName("format_date")
    var format_date : String?=null,
    @SerializedName("title")
    var title : String?=null,
    @SerializedName("category")
    var category : String?=null,
    @SerializedName("write_user")
    var write_user : String?=null
)