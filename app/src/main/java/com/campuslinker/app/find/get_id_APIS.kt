package com.campuslinker.app.find

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface get_id_APIS {
    @GET("user")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun get_email_info(
        @Query("email", encoded = true) email: String
    ): Call<get_id_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): get_id_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(get_id_APIS::class.java)
        }
    }
}
data class get_id_model(

    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null
)