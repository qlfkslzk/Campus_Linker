package com.campuslinker.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.campuslinker.app.databinding.ReadMatchBoardBinding
import com.campuslinker.app.read_board.read_match_board
import com.campuslinker.app.read_board.read_match_board_response_model
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReadMatchBoardActivity : AppCompatActivity(), OnMapReadyCallback {
    val read_match_board_APIS = read_match_board.create()
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var board_num :String?=null
    private lateinit var map: GoogleMap
    private var lat : Double=0.0
    private var lon : Double=0.0
    private var location : String?=null
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding : ReadMatchBoardBinding;
        binding = ReadMatchBoardBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapview) as SupportMapFragment

        var accessToken = token_management.prefs.getString("access_token","기본값")
        val board_num = intent.getStringExtra("boadr_num").toString()
        read_match_board_APIS.get_read_match_board_Info(board_num, accessToken)?.enqueue(object :
            Callback<read_match_board_response_model> {
            override fun onResponse(
                call: Call<read_match_board_response_model>,
                response: Response<read_match_board_response_model>
            ) {
                if(response.body()?.result == 200){
                    binding.userName.setText(response.body()?.board?.get(0)?.write_user.toString())
                    binding.category.setText(response.body()?.board?.get(0)?.category.toString())
                    binding.content.setText(response.body()?.board?.get(0)?.contents.toString())
                    binding.createDate.setText(response.body()?.board?.get(0)?.create_date.toString())
                    binding.titleFreeboard.setText(response.body()?.board?.get(0)?.title.toString())
                    binding.reaction.setText("좋아요 " + response.body()?.board?.get(0)?.reaction_count.toString())
                    binding.comment.setText("댓글 수 " + response.body()?.board?.get(0)?.comment.toString())
                    location = response.body()?.board?.get(0)?.location.toString()



//                    var dialog = AlertDialog.Builder(this@ReadMatchBoardActivity)
//                    dialog.setTitle("결과")
//                    dialog.setMessage(response.body()?.board.toString() + board_num)
//                    dialog.show()
                }
            }
            override fun onFailure(call: Call<read_match_board_response_model>, t: Throwable) {

            }

        })
        mapFragment.getMapAsync(this@ReadMatchBoardActivity)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the came
        mMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).draggable(true).title("현재 위치"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 17f))
    }
}