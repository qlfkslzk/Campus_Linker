package com.campuslinker.app.alarm

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.ArrayList

interface alarm_list_read_APIS {
    @GET("alarm")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun get_alarm_list_info(
        @Query("accToken", encoded = true) accToken: String
    ): Call<alarm_list_read_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): alarm_list_read_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(alarm_list_read_APIS::class.java)
        }
    }
}
data class alarm_list_read_model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null,
    @SerializedName("school_list")
    var school_list  : ArrayList<alarm_list_model>? =null
)
data class alarm_list_model(
    @SerializedName("purpose")
    var purpose : String?=null,
    @SerializedName("num")
    var num : Int?=null,
    @SerializedName("purpose_num")
    var purpose_num : Int?=null,
    @SerializedName("create_date")
    var create_date : String?=null,
    @SerializedName("user")
    var user : String?=null,
    @SerializedName("content")
    var content : String?=null,
    @SerializedName("format_date")
    var format_date : String?=null
)