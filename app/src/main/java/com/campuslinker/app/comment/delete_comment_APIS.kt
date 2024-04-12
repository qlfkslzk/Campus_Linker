package com.campuslinker.app.comment

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Headers
import retrofit2.http.Path

interface delete_comment_APIS {
    @DELETE("{comment_num}/{accToken}")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun Delete_Token_Info(
        @Path("comment_num", encoded = true) comment_num: String,
        @Path("accToken", encoded = true) accToken: String
    ): Call<Delete_comment_result>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/comment/" // 주소

        fun create(): delete_comment_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(delete_comment_APIS::class.java)
        }
    }
}

data class Delete_comment_result(
    @SerializedName("result")
    var result : String? =null,
    @SerializedName("message")
    var message : String? =null
)