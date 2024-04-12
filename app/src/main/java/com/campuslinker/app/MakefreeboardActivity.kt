package com.campuslinker.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.campuslinker.app.databinding.MakefreeboardBinding
import com.campuslinker.app.make_board.Make_Free_Board_APIS
import com.campuslinker.app.make_board.Make_Free_Board_Body_Model
import com.campuslinker.app.make_board.Make_Free_Board_Response_Model
import com.campuslinker.app.rewrite_board.Rewrite_borad_APIS
import com.campuslinker.app.rewrite_board.rewrite_free_board_model
import com.campuslinker.app.rewrite_board.rewrite_free_board_result_model
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.gun0912.tedpermission.provider.TedPermissionProvider
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakefreeboardActivity : AppCompatActivity() {
    lateinit var binding : MakefreeboardBinding;
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var refreshToken = token_management.prefs.getString("refresh_token","기본값")
    val rewrite_free_board = Rewrite_borad_APIS.create()
    val Make_Free_Board = Make_Free_Board_APIS.create()
    var check_title : Boolean ?=null
    var check_content : Boolean ?=null
    var hidden_name : String ?=null
    var board_num :Int ?=null
    var type : String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = MakefreeboardBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        board_num = intent.getStringExtra("board_num")?.toInt()
        type = intent.getStringExtra("type")?.toString()
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

                    if(type.equals("write")){
                        hidden_name = "N"
                        binding.spinner.adapter = ArrayAdapter.createFromResource(
                            this@MakefreeboardActivity,
                            R.array.free_board_category,
                            android.R.layout.simple_spinner_item
                        ) //성별 스피너 구현
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
                        binding.PostButton.setOnClickListener {
                            var data = Make_Free_Board_Body_Model(
                                accessToken,
                                binding.editTitle.text.toString(),
                                binding.spinner.selectedItem.toString(),
                                binding.editContent.text.toString(),
                                hidden_name,
                                "FREE"
                            )
                            Make_Free_Board.Post_Make_Free_Board(data).enqueue(object : Callback<Make_Free_Board_Response_Model> {
                                override fun onResponse(
                                    call: Call<Make_Free_Board_Response_Model>,
                                    response: Response<Make_Free_Board_Response_Model>
                                ) {
                                    if(response.body()?.result==200) {
                                        var dialog = AlertDialog.Builder(this@MakefreeboardActivity)
                                        dialog.setMessage("${response.body()?.message}")
                                        dialog.show()
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            val intent = Intent(this@MakefreeboardActivity, BoardActivity::class.java)
                                            intent.putExtra("boadr_type", "free") // 데이터 넣기
                                            startActivity(intent)
                                            finish()
                                        },1000)
                                    }
                                    else{
                                        var dialog = AlertDialog.Builder(this@MakefreeboardActivity)
                                        dialog.setMessage("${response.body()?.message}")
                                        dialog.show()
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            val intent = Intent(this@MakefreeboardActivity, BoardActivity::class.java)
                                            intent.putExtra("boadr_type", "free") // 데이터 넣기
                                            startActivity(intent)
                                            finish()
                                        },1000)
                                    }
                                }

                                override fun onFailure(call: Call<Make_Free_Board_Response_Model>, t: Throwable) {

                                }
                            })
                        }
                    }
                    else if(type.equals("rewrite")){
                        hidden_name = "N"
                        binding.editTitle.setText(intent.getStringExtra("title").toString())
                        binding.editContent.setText(intent.getStringExtra("contents").toString())
                        if(intent.getStringExtra("hiden").toString().equals("Y")){
                            binding.checkBox.isChecked = true
                        }
                        else{
                            binding.checkBox.isChecked = false
                        }
                        binding.spinner.adapter = ArrayAdapter.createFromResource(
                            this@MakefreeboardActivity,
                            R.array.free_board_category,
                            android.R.layout.simple_spinner_item
                        ) //성별 스피너 구현
                        if(intent.getStringExtra("category").toString().equals("자유")){
                            binding.spinner.setSelection(0)
                        }
                        else if(intent.getStringExtra("category").toString().equals("질문")){
                            binding.spinner.setSelection(1)
                        }
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
                        binding.PostButton.setOnClickListener {
                            var data = rewrite_free_board_model(
                                board_num,
                                accessToken,
                                binding.editTitle.text.toString(),
                                binding.spinner.selectedItem.toString(),
                                binding.editContent.text.toString(),
                                hidden_name,
                                "FREE"
                            )
                            rewrite_free_board.rewrite_free_board_info(data)?.enqueue(object : Callback<rewrite_free_board_result_model> {
                                override fun onResponse(
                                    call: Call<rewrite_free_board_result_model>,
                                    response: Response<rewrite_free_board_result_model>
                                ) {
                                    Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                    var intent = Intent(this@MakefreeboardActivity, ReadFreeBoardActivity::class.java)
                                    intent.putExtra("board_num", board_num)
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onFailure(call: Call<rewrite_free_board_result_model>, t: Throwable) {

                                }
                            })
                        }

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

                                val board_num = intent.getStringExtra("board_num")?.toInt()

                                val type = intent.getStringExtra("type").toString()
                                if(type.equals("write")){
                                    hidden_name = "N"
                                    binding.spinner.adapter = ArrayAdapter.createFromResource(
                                        this@MakefreeboardActivity,
                                        R.array.free_board_category,
                                        android.R.layout.simple_spinner_item
                                    ) //성별 스피너 구현
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
                                    binding.PostButton.setOnClickListener {
                                        var data = Make_Free_Board_Body_Model(
                                            accessToken,
                                            binding.editTitle.text.toString(),
                                            binding.spinner.selectedItem.toString(),
                                            binding.editContent.text.toString(),
                                            hidden_name,
                                            "FREE"
                                        )
                                        Make_Free_Board.Post_Make_Free_Board(data).enqueue(object : Callback<Make_Free_Board_Response_Model> {
                                            override fun onResponse(
                                                call: Call<Make_Free_Board_Response_Model>,
                                                response: Response<Make_Free_Board_Response_Model>
                                            ) {
                                                if(response.body()?.result==200) {
                                                    var dialog = AlertDialog.Builder(this@MakefreeboardActivity)
                                                    dialog.setMessage("${response.body()?.message}")
                                                    dialog.show()
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        val intent = Intent(this@MakefreeboardActivity, BoardActivity::class.java)
                                                        intent.putExtra("boadr_type", "free") // 데이터 넣기
                                                        startActivity(intent)
                                                        finish()
                                                    },1000)
                                                }
                                                else{
                                                    var dialog = AlertDialog.Builder(this@MakefreeboardActivity)
                                                    dialog.setMessage("${response.body()?.message}")
                                                    dialog.show()
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        val intent = Intent(this@MakefreeboardActivity, BoardActivity::class.java)
                                                        intent.putExtra("boadr_type", "free") // 데이터 넣기
                                                        startActivity(intent)
                                                        finish()
                                                    },1000)
                                                }
                                            }

                                            override fun onFailure(call: Call<Make_Free_Board_Response_Model>, t: Throwable) {

                                            }
                                        })
                                    }
                                }
                                else if(type.equals("rewrite")){
                                    hidden_name = "N"
                                    binding.editTitle.setText(intent.getStringExtra("title").toString())
                                    binding.editContent.setText(intent.getStringExtra("contents").toString())
                                    if(intent.getStringExtra("hiden").toString().equals("Y")){
                                        binding.checkBox.isChecked = true
                                    }
                                    else{
                                        binding.checkBox.isChecked = false
                                    }
                                    binding.spinner.adapter = ArrayAdapter.createFromResource(
                                        this@MakefreeboardActivity,
                                        R.array.free_board_category,
                                        android.R.layout.simple_spinner_item
                                    ) //성별 스피너 구현
                                    if(intent.getStringExtra("category").toString().equals("자유")){
                                        binding.spinner.setSelection(0)
                                    }
                                    else if(intent.getStringExtra("category").toString().equals("질문")){
                                        binding.spinner.setSelection(1)
                                    }
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
                                    binding.PostButton.setOnClickListener {
                                        var data = rewrite_free_board_model(
                                            board_num,
                                            accessToken,
                                            binding.editTitle.text.toString(),
                                            binding.spinner.selectedItem.toString(),
                                            binding.editContent.text.toString(),
                                            hidden_name,
                                            "FREE"
                                        )
                                        rewrite_free_board.rewrite_free_board_info(data)?.enqueue(object : Callback<rewrite_free_board_result_model> {
                                            override fun onResponse(
                                                call: Call<rewrite_free_board_result_model>,
                                                response: Response<rewrite_free_board_result_model>
                                            ) {
                                                Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                var intent = Intent(this@MakefreeboardActivity, ReadFreeBoardActivity::class.java)
                                                intent.putExtra("board_num", board_num)
                                                startActivity(intent)
                                                finish()
                                            }

                                            override fun onFailure(call: Call<rewrite_free_board_result_model>, t: Throwable) {

                                            }
                                        })
                                    }

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
        if(type.equals("rewrite")){
            var intent = Intent(this@MakefreeboardActivity, ReadFreeBoardActivity::class.java)
            intent.putExtra("board_num", board_num)
            startActivity(intent)
            finish()
        }
        finish()
    }








}
