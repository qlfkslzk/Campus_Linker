package com.campuslinker.app

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
import com.campuslinker.app.databinding.ActivityReadMatchboardMapsBinding
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.gun0912.tedpermission.provider.TedPermissionProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Read_matchboard_maps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityReadMatchboardMapsBinding
    var lat : Double?=null
    var lon : Double?=null
    var accessToken = token_management.prefs.getString("access_token","기본값")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReadMatchboardMapsBinding.inflate(layoutInflater)
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
                    val title = intent.getStringExtra("title").toString()
                    lat = intent.getStringExtra("lat")!!.toDouble()
                    lon = intent.getStringExtra("lon")!!.toDouble()
                    binding.mapsTitle.setText(title)
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.mapView) as SupportMapFragment

                    mapFragment.getMapAsync(this@Read_matchboard_maps)
                    binding.freeboardExit.setOnClickListener{
                        finish()
                    }
                    binding.mapMenu1.setOnClickListener { view ->
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
                                val title = intent.getStringExtra("title").toString()
                                lat = intent.getStringExtra("lat")!!.toDouble()
                                lon = intent.getStringExtra("lon")!!.toDouble()
                                binding.mapsTitle.setText(title)
                                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                                val mapFragment = supportFragmentManager
                                    .findFragmentById(R.id.mapView) as SupportMapFragment

                                mapFragment.getMapAsync(this@Read_matchboard_maps)
                                binding.freeboardExit.setOnClickListener{
                                    finish()
                                }
                                binding.mapMenu1.setOnClickListener { view ->
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
                                Toast.makeText(
                                    TedPermissionProvider.context, refresh_response.body()?.massage.toString(),
                                    Toast.LENGTH_SHORT).show()
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
        mMap.addMarker(MarkerOptions().position(LatLng(lat!!, lon!!)).draggable(true).title("지정 위치"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat!!, lon!!)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!, lon!!), 15f))
    }
}