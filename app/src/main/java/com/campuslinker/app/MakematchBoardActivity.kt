package com.campuslinker.app

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.campuslinker.app.databinding.MakematchboardBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment



class MakematchBoardActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var map: GoogleMap
    private var lat : String?=null
    private var lon : String?=null
    lateinit var binding : MakematchboardBinding
    var check_title : Boolean = false
    var check_content : Boolean = false
    var hidden_name : String = "N"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationListener: LocationListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = MakematchboardBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapview1) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setPermission()
        var accessToken = token_management.prefs.getString("access_token","기본값")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
//        fusedLocationClient.removeLocationUpdates(locationCallback); //gps종료



    }
    private fun setPermission() {
        // 권한 묻는 팝업 만들기
        val permissionListener = object : PermissionListener {
            // 설정해놓은 권한을 허용됐을 때
            override fun onPermissionGranted() {
                // 1초간 스플래시 보여주기
                val backgroundExecutable : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
                val mainExecutor : Executor = ContextCompat.getMainExecutor(this@MakematchBoardActivity)
            }

            // 설정해놓은 권한을 거부됐을 때
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                // 권한 없어서 요청
                AlertDialog.Builder(this@MakematchBoardActivity)
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

                Toast.makeText(this@MakematchBoardActivity, "권한 거부", Toast.LENGTH_SHORT).show()
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
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


}