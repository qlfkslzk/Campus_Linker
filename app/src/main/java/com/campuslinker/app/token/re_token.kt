package com.campuslinker.app.token

import com.campuslinker.app.token_management
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class re_token { //토큰 재발급 검증 클래스
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var refreshToken = token_management.prefs.getString("refresh_token","기본값")
    val data = re_token_model(
        refreshToken
    )
    val api_token = Get_token_APIS.create()
    val re_api_token = Re_Put_Token_APIS.create()
    var result :Int?=null

    fun access_login(){
        api_token.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
            override fun onResponse(
                call: Call<Token_data>,
                response1: Response<Token_data>
            ) {
                if(response1.body()?.result!=200)
                {
                    re_api_token.post_re_token(data).enqueue(object : Callback<re_token_data> {
                        override fun onResponse(
                            call: Call<re_token_data>,
                            response2: Response<re_token_data>
                        ) {
                            if (response2.body()?.result == 200) {
                                token_management.prefs.setString("access_token", response2.body()?.accessToken.toString())
                                token_management.prefs.setString("refresh_token", response2.body()?.refreshToken.toString())
                                result = response2.body()?.result
                            }
                            else{
                                result = response2.body()?.result
                            }

                        }

                        override fun onFailure(call: Call<re_token_data>, t: Throwable) {
                        }
                    })
                }

            }

            override fun onFailure(call: Call<Token_data>, t: Throwable) {
            }
        })
    }
}

data class re_token_model(
    @SerializedName("refToken")
    var refToken : String? =null,
    @SerializedName("message")
    var message : String? =null
)
data class del_token_model(
    @SerializedName("result")
    var result : String? =null,
    @SerializedName("message")
    var message : String? =null
)

data class re_token_data(
    @SerializedName("result")
    var result : Int? =null,
    @SerializedName("massage")
    var massage : String? =null,
    @SerializedName("accessToken")
    var accessToken: String? =null,
    @SerializedName("refreshToken")
    var refreshToken: String? =null
        )
