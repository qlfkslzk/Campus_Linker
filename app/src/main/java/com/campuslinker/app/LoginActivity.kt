package com.campuslinker.app

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.campuslinker.app.databinding.LoginBinding
import com.campuslinker.app.login.*
import android.content.ActivityNotFoundException
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import com.campuslinker.app.User_Cert.Responce_Cert_Model
import com.campuslinker.app.User_Cert.User_Cert_APIS2
import com.campuslinker.app.User_Cert.find_pwd_model
import com.campuslinker.app.change_user_info.change_pwd_APIS
import com.campuslinker.app.change_user_info.change_pwd_model
import com.campuslinker.app.change_user_info.change_result_model
import com.campuslinker.app.dialog.find_id
import com.campuslinker.app.dialog.find_pwd
import com.campuslinker.app.dialog.find_user_info
import com.campuslinker.app.find.get_id_APIS
import com.campuslinker.app.find.get_id_model
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


class LoginActivity : AppCompatActivity() {
    lateinit var binding : LoginBinding;
    val api = Login_APIS.create();
    val find_id_APIS = get_id_APIS.create()
    val pwd_token_APIS = User_Cert_APIS2.create()
    val pwd_change_APIS = change_pwd_APIS.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        val view = binding.root
        val intent_navi = Intent(this, NaviActivity::class.java)
        setContentView(view)
        val connection = NetworkConnection(applicationContext)
        val edit_name: EditText =findViewById(R.id.edit_name) // 입력된 아이디
        val edit_pwd: EditText =findViewById(R.id.password)  // 입력된 패스워드
        var login_btn: Button = findViewById(R.id.btn_login)  //로그인 버튼
//        var retrofit = Retrofit.Builder().// 레트로핏 생성
//        baseUrl("https://sirobako.co.kr/campus-linker/token/"). //사용할 서버 url
//        addConverterFactory(GsonConverterFactory.create()).
//        build()
//
        setPermission()
//
//        var loginservice = retrofit.create(LoginPost::class.java) //만들어둔 loginpost에 레스토핏 적용
        val resist : TextView = findViewById(R.id.resister)
        connection.observe(this, Observer { isConnected ->
            if (isConnected) {
                resist.setOnClickListener {
                    val intent = Intent(this, ResisterActivity::class.java)
                    startActivity(intent)
                }

//        login_btn.setOnClickListener{ //로그인 버튼 눌렀을 시 이벤트 처리
//            var textid = edit_name.text.toString()
//            var textpwd = edit_pwd.text.toString()
//
//            loginservice.requestLogin(textid,textpwd).enqueue(object : Callback<Login> {
//                override fun onResponse(call: Call<Login>, response: Response<Login>) { //웹통신 성공시 코드
//
//                    var login = response.body()
//                    var dialog = AlertDialog.Builder(this@LoginActivity)
//                    dialog.setTitle("성공")
//                    dialog.setMessage("code = "+login?.result +"msg = "+login?.massage
//                            + "access_token = " + login?.accessToken+ "refresh_token = " + login?.refreshToken)
//                    dialog.show()
//
//
//                }
//
//                override fun onFailure(call: Call<Login>, t: Throwable) { //웹통신 실패시 코드
//                    var dialog = AlertDialog.Builder(this@LoginActivity)
//                    dialog.setTitle("실패")
//                    dialog.setMessage("id = "+textid+"pwd = "+textpwd)
//                    dialog.show()
//                }
//            }) //만들어둔 로그인 아웃풋 처리 호출
//
//
//
//        }
                binding.btnLogin.setOnClickListener {
                    val data = PostModel(
                        binding.editName.text.toString(),
                        binding.password.text.toString()
                    )


                    api.post_users(data).enqueue(object : Callback<PostResult> {
                        override fun onResponse(
                            call: Call<PostResult>,
                            response: Response<PostResult>
                        ) {
                            if(response.body()?.result==200) {
                                token_management.prefs.setString("access_token", response.body()?.accessToken.toString())
                                token_management.prefs.setString("refresh_token", response.body()?.refreshToken.toString())
                                Log.d("before fcm token : ","${FirebaseMessaging.getInstance().token}")
                                refreshFCMToken()
                                Log.d("new fcm token : ","${FirebaseMessaging.getInstance().token}")
                                startActivity(intent_navi)
                                finish()
                            }
                            else{
                                var dialog = AlertDialog.Builder(this@LoginActivity)
                                dialog.setTitle("실패")
                                dialog.setMessage("정확한 아이디와 패스워드를 입력해주세요")
                                dialog.show()
                                binding.password.setText("")
                            }
                        }

                        override fun onFailure(call: Call<PostResult>, t: Throwable) {

                        }
                    })


                }
                binding.password.setOnKeyListener{ view, keyCode, event ->
                    // Enter Key Action
                    if (event.action == KeyEvent.ACTION_DOWN
                            && keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        val data = PostModel(
                                binding.editName.text.toString(),
                                binding.password.text.toString()
                        )
                        api.post_users(data).enqueue(object : Callback<PostResult> {
                            override fun onResponse(
                                    call: Call<PostResult>,
                                    response: Response<PostResult>
                            ) {
                                if(response.body()?.result==200) {
                                    token_management.prefs.setString("access_token", response.body()?.accessToken.toString())
                                    token_management.prefs.setString("refresh_token", response.body()?.refreshToken.toString())
                                    Log.d("before fcm token : ","${FirebaseMessaging.getInstance().token}")
                                    refreshFCMToken()
                                    Log.d("new fcm token : ","${FirebaseMessaging.getInstance().token}")
                                    startActivity(intent_navi)
                                    finish()
                                }
                                else{
                                    var dialog = AlertDialog.Builder(this@LoginActivity)
                                    dialog.setTitle("실패")
                                    dialog.setMessage("정확한 아이디와 패스워드를 입력해주세요")
                                    dialog.show()
                                    binding.password.setText("")
                                }
                            }

                            override fun onFailure(call: Call<PostResult>, t: Throwable) {

                            }
                        })

                        true
                    }

                    false
                }

            }
            else{
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        binding.textView3.setOnClickListener {
            val dialog_user_info = find_user_info(this@LoginActivity)
            dialog_user_info.showDialog_user_info()
            dialog_user_info.setOnClickListener(object : find_user_info.OnDialogClickListener {
                override fun onClicked_id()
                {
                    val dialog_id = find_id(this@LoginActivity)
                    dialog_id.showDialog_id()
                    dialog_id.setOnClickListener(object : find_id.OnDialogClickListener {
                        override fun onClicked(email: String)
                        {
                            find_id_APIS.get_email_info(email).enqueue(object : Callback<get_id_model> {
                                override fun onResponse(
                                    call: Call<get_id_model>,
                                    response: Response<get_id_model>
                                ) {
                                    Toast.makeText(this@LoginActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailure(call: Call<get_id_model>, t: Throwable) {

                                }
                            })
                        }

                    })
                }
                override fun onClicked_pwd()
                {
                    val dialog_pwd = find_pwd(this@LoginActivity)
                    dialog_pwd.showDialog_pwd()
                    dialog_pwd.setOnClickListener(object : find_pwd.OnDialogClickListener {
                        override fun onClicked1(id: String, email: String) {
                            val token_model = find_pwd_model(
                                "비밀번호",
                                id,
                                email
                            )
                            pwd_token_APIS.find_pwd_info(token_model)?.enqueue(object : Callback<Responce_Cert_Model> {
                                override fun onResponse(
                                    call: Call<Responce_Cert_Model>,
                                    response: Response<Responce_Cert_Model>
                                ) {
                                    Toast.makeText(this@LoginActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailure(call: Call<Responce_Cert_Model>, t: Throwable) {

                                }
                            })
//                            Handler(Looper.getMainLooper()).postDelayed({
//
//                            //실행할 코드
//                        }, 2000)

                        }

                        override fun onClicked2(id: String, token: String, pwd1: String, pwd2: String) {

                            if(pwd1.equals(pwd2)){
                                val pwd_model = change_pwd_model(
                                    "reset",
                                    token,
                                    id,
                                    pwd1
                                )
                                pwd_change_APIS.change_pwd_model_info(pwd_model)?.enqueue(object : Callback<change_result_model> {
                                    override fun onResponse(
                                        call: Call<change_result_model>,
                                        response: Response<change_result_model>
                                    ) {
                                        Toast.makeText(this@LoginActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onFailure(call: Call<change_result_model>, t: Throwable) {

                                    }
                                })
                            }
                            else{
                                Toast.makeText(this@LoginActivity, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                            }
                        }


                    })
                }

            })
        }
    }
    private var backPressedTime: Long = 0
    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >= 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.finishAffinity(this)
            System.exit(0)
        }
    }
    private fun setPermission() {
        // 권한 묻는 팝업 만들기
        val permissionListener = object : PermissionListener {
            // 설정해놓은 권한을 허용됐을 때
            override fun onPermissionGranted() {
                // 1초간 스플래시 보여주기
                val backgroundExecutable : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
                val mainExecutor : Executor = ContextCompat.getMainExecutor(TedPermissionProvider.context)
            }

            // 설정해놓은 권한을 거부됐을 때
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                // 권한 없어서 요청
                AlertDialog.Builder(TedPermissionProvider.context)
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

                Toast.makeText(TedPermissionProvider.context, "권한 거부", Toast.LENGTH_SHORT).show()
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
    fun refreshFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 새로운 토큰을 얻음
                    val newToken: String? = task.result

                    // 새 토큰 처리 (예: 서버에서 업데이트)
                    if (newToken != null) {
                        Log.d("new fcm token : ","$newToken")
                    }
                } else {
                    // 오류 처리
                    Log.d("false change token : ","${task.exception}")
                }
            }
    }
}