package com.campuslinker.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.campuslinker.app.databinding.StartBinding
import com.campuslinker.app.token.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class StartActivity : AppCompatActivity() {

    lateinit var binding : StartBinding;
    val api_token = Get_token_APIS.create()
    val re_token_receive = re_token()
    val re_api_token = Re_Put_Token_APIS.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.layoutDisconnected.visibility= View.GONE
        val intent_login = Intent(this, LoginActivity::class.java)
        val intent_navi = Intent(this, NaviActivity::class.java)
        var accessToken = token_management.prefs.getString("access_token","기본값")
        var refreshToken = token_management.prefs.getString("refresh_token","기본값")
        val connection = NetworkConnection(applicationContext)
//        Handler(Looper.getMainLooper()).postDelayed({
//            }, 5000)

        connection.observe(this, Observer { isConnected ->
            if (isConnected)
            {
                if (token_management.prefs.getString("access_token", "기본값") == null)  {
                    startActivity(intent_login)
                }
                else{

                    api_token.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                        override fun onResponse(
                            call: Call<Token_data>,
                            response3: Response<Token_data>
                        ) {
                            if(response3.body()?.result==200){
                                startActivity(intent_navi)
                                finish()
                            }
                            else{
                                var data = re_token_model(
                                    refreshToken
                                )
                                if (token_management.prefs.getString("refresh_token", "기본값") != null){
                                    re_api_token.post_re_token(data).enqueue(object : Callback<re_token_data> {
                                        override fun onResponse(
                                            call: Call<re_token_data>,
                                            response2: Response<re_token_data>
                                        ) {
                                            if (response2.body()?.result == 200) {
                                                token_management.prefs.setString("access_token", response2.body()?.accessToken.toString())
                                                token_management.prefs.setString("refresh_token", response2.body()?.refreshToken.toString())
                                                var accessToken2 = token_management.prefs.getString("access_token","기본값")
                                                var refreshToken2 = token_management.prefs.getString("refresh_token","기본값")
                                                api_token.gettokenlInfo(accessToken2)?.enqueue(object : Callback<Token_data> {
                                                    override fun onResponse(
                                                        call: Call<Token_data>,
                                                        response1: Response<Token_data>
                                                    ) {
                                                        if(response1.body()?.result==200)
                                                        {
                                                            startActivity(intent_navi)
                                                            finish()
                                                        }
                                                        else{
                                                            val data = re_token_model(
                                                                refreshToken2
                                                            )
                                                            re_api_token.post_re_token(data).enqueue(object : Callback<re_token_data> {
                                                                override fun onResponse(
                                                                    call: Call<re_token_data>,
                                                                    response: Response<re_token_data>
                                                                ) {
                                                                    if (response.body()?.result == 200) {
                                                                        token_management.prefs.setString("access_token", response.body()?.accessToken.toString())
                                                                        token_management.prefs.setString("refresh_token", response.body()?.refreshToken.toString())
                                                                        startActivity(intent_navi)
                                                                        finish()
                                                                    }
                                                                    else{
                                                                        var dialog = AlertDialog.Builder(this@StartActivity)
                                                                        dialog.setTitle("실패")
                                                                        dialog.setMessage("로그인이 만료되었습니다")
                                                                        dialog.show()
                                                                        token_management.prefs.removeString("access_token")
                                                                        token_management.prefs.removeString("refresh_token")
                                                                        Handler(Looper.getMainLooper()).postDelayed({
                                                                            val intent = Intent(this@StartActivity, LoginActivity::class.java)
                                                                            startActivity(intent_login)
                                                                            finish()
                                                                        }, 1000)

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
                                            else{
                                                token_management.prefs.removeString("access_token")
                                                token_management.prefs.removeString("refresh_token")
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    val intent = Intent(this@StartActivity, LoginActivity::class.java)
                                                    startActivity(intent_login)
                                                    finish()
                                                }, 2000)

                                            }

                                        }

                                        override fun onFailure(call: Call<re_token_data>, t: Throwable) {
                                        }
                                    })
                                    accessToken = token_management.prefs.getString("access_token","기본값")
                                    refreshToken = token_management.prefs.getString("refresh_token","기본값")
                                }
                                else{
                                    val intent = Intent(this@StartActivity, LoginActivity::class.java)
                                    startActivity(intent_login)
                                }


//                                if(re_token_receive.result==200){
//                                    startActivity(intent_navi)
//                                }
//                                else{
//                                    var dialog = AlertDialog.Builder(this@StartActivity)
//                                    dialog.setTitle("실패")
//                                    dialog.setMessage("로그인이 만료되었습니다")
//                                    dialog.show()
//                                    token_management.prefs.removeString("access_token")
//                                    token_management.prefs.removeString("refresh_token")
//                                    startActivity(intent_login)
//
////
//                                }

                            }

                        }

                        override fun onFailure(call: Call<Token_data>, t: Throwable) {
                        }

                    })
                }
            }
            else
            {
                binding.layoutDisconnected.visibility= View.VISIBLE
            }

        })
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("testt", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("testt", token)
        })
//        val channelId = "Campus_Linker_Alarm_Id"
//        val channelName = "Campus_Linker_Alarm"
//        val channelDescription = "TCampus_Linker_Alarm"
//        val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
//
//// 알림 채널을 생성합니다.
//        createNotificationChannel(context, channelId, channelName, channelDescription, channelImportance)
    }
    private fun setPermission() {
        // 권한 묻는 팝업 만들기
        val permissionListener = object : PermissionListener {
            // 설정해놓은 권한을 허용됐을 때
            override fun onPermissionGranted() {
                // 1초간 스플래시 보여주기
                val backgroundExecutable : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
                val mainExecutor : Executor = ContextCompat.getMainExecutor(context)
            }

            // 설정해놓은 권한을 거부됐을 때
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                // 권한 없어서 요청
                AlertDialog.Builder(context)
                    .setMessage("권한 거절로 인해 일부 기능이 제한됩니다.")
                    .setPositiveButton("권한 설정하러 가기") { dialog, which ->
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:com.example.myweathertest2"))
                            startActivity(intent)
                        } catch (e : ActivityNotFoundException) {
                            e.printStackTrace()
                            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                            startActivity(intent)
                        }
                    }
                    .show()

                Toast.makeText(context, "권한 거부", Toast.LENGTH_SHORT).show()
            }

        }

        // 권한 설정
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setRationaleMessage("정확한 위치 정보와 알림을 위해 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다. [앱 설정]->[권한] 항목에서 허용해주세요.")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS) // 필수 권한만 문기
            .check()
    }
    fun createNotificationChannel(context: Context, channelId: String, channelName: String, channelDescription: String, channelImportance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
                description = channelDescription
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}