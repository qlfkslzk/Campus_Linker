package com.campuslinker.app.reaction

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface reaction_APIS {@POST("reaction")
@Headers("accept: application/json",
    "content-type: application/json")
fun reaction_post_users(
    @Body json: reaction_json_model
): Call<reaction_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/board/" // 주소

        fun create(): reaction_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(reaction_APIS::class.java)
        }
    }
}
data class reaction_json_model(
    @SerializedName("accToken")
    var accToken : String?=null,
    @SerializedName("board_num")
    var board_num : Int?=null
)
data class reaction_model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null
        )