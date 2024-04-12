package com.campuslinker.app.resister

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Resister_APIS {
    @POST("user")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun post_users(
        @Body json: Resister_Post
    ): Call<Resister_Post_result>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): Resister_APIS {

            val gson :Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Resister_APIS::class.java)
        }
    }
}