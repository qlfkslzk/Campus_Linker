package com.campuslinker.app

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.campuslinker.app.adapter.MatchRecycleAdapter
import com.campuslinker.app.check_board_list.Board_List_Model
import com.campuslinker.app.check_board_list.Match_Board_List_Model
import com.campuslinker.app.check_board_list.check_board_APIS
import com.campuslinker.app.check_board_list.check_board_APIS_match
import com.campuslinker.app.databinding.BoardBinding
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardActivity : AppCompatActivity() {
    val free_board_list = check_board_APIS.create()
    val match_board_list = check_board_APIS_match.create()
    var accessToken = token_management.prefs.getString("access_token","기본값")
    val board_type = token_management.prefs.getString("boadr_type","기본값")
    var page :Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding : BoardBinding;
        binding = BoardBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
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
                    var free_board_list_array =arrayListOf<Board_class>()
                    var match_board_list_array =arrayListOf<Board_class>()
                    var board_list_array =arrayListOf<Board_class>(
                    )
                    if(board_type.equals("free")){
                        binding.button.setOnClickListener{
                            val intent = Intent(this@BoardActivity, MakefreeboardActivity::class.java)
                            intent.putExtra("type", "write") // 데이터 넣기
                            startActivity(intent)
                        }
                        binding.textView18.setText("자유 게시판")
                        free_board_list.getboardlistlInfo("$board_type","0","","",accessToken)?.enqueue(object :
                            Callback<Board_List_Model> {
                            override fun onResponse(
                                call: Call<Board_List_Model>,
                                response: Response<Board_List_Model>
                            ) {


                                var free_length = response.body()?.list?.size
                                if(free_length !=null) {
                                    for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                        free_board_list_array.add(
                                            Board_class(
                                                response.body()?.list?.get(i)?.category,
                                                response.body()?.list?.get(i)?.title,
                                                response.body()?.list?.get(i)?.format_date.toString(),
                                                response.body()?.list?.get(i)?.reaction_count.toString(),
                                                response.body()?.list?.get(i)?.comment.toString(),
                                                response.body()?.list?.get(i)?.num
                                            )
                                        )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                    }

                                    var free_board_adapter = RecycleAdapter(free_board_list_array)
                                    binding.reboard.apply{
                                        adapter = free_board_adapter
                                        layoutManager = LinearLayoutManager(this@BoardActivity)
                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                    }
                                    binding.reboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                            super.onScrolled(recyclerView, dx, dy)
                                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                            val visibleItemCount = layoutManager.childCount
                                            val totalItemCount = layoutManager.itemCount
                                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                                                // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                page = page + 1
                                                free_board_list.getboardlistlInfo("$board_type","$page","","",accessToken)?.enqueue(object :
                                                    Callback<Board_List_Model> {
                                                    override fun onResponse(
                                                        call: Call<Board_List_Model>,
                                                        response: Response<Board_List_Model>
                                                    ) {

                                                        var free_length = response.body()?.list?.size
                                                        if(free_length !=null) {
                                                            for (i in 0..free_length - 1) {
                                                                free_board_list_array.add(
                                                                    Board_class(
                                                                        response.body()?.list?.get(i)?.category,
                                                                        response.body()?.list?.get(i)?.title,
                                                                        response.body()?.list?.get(i)?.format_date.toString(),
                                                                        response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                        response.body()?.list?.get(i)?.comment.toString(),
                                                                        response.body()?.list?.get(i)?.num
                                                                    )
                                                                )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                            }
                                                        }
                                                        //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
                                                        free_board_adapter.notifyDataSetChanged()
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                    }
                                                    override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                                    }

                                                })
                                            }
                                        }
                                    })
                                    free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                        override fun onClick(v: View, position: Int) {
                                            var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                            intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                            token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                            startActivity(intent)
                                        }

                                    })


                                }



                                //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                            }
                            override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                            }

                        })
                        binding.swipeRefreshLayout.setOnRefreshListener {
                            // 사용자가 당겨서 새로고침을 요청하면 이 코드 블록이 실행됩니다.
                            // 여기에서 원하는 작업(데이터 다시 로드 등)을 수행한 후 아래 코드로 새로고침 완료를 알립니다.
                            free_board_list.getboardlistlInfo("$board_type","0","","",accessToken)?.enqueue(object :
                                Callback<Board_List_Model> {
                                override fun onResponse(
                                    call: Call<Board_List_Model>,
                                    response: Response<Board_List_Model>
                                ) {

                                    free_board_list_array.clear()
                                    var free_length = response.body()?.list?.size
                                    if(free_length !=null) {
                                        for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                            free_board_list_array.add(
                                                Board_class(
                                                    response.body()?.list?.get(i)?.category,
                                                    response.body()?.list?.get(i)?.title,
                                                    response.body()?.list?.get(i)?.format_date.toString(),
                                                    response.body()?.list?.get(i)?.reaction_count.toString(),
                                                    response.body()?.list?.get(i)?.comment.toString(),
                                                    response.body()?.list?.get(i)?.num
                                                )
                                            )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                        }

                                        var free_board_adapter = RecycleAdapter(free_board_list_array)
                                        binding.reboard.apply{
                                            adapter = free_board_adapter
                                            layoutManager = LinearLayoutManager(this@BoardActivity)
                                            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                        }
                                        binding.reboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                                super.onScrolled(recyclerView, dx, dy)
                                                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                                val visibleItemCount = layoutManager.childCount
                                                val totalItemCount = layoutManager.itemCount
                                                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                                                    // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                    // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                    page = page + 1
                                                    free_board_list.getboardlistlInfo("$board_type","$page","","",accessToken)?.enqueue(object :
                                                        Callback<Board_List_Model> {
                                                        override fun onResponse(
                                                            call: Call<Board_List_Model>,
                                                            response: Response<Board_List_Model>
                                                        ) {

                                                            var free_length = response.body()?.list?.size
                                                            if(free_length !=null) {
                                                                for (i in 0..free_length - 1) {
                                                                    free_board_list_array.add(
                                                                        Board_class(
                                                                            response.body()?.list?.get(i)?.category,
                                                                            response.body()?.list?.get(i)?.title,
                                                                            response.body()?.list?.get(i)?.format_date.toString(),
                                                                            response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                            response.body()?.list?.get(i)?.comment.toString(),
                                                                            response.body()?.list?.get(i)?.num
                                                                        )
                                                                    )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                                }
                                                            }
                                                            //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
                                                            free_board_adapter.notifyDataSetChanged()
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                        }
                                                        override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                                        }

                                                    })
                                                }
                                            }
                                        })
                                        free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                            override fun onClick(v: View, position: Int) {
                                                var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                startActivity(intent)
                                            }

                                        })

                                    }



                                    //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                }
                                override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                }

                            })
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        binding.searchBoard.setOnKeyListener{ view, keyCode, event ->
                            // Enter Key Action
                            if (event.action == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_ENTER
                            ) {
                                free_board_list_array.clear()
                                binding.reboard.adapter?.notifyDataSetChanged()
                                var title = binding.searchBoard.text.toString()
                                free_board_list.getboardlistlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                    Callback<Board_List_Model> {
                                    override fun onResponse(
                                        call: Call<Board_List_Model>,
                                        response: Response<Board_List_Model>
                                    ) {


                                        var free_length = response.body()?.list?.size
                                        if(free_length !=null) {
                                            for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                                free_board_list_array.add(
                                                    Board_class(
                                                        response.body()?.list?.get(i)?.category,
                                                        response.body()?.list?.get(i)?.title,
                                                        response.body()?.list?.get(i)?.format_date.toString(),
                                                        response.body()?.list?.get(i)?.reaction_count.toString(),
                                                        response.body()?.list?.get(i)?.comment.toString(),
                                                        response.body()?.list?.get(i)?.num
                                                    )
                                                )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                            }

                                            var free_board_adapter = RecycleAdapter(free_board_list_array)
                                            binding.reboard.apply{
                                                adapter = free_board_adapter
                                                layoutManager = LinearLayoutManager(this@BoardActivity)
                                                addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                            }
                                            binding.reboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                                    super.onScrolled(recyclerView, dx, dy)
                                                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                                    val visibleItemCount = layoutManager.childCount
                                                    val totalItemCount = layoutManager.itemCount
                                                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                                                        // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                        // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                        page = page + 1
                                                        free_board_list.getboardlistlInfo("$board_type","$page","",title,accessToken)?.enqueue(object :
                                                            Callback<Board_List_Model> {
                                                            override fun onResponse(
                                                                call: Call<Board_List_Model>,
                                                                response: Response<Board_List_Model>
                                                            ) {

                                                                var free_length = response.body()?.list?.size
                                                                if(free_length !=null) {
                                                                    for (i in 0..free_length - 1) {
                                                                        free_board_list_array.add(
                                                                            Board_class(
                                                                                response.body()?.list?.get(i)?.category,
                                                                                response.body()?.list?.get(i)?.title,
                                                                                response.body()?.list?.get(i)?.format_date.toString(),
                                                                                response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                                response.body()?.list?.get(i)?.comment.toString(),
                                                                                response.body()?.list?.get(i)?.num
                                                                            )
                                                                        )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                                    }
                                                                }
                                                                //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
                                                                free_board_adapter.notifyDataSetChanged()
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                            }
                                                            override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                                            }

                                                        })
                                                    }
                                                }
                                            })
                                            free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                override fun onClick(v: View, position: Int) {
                                                    var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                    intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                    token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                    startActivity(intent)
                                                }

                                            })

                                        }

                                        //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                    }
                                    override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                    }

                                })
                                // 키패드 내리기

                                true
                            }

                            false
                        }
                        binding.btnSearch.setOnClickListener {
                            free_board_list_array.clear()
                            binding.reboard.adapter?.notifyDataSetChanged()
                            var title = binding.searchBoard.text.toString()
                            free_board_list.getboardlistlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                Callback<Board_List_Model> {
                                override fun onResponse(
                                    call: Call<Board_List_Model>,
                                    response: Response<Board_List_Model>
                                ) {


                                    var free_length = response.body()?.list?.size
                                    if(free_length !=null) {
                                        for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                            free_board_list_array.add(
                                                Board_class(
                                                    response.body()?.list?.get(i)?.category,
                                                    response.body()?.list?.get(i)?.title,
                                                    response.body()?.list?.get(i)?.format_date.toString(),
                                                    response.body()?.list?.get(i)?.reaction_count.toString(),
                                                    response.body()?.list?.get(i)?.comment.toString(),
                                                    response.body()?.list?.get(i)?.num
                                                )
                                            )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                        }

                                        var free_board_adapter = RecycleAdapter(free_board_list_array)
                                        binding.reboard.apply{
                                            adapter = free_board_adapter
                                            layoutManager = LinearLayoutManager(this@BoardActivity)
                                            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                        }
                                        binding.reboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                                super.onScrolled(recyclerView, dx, dy)
                                                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                                val visibleItemCount = layoutManager.childCount
                                                val totalItemCount = layoutManager.itemCount
                                                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                                                    // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                    // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                    page = page + 1
                                                    free_board_list.getboardlistlInfo("$board_type","$page","",title,accessToken)?.enqueue(object :
                                                        Callback<Board_List_Model> {
                                                        override fun onResponse(
                                                            call: Call<Board_List_Model>,
                                                            response: Response<Board_List_Model>
                                                        ) {

                                                            var free_length = response.body()?.list?.size
                                                            if(free_length !=null) {
                                                                for (i in 0..free_length - 1) {
                                                                    free_board_list_array.add(
                                                                        Board_class(
                                                                            response.body()?.list?.get(i)?.category,
                                                                            response.body()?.list?.get(i)?.title,
                                                                            response.body()?.list?.get(i)?.format_date.toString(),
                                                                            response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                            response.body()?.list?.get(i)?.comment.toString(),
                                                                            response.body()?.list?.get(i)?.num
                                                                        )
                                                                    )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                                }
                                                            }
                                                            //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
                                                            free_board_adapter.notifyDataSetChanged()
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                        }
                                                        override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                                        }

                                                    })
                                                }
                                            }
                                        })
                                        free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                            override fun onClick(v: View, position: Int) {
                                                var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                startActivity(intent)
                                            }

                                        })

                                    }

                                    //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                }
                                override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                }

                            })
                        }
                    }
                    else if(board_type.toString().equals("match")){
                        binding.button.setOnClickListener{
                            val intent = Intent(this@BoardActivity, GoogleMaps::class.java)
                            intent.putExtra("type", "write") // 데이터 넣기
                            startActivity(intent)
                        }
                        binding.textView18.setText("모집 게시판")
                        binding.swipeRefreshLayout.setOnRefreshListener {
                            // 사용자가 당겨서 새로고침을 요청하면 이 코드 블록이 실행됩니다.
                            // 여기에서 원하는 작업(데이터 다시 로드 등)을 수행한 후 아래 코드로 새로고침 완료를 알립니다.
                            match_board_list.getboardlist_matchlInfo("$board_type","0","","",accessToken)?.enqueue(object :
                                Callback<Match_Board_List_Model> {
                                override fun onResponse(
                                    call: Call<Match_Board_List_Model>,
                                    response: Response<Match_Board_List_Model>
                                ) {
                                    free_board_list_array.clear()
                                    var free_length = response.body()?.list?.size
                                    if(free_length !=null) {
                                        for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val outputFormat = SimpleDateFormat("MM/dd", Locale.KOREA)
//                            val NowTime = System.currentTimeMillis()
//                            val format_date = outputFormat.format(NowTime)
//                            val localDate = LocalDate.parse(date_cu, DateTimeFormatter.ofPattern("MM/dd"))
//                            var dialog = AlertDialog.Builder(this@BoardActivity)
//                            dialog.setTitle("결과")
//                            dialog.setMessage(localDate.toString())
//                            dialog.show()
                                            free_board_list_array.add(
                                                Board_class(
                                                    response.body()?.list?.get(i)?.category,
                                                    response.body()?.list?.get(i)?.title,
                                                    response.body()?.list?.get(i)?.format_create_date.toString(),
                                                    response.body()?.list?.get(i)?.gender.toString(),
                                                    response.body()?.list?.get(i)?.users.toString(),
                                                    response.body()?.list?.get(i)?.num
                                                )
                                            )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                        }

                                        var free_board_adapter = MatchRecycleAdapter(free_board_list_array)
                                        binding.reboard.apply{
                                            adapter = free_board_adapter
                                            layoutManager = LinearLayoutManager(this@BoardActivity)
                                            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                        }
                                        binding.reboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                                super.onScrolled(recyclerView, dx, dy)
                                                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                                val visibleItemCount = layoutManager.childCount
                                                val totalItemCount = layoutManager.itemCount
                                                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                                                    // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                    // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                    page = page + 1

                                                    match_board_list.getboardlist_matchlInfo("$board_type",page.toString(),"","",accessToken)?.enqueue(object :
                                                        Callback<Match_Board_List_Model> {
                                                        override fun onResponse(
                                                            call: Call<Match_Board_List_Model>,
                                                            response: Response<Match_Board_List_Model>
                                                        ) {
                                                            var free_length = response.body()?.list?.size
                                                            if(free_length !=null) {
                                                                for (i in 0..free_length - 1) {
                                                                    free_board_list_array.add(
                                                                        Board_class(
                                                                            response.body()?.list?.get(i)?.category,
                                                                            response.body()?.list?.get(i)?.title,
                                                                            response.body()?.list?.get(i)?.format_create_date.toString(),
                                                                            response.body()?.list?.get(i)?.gender.toString(),
                                                                            response.body()?.list?.get(i)?.users.toString(),
                                                                            response.body()?.list?.get(i)?.num
                                                                        )
                                                                    )
                                                                }

                                                                free_board_adapter.notifyDataSetChanged()
                                                                free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                                    override fun onClick(v: View, position: Int) {
                                                                        var intent = Intent(this@BoardActivity, MapsActivity::class.java)
                                                                        intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                                        token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                                        startActivity(intent)
                                                                    }

                                                                })
                                                            }
                                                            //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                        }
                                                        override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                                        }

                                                    })
                                                }
                                            }
                                        })
                                        free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                            override fun onClick(v: View, position: Int) {
                                                var intent = Intent(this@BoardActivity, MapsActivity::class.java)
                                                intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                startActivity(intent)
                                            }

                                        })
                                    }
                                    //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                }
                                override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                }

                            })
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        match_board_list.getboardlist_matchlInfo("$board_type","0","","",accessToken)?.enqueue(object :
                            Callback<Match_Board_List_Model> {
                            override fun onResponse(
                                call: Call<Match_Board_List_Model>,
                                response: Response<Match_Board_List_Model>
                            ) {
                                var free_length = response.body()?.list?.size
                                if(free_length !=null) {
                                    for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val outputFormat = SimpleDateFormat("MM/dd", Locale.KOREA)
//                            val NowTime = System.currentTimeMillis()
//                            val format_date = outputFormat.format(NowTime)
//                            val localDate = LocalDate.parse(date_cu, DateTimeFormatter.ofPattern("MM/dd"))
//                            var dialog = AlertDialog.Builder(this@BoardActivity)
//                            dialog.setTitle("결과")
//                            dialog.setMessage(localDate.toString())
//                            dialog.show()
                                        free_board_list_array.add(
                                            Board_class(
                                                response.body()?.list?.get(i)?.category,
                                                response.body()?.list?.get(i)?.title,
                                                response.body()?.list?.get(i)?.format_create_date.toString(),
                                                response.body()?.list?.get(i)?.gender.toString(),
                                                response.body()?.list?.get(i)?.users.toString(),
                                                response.body()?.list?.get(i)?.num
                                            )
                                        )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                    }

                                    var free_board_adapter = MatchRecycleAdapter(free_board_list_array)
                                    binding.reboard.apply{
                                        adapter = free_board_adapter
                                        layoutManager = LinearLayoutManager(this@BoardActivity)
                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                    }
                                    binding.reboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                            super.onScrolled(recyclerView, dx, dy)
                                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                            val visibleItemCount = layoutManager.childCount
                                            val totalItemCount = layoutManager.itemCount
                                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                                                // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                page = page + 1

                                                match_board_list.getboardlist_matchlInfo("$board_type",page.toString(),"","",accessToken)?.enqueue(object :
                                                    Callback<Match_Board_List_Model> {
                                                    override fun onResponse(
                                                        call: Call<Match_Board_List_Model>,
                                                        response: Response<Match_Board_List_Model>
                                                    ) {
                                                        var free_length = response.body()?.list?.size
                                                        if(free_length !=null) {
                                                            for (i in 0..free_length - 1) {
                                                                free_board_list_array.add(
                                                                    Board_class(
                                                                        response.body()?.list?.get(i)?.category,
                                                                        response.body()?.list?.get(i)?.title,
                                                                        response.body()?.list?.get(i)?.format_create_date.toString(),
                                                                        response.body()?.list?.get(i)?.gender.toString(),
                                                                        response.body()?.list?.get(i)?.users.toString(),
                                                                        response.body()?.list?.get(i)?.num
                                                                    )
                                                                )
                                                            }

                                                            free_board_adapter.notifyDataSetChanged()
                                                            free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                                override fun onClick(v: View, position: Int) {
                                                                    var intent = Intent(this@BoardActivity, MapsActivity::class.java)
                                                                    intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                                    token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                                    startActivity(intent)
                                                                }

                                                            })
                                                        }
                                                        //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                    }
                                                    override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                                    }

                                                })
                                            }
                                        }
                                    })
                                    free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                        override fun onClick(v: View, position: Int) {
                                            var intent = Intent(this@BoardActivity, MapsActivity::class.java)
                                            intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                            token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                            startActivity(intent)
                                        }

                                    })
                                }
                                //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                            }
                            override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                            }

                        })
                        binding.searchBoard.setOnKeyListener{ view, keyCode, event ->
                            // Enter Key Action
                            if (event.action == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_ENTER
                            ) {
                                free_board_list_array.clear()
                                binding.reboard.adapter?.notifyDataSetChanged()
                                var title = binding.searchBoard.text.toString()
                                free_board_list.getboardlistlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                    Callback<Board_List_Model> {
                                    override fun onResponse(
                                        call: Call<Board_List_Model>,
                                        response: Response<Board_List_Model>
                                    ) {


                                        var free_length = response.body()?.list?.size
                                        if(free_length !=null) {
                                            for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                                free_board_list_array.add(
                                                    Board_class(
                                                        response.body()?.list?.get(i)?.category,
                                                        response.body()?.list?.get(i)?.title,
                                                        response.body()?.list?.get(i)?.format_date.toString(),
                                                        response.body()?.list?.get(i)?.reaction_count.toString(),
                                                        response.body()?.list?.get(i)?.comment.toString(),
                                                        response.body()?.list?.get(i)?.num
                                                    )
                                                )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                            }

                                            var free_board_adapter = MatchRecycleAdapter(free_board_list_array)
                                            binding.reboard.apply{
                                                adapter = free_board_adapter
                                                layoutManager = LinearLayoutManager(this@BoardActivity)
                                                addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                            }
                                            binding.reboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                                    super.onScrolled(recyclerView, dx, dy)
                                                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                                    val visibleItemCount = layoutManager.childCount
                                                    val totalItemCount = layoutManager.itemCount
                                                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                                                        // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                        // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                        page = page + 1

                                                        match_board_list.getboardlist_matchlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                                            Callback<Match_Board_List_Model> {
                                                            override fun onResponse(
                                                                call: Call<Match_Board_List_Model>,
                                                                response: Response<Match_Board_List_Model>
                                                            ) {
                                                                var free_length = response.body()?.list?.size
                                                                if(free_length !=null) {
                                                                    for (i in 0..free_length - 1) {
                                                                        free_board_list_array.add(
                                                                            Board_class(
                                                                                response.body()?.list?.get(i)?.category,
                                                                                response.body()?.list?.get(i)?.title,
                                                                                response.body()?.list?.get(i)?.format_create_date.toString(),
                                                                                response.body()?.list?.get(i)?.gender.toString(),
                                                                                response.body()?.list?.get(i)?.users.toString(),
                                                                                response.body()?.list?.get(i)?.num
                                                                            )
                                                                        )
                                                                    }

                                                                    free_board_adapter.notifyDataSetChanged()
                                                                    free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                                        override fun onClick(v: View, position: Int) {
                                                                            var intent = Intent(this@BoardActivity, MapsActivity::class.java)
                                                                            intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                                            token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                                            startActivity(intent)
                                                                        }

                                                                    })
                                                                }
                                                                //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                            }
                                                            override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                                            }

                                                        })
                                                    }
                                                }
                                            })
                                            free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                override fun onClick(v: View, position: Int) {
                                                    var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                    intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                    token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                    startActivity(intent)
                                                }

                                            })

                                        }

                                        //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                    }
                                    override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                    }

                                })
                                // 키패드 내리기

                                true
                            }

                            false
                        }
                        binding.btnSearch.setOnClickListener {
                            free_board_list_array.clear()
                            binding.reboard.adapter?.notifyDataSetChanged()
                            var title = binding.searchBoard.text.toString()
                            match_board_list.getboardlist_matchlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                Callback<Match_Board_List_Model> {
                                override fun onResponse(
                                    call: Call<Match_Board_List_Model>,
                                    response: Response<Match_Board_List_Model>
                                ) {


                                    var free_length = response.body()?.list?.size
                                    if(free_length !=null) {
                                        for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                            free_board_list_array.add(
                                                Board_class(
                                                    response.body()?.list?.get(i)?.category,
                                                    response.body()?.list?.get(i)?.title,
                                                    response.body()?.list?.get(i)?.format_create_date.toString(),
                                                    response.body()?.list?.get(i)?.gender.toString(),
                                                    response.body()?.list?.get(i)?.users.toString(),
                                                    response.body()?.list?.get(i)?.num
                                                )
                                            )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                        }

                                        var free_board_adapter = RecycleAdapter(free_board_list_array)
                                        binding.reboard.apply{
                                            adapter = free_board_adapter
                                            layoutManager = LinearLayoutManager(this@BoardActivity)
                                            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                        }
                                        binding.reboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                                super.onScrolled(recyclerView, dx, dy)
                                                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                                val visibleItemCount = layoutManager.childCount
                                                val totalItemCount = layoutManager.itemCount
                                                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                                                    // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                    // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                    page = page + 1

                                                    match_board_list.getboardlist_matchlInfo("$board_type","$page","",title,accessToken)?.enqueue(object :
                                                        Callback<Match_Board_List_Model> {
                                                        override fun onResponse(
                                                            call: Call<Match_Board_List_Model>,
                                                            response: Response<Match_Board_List_Model>
                                                        ) {
                                                            var free_length = response.body()?.list?.size
                                                            if(free_length !=null) {
                                                                for (i in 0..free_length - 1) {
                                                                    free_board_list_array.add(
                                                                        Board_class(
                                                                            response.body()?.list?.get(i)?.category,
                                                                            response.body()?.list?.get(i)?.title,
                                                                            response.body()?.list?.get(i)?.format_create_date.toString(),
                                                                            response.body()?.list?.get(i)?.gender.toString(),
                                                                            response.body()?.list?.get(i)?.users.toString(),
                                                                            response.body()?.list?.get(i)?.num
                                                                        )
                                                                    )
                                                                }

                                                                free_board_adapter.notifyDataSetChanged()
                                                                free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                                    override fun onClick(v: View, position: Int) {
                                                                        var intent = Intent(this@BoardActivity, MapsActivity::class.java)
                                                                        intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                                        token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                                        startActivity(intent)
                                                                    }

                                                                })
                                                            }
                                                            //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                        }
                                                        override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                                        }

                                                    })
                                                }
                                            }
                                        })
                                        free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                            override fun onClick(v: View, position: Int) {
                                                var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                startActivity(intent)
                                                finish()
                                            }

                                        })

                                    }

                                    //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                }
                                override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                }

                            })
                        }
                    }

                    var dialog = AlertDialog.Builder(this@BoardActivity)

//        dialog.setTitle("결과")
//        dialog.setMessage(formattedDate)
//        dialog.show()

                    var profileList  = arrayListOf<Profile>()
                    profileList.add(Profile("홍길동", 20))
                    profileList.add(Profile("홍길동", 21))
                    profileList.add(Profile("홍길동", 22))
                    profileList.add(Profile("홍길동", 23))
                    profileList.add(Profile("홍길동", 24))
                    profileList.add(Profile("홍길동", 25))
                    profileList.add(Profile("홍길동", 26))
                    profileList.add(Profile("홍길동", 27))
                    profileList.add(Profile("홍길동", 28))
                    profileList.add(Profile("홍길동", 29))
                    profileList.add(Profile("홍길동", 30))
                    profileList.add(Profile("홍길동", 31))
                    profileList.add(Profile("홍길동", 32))
                    profileList.add(Profile("홍길동", 33))
                    profileList.add(Profile("홍길동", 34))
                    profileList.add(Profile("홍길동", 35))
                    profileList.add(Profile("홍길동", 36))
                    profileList.add(Profile("홍길동", 37))

//        val adapter = RecycleAdapter(board_list_array)
//        binding.reboard.layoutManager = LinearLayoutManager(this)
//        binding.reboard.adapter = adapter




//        match_board_list.getboardlistlInfo("match","0","","","$accessToken")?.enqueue(object :
//            Callback<Board_List_Model> {
//            override fun onResponse(
//                call: Call<Board_List_Model>,
//                response: Response<Board_List_Model>
//            ) {
//                var match_length = response.body()?.list?.size
//                if (match_length != null) {
//                    if(match_length<3){
//                        for(i in 0..match_length-1){
//                            match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
//                                response.body()?.list?.get(i)?.title,
//                                response.body()?.list?.get(i)?.create_date,
//                                response.body()?.list?.get(i)?.reaction.toString(),
//                                response.body()?.list?.get(i)?.comment.toString()))
//                        }
//                    }
//                    else{
//                        for(i in 0..2){
//                            match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
//                                response.body()?.list?.get(i)?.title,
//                                response.body()?.list?.get(i)?.create_date,
//                                response.body()?.list?.get(i)?.reaction.toString(),
//                                response.body()?.list?.get(i)?.comment.toString()))
//                        }
//                    }
//                }
//
////                GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body().toString()) //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(requireContext(), match_board_list_array)
//                match_board_list.adapter = list_adapter
////                var text = response.body()
////                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
//            }
//            override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {
//
//            }
//
//        })
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
                                var free_board_list_array =arrayListOf<Board_class>()
                                var match_board_list_array =arrayListOf<Board_class>()
                                var board_list_array =arrayListOf<Board_class>(
                                )
                                if(board_type.equals("free")){
                                    binding.button.setOnClickListener{
                                        val intent = Intent(context, MakefreeboardActivity::class.java)
                                        intent.putExtra("type", "write") // 데이터 넣기
                                        startActivity(intent)
                                    }
                                    binding.textView18.setText("자유 게시판")
                                    free_board_list.getboardlistlInfo("$board_type","0","","",accessToken)?.enqueue(object :
                                        Callback<Board_List_Model> {
                                        override fun onResponse(
                                            call: Call<Board_List_Model>,
                                            response: Response<Board_List_Model>
                                        ) {


                                            var free_length = response.body()?.list?.size
                                            if(free_length !=null) {
                                                for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                                    free_board_list_array.add(
                                                        Board_class(
                                                            response.body()?.list?.get(i)?.category,
                                                            response.body()?.list?.get(i)?.title,
                                                            response.body()?.list?.get(i)?.format_date.toString(),
                                                            response.body()?.list?.get(i)?.reaction_count.toString(),
                                                            response.body()?.list?.get(i)?.comment.toString(),
                                                            response.body()?.list?.get(i)?.num
                                                        )
                                                    )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                }

                                                var free_board_adapter = RecycleAdapter(free_board_list_array)
                                                binding.reboard.apply{
                                                    adapter = free_board_adapter
                                                    layoutManager = LinearLayoutManager(this@BoardActivity)
                                                    addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                }
                                                free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                    override fun onClick(v: View, position: Int) {
                                                        var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                        intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                        token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                        startActivity(intent)
                                                    }

                                                })

                                            }



                                            //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                        }
                                        override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                        }

                                    })
                                    binding.swipeRefreshLayout.setOnRefreshListener {
                                        // 사용자가 당겨서 새로고침을 요청하면 이 코드 블록이 실행됩니다.
                                        // 여기에서 원하는 작업(데이터 다시 로드 등)을 수행한 후 아래 코드로 새로고침 완료를 알립니다.
                                        free_board_list.getboardlistlInfo("$board_type","0","","",accessToken)?.enqueue(object :
                                            Callback<Board_List_Model> {
                                            override fun onResponse(
                                                call: Call<Board_List_Model>,
                                                response: Response<Board_List_Model>
                                            ) {

                                                free_board_list_array.clear()
                                                var free_length = response.body()?.list?.size
                                                if(free_length !=null) {
                                                    for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                                        free_board_list_array.add(
                                                            Board_class(
                                                                response.body()?.list?.get(i)?.category,
                                                                response.body()?.list?.get(i)?.title,
                                                                response.body()?.list?.get(i)?.format_date.toString(),
                                                                response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                response.body()?.list?.get(i)?.comment.toString(),
                                                                response.body()?.list?.get(i)?.num
                                                            )
                                                        )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                    }

                                                    var free_board_adapter = RecycleAdapter(free_board_list_array)
                                                    binding.reboard.apply{
                                                        adapter = free_board_adapter
                                                        layoutManager = LinearLayoutManager(this@BoardActivity)
                                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                    }
                                                    free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                        override fun onClick(v: View, position: Int) {
                                                            var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                            intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                            token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                            startActivity(intent)
                                                        }

                                                    })

                                                }



                                                //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                            }
                                            override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                            }

                                        })
                                        binding.swipeRefreshLayout.isRefreshing = false
                                    }
                                    binding.searchBoard.setOnKeyListener{ view, keyCode, event ->
                                        // Enter Key Action
                                        if (event.action == KeyEvent.ACTION_DOWN
                                            && keyCode == KeyEvent.KEYCODE_ENTER
                                        ) {
                                            free_board_list_array.clear()
                                            binding.reboard.adapter?.notifyDataSetChanged()
                                            var title = binding.searchBoard.text.toString()
                                            free_board_list.getboardlistlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                                Callback<Board_List_Model> {
                                                override fun onResponse(
                                                    call: Call<Board_List_Model>,
                                                    response: Response<Board_List_Model>
                                                ) {


                                                    var free_length = response.body()?.list?.size
                                                    if(free_length !=null) {
                                                        for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                                            free_board_list_array.add(
                                                                Board_class(
                                                                    response.body()?.list?.get(i)?.category,
                                                                    response.body()?.list?.get(i)?.title,
                                                                    response.body()?.list?.get(i)?.format_date.toString(),
                                                                    response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                    response.body()?.list?.get(i)?.comment.toString(),
                                                                    response.body()?.list?.get(i)?.num
                                                                )
                                                            )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                        }

                                                        var free_board_adapter = RecycleAdapter(free_board_list_array)
                                                        binding.reboard.apply{
                                                            adapter = free_board_adapter
                                                            layoutManager = LinearLayoutManager(this@BoardActivity)
                                                            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                        }
                                                        free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                            override fun onClick(v: View, position: Int) {
                                                                var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                                intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                                token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                                startActivity(intent)
                                                            }

                                                        })

                                                    }

                                                    //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                }
                                                override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                                }

                                            })
                                            // 키패드 내리기

                                            true
                                        }

                                        false
                                    }
                                    binding.btnSearch.setOnClickListener {
                                        free_board_list_array.clear()
                                        binding.reboard.adapter?.notifyDataSetChanged()
                                        var title = binding.searchBoard.text.toString()
                                        free_board_list.getboardlistlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                            Callback<Board_List_Model> {
                                            override fun onResponse(
                                                call: Call<Board_List_Model>,
                                                response: Response<Board_List_Model>
                                            ) {


                                                var free_length = response.body()?.list?.size
                                                if(free_length !=null) {
                                                    for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                                        free_board_list_array.add(
                                                            Board_class(
                                                                response.body()?.list?.get(i)?.category,
                                                                response.body()?.list?.get(i)?.title,
                                                                response.body()?.list?.get(i)?.format_date.toString(),
                                                                response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                response.body()?.list?.get(i)?.comment.toString(),
                                                                response.body()?.list?.get(i)?.num
                                                            )
                                                        )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                    }

                                                    var free_board_adapter = RecycleAdapter(free_board_list_array)
                                                    binding.reboard.apply{
                                                        adapter = free_board_adapter
                                                        layoutManager = LinearLayoutManager(this@BoardActivity)
                                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                    }
                                                    free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                        override fun onClick(v: View, position: Int) {
                                                            var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                            intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                            token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                            startActivity(intent)
                                                        }

                                                    })

                                                }

                                                //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                            }
                                            override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                            }

                                        })
                                    }
                                }
                                else if(board_type.toString().equals("match")){
                                    binding.button.setOnClickListener{
                                        val intent = Intent(context, GoogleMaps::class.java)
                                        intent.putExtra("type", "write") // 데이터 넣기
                                        startActivity(intent)
                                    }
                                    binding.textView18.setText("모집 게시판")
                                    binding.swipeRefreshLayout.setOnRefreshListener {
                                        // 사용자가 당겨서 새로고침을 요청하면 이 코드 블록이 실행됩니다.
                                        // 여기에서 원하는 작업(데이터 다시 로드 등)을 수행한 후 아래 코드로 새로고침 완료를 알립니다.
                                        match_board_list.getboardlist_matchlInfo("$board_type","0","","",accessToken)?.enqueue(object :
                                            Callback<Match_Board_List_Model> {
                                            override fun onResponse(
                                                call: Call<Match_Board_List_Model>,
                                                response: Response<Match_Board_List_Model>
                                            ) {
                                                free_board_list_array.clear()
                                                var free_length = response.body()?.list?.size
                                                if(free_length !=null) {
                                                    for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val outputFormat = SimpleDateFormat("MM/dd", Locale.KOREA)
//                            val NowTime = System.currentTimeMillis()
//                            val format_date = outputFormat.format(NowTime)
//                            val localDate = LocalDate.parse(date_cu, DateTimeFormatter.ofPattern("MM/dd"))
//                            var dialog = AlertDialog.Builder(this@BoardActivity)
//                            dialog.setTitle("결과")
//                            dialog.setMessage(localDate.toString())
//                            dialog.show()
                                                        free_board_list_array.add(
                                                            Board_class(
                                                                response.body()?.list?.get(i)?.category,
                                                                response.body()?.list?.get(i)?.title,
                                                                response.body()?.list?.get(i)?.format_create_date.toString(),
                                                                response.body()?.list?.get(i)?.gender.toString(),
                                                                response.body()?.list?.get(i)?.users.toString(),
                                                                response.body()?.list?.get(i)?.num
                                                            )
                                                        )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                    }

                                                    var free_board_adapter = MatchRecycleAdapter(free_board_list_array)
                                                    binding.reboard.apply{
                                                        adapter = free_board_adapter
                                                        layoutManager = LinearLayoutManager(this@BoardActivity)
                                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                    }
                                                    free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                        override fun onClick(v: View, position: Int) {
                                                            var intent = Intent(this@BoardActivity, MapsActivity::class.java)
                                                            intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                            token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                            startActivity(intent)
                                                        }

                                                    })
                                                }
                                                //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                            }
                                            override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                            }

                                        })
                                        binding.swipeRefreshLayout.isRefreshing = false
                                    }
                                    match_board_list.getboardlist_matchlInfo("$board_type","0","","",accessToken)?.enqueue(object :
                                        Callback<Match_Board_List_Model> {
                                        override fun onResponse(
                                            call: Call<Match_Board_List_Model>,
                                            response: Response<Match_Board_List_Model>
                                        ) {
                                            var free_length = response.body()?.list?.size
                                            if(free_length !=null) {
                                                for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val outputFormat = SimpleDateFormat("MM/dd", Locale.KOREA)
//                            val NowTime = System.currentTimeMillis()
//                            val format_date = outputFormat.format(NowTime)
//                            val localDate = LocalDate.parse(date_cu, DateTimeFormatter.ofPattern("MM/dd"))
//                            var dialog = AlertDialog.Builder(this@BoardActivity)
//                            dialog.setTitle("결과")
//                            dialog.setMessage(localDate.toString())
//                            dialog.show()
                                                    free_board_list_array.add(
                                                        Board_class(
                                                            response.body()?.list?.get(i)?.category,
                                                            response.body()?.list?.get(i)?.title,
                                                            response.body()?.list?.get(i)?.format_create_date.toString(),
                                                            response.body()?.list?.get(i)?.gender.toString(),
                                                            response.body()?.list?.get(i)?.users.toString(),
                                                            response.body()?.list?.get(i)?.num
                                                        )
                                                    )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                }

                                                var free_board_adapter = MatchRecycleAdapter(free_board_list_array)
                                                binding.reboard.apply{
                                                    adapter = free_board_adapter
                                                    layoutManager = LinearLayoutManager(this@BoardActivity)
                                                    addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                }
                                                free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                    override fun onClick(v: View, position: Int) {
                                                        var intent = Intent(this@BoardActivity, MapsActivity::class.java)
                                                        intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                        token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                        startActivity(intent)
                                                    }

                                                })
                                            }
                                            //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                        }
                                        override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                        }

                                    })
                                    binding.searchBoard.setOnKeyListener{ view, keyCode, event ->
                                        // Enter Key Action
                                        if (event.action == KeyEvent.ACTION_DOWN
                                            && keyCode == KeyEvent.KEYCODE_ENTER
                                        ) {
                                            free_board_list_array.clear()
                                            binding.reboard.adapter?.notifyDataSetChanged()
                                            var title = binding.searchBoard.text.toString()
                                            free_board_list.getboardlistlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                                Callback<Board_List_Model> {
                                                override fun onResponse(
                                                    call: Call<Board_List_Model>,
                                                    response: Response<Board_List_Model>
                                                ) {


                                                    var free_length = response.body()?.list?.size
                                                    if(free_length !=null) {
                                                        for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                                            free_board_list_array.add(
                                                                Board_class(
                                                                    response.body()?.list?.get(i)?.category,
                                                                    response.body()?.list?.get(i)?.title,
                                                                    response.body()?.list?.get(i)?.format_date.toString(),
                                                                    response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                    response.body()?.list?.get(i)?.comment.toString(),
                                                                    response.body()?.list?.get(i)?.num
                                                                )
                                                            )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                        }

                                                        var free_board_adapter = MatchRecycleAdapter(free_board_list_array)
                                                        binding.reboard.apply{
                                                            adapter = free_board_adapter
                                                            layoutManager = LinearLayoutManager(this@BoardActivity)
                                                            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                        }
                                                        free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                            override fun onClick(v: View, position: Int) {
                                                                var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                                intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                                token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                                startActivity(intent)
                                                            }

                                                        })

                                                    }

                                                    //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                }
                                                override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                                }

                                            })
                                            // 키패드 내리기

                                            true
                                        }

                                        false
                                    }
                                    binding.btnSearch.setOnClickListener {
                                        free_board_list_array.clear()
                                        binding.reboard.adapter?.notifyDataSetChanged()
                                        var title = binding.searchBoard.text.toString()
                                        match_board_list.getboardlist_matchlInfo("$board_type","0","",title,accessToken)?.enqueue(object :
                                            Callback<Match_Board_List_Model> {
                                            override fun onResponse(
                                                call: Call<Match_Board_List_Model>,
                                                response: Response<Match_Board_List_Model>
                                            ) {


                                                var free_length = response.body()?.list?.size
                                                if(free_length !=null) {
                                                    for (i in 0..free_length - 1) {

//                            dialog.setTitle("결과")
//                            dialog.setMessage(formattedDate)
//                            dialog.show()
//                            var date_cu = response.body()?.list?.get(i)?.format_date.toString()
//                            val inputFormat = SimpleDateFormat("mm dd hh:mm", Locale.KOREA)
//                            val outputFormat = SimpleDateFormat("mm dd", Locale.KOREA)
//                            val date = inputFormat.parse(date_cu)
//                            val format_date = outputFormat.format(date)
                                                        free_board_list_array.add(
                                                            Board_class(
                                                                response.body()?.list?.get(i)?.category,
                                                                response.body()?.list?.get(i)?.title,
                                                                response.body()?.list?.get(i)?.format_create_date.toString(),
                                                                response.body()?.list?.get(i)?.gender.toString(),
                                                                response.body()?.list?.get(i)?.users.toString(),
                                                                response.body()?.list?.get(i)?.num
                                                            )
                                                        )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                    }

                                                    var free_board_adapter = RecycleAdapter(free_board_list_array)
                                                    binding.reboard.apply{
                                                        adapter = free_board_adapter
                                                        layoutManager = LinearLayoutManager(this@BoardActivity)
                                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                    }
                                                    free_board_adapter.setItemClickListener(object : RecycleAdapter.OnItemClickListener{
                                                        override fun onClick(v: View, position: Int) {
                                                            var intent = Intent(this@BoardActivity, ReadFreeBoardActivity::class.java)
                                                            intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                            token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                            startActivity(intent)
                                                            finish()
                                                        }

                                                    })

                                                }

                                                //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(this@BoardActivity, free_board_list_array)
//                binding.boardRecycle.adapter = list_adapter
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                            }
                                            override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                            }

                                        })
                                    }
                                }

                                var dialog = AlertDialog.Builder(this@BoardActivity)

//        dialog.setTitle("결과")
//        dialog.setMessage(formattedDate)
//        dialog.show()

                                var profileList  = arrayListOf<Profile>()
                                profileList.add(Profile("홍길동", 20))
                                profileList.add(Profile("홍길동", 21))
                                profileList.add(Profile("홍길동", 22))
                                profileList.add(Profile("홍길동", 23))
                                profileList.add(Profile("홍길동", 24))
                                profileList.add(Profile("홍길동", 25))
                                profileList.add(Profile("홍길동", 26))
                                profileList.add(Profile("홍길동", 27))
                                profileList.add(Profile("홍길동", 28))
                                profileList.add(Profile("홍길동", 29))
                                profileList.add(Profile("홍길동", 30))
                                profileList.add(Profile("홍길동", 31))
                                profileList.add(Profile("홍길동", 32))
                                profileList.add(Profile("홍길동", 33))
                                profileList.add(Profile("홍길동", 34))
                                profileList.add(Profile("홍길동", 35))
                                profileList.add(Profile("홍길동", 36))
                                profileList.add(Profile("홍길동", 37))

//        val adapter = RecycleAdapter(board_list_array)
//        binding.reboard.layoutManager = LinearLayoutManager(this)
//        binding.reboard.adapter = adapter




//        match_board_list.getboardlistlInfo("match","0","","","$accessToken")?.enqueue(object :
//            Callback<Board_List_Model> {
//            override fun onResponse(
//                call: Call<Board_List_Model>,
//                response: Response<Board_List_Model>
//            ) {
//                var match_length = response.body()?.list?.size
//                if (match_length != null) {
//                    if(match_length<3){
//                        for(i in 0..match_length-1){
//                            match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
//                                response.body()?.list?.get(i)?.title,
//                                response.body()?.list?.get(i)?.create_date,
//                                response.body()?.list?.get(i)?.reaction.toString(),
//                                response.body()?.list?.get(i)?.comment.toString()))
//                        }
//                    }
//                    else{
//                        for(i in 0..2){
//                            match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
//                                response.body()?.list?.get(i)?.title,
//                                response.body()?.list?.get(i)?.create_date,
//                                response.body()?.list?.get(i)?.reaction.toString(),
//                                response.body()?.list?.get(i)?.comment.toString()))
//                        }
//                    }
//                }
//
////                GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body().toString()) //리스폰스 값 메일로 확인
//                var list_adapter = ListAdapter(requireContext(), match_board_list_array)
//                match_board_list.adapter = list_adapter
////                var text = response.body()
////                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
//            }
//            override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {
//
//            }
//
//        })
                            }
                            else {
                                var intent = Intent(context, StartActivity::class.java)
                                Toast.makeText(context, refresh_response.body()?.massage.toString(),Toast.LENGTH_SHORT).show()
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
        finish()
    }
}

class DividerItemDecoration(context: Context, orientation: Int) : RecyclerView.ItemDecoration() {
    private val divider: Drawable? // 구분선을 그리는 Drawable

    init {
        val styledAttributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        divider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()
        if (orientation == LinearLayoutManager.VERTICAL) {
            // 수직 리사이클러뷰의 경우
            // 아래와 같이 설정할 수 있습니다.
            // ...
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            // 수평 리사이클러뷰의 경우
            // 아래와 같이 설정할 수 있습니다.
            // ...
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + (divider?.intrinsicHeight ?: 0)

            divider?.setBounds(left, top, right, bottom)
            divider?.draw(c)
        }

    }
    fun setOrientation(orientation: Int) {
        // 여기서 orientation을 설정하면 됩니다.
        // orientation 값에 따라 구분선을 그리는 방향을 다르게 설정할 수 있습니다.
    }
}