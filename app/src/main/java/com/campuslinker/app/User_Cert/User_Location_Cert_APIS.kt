package com.campuslinker.app.User_Cert
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface User_Location_Cert_APIS {
    @GET("cert")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun Get_Cert_Info(
        @Query("accToken", encoded = true) accToken: String,
        @Query("location", encoded = true) location: String
    ): Call<Responce_Location_Cert_Model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): User_Location_Cert_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(User_Location_Cert_APIS::class.java)
        }
    }
}

data class Responce_Location_Cert_Model(
    var result : Int,
    var message : String
)