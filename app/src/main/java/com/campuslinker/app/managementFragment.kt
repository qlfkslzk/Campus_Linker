package com.campuslinker.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.campuslinker.app.User_Cert.Call_Cert_Model
import com.campuslinker.app.User_Cert.Responce_Cert_Model
import com.campuslinker.app.User_Cert.User_Cert_APIS
import com.campuslinker.app.delete_user.Delete_User_APIS_Data
import com.campuslinker.app.delete_user.Delete_result
import com.campuslinker.app.school.Get_school_APIS
import com.campuslinker.app.token.Del_Token_APIS
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.del_token_model
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.location.*
import android.location.LocationRequest
import com.google.android.gms.location.*
import java.util.*
import android.location.Location
import android.util.Log
import com.campuslinker.app.change_user_info.change_name_APIS
import com.campuslinker.app.change_user_info.change_name_model
import com.campuslinker.app.change_user_info.change_pwd_APIS2
import com.campuslinker.app.change_user_info.change_pwd_model2
import com.campuslinker.app.change_user_info.change_result_model
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.provider.TedPermissionProvider
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
val api_token = Get_token_APIS.create()
val del_token = Del_Token_APIS.create()
val del_user = Delete_User_APIS_Data.create()
val cert_user = User_Cert_APIS.create()
val change_name = change_name_APIS.create()
val change_pwd = change_pwd_APIS2.create()
/**
 * A simple [Fragment] subclass.
 * Use the [managementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class managementFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10
    val api_school = Get_school_APIS.create()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationListener: LocationListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var email_address : String?=null
        var view : View=inflater!!.inflate(R.layout.fragment_management, container, false)
        var accessToken = token_management.prefs.getString("access_token","기본값")
        var name1 : TextView = view.findViewById(R.id.myname)
        var email : TextView = view.findViewById(R.id.myemail)
        var school : TextView = view.findViewById(R.id.myschool)
        var cert : TextView = view.findViewById(R.id.mycert)
        var logout : TextView = view.findViewById(R.id.logout)
        var rename : TextView = view.findViewById(R.id.change_name)
        var repwd : TextView = view.findViewById(R.id.change_psw)
        var delete_user_button : TextView = view.findViewById(R.id.delete_user)
        var school_cert : TextView = view.findViewById(R.id.cert)
        var cert_info :Boolean = true
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        var refreshToken = token_management.prefs.getString("refresh_token","기본값")
        val refresh_token_data = re_token_model(
            refreshToken
        )
        Get_token_APIS.create().gettokenlInfo(accessToken)?.enqueue(object :
            Callback<Token_data> {
            override fun onResponse(
                call: Call<Token_data>,
                response: Response<Token_data>
            ) {
                if(response.body()?.result==200){
                    api_token.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                        override fun onResponse(
                            call: Call<Token_data>,
                            response: Response<Token_data>
                        ) {
                            if(response.body()?.result==200){
                                email_address = response.body()?.user?.get(0)?.email.toString()
                                name1.setText("닉네임 : "+response.body()?.user?.get(0)?.name.toString())
                                email.setText("E-mail : "+response.body()?.user?.get(0)?.email.toString())
                                school.setText("학교 : "+response.body()?.user?.get(0)?.univ.toString())
                                if(response.body()?.user?.get(0)?.cert.toString().equals("Y"))
                                {
                                    cert.setText("인증 : 인증되었습니다")
                                    cert_info = false
                                }
                                else{
                                    cert.setText("인증 : 인증 안됨")
                                    cert_info = true
                                }
                            }
                            else{
                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                    var intent = Intent(context, StartActivity::class.java)
                                    startActivity(intent)
                                }
                            }

                        }
                        override fun onFailure(call: Call<Token_data>, t: Throwable) {

                        }

                    })

                    rename.setOnClickListener{

                    }

                    school_cert.setOnClickListener {
                        val data = Call_Cert_Model(
                            "학교인증",
                            accessToken,
                            email_address.toString()
                        )
//            if (checkPermissionForLocation(this)) {
//                startLocationUpdates()
//            }
                        val dialog = CertFragment()
                        dialog.setButtonClickListener(object: CertFragment.OnButtonClickListener{
                            override fun onButton1Clicked() {
                                cert_user.Get_Cert_Info(data)?.enqueue(object : Callback<Responce_Cert_Model> {
                                    override fun onResponse(
                                        call: Call<Responce_Cert_Model>,
                                        response: Response<Responce_Cert_Model>
                                    ) {
                                        Toast.makeText(getActivity(), response.body()?.message, Toast.LENGTH_SHORT).show()
                                    }
                                    override fun onFailure(call: Call<Responce_Cert_Model>, t: Throwable) {

                                    }

                                })
                            }
                            override fun onButton2Clicked() {
                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                    var intent = Intent(context, LocationActivity::class.java)
                                    startActivity(intent)
//                        activity?.supportFragmentManager?.beginTransaction()?.remove(this@managementFragment)?.commit()//프래그먼트 종료 함수
                                }
                            }
                        })
                        dialog.show(requireActivity().supportFragmentManager, "CertFragment")

                    }
                    logout.setOnClickListener {  //로그아웃 구현
                        del_token.deltokenlInfo(accessToken)?.enqueue(object : Callback<del_token_model> {
                            override fun onResponse(
                                call: Call<del_token_model>,
                                response: Response<del_token_model>
                            ) {
                                token_management.prefs.removeString("access_token")
                                token_management.prefs.removeString("refresh_token")
                                deleteFCMToken()
                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                    var intent = Intent(context, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            override fun onFailure(call: Call<del_token_model>, t: Throwable) {

                            }

                        })
                    }
                    delete_user_button.setOnClickListener {
                        val dialog = User_Delete_Fragment()
                        dialog.setButtonClickListener(object: User_Delete_Fragment.OnButtonClickListener{
                            override fun onButton1Clicked(psw: String) {
                                del_user.Delete_Token_Info(accessToken,"$psw")?.enqueue(object : Callback<Delete_result> {
                                    override fun onResponse(
                                        call: Call<Delete_result>,
                                        response: Response<Delete_result>
                                    ) {
                                        token_management.prefs.removeString("access_token")
                                        token_management.prefs.removeString("refresh_token")
                                        deleteFCMToken()
                                        activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                            var intent = Intent(context, LoginActivity::class.java)
                                            startActivity(intent)

                                        }

                                    }
                                    override fun onFailure(call: Call<Delete_result>, t: Throwable) {

                                    }

                                })
                            }
                        })
                        dialog.show(requireActivity().supportFragmentManager, "User_Delete_Fragment")

//
                    }
                    rename.setOnClickListener {
                        val dialog = Change_Name_Fragment()
                        dialog.setButtonClickListener(object: Change_Name_Fragment.OnButtonClickListener{
                            override fun onButton1Clicked(name: String) {
                                var data = change_name_model(
                                    accessToken,
                                    "name",
                                    name
                                )
                                change_name.change_name_model_info(data)?.enqueue(object : Callback<change_result_model> {
                                    override fun onResponse(
                                        call: Call<change_result_model>,
                                        response: Response<change_result_model>
                                    ) {
                                        if(response.body()?.result ==200){
                                            Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                            api_token.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                                                override fun onResponse(
                                                    call: Call<Token_data>,
                                                    response: Response<Token_data>
                                                ) {
                                                    if(response.body()?.result==200){
                                                        token_management.prefs.setString("name", response.body()?.user?.get(0)?.name.toString())
                                                        email_address = response.body()?.user?.get(0)?.email.toString()
                                                        name1.setText("닉네임 : "+response.body()?.user?.get(0)?.name.toString())
                                                        email.setText("E-mail : "+response.body()?.user?.get(0)?.email.toString())
                                                        school.setText("학교 : "+response.body()?.user?.get(0)?.univ.toString())
                                                        if(response.body()?.user?.get(0)?.cert.toString().equals("Y"))
                                                        {
                                                            cert.setText("인증 : 인증되었습니다")
                                                            cert_info = false
                                                        }
                                                        else{
                                                            cert.setText("인증 : 인증 안됨")
                                                            cert_info = true
                                                        }
                                                    }
                                                    else{
                                                        activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                            var intent = Intent(context, StartActivity::class.java)
                                                            startActivity(intent)
                                                        }
                                                    }

                                                }
                                                override fun onFailure(call: Call<Token_data>, t: Throwable) {

                                                }

                                            })
                                        }


                                    }
                                    override fun onFailure(call: Call<change_result_model>, t: Throwable) {

                                    }

                                })
                            }
                        })
                        dialog.show(requireActivity().supportFragmentManager, "User_Delete_Fragment")
                    }
                    repwd.setOnClickListener {
                        val dialog = Change_pwd_Fragment()
                        dialog.setButtonClickListener(object: Change_pwd_Fragment.OnButtonClickListener{
                            override fun onButton1Clicked(pwd: String) {
                                var data = change_pwd_model2(
                                    accessToken,
                                    "pwd",
                                    pwd
                                )
                                change_pwd.change_pwd_model_info2(data)?.enqueue(object : Callback<change_result_model> {
                                    override fun onResponse(
                                        call: Call<change_result_model>,
                                        response: Response<change_result_model>
                                    ) {
                                        Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                    override fun onFailure(call: Call<change_result_model>, t: Throwable) {

                                    }

                                })
                            }
                        })
                        dialog.show(requireActivity().supportFragmentManager, "User_Delete_Fragment")
                    }
                }
                else {
                    Re_Put_Token_APIS.create().post_re_token(refresh_token_data)?.enqueue(object :
                        Callback<re_token_data> {
                        override fun onResponse(
                            call: Call<re_token_data>,
                            refresh_response: Response<re_token_data>
                        ) {
                            if(refresh_response.body()?.result==200){
                                token_management.prefs.setString("access_token", refresh_response.body()?.accessToken.toString())
                                token_management.prefs.setString("refresh_token", refresh_response.body()?.refreshToken.toString())
                                accessToken = token_management.prefs.getString("access_token","기본값")
                                refreshToken = token_management.prefs.getString("refresh_token","기본값")

                                api_token.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                                    override fun onResponse(
                                        call: Call<Token_data>,
                                        response: Response<Token_data>
                                    ) {
                                        if(response.body()?.result==200){
                                            email_address = response.body()?.user?.get(0)?.email.toString()
                                            name1.setText("닉네임 : "+response.body()?.user?.get(0)?.name.toString())
                                            email.setText("E-mail : "+response.body()?.user?.get(0)?.email.toString())
                                            school.setText("학교 : "+response.body()?.user?.get(0)?.univ.toString())
                                            if(response.body()?.user?.get(0)?.cert.toString().equals("Y"))
                                            {
                                                cert.setText("인증 : 인증되었습니다")
                                                cert_info = false
                                            }
                                            else{
                                                cert.setText("인증 : 인증 안됨")
                                                cert_info = true
                                            }
                                        }
                                        else{
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                var intent = Intent(context, StartActivity::class.java)
                                                startActivity(intent)
                                            }
                                        }

                                    }
                                    override fun onFailure(call: Call<Token_data>, t: Throwable) {

                                    }

                                })

                                rename.setOnClickListener{

                                }

                                school_cert.setOnClickListener {
                                    val data = Call_Cert_Model(
                                        "학교인증",
                                        accessToken,
                                        email_address.toString()
                                    )
//            if (checkPermissionForLocation(this)) {
//                startLocationUpdates()
//            }
                                    val dialog = CertFragment()
                                    dialog.setButtonClickListener(object: CertFragment.OnButtonClickListener{
                                        override fun onButton1Clicked() {
                                            cert_user.Get_Cert_Info(data)?.enqueue(object : Callback<Responce_Cert_Model> {
                                                override fun onResponse(
                                                    call: Call<Responce_Cert_Model>,
                                                    response: Response<Responce_Cert_Model>
                                                ) {
                                                    Toast.makeText(getActivity(), response.body()?.message, Toast.LENGTH_SHORT).show()
                                                }
                                                override fun onFailure(call: Call<Responce_Cert_Model>, t: Throwable) {

                                                }

                                            })
                                        }
                                        override fun onButton2Clicked() {
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                var intent = Intent(context, LocationActivity::class.java)
                                                startActivity(intent)
//                        activity?.supportFragmentManager?.beginTransaction()?.remove(this@managementFragment)?.commit()//프래그먼트 종료 함수
                                            }
                                        }
                                    })
                                    dialog.show(requireActivity().supportFragmentManager, "CertFragment")

                                }
                                logout.setOnClickListener {  //로그아웃 구현
                                    del_token.deltokenlInfo(accessToken)?.enqueue(object : Callback<del_token_model> {
                                        override fun onResponse(
                                            call: Call<del_token_model>,
                                            response: Response<del_token_model>
                                        ) {
                                            token_management.prefs.removeString("access_token")
                                            token_management.prefs.removeString("refresh_token")
                                            deleteFCMToken()
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                var intent = Intent(context, LoginActivity::class.java)
                                                startActivity(intent)
                                            }
                                        }
                                        override fun onFailure(call: Call<del_token_model>, t: Throwable) {

                                        }

                                    })
                                }
                                delete_user_button.setOnClickListener {
                                    val dialog = User_Delete_Fragment()
                                    dialog.setButtonClickListener(object: User_Delete_Fragment.OnButtonClickListener{
                                        override fun onButton1Clicked(psw: String) {
                                            del_user.Delete_Token_Info(accessToken,"$psw")?.enqueue(object : Callback<Delete_result> {
                                                override fun onResponse(
                                                    call: Call<Delete_result>,
                                                    response: Response<Delete_result>
                                                ) {
                                                    token_management.prefs.removeString("access_token")
                                                    token_management.prefs.removeString("refresh_token")
                                                    deleteFCMToken()
                                                    activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                        var intent = Intent(context, LoginActivity::class.java)
                                                        startActivity(intent)

                                                    }

                                                }
                                                override fun onFailure(call: Call<Delete_result>, t: Throwable) {

                                                }

                                            })
                                        }
                                    })
                                    dialog.show(requireActivity().supportFragmentManager, "User_Delete_Fragment")

//
                                }
                                rename.setOnClickListener {
                                    val dialog = Change_Name_Fragment()
                                    dialog.setButtonClickListener(object: Change_Name_Fragment.OnButtonClickListener{
                                        override fun onButton1Clicked(name: String) {
                                            var data = change_name_model(
                                                accessToken,
                                                "name",
                                                name
                                            )
                                            change_name.change_name_model_info(data)?.enqueue(object : Callback<change_result_model> {
                                                override fun onResponse(
                                                    call: Call<change_result_model>,
                                                    response: Response<change_result_model>
                                                ) {
                                                    if(response.body()?.result ==200){
                                                        Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                        api_token.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                                                            override fun onResponse(
                                                                call: Call<Token_data>,
                                                                response: Response<Token_data>
                                                            ) {
                                                                if(response.body()?.result==200){
                                                                    token_management.prefs.setString("name", response.body()?.user?.get(0)?.name.toString())
                                                                    email_address = response.body()?.user?.get(0)?.email.toString()
                                                                    name1.setText("닉네임 : "+response.body()?.user?.get(0)?.name.toString())
                                                                    email.setText("E-mail : "+response.body()?.user?.get(0)?.email.toString())
                                                                    school.setText("학교 : "+response.body()?.user?.get(0)?.univ.toString())
                                                                    if(response.body()?.user?.get(0)?.cert.toString().equals("Y"))
                                                                    {
                                                                        cert.setText("인증 : 인증되었습니다")
                                                                        cert_info = false
                                                                    }
                                                                    else{
                                                                        cert.setText("인증 : 인증 안됨")
                                                                        cert_info = true
                                                                    }
                                                                }
                                                                else{
                                                                    activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                                        var intent = Intent(context, StartActivity::class.java)
                                                                        startActivity(intent)
                                                                    }
                                                                }

                                                            }
                                                            override fun onFailure(call: Call<Token_data>, t: Throwable) {

                                                            }

                                                        })
                                                    }


                                                }
                                                override fun onFailure(call: Call<change_result_model>, t: Throwable) {

                                                }

                                            })
                                        }
                                    })
                                    dialog.show(requireActivity().supportFragmentManager, "User_Delete_Fragment")
                                }
                                repwd.setOnClickListener {
                                    val dialog = Change_pwd_Fragment()
                                    dialog.setButtonClickListener(object: Change_pwd_Fragment.OnButtonClickListener{
                                        override fun onButton1Clicked(pwd: String) {
                                            var data = change_pwd_model2(
                                                accessToken,
                                                "pwd",
                                                pwd
                                            )
                                            change_pwd.change_pwd_model_info2(data)?.enqueue(object : Callback<change_result_model> {
                                                override fun onResponse(
                                                    call: Call<change_result_model>,
                                                    response: Response<change_result_model>
                                                ) {
                                                    Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                }
                                                override fun onFailure(call: Call<change_result_model>, t: Throwable) {

                                                }

                                            })
                                        }
                                    })
                                    dialog.show(requireActivity().supportFragmentManager, "User_Delete_Fragment")
                                }

                            }
                            else {
                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                    var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                    Toast.makeText(TedPermissionProvider.context, refresh_response.body()?.massage.toString(),Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                }
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


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////






        return view
    }
    fun deleteFCMToken() {
        Thread {
            try {
                FirebaseMessaging.getInstance().deleteToken()
                Log.d("토큰 삭제 성공 : ", "${FirebaseMessaging.getInstance().token}")
                // 토큰이 성공적으로 삭제되었을 때 원하는 동작 수행
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("토큰 삭제 실패 : ", "${FirebaseMessaging.getInstance().token}")
                // 토큰 삭제 중 오류 발생 시 예외 처리
            }
        }.start()
    }

}