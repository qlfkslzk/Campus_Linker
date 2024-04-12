package com.campuslinker.app.rewrite_board

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT

interface Rewrite_borad_APIS {
    @PUT("board")
    @Headers("accept: application/json",
        "content-type: application/json")
    fun rewrite_free_board_info(
        @Body json: rewrite_free_board_model
    ): Call<rewrite_free_board_result_model>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): Rewrite_borad_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Rewrite_borad_APIS::class.java)
        }
    }

}

data class rewrite_free_board_result_model(
    @SerializedName("result")
    var result : Int,
    @SerializedName("message")
    var message : String
)
data class rewrite_free_board_model(
    @SerializedName("board_num")
    var board_num : Int? =null,
    @SerializedName("accToken")
    var accToken:String? = null,
    @SerializedName("title")
    var title:String? = null,
    @SerializedName("category")
    var category:String? = null,
    @SerializedName("contents")
    var contents:String? = null,
    @SerializedName("hidden_name")
    var hidden_name:String? = null,
    @SerializedName("board_case")
    var board_case:String? = null

)