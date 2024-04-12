package com.campuslinker.app.rewrite_board

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Delete_board_APIS {
    @DELETE("{board_case}/{board_num}")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun Delete_Board_Info(
        @Path("board_case", encoded = true) board_case: String,
        @Path("board_num", encoded = true) board_num: String,
        @Query("accToken", encoded = true) accToken: String
    ): Call<Delete_result>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/board/" // 주소

        fun create(): Delete_board_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Delete_board_APIS::class.java)
        }
    }
}

data class Delete_result(
    @SerializedName("result")
    var result : Int? =null,
    @SerializedName("message")
    var message : String? =null
)