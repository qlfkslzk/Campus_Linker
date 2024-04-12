package com.campuslinker.app.change_user_info

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT

interface change_name_APIS {@PUT("user")
@Headers("accept: application/json",
    "content-type: application/json")
fun change_name_model_info(
    @Body json: change_name_model
): Call<change_result_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): change_name_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(change_name_APIS::class.java)
        }
    }
}
interface change_email_APIS {@PUT("user")
@Headers("accept: application/json",
    "content-type: application/json")
fun change_email_model_info(
    @Body json: change_email_model
): Call<change_result_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): change_email_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(change_email_APIS::class.java)
        }
    }
}
interface change_pwd_APIS {@PUT("user")
@Headers("accept: application/json",
    "content-type: application/json")
fun change_pwd_model_info(
    @Body json: change_pwd_model
): Call<change_result_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): change_pwd_APIS {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(change_pwd_APIS::class.java)
        }
    }
}
interface change_pwd_APIS2 {@PUT("user")
@Headers("accept: application/json",
    "content-type: application/json")
fun change_pwd_model_info2(
    @Body json: change_pwd_model2
): Call<change_result_model>
    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://sirobako.co.kr/campus-linker/" // 주소

        fun create(): change_pwd_APIS2 {

            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(change_pwd_APIS2::class.java)
        }
    }
}
data class change_name_model(
    @SerializedName("accToken")
    var accToken : String? =null,
    @SerializedName("purpose")
    var purpose : String? =null,
    @SerializedName("name")
    var name : String? =null
)
data class change_email_model(
    @SerializedName("accToken")
    var accToken : String? =null,
    @SerializedName("purpose")
    var purpose : String? =null,
    @SerializedName("email")
    var email : String? =null
)
data class change_pwd_model2(
    @SerializedName("accToken")
    var accToken : String? =null,
    @SerializedName("purpose")
    var purpose : String? =null,
    @SerializedName("pwd")
    var pwd : String? =null
)
data class change_pwd_model(
    @SerializedName("purpose")
    var purpose : String? =null,
    @SerializedName("token")
    var token : String? =null,
    @SerializedName("id")
    var id : String? =null,
    @SerializedName("pwd")
    var pwd : String? =null
)
data class change_result_model(
    @SerializedName("result")
    var result : Int? =null,
    @SerializedName("message")
    var message : String? =null
)