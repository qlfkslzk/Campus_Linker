package com.campuslinker.app.token

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Get_token_APIS{ //유저정보 반환
    @GET("{ACC_TOKEN}")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun gettokenlInfo(
        @Path("ACC_TOKEN", encoded = true) accToken: String
    ): Call<Token_data>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/user/" // 주소

        fun create(): Get_token_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Get_token_APIS::class.java)
        }
    }
}
interface Re_Put_Token_APIS {
    @PUT("token")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun post_re_token(
        @Body json: re_token_model
    ): Call<re_token_data>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): Re_Put_Token_APIS {

            val gson :Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Re_Put_Token_APIS::class.java)
        }
    }
}
interface Del_Token_APIS {
    @DELETE("{accToken}")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun deltokenlInfo(
        @Path("accToken", encoded = true) accToken: String
    ): Call<del_token_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/token/" // 주소

        fun create(): Del_Token_APIS {

            val gson :Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Del_Token_APIS::class.java)
        }
    }
}