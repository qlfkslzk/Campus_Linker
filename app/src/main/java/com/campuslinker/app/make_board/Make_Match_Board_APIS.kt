package com.campuslinker.app.make_board

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Make_Match_Board_APIS {
    @POST("board")
@Headers("accept: application/json",
    "content-type: application/json")
fun Make_Match_Board_Body_Model(
    @Body json: Make_Match_Board_Body_Model
): Call<Make_Match_Board_Response_Model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): Make_Match_Board_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(Make_Match_Board_APIS::class.java)
        }
    }
}

data class Make_Match_Board_Body_Model(
    @SerializedName("accToken")
    var accToken : String?=null,
    @SerializedName("title")
    var title : String?=null,
    @SerializedName("category")
    var category : String?=null,
    @SerializedName("contents")
    var contents : String?=null,
    @SerializedName("hidden_name")
    var hidden_name : String?=null,
    @SerializedName("location")
    var location : String?=null,
    @SerializedName("gender")
    var gender : String?=null,
    @SerializedName("student_id")
    var student_id : String?=null,
    @SerializedName("delete_date")
    var delete_date : String?=null,
    @SerializedName("max_user")
    var max_user : Int?=null,
    @SerializedName("board_case")
    var board_case : String?=null
)
data class Make_Match_Board_Response_Model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null
)