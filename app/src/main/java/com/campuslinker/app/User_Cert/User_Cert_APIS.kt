package com.campuslinker.app.User_Cert

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface User_Cert_APIS {
    @POST("cert")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun Get_Cert_Info(
        @Body json: Call_Cert_Model
    ): Call<Responce_Cert_Model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): User_Cert_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(User_Cert_APIS::class.java)
        }
    }
}
interface User_Cert_APIS2 {
    @POST("cert")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun find_pwd_info(
        @Body json: find_pwd_model
    ): Call<Responce_Cert_Model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): User_Cert_APIS2 {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(User_Cert_APIS2::class.java)
        }
    }
}
data class Call_Cert_Model(
    var purpose : String,
    var accToken : String,
    var email : String
)

data class Responce_Cert_Model(
    var result : Int,
    var message : String
)
data class find_pwd_model(
    var purpose : String,
    var id : String,
    var email : String
)