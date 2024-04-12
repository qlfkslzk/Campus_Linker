package com.campuslinker.app.comment

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface make_comment_APIS {@POST("comment")
@Headers("accept: application/json",
    "content-type: application/json")
fun comment_post_users(
    @Body json: comment_json_model
): Call<comment_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): make_comment_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(make_comment_APIS::class.java)
        }
    }
}
data class comment_json_model(
    @SerializedName("accToken")
    var accToken : String?=null,
    @SerializedName("ref")
    var ref : Int?=null,
    @SerializedName("ref_comment")
    var ref_comment : Int?=null,
    @SerializedName("comment")
    var comment : String?=null,
    @SerializedName("hidden_name")
    var hidden_name : String?=null
)
data class comment_model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null
)