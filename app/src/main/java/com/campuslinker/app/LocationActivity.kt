package com.campuslinker.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.campuslinker.app.databinding.LocationBinding
import android.content.ActivityNotFoundException
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.campuslinker.app.User_Cert.Responce_Location_Cert_Model
import com.campuslinker.app.User_Cert.User_Location_Cert_APIS
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.google.android.gms.location.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class LocationActivity : AppCompatActivity() {
    private lateinit var binding : LocationBinding
    private var lat : String?=null
    private var lon : String?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationListener: LocationListener? = null
    var accessToken = token_management.prefs.getString("access_token","기본값")
    val location_APIS = User_Location_Cert_APIS.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val view = binding.root
        setContentView(view)

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
                    setPermission()
                    var accessToken = token_management.prefs.getString("access_token","기본값")
                    if (ActivityCompat.checkSelfPermission(
                            this@LocationActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@LocationActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@LocationActivity)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                        if (location != null) {

                            lat = location.latitude.toString()
                            lon = location.longitude.toString()
                            val latitude = location.latitude
                            val longitude = location.longitude
                            Log.d("Test", "GPS Location Latitude: $latitude" +
                                    ", Longitude: $longitude")

                        }
                    }
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            if (locationResult == null) {
                                return
                            }
                            for (location in locationResult.locations) {
                                if (location != null) {
                                    lat = location.latitude.toString()
                                    lon = location.longitude.toString()
                                    val latitude = location.latitude
                                    val longitude = location.longitude
                                    Log.d("Test", "GPS Location changed, Latitude: $latitude" +
                                            ", Longitude: $longitude")
                                }
                            }
                        }
                    }
                    val locationRequest = LocationRequest.create()
                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    locationRequest.interval = 20 * 1000
                    fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback,
                        Looper.getMainLooper());
//        fusedLocationClient.removeLocationUpdates(locationCallback);
                    Handler(Looper.getMainLooper()).postDelayed({
                        location_APIS.Get_Cert_Info(accessToken,"$lat,$lon")?.enqueue(object :
                            Callback<Responce_Location_Cert_Model> {
                            override fun onResponse(
                                call: Call<Responce_Location_Cert_Model>,
                                response: Response<Responce_Location_Cert_Model>
                            ) {
                                if(response.body()?.result==200){
                                    binding.textView19.setText(response.body()?.message)
                                    fusedLocationClient.removeLocationUpdates(locationCallback);
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        finish()
                                    },2000)
                                }
                                else{
                                    binding.textView19.setText(response.body()?.message)
                                    fusedLocationClient.removeLocationUpdates(locationCallback);
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        finish()
                                    }, 2000)
                                }
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                            }
                            override fun onFailure(call: Call<Responce_Location_Cert_Model>, t: Throwable) {

                            }

                        })

//            Toast.makeText(this, "$lat, $lon.", Toast.LENGTH_SHORT).show()
                    }, 1000)
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

                                setPermission()
                                var accessToken = token_management.prefs.getString("access_token","기본값")
                                if (ActivityCompat.checkSelfPermission(
                                        this@LocationActivity,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                        this@LocationActivity,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return
                                }
                                val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@LocationActivity)
                                fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                                    if (location != null) {

                                        lat = location.latitude.toString()
                                        lon = location.longitude.toString()
                                        val latitude = location.latitude
                                        val longitude = location.longitude
                                        Log.d("Test", "GPS Location Latitude: $latitude" +
                                                ", Longitude: $longitude")

                                    }
                                }
                                val locationCallback = object : LocationCallback() {
                                    override fun onLocationResult(locationResult: LocationResult) {
                                        if (locationResult == null) {
                                            return
                                        }
                                        for (location in locationResult.locations) {
                                            if (location != null) {
                                                lat = location.latitude.toString()
                                                lon = location.longitude.toString()
                                                val latitude = location.latitude
                                                val longitude = location.longitude
                                                Log.d("Test", "GPS Location changed, Latitude: $latitude" +
                                                        ", Longitude: $longitude")
                                            }
                                        }
                                    }
                                }
                                val locationRequest = LocationRequest.create()
                                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                                locationRequest.interval = 20 * 1000
                                fusedLocationClient.requestLocationUpdates(locationRequest,
                                    locationCallback,
                                    Looper.getMainLooper());
//        fusedLocationClient.removeLocationUpdates(locationCallback);
                                Handler(Looper.getMainLooper()).postDelayed({
                                    location_APIS.Get_Cert_Info(accessToken,"$lat,$lon")?.enqueue(object :
                                        Callback<Responce_Location_Cert_Model> {
                                        override fun onResponse(
                                            call: Call<Responce_Location_Cert_Model>,
                                            response: Response<Responce_Location_Cert_Model>
                                        ) {
                                            if(response.body()?.result==200){
                                                binding.textView19.setText(response.body()?.message)
                                                fusedLocationClient.removeLocationUpdates(locationCallback);
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    finish()
                                                },2000)
                                            }
                                            else{
                                                binding.textView19.setText(response.body()?.message)
                                                fusedLocationClient.removeLocationUpdates(locationCallback);
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    finish()
                                                }, 2000)
                                            }
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                        }
                                        override fun onFailure(call: Call<Responce_Location_Cert_Model>, t: Throwable) {

                                        }

                                    })

//            Toast.makeText(this, "$lat, $lon.", Toast.LENGTH_SHORT).show()
                                }, 1000)

                            }
                            else {
                                var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                Toast.makeText(TedPermissionProvider.context, refresh_response.body()?.massage.toString(),Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
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



    }
    private fun setPermission() {
        // 권한 묻는 팝업 만들기
        val permissionListener = object : PermissionListener {
            // 설정해놓은 권한을 허용됐을 때
            override fun onPermissionGranted() {
                // 1초간 스플래시 보여주기
                val backgroundExecutable : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
                val mainExecutor : Executor = ContextCompat.getMainExecutor(this@LocationActivity)
            }

            // 설정해놓은 권한을 거부됐을 때
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                // 권한 없어서 요청
                AlertDialog.Builder(this@LocationActivity)
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

                Toast.makeText(this@LocationActivity, "권한 거부", Toast.LENGTH_SHORT).show()
            }

        }

        // 권한 설정
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setRationaleMessage("정확한 위치 정보를 위해 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다. [앱 설정]->[권한] 항목에서 허용해주세요.")
            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION) // 필수 권한만 문기
            .check()
    }
}