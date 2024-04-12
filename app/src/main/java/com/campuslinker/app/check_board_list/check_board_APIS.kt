package com.campuslinker.app.check_board_list

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface check_board_APIS {
    @GET("{BOARD_CASE}")
@Headers("accept: application/json",
    "content-type: application/json"
)
fun getboardlistlInfo(
    @Path("BOARD_CASE", encoded = true) board_case: String,
    @Query("page", encoded = true) page: String,
    @Query("category", encoded = true) category: String,
    @Query("title", encoded = true) title: String,
    @Query("accToken", encoded = true) accToken: String
): Call<Board_List_Model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/board/" // 주소

        fun create(): check_board_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(check_board_APIS::class.java)
        }
    }
}
interface check_board_APIS_match {
    @GET("{BOARD_CASE}")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun getboardlist_matchlInfo(
        @Path("BOARD_CASE", encoded = true) board_case: String,
        @Query("page", encoded = true) page: String,
        @Query("category", encoded = true) category: String,
        @Query("title", encoded = true) title: String,
        @Query("accToken", encoded = true) accToken: String
    ): Call<Match_Board_List_Model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/board/" // 주소

        fun create(): check_board_APIS_match {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(check_board_APIS_match::class.java)
        }
    }
}