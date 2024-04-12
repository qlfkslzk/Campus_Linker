package com.campuslinker.app.FCM

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Post_FCM_Token_APIS {
    @POST("token")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun Post_FCM__info(
        @Body json: FCM_Token_model
    ): Call<FCM_Token_result_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/fcm/" // 주소

        fun create(): Post_FCM_Token_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Post_FCM_Token_APIS::class.java)
        }
    }
}

data class FCM_Token_result_model(
    @SerializedName("result")
    var result : Int,
    @SerializedName("message")
    var message : String
)
data class FCM_Token_model(
    @SerializedName("accToken")
    var accToken:String? = null,
    @SerializedName("deviceToken")
    var deviceToken : String? =null
)
