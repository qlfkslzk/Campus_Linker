package com.campuslinker.app.delete_user

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Delete_User_APIS_Data {
    @DELETE("{ACC_TOKEN}")
@Headers("accept: application/json",
    "content-type: application/json"
)
fun Delete_Token_Info(
    @Path("ACC_TOKEN", encoded = true) accToken: String,
    @Query("pwd", encoded = true) psw: String
): Call<Delete_result>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/user/" // 주소

        fun create(): Delete_User_APIS_Data {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Delete_User_APIS_Data::class.java)
        }
    }
}

data class Delete_result(
    @SerializedName("result")
    var result : String? =null,
    @SerializedName("message")
    var message : String? =null
        )