package com.campuslinker.app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.campuslinker.app.databinding.ActivityNaviBinding
import com.campuslinker.app.school.Get_school_APIS
import com.google.android.gms.location.*
import java.util.*

private const val TAG_NOTICE = "notice_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_MANAGEMENT = "manegement_fragment"
private const val TAG_SEARCH = "search_fragment"
class NaviActivity : AppCompatActivity() {
    companion object{
        // MainActivity 타입의 객체를 동반 객체로 선언한다(자바에서는 static)
        var naviActivity : NaviActivity? = null
    }
    private var lat : String?=null
    private var lon : String?=null
    val api_school = Get_school_APIS.create()
    var myname:String?=null
    var myemail:String?=null
    var myuniv:String?=null
    var mycert:String?=null
    var my_location : String?=null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationListener: LocationListener? = null
    private lateinit var binding : ActivityNaviBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFragment(TAG_HOME, HomeFragment())
        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.ManagementeFragment -> setFragment(TAG_MANAGEMENT, managementFragment())
                R.id.HomeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.NoticeFragment-> setFragment(TAG_NOTICE, NoticeFragment())
                R.id.SearchFragment-> setFragment(TAG_SEARCH, searchFragment())
            }
            true
        }
        naviActivity =this
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
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
        var accessToken = token_management.prefs.getString("access_token","기본값")
        val bundle :Bundle = Bundle()
        bundle.putString("location", my_location)
        val fragment_management = managementFragment()
        fragment_management.arguments=bundle
        val transaction = supportFragmentManager.beginTransaction()
    }
    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }
        val search = manager.findFragmentByTag(TAG_SEARCH)
        val home = manager.findFragmentByTag(TAG_HOME)
        val management = manager.findFragmentByTag(TAG_MANAGEMENT)
        val notice = manager.findFragmentByTag(TAG_NOTICE)
        if (search != null){
            fragTransaction.hide(search)
        }

        if (home != null){
            fragTransaction.hide(home)
        }

        if (management != null) {
            fragTransaction.hide(management)
        }
        if (notice != null) {
            fragTransaction.hide(notice)
        }

        if (tag == TAG_SEARCH) {
            if (search!=null){
                fragTransaction.show(search)
            }
        }
        else if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == TAG_NOTICE){
            if (notice != null){
                fragTransaction.show(notice)
            }
        }
        else if (tag == TAG_MANAGEMENT){
            if (management != null){
                fragTransaction.show(management)
            }
        }

        fragTransaction.commitAllowingStateLoss()
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


//    private fun startLocationUpdates() {
//
//        //FusedLocationProviderClient의 인스턴스를 생성.
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
//        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
//        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
//    }
//
//    // 시스템으로 부터 위치 정보를 콜백으로 받음
//    private val mLocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
//            locationResult.lastLocation
//            locationResult.lastLocation?.let { onLocationChanged(it) }
//        }
//    }
//
//    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
//    fun onLocationChanged(location: Location) {
//        mLastLocation = location
//        my_location = mLastLocation.latitude.toString()+","+mLastLocation.longitude.toString() // 갱신 된 위도
//    }
//
//
//    // 위치 권한이 있는지 확인하는 메서드
//    private fun checkPermissionForLocation(context: Context): Boolean {
//        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                true
//            } else {
//                // 권한이 없으므로 권한 요청 알림 보내기
//                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
//                false
//            }
//        } else {
//            true
//        }
//    }
//
//    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSION_LOCATION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationUpdates()
//
//            } else {
//                Log.d("ttt", "onRequestPermissionsResult() _ 권한 허용 거부")
//                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

}



interface LocationListener {
    fun onLocationReceived(latitude: Double, longitude: Double)
}