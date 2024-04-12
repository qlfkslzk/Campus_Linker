package com.campuslinker.app.login

//class AuthInterceptor(
//    private val context: Context,
//    private val tokenApi: TokenRefreshApi,
//) : Interceptor,BaseRepository(){
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val response = chain.proceed(request)
//
//        when(response.code){
//            401 ->{
//                // 토큰 재인증
//                return runBlocking {
//                    when (val token = getUpdateToken()) {
//                        is Resource.Success -> {
//                            val sp  =context.getSharedPreferences("login_sp",Context.MODE_PRIVATE)
//                            val editor = sp.edit()
//                            // 기존 토큰
//                            Log.d("test code ists sssss 2",sp.getString("accessToken","no token").toString())
//                            val tokenValue = token.value!!
//                            // new 토큰
//                            Log.d("test code ists sssss 3",tokenValue.accessToken)
//                            val accessToken = tokenValue.accessToken
//                            val refreshToken =tokenValue.refreshToken
//                            editor.putString("accessToken", accessToken)
//                            editor.putString("refreshToken", refreshToken)
//                            editor.apply()
//
//                            //기존 토큰 지우고 새로 response 반환 *** 중요 /////
//                            val newRequest = chain.request().newBuilder().removeHeader("Authorization")
//                            newRequest.addHeader("Authorization","Bearer $accessToken")
//                            Log.d("qazwsxedc response 1",newRequest.toString())
//                            return@runBlocking chain.proceed(newRequest.build())
//
//                        }
//                        else ->{
//                            Log.d("qazwsxedc response 2",response.request.toString())
//
//                            return@runBlocking response
//                        }
//                    }
//                }
//            }
//        }
//        Log.d("qazwsxedc response 3",response.request.toString())
//        // 에러가 아니라면 정상 response 반환
//        return response
//    }
//
//    private suspend fun getUpdateToken() : Resource<Token?> {
//        val refreshToken = context.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
//            .getString("refreshToken","").toString()
//
//        //safeApiCall을 통한 api 요청
//        // refresh token이 비었을 경우에는 null 전송을 통해서 에러 반환을 받음
//        return safeApiCall { tokenApi.patchToken(refreshToken) }
//    }


//}