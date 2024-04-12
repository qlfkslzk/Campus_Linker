package com.campuslinker.app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.campuslinker.app.databinding.ActivityMakeMatchboardMapsBinding
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.google.android.gms.maps.model.Marker
import com.gun0912.tedpermission.provider.TedPermissionProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Make_matchboard_maps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMakeMatchboardMapsBinding
    var lat : Double?=null
    var lon : Double?=null
    var accessToken = token_management.prefs.getString("access_token","기본값")
    private var marker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeMatchboardMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    val latString = intent.getStringExtra("lat")
                    val lonString = intent.getStringExtra("lon")

                    if (latString != null && lonString != null) {
                        lat = latString.toDouble()
                        lon = lonString.toDouble()
                        val mapFragment = supportFragmentManager
                            .findFragmentById(R.id.mapView3) as SupportMapFragment
                        mapFragment.getMapAsync(this@Make_matchboard_maps)
                    }
                    binding.freeboardExit.setOnClickListener{
                        val updatedLat = lat
                        val updatedLon = lon
                        var intent = Intent()
                        intent.putExtra("updatedLat", updatedLat) // 변경된 위도 데이터를 넣기
                        intent.putExtra("updatedLon", updatedLon) // 변경된 경도 데이터를 넣기
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    binding.mapMenu2.setOnClickListener { view ->
                        val popupMenu = PopupMenu(TedPermissionProvider.context, view)
                        val inflater = popupMenu.menuInflater
                        inflater.inflate(R.menu.googlemaps_menu, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.nomal -> {
                                    // 메뉴 항목 1이 선택된 경우
                                    // 원하는 동작을 수행
                                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                                    true
                                }
                                R.id.hybrid -> {
                                    // 메뉴 항목 1이 선택된 경우
                                    // 원하는 동작을 수행
                                    mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                                    true
                                }
                                R.id.satellite -> {
                                    // 메뉴 항목 1이 선택된 경우
                                    // 원하는 동작을 수행
                                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                                    true
                                }
                                R.id.terrian -> {
                                    // 메뉴 항목 1이 선택된 경우
                                    // 원하는 동작을 수행
                                    mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                                    true
                                }
                                else -> false
                            }
                        }

                        popupMenu.show()
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
                                val latString = intent.getStringExtra("lat")
                                val lonString = intent.getStringExtra("lon")

                                if (latString != null && lonString != null) {
                                    lat = latString.toDouble()
                                    lon = lonString.toDouble()
                                    val mapFragment = supportFragmentManager
                                        .findFragmentById(R.id.mapView3) as SupportMapFragment
                                    mapFragment.getMapAsync(this@Make_matchboard_maps)
                                }
                                binding.freeboardExit.setOnClickListener{
                                    val updatedLat = lat
                                    val updatedLon = lon
                                    var intent = Intent()
                                    intent.putExtra("updatedLat", updatedLat) // 변경된 위도 데이터를 넣기
                                    intent.putExtra("updatedLon", updatedLon) // 변경된 경도 데이터를 넣기
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                                binding.mapMenu2.setOnClickListener { view ->
                                    val popupMenu = PopupMenu(TedPermissionProvider.context, view)
                                    val inflater = popupMenu.menuInflater
                                    inflater.inflate(R.menu.googlemaps_menu, popupMenu.menu)
                                    popupMenu.setOnMenuItemClickListener { item ->
                                        when (item.itemId) {
                                            R.id.nomal -> {
                                                // 메뉴 항목 1이 선택된 경우
                                                // 원하는 동작을 수행
                                                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                                                true
                                            }
                                            R.id.hybrid -> {
                                                // 메뉴 항목 1이 선택된 경우
                                                // 원하는 동작을 수행
                                                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                                                true
                                            }
                                            R.id.satellite -> {
                                                // 메뉴 항목 1이 선택된 경우
                                                // 원하는 동작을 수행
                                                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                                                true
                                            }
                                            R.id.terrian -> {
                                                // 메뉴 항목 1이 선택된 경우
                                                // 원하는 동작을 수행
                                                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                                                true
                                            }
                                            else -> false
                                        }
                                    }

                                    popupMenu.show()
                                }


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
    override fun onBackPressed() {
        val updatedLat = lat
        val updatedLon = lon
        var intent = Intent()
        intent.putExtra("updatedLat", updatedLat) // 변경된 위도 데이터를 넣기
        intent.putExtra("updatedLon", updatedLon) // 변경된 경도 데이터를 넣기
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the came
        if(lat!=null || lon !=null){
            marker =mMap.addMarker(MarkerOptions().position(LatLng(lat!!, lon!!)).title("현재 위치"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat!!, lon!!)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!, lon!!), 17f))
            mMap.setOnMapClickListener(this)
        }
    }
    override fun onMapClick(p0: LatLng) {
        // 클릭한 위치로 마커를 이동합니다.
        if (p0 != null) {
            // 클릭한 위치로 마커를 이동합니다.
            marker?.position = p0
            val newPosition = marker?.position
            if (newPosition != null) {
                lat = newPosition.latitude
            }
            if (newPosition != null) {
                lon = newPosition.longitude
            }
        }
//        var dialog = AlertDialog.Builder(this@GoogleMaps)
//        dialog.setMessage(lat.toString()+"," + lon.toString())
//        dialog.show()

    }
}