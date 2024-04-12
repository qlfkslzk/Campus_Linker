package com.campuslinker.app.comment

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

interface resd_comment_APIS {@GET("{board_num}/{accToken}")
@Headers("accept: application/json",
        "content-type: application/json")
fun get_comment_read(
        @Path("board_num", encoded = true) board_num: String,
        @Path("accToken", encoded = true) accToken: String,
): Call<comment_read_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/comment/" // 주소

        fun create(): resd_comment_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
//                .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(resd_comment_APIS::class.java)
        }
    }
}
data class comment_read_model(
        @SerializedName("result")
        var result : Int?=null,
        @SerializedName("message")
        var message : String?=null,
        @SerializedName("list")
        var list  : ArrayList<comment_read_list_model>? =null,
        @SerializedName("revers_list")
        var revers_list  : ArrayList<comment_read_list_model>? =null
)
data class comment_read_list_model(
        @SerializedName("hidden_name")
        var hidden_name : String?=null,
        @SerializedName("ref")
        var ref : Int?=null,
        @SerializedName("user_id")
        var user_id : String?=null,
        @SerializedName("num")
        var num : Int?=null,
        @SerializedName("ref_comment")
        var ref_comment : Int?=null,
        @SerializedName("comment")
        var comment : String?=null,
        @SerializedName("create_date")
        var create_date : String?=null,
        @SerializedName("format_date")
        var format_date : String?=null
)
