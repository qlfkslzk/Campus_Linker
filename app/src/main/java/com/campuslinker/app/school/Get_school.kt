package com.campuslinker.app.school

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface Get_school_APIS{
    @GET("{SCHOOL_NAME}")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun getSchoolInfo(
        @Path("SCHOOL_NAME", encoded = true) schoolname: String
    ): Call<school_data>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/school/" // 주소

        fun create(): Get_school_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Get_school_APIS::class.java)
        }
    }
}