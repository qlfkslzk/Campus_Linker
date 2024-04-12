package com.campuslinker.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.campuslinker.app.chatting.into_chatting_room_APIS
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.campuslinker.app.databinding.ActivityGoogleMapsBinding
import com.campuslinker.app.make_board.*
import com.campuslinker.app.rewrite_board.rewrite_match_board_APIS
import com.campuslinker.app.rewrite_board.rewrite_match_board_model
import com.campuslinker.app.rewrite_board.rewrite_match_board_result_model
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
import java.util.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GoogleMaps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private var lat : Double?=null
    private var lon : Double?=null
    private lateinit var mapFragment: SupportMapFragment
    val into_chat_room_APIS = into_chatting_room_APIS.create()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var locationListener: LocationListener? = null
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    var check_title : Boolean = false
    var check_content : Boolean = false
    var hidden_name : String = "N"
    var check_max_user : Boolean = false
    var check_user_stid : Boolean = false
    var check_delete_date : Boolean = false
    var post_location : String ?=null
    var delete_date : Int =0
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var refreshToken = token_management.prefs.getString("refresh_token","기본값")
    var select_stid : String?=null
    private lateinit var binding: ActivityGoogleMapsBinding
    val Make_Match_Board = Make_Match_Board_APIS.create()
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            lat = data?.getDoubleExtra("updatedLat", 0.0) ?: 0.0
            lon = data?.getDoubleExtra("updatedLon", 0.0) ?: 0.0
            Log.d("update lat", lat.toString())
            Log.d("update lon", lon.toString())
            updateLocation(lat!!, lon!!)
            // 여기서 변경된 좌표를 처리할 수 있습니다.
            // updatedLat와 updatedLon을 사용하여 처리
            Log.d("update location", post_location.toString())
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleMapsBinding.inflate(layoutInflater)
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
                    setPermission()

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    mapFragment = supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
                    binding.scrollview.setOnClickListener {
                        // 루트 뷰 클릭 시 EditText로부터 포커스 제거
                        binding.editContent.clearFocus()
                    }
                    if (ActivityCompat.checkSelfPermission(
                            this@GoogleMaps,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@GoogleMaps,
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

                    val type = intent.getStringExtra("type").toString()
                    Log.d("타입", type)
                    val board_num = intent.getStringExtra("board_num")?.toInt()
                    var accessToken = token_management.prefs.getString("access_token","기본값")
                    if (ActivityCompat.checkSelfPermission(
                            this@GoogleMaps,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@GoogleMaps,
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
                    binding.caterigory.adapter = ArrayAdapter.createFromResource(
                        this@GoogleMaps,
                        R.array.match_board_category,
                        android.R.layout.simple_spinner_item
                    )
                    binding.gender.adapter = ArrayAdapter.createFromResource(
                        this@GoogleMaps,
                        R.array.match_board_gender,
                        android.R.layout.simple_spinner_item
                    )
                    binding.spinner3.adapter = ArrayAdapter.createFromResource(
                        this@GoogleMaps,
                        R.array.match_board_delete_date,
                        android.R.layout.simple_spinner_item
                    )
                    binding.spinner4.adapter = ArrayAdapter.createFromResource(
                        this@GoogleMaps,
                        R.array.match_board_max_user,
                        android.R.layout.simple_spinner_item
                    )
                    binding.spinner8.adapter = ArrayAdapter.createFromResource(
                        this@GoogleMaps,
                        R.array.match_board_stid_1,
                        android.R.layout.simple_spinner_item
                    )
                    binding.spinner9.adapter = ArrayAdapter.createFromResource(
                        this@GoogleMaps,
                        R.array.match_board_stid_2,
                        android.R.layout.simple_spinner_item
                    )
                    binding.spinner10.adapter = ArrayAdapter.createFromResource(
                        this@GoogleMaps,
                        R.array.match_board_stid_1,
                        android.R.layout.simple_spinner_item
                    )
                    if(binding.spinner9.selectedItem.toString().equals("부터")){
                        binding.spinner10.visibility = View.GONE
                    }
                    else if(binding.spinner9.selectedItem.toString().equals("만")){
                        binding.spinner10.visibility = View.VISIBLE
                    }
                    binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            // 아이템을 선택했을 때 처리할 로직
                            binding.editContent.clearFocus()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // 아무 것도 선택되지 않았을 때 처리할 로직
                        }
                    }
//        binding.spinner4.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                binding.editContent.clearFocus()
//            }
//            false
//        }
//        binding.spinner8.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                binding.editContent.clearFocus()
//            }
//            false
//        }
//        binding.spinner9.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                binding.editContent.clearFocus()
//            }
//            false
//        }
//        binding.spinner10.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                binding.editContent.clearFocus()
//            }
//            false
//        }
                    binding.scrollview.setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            // 다른 위젯을 터치한 순간 스피너의 포커스를 제거
                            binding.editContent.clearFocus()
                        }
                        false
                    }
                    if(type.equals("write")) {
                        val fusedLocationClient: FusedLocationProviderClient =
                            LocationServices.getFusedLocationProviderClient(this@GoogleMaps)
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null) {

                                lat = location.latitude
                                lon = location.longitude
                                val latitude = location.latitude
                                val longitude = location.longitude
                                Log.d(
                                    "Test", "GPS Location Latitude: $latitude" +
                                            ", Longitude: $longitude"
                                )

                            }
                        }
                        val locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                if (locationResult == null) {
                                    return
                                }
                                for (location in locationResult.locations) {
                                    if (location != null) {
                                        lat = location.latitude
                                        lon = location.longitude
                                        val latitude = location.latitude
                                        val longitude = location.longitude
                                        Log.d(
                                            "Test", "GPS Location changed, Latitude: $latitude" +
                                                    ", Longitude: $longitude"
                                        )
                                    }
                                }
                            }
                        }
                        val locationRequest = LocationRequest.create()
                        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        locationRequest.interval = 20 * 1000
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                        Log.d("first lat", lat.toString())
                        Log.d("first lon", lon.toString())
                        Handler(Looper.getMainLooper()).postDelayed({
                            mapFragment.getMapAsync(this@GoogleMaps)
                        }, 1000)
                    }
                    // Add a marker in Sydney and move the came
                    else if(type.equals("rewrite")){
                        binding.editTitle.setText(intent.getStringExtra("title").toString())
                        binding.editContent.setText(intent.getStringExtra("contents").toString())
                        if(intent.getStringExtra("hiden").toString().equals("Y")){
                            binding.checkBox.isChecked = true
                        }
                        else{
                            binding.checkBox.isChecked = false
                        }

                        if(intent.getStringExtra("category").toString().equals("자유")){
                            binding.caterigory.setSelection(0)
                        }
                        else if(intent.getStringExtra("category").toString().equals("수업")){
                            binding.caterigory.setSelection(1)
                        }
                        else if(intent.getStringExtra("category").toString().equals("식사")){
                            binding.caterigory.setSelection(2)
                        }
                        else if(intent.getStringExtra("category").toString().equals("여가")){
                            binding.caterigory.setSelection(3)
                        }
                        else if(intent.getStringExtra("category").toString().equals("공부")){
                            binding.caterigory.setSelection(4)
                        }
                        if(intent.getStringExtra("gender").toString().equals("여자만")){
                            binding.gender.setSelection(0)
                        }
                        else if(intent.getStringExtra("category").toString().equals("남자만")){
                            binding.gender.setSelection(1)
                        }
                        else if(intent.getStringExtra("category").toString().equals("성별무관")){
                            binding.gender.setSelection(2)
                        }
                        lat = intent.getStringExtra("lat")?.toDouble()
                        lon = intent.getStringExtra("lon")?.toDouble()
                        Log.d("first lat", lat.toString())
                        Log.d("first lon", lon.toString())
                        Log.d("location", post_location.toString())

                        mapFragment.getMapAsync(this@GoogleMaps)

                    }




                    binding.PostButton.setOnClickListener {
                        post_location = lat.toString()+","+lon.toString()
                        if(binding.editTitle.text.toString().equals("")){
                            check_title = false
                        }
                        else {
                            check_title = true
                        }
                        if(binding.editContent.text.toString().equals("")){
                            check_content = false
                        }
                        else {
                            check_content = true
                        }
                        binding.checkBox.setOnCheckedChangeListener{ _, isChecked ->
                            if(isChecked) {
                                hidden_name = "Y"
                            }else{
                                hidden_name = "N"
                            }
                        }
                        if(binding.spinner3.selectedItem.toString().equals("삭제 날짜")){
                            check_delete_date = false
                        }
                        else if(binding.spinner3.selectedItem.toString().equals("1일")){
                            delete_date = 1
                            check_delete_date = true
                        }
                        else if(binding.spinner3.selectedItem.toString().equals("3일")){
                            delete_date = 3
                            check_delete_date = true
                        }
                        else if(binding.spinner3.selectedItem.toString().equals("5일")){
                            delete_date =5
                            check_delete_date = true
                        }
                        else if(binding.spinner3.selectedItem.toString().equals("7일")){
                            delete_date =7
                            check_delete_date = true
                        }
                        if(binding.spinner4.selectedItem.toString().equals("모집 인원")){
                            check_max_user = false
                        }
                        else{
                            check_max_user = true
                        }
                        if(binding.spinner9.selectedItem.toString().equals("만")){
                            select_stid = binding.spinner8.selectedItem.toString()+binding.spinner9.selectedItem.toString()
                        }
                        else{
                            select_stid = binding.spinner8.selectedItem.toString()+binding.spinner9.selectedItem.toString()+" "+ binding.spinner10.selectedItem.toString()
                        }
                        val currentTime = Calendar.getInstance().time

                        // 3일을 더합니다.
                        val calendar = Calendar.getInstance()
                        calendar.time = Date()
                        calendar.add(Calendar.DATE, delete_date)

                        // SimpleDateFormat을 사용하여 원하는 형식으로 날짜를 포맷합니다.
                        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                        val threeDaysLater = dateFormat.format(calendar.time)
                        if (check_title == true and check_content == true and check_delete_date == true and check_max_user == true
                        ) {
                            if(type.equals("write")){
                                var max_user = 1
                                if(binding.spinner4.selectedItem.toString().equals("모집 인원")){
                                    max_user = 1
                                }
                                else{
                                    max_user = Integer.parseInt(binding.spinner4.selectedItem.toString())
                                }
                                var data_match = Make_Match_Board_Body_Model(
                                    accessToken,
                                    binding.editTitle.text.toString(),
                                    binding.caterigory.selectedItem.toString(),
                                    binding.editContent.text.toString(),
                                    hidden_name,
                                    post_location ,
                                    binding.gender.selectedItem.toString(),
                                    select_stid,
                                    threeDaysLater.toString(),
                                    max_user,
                                    "MATCH"
                                )
                                Log.d("post location", post_location.toString())
                                Make_Match_Board.Make_Match_Board_Body_Model(data_match)?.enqueue(object :
                                    Callback<Make_Match_Board_Response_Model> {
                                    override fun onResponse(
                                        call: Call<Make_Match_Board_Response_Model>,
                                        response: Response<Make_Match_Board_Response_Model>
                                    ) {
                                        if (response.body()?.result == 200) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                val intent = Intent(this@GoogleMaps, BoardActivity::class.java)
                                                intent.putExtra("boadr_type", "match") // 데이터 넣기
                                                startActivity(intent)
                                                finish()
                                            }, 1000)
                                        }
                                        else if (response.body()?.result == 400){

                                        }
                                        else if (response.body()?.result == 401){

                                        }
                                        else{
                                            var dialog = AlertDialog.Builder(this@GoogleMaps)
                                            dialog.setMessage("${response.body()?.message}")
                                            dialog.show()
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                val intent = Intent(this@GoogleMaps, BoardActivity::class.java)
                                                intent.putExtra("boadr_type", "match") // 데이터 넣기
                                                startActivity(intent)
                                                finish()
                                            }, 1000)
                                        }
                                        Toast.makeText(this@GoogleMaps, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onFailure(
                                        call: Call<Make_Match_Board_Response_Model>,
                                        t: Throwable
                                    ) {

                                    }
                                })
                            }
                            else if(type.equals("rewrite")){
                                var data_match = rewrite_match_board_model(
                                    board_num,
                                    accessToken,
                                    binding.editTitle.text.toString(),
                                    binding.caterigory.selectedItem.toString(),
                                    binding.editContent.text.toString(),
                                    hidden_name,
                                    post_location,
                                    binding.gender.selectedItem.toString(),
                                    select_stid,
                                    threeDaysLater.toString(),
                                    binding.spinner4.selectedItem.toString().toInt(),
                                    "MATCH"
                                )
                                rewrite_match_board_APIS.create().rewrite_match_board_info(data_match)?.enqueue(object :
                                    Callback<rewrite_match_board_result_model> {
                                    override fun onResponse(
                                        call: Call<rewrite_match_board_result_model>,
                                        response: Response<rewrite_match_board_result_model>
                                    ) {
                                        Toast.makeText(this@GoogleMaps, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                        if(response.body()?.result==200){
                                            var intent = Intent(this@GoogleMaps, MapsActivity::class.java)
                                            intent.putExtra("board_num", board_num)
                                            startActivity(intent)
                                            finish()
                                        }
                                        else if (response.body()?.result == 400){

                                        }
                                        else if (response.body()?.result == 401){

                                        }
                                        Toast.makeText(this@GoogleMaps, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onFailure(
                                        call: Call<rewrite_match_board_result_model>,
                                        t: Throwable
                                    ) {

                                    }
                                })
                            }

                        }
                        else{
                            var dialog = AlertDialog.Builder(this@GoogleMaps)
                            dialog.setMessage("입력 정보를 확인해주세요")
                            dialog.show()
                        }
                    }
                    binding.plusMap.setOnClickListener {
                        lat = lat!!.toDouble()
                        lon = lon!!.toDouble()
                        var intent = Intent(this@GoogleMaps, Make_matchboard_maps::class.java)
                        intent.putExtra("lat", lat.toString()) // 데이터 넣기
                        intent.putExtra("lon", lon.toString()) // 데이터 넣기
                        Log.d("lat2", lat.toString())
                        Log.d("lon2", lon.toString())
                        launcher.launch(intent)
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
                                setPermission()

                                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                                mapFragment = supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
                                binding.scrollview.setOnClickListener {
                                    // 루트 뷰 클릭 시 EditText로부터 포커스 제거
                                    binding.editContent.clearFocus()
                                }
                                if (ActivityCompat.checkSelfPermission(
                                        this@GoogleMaps,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                        this@GoogleMaps,
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

                                val type = intent.getStringExtra("type").toString()
                                val board_num = intent.getStringExtra("board_num")?.toInt()
                                var accessToken = token_management.prefs.getString("access_token","기본값")
                                if (ActivityCompat.checkSelfPermission(
                                        this@GoogleMaps,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                        this@GoogleMaps,
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
                                binding.caterigory.adapter = ArrayAdapter.createFromResource(
                                    this@GoogleMaps,
                                    R.array.match_board_category,
                                    android.R.layout.simple_spinner_item
                                )
                                binding.gender.adapter = ArrayAdapter.createFromResource(
                                    this@GoogleMaps,
                                    R.array.match_board_gender,
                                    android.R.layout.simple_spinner_item
                                )
                                binding.spinner3.adapter = ArrayAdapter.createFromResource(
                                    this@GoogleMaps,
                                    R.array.match_board_delete_date,
                                    android.R.layout.simple_spinner_item
                                )
                                binding.spinner4.adapter = ArrayAdapter.createFromResource(
                                    this@GoogleMaps,
                                    R.array.match_board_max_user,
                                    android.R.layout.simple_spinner_item
                                )
                                binding.spinner8.adapter = ArrayAdapter.createFromResource(
                                    this@GoogleMaps,
                                    R.array.match_board_stid_1,
                                    android.R.layout.simple_spinner_item
                                )
                                binding.spinner9.adapter = ArrayAdapter.createFromResource(
                                    this@GoogleMaps,
                                    R.array.match_board_stid_2,
                                    android.R.layout.simple_spinner_item
                                )
                                binding.spinner10.adapter = ArrayAdapter.createFromResource(
                                    this@GoogleMaps,
                                    R.array.match_board_stid_1,
                                    android.R.layout.simple_spinner_item
                                )
                                if(binding.spinner9.selectedItem.toString().equals("부터")){
                                    binding.spinner10.visibility = View.GONE
                                }
                                else if(binding.spinner9.selectedItem.toString().equals("만")){
                                    binding.spinner10.visibility = View.VISIBLE
                                }
                                binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                        // 아이템을 선택했을 때 처리할 로직
                                        binding.editContent.clearFocus()
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        // 아무 것도 선택되지 않았을 때 처리할 로직
                                    }
                                }
//        binding.spinner4.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                binding.editContent.clearFocus()
//            }
//            false
//        }
//        binding.spinner8.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                binding.editContent.clearFocus()
//            }
//            false
//        }
//        binding.spinner9.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                binding.editContent.clearFocus()
//            }
//            false
//        }
//        binding.spinner10.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                binding.editContent.clearFocus()
//            }
//            false
//        }
                                binding.scrollview.setOnTouchListener { _, event ->
                                    if (event.action == MotionEvent.ACTION_DOWN) {
                                        // 다른 위젯을 터치한 순간 스피너의 포커스를 제거
                                        binding.editContent.clearFocus()
                                    }
                                    false
                                }
                                if(type.equals("write")) {
                                    val fusedLocationClient: FusedLocationProviderClient =
                                        LocationServices.getFusedLocationProviderClient(this@GoogleMaps)
                                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                        if (location != null) {

                                            lat = location.latitude
                                            lon = location.longitude
                                            val latitude = location.latitude
                                            val longitude = location.longitude
                                            Log.d(
                                                "Test", "GPS Location Latitude: $latitude" +
                                                        ", Longitude: $longitude"
                                            )

                                        }
                                    }
                                    val locationCallback = object : LocationCallback() {
                                        override fun onLocationResult(locationResult: LocationResult) {
                                            if (locationResult == null) {
                                                return
                                            }
                                            for (location in locationResult.locations) {
                                                if (location != null) {
                                                    lat = location.latitude
                                                    lon = location.longitude
                                                    val latitude = location.latitude
                                                    val longitude = location.longitude
                                                    Log.d(
                                                        "Test", "GPS Location changed, Latitude: $latitude" +
                                                                ", Longitude: $longitude"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    val locationRequest = LocationRequest.create()
                                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                                    locationRequest.interval = 20 * 1000
                                    fusedLocationClient.requestLocationUpdates(
                                        locationRequest,
                                        locationCallback,
                                        Looper.getMainLooper()
                                    )
                                    Log.d("first lat", lat.toString())
                                    Log.d("first lon", lon.toString())
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        mapFragment.getMapAsync(this@GoogleMaps)
                                    }, 1000)
                                }
                                // Add a marker in Sydney and move the came
                                else if(type.equals("rewrite")){
                                    binding.editTitle.setText(intent.getStringExtra("title").toString())
                                    binding.editContent.setText(intent.getStringExtra("contents").toString())
                                    if(intent.getStringExtra("hiden").toString().equals("Y")){
                                        binding.checkBox.isChecked = true
                                    }
                                    else{
                                        binding.checkBox.isChecked = false
                                    }

                                    if(intent.getStringExtra("category").toString().equals("자유")){
                                        binding.caterigory.setSelection(0)
                                    }
                                    else if(intent.getStringExtra("category").toString().equals("수업")){
                                        binding.caterigory.setSelection(1)
                                    }
                                    else if(intent.getStringExtra("category").toString().equals("식사")){
                                        binding.caterigory.setSelection(2)
                                    }
                                    else if(intent.getStringExtra("category").toString().equals("여가")){
                                        binding.caterigory.setSelection(3)
                                    }
                                    else if(intent.getStringExtra("category").toString().equals("공부")){
                                        binding.caterigory.setSelection(4)
                                    }
                                    if(intent.getStringExtra("gender").toString().equals("여자만")){
                                        binding.gender.setSelection(0)
                                    }
                                    else if(intent.getStringExtra("category").toString().equals("남자만")){
                                        binding.gender.setSelection(1)
                                    }
                                    else if(intent.getStringExtra("category").toString().equals("성별무관")){
                                        binding.gender.setSelection(2)
                                    }
                                    lat = intent.getStringExtra("lat")?.toDouble()
                                    lon = intent.getStringExtra("lon")?.toDouble()
                                    Log.d("first lat", lat.toString())
                                    Log.d("first lon", lon.toString())
                                    Log.d("location", post_location.toString())

                                    mapFragment.getMapAsync(this@GoogleMaps)
                                }



                                binding.PostButton.setOnClickListener {
                                    post_location = lat.toString()+","+lon.toString()
                                    if(binding.editTitle.text.toString().equals("")){
                                        check_title = false
                                    }
                                    else {
                                        check_title = true
                                    }
                                    if(binding.editContent.text.toString().equals("")){
                                        check_content = false
                                    }
                                    else {
                                        check_content = true
                                    }
                                    binding.checkBox.setOnCheckedChangeListener{ _, isChecked ->
                                        if(isChecked) {
                                            hidden_name = "Y"
                                        }else{
                                            hidden_name = "N"
                                        }
                                    }
                                    if(binding.spinner3.selectedItem.toString().equals("삭제 날짜")){
                                        check_delete_date = false
                                    }
                                    else if(binding.spinner3.selectedItem.toString().equals("1일")){
                                        delete_date = 1
                                        check_delete_date = true
                                    }
                                    else if(binding.spinner3.selectedItem.toString().equals("3일")){
                                        delete_date = 3
                                        check_delete_date = true
                                    }
                                    else if(binding.spinner3.selectedItem.toString().equals("5일")){
                                        delete_date =5
                                        check_delete_date = true
                                    }
                                    else if(binding.spinner3.selectedItem.toString().equals("7일")){
                                        delete_date =7
                                        check_delete_date = true
                                    }
                                    if(binding.spinner4.selectedItem.toString().equals("모집 인원")){
                                        check_max_user = false
                                    }
                                    else{
                                        check_max_user = true
                                    }
                                    if(binding.spinner9.selectedItem.toString().equals("만")){
                                        select_stid = binding.spinner8.selectedItem.toString()+binding.spinner9.selectedItem.toString()
                                    }
                                    else{
                                        select_stid = binding.spinner8.selectedItem.toString()+binding.spinner9.selectedItem.toString()+" "+ binding.spinner10.selectedItem.toString()
                                    }
                                    val currentTime = Calendar.getInstance().time

                                    // 3일을 더합니다.
                                    val calendar = Calendar.getInstance()
                                    calendar.time = Date()
                                    calendar.add(Calendar.DATE, delete_date)

                                    // SimpleDateFormat을 사용하여 원하는 형식으로 날짜를 포맷합니다.
                                    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                                    val threeDaysLater = dateFormat.format(calendar.time)
                                    if (check_title == true and check_content == true and check_delete_date == true and check_max_user == true
                                    ) {
                                        if(type.equals("write")){
                                            var max_user = 1
                                            if(binding.spinner4.selectedItem.toString().equals("모집 인원")){
                                                max_user = 1
                                            }
                                            else{
                                                max_user = Integer.parseInt(binding.spinner4.selectedItem.toString())
                                            }
                                            var data_match = Make_Match_Board_Body_Model(
                                                accessToken,
                                                binding.editTitle.text.toString(),
                                                binding.caterigory.selectedItem.toString(),
                                                binding.editContent.text.toString(),
                                                hidden_name,
                                                post_location ,
                                                binding.gender.selectedItem.toString(),
                                                select_stid,
                                                threeDaysLater.toString(),
                                                max_user,
                                                "MATCH"
                                            )
                                            Log.d("post location", post_location.toString())
                                            Make_Match_Board.Make_Match_Board_Body_Model(data_match)?.enqueue(object :
                                                Callback<Make_Match_Board_Response_Model> {
                                                override fun onResponse(
                                                    call: Call<Make_Match_Board_Response_Model>,
                                                    response: Response<Make_Match_Board_Response_Model>
                                                ) {
                                                    if (response.body()?.result == 200) {
                                                        Handler(Looper.getMainLooper()).postDelayed({
                                                            val intent = Intent(this@GoogleMaps, BoardActivity::class.java)
                                                            intent.putExtra("boadr_type", "match") // 데이터 넣기
                                                            startActivity(intent)
                                                            finish()
                                                        }, 1000)
                                                    }
                                                    else if (response.body()?.result == 400){

                                                    }
                                                    else if (response.body()?.result == 401){

                                                    }
                                                    else {
                                                        var dialog = AlertDialog.Builder(this@GoogleMaps)
                                                        dialog.setMessage("${response.body()?.message}")
                                                        dialog.show()
                                                        Handler(Looper.getMainLooper()).postDelayed({
                                                            val intent = Intent(this@GoogleMaps, BoardActivity::class.java)
                                                            intent.putExtra("boadr_type", "match") // 데이터 넣기
                                                            startActivity(intent)
                                                            finish()
                                                        }, 1000)
                                                    }
                                                    Toast.makeText(this@GoogleMaps, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                }

                                                override fun onFailure(
                                                    call: Call<Make_Match_Board_Response_Model>,
                                                    t: Throwable
                                                ) {

                                                }
                                            })
                                        }
                                        else if(type.equals("rewrite")){
                                            var data_match = rewrite_match_board_model(
                                                board_num,
                                                accessToken,
                                                binding.editTitle.text.toString(),
                                                binding.caterigory.selectedItem.toString(),
                                                binding.editContent.text.toString(),
                                                hidden_name,
                                                post_location,
                                                binding.gender.selectedItem.toString(),
                                                select_stid,
                                                threeDaysLater.toString(),
                                                binding.spinner4.selectedItem.toString().toInt(),
                                                "MATCH"
                                            )
                                            rewrite_match_board_APIS.create().rewrite_match_board_info(data_match)?.enqueue(object :
                                                Callback<rewrite_match_board_result_model> {
                                                override fun onResponse(
                                                    call: Call<rewrite_match_board_result_model>,
                                                    response: Response<rewrite_match_board_result_model>
                                                ) {
                                                    Toast.makeText(this@GoogleMaps, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                    if(response.body()?.result==200){
                                                        var intent = Intent(this@GoogleMaps, MapsActivity::class.java)
                                                        intent.putExtra("board_num", board_num)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    else if (response.body()?.result == 400){

                                                    }
                                                    else if (response.body()?.result == 401){

                                                    }
                                                    Toast.makeText(this@GoogleMaps, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                }

                                                override fun onFailure(
                                                    call: Call<rewrite_match_board_result_model>,
                                                    t: Throwable
                                                ) {

                                                }
                                            })
                                        }

                                    }
                                    else{
                                        var dialog = AlertDialog.Builder(this@GoogleMaps)
                                        dialog.setMessage("입력 정보를 확인해주세요")
                                        dialog.show()
                                    }
                                }
                                binding.plusMap.setOnClickListener {
                                    lat = lat!!.toDouble()
                                    lon = lon!!.toDouble()
                                    var intent = Intent(this@GoogleMaps, Make_matchboard_maps::class.java)
                                    intent.putExtra("lat", lat.toString()) // 데이터 넣기
                                    intent.putExtra("lon", lon.toString()) // 데이터 넣기
                                    Log.d("lat2", lat.toString())
                                    Log.d("lon2", lon.toString())
                                    launcher.launch(intent)
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
    private fun setPermission() {
        // 권한 묻는 팝업 만들기
        val permissionListener = object : PermissionListener {
            // 설정해놓은 권한을 허용됐을 때
            override fun onPermissionGranted() {
                // 1초간 스플래시 보여주기
                val backgroundExecutable: ScheduledExecutorService =
                    Executors.newSingleThreadScheduledExecutor()
                val mainExecutor: Executor = ContextCompat.getMainExecutor(this@GoogleMaps)
            }

            // 설정해놓은 권한을 거부됐을 때
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                // 권한 없어서 요청
                AlertDialog.Builder(this@GoogleMaps)
                    .setMessage("권한 거절로 인해 일부 기능이 제한됩니다.")
                    .setPositiveButton("권한 설정하러 가기") { dialog, which ->
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:com.example.myweathertest2"))
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                            startActivity(intent)
                        }
                    }
                    .show()

                Toast.makeText(this@GoogleMaps, "권한 거부", Toast.LENGTH_SHORT).show()
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
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    fun getlocation(la : Double, lo : Double){
        lat = la
        lon = lo
    }
    private fun updateLocation(updatedLat: Double, updatedLon: Double) {
        lat = updatedLat
        lon = updatedLon
        Log.d("update lat", lat.toString())
        Log.d("update lon", lon.toString())
        mapFragment.getMapAsync(this)
        // 여기서 변경된 좌표를 처리할 수 있습니다.
        // updatedLat와 updatedLon을 사용하여 처리
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the came
        if(lat!=null || lon !=null){
            marker?.remove()
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