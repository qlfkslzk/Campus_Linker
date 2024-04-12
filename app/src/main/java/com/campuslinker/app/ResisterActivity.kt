package com.campuslinker.app

import java.util.Random
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.campuslinker.app.check_parameter.Get_check_APIS
import com.campuslinker.app.check_parameter.check_data
import com.campuslinker.app.databinding.ResisterBinding
import com.campuslinker.app.login.*
import com.campuslinker.app.resister.*
import com.campuslinker.app.school.Get_school_APIS
import com.campuslinker.app.school.school_data
import java.util.*
import java.util.regex.Pattern

class ResisterActivity : AppCompatActivity() {

    var check_id = false
    var check_psw = false
    var check_studentnum = false
    var check_gender = false
    var check_campus = false
    var check_duplication_email = false
    var check_email = false
    var random_number : Int?=null

    var email: String? = null
    var filterkorean = InputFilter { source, start, end, dest, dstart, dend -> //한국어 필터
        /*
            [요약 설명]
            1. 정규식 패턴 ^[a-z] : 영어 소문자 허용
            2. 정규식 패턴 ^[A-Z] : 영어 대문자 허용
            3. 정규식 패턴 ^[ㄱ-ㅣ가-힣] : 한글 허용
            4. 정규식 패턴 ^[0-9] : 숫자 허용
            5. 정규식 패턴 ^[ ] or ^[\\s] : 공백 허용
        */
        val ps = Pattern.compile("^[ㄱ-ㅣ가-힣]+$")
        if (!ps.matcher(source).matches()) {
            ""
        } else source
    }
    var filterenglish = InputFilter { source, start, end, dest, dstart, dend ->  //영어 필터
        /*
            [요약 설명]
            1. 정규식 패턴 ^[a-z] : 영어 소문자 허용
            2. 정규식 패턴 ^[A-Z] : 영어 대문자 허용
            3. 정규식 패턴 ^[ㄱ-ㅣ가-힣] : 한글 허용
            4. 정규식 패턴 ^[0-9] : 숫자 허용
            5. 정규식 패턴 ^[ ] or ^[\\s] : 공백 허용
        */
        val ps = Pattern.compile("^[a-zA-Z]+$")
        if (!ps.matcher(source).matches()) {
            ""
        } else source
    }

    private val scan_psw = object : TextWatcher { //패스워드 텍스트 변경 감지
        override fun afterTextChanged(s: Editable?) {

            if(s != null && !s.toString().equals("")){}//만약 다른 에딧텍스트 변경할 사항이 있을 시 이 안에서 실행
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }


    lateinit var binding : ResisterBinding
    val api_resister = Resister_APIS.create()
    val api_school = Get_school_APIS.create()
    val api_check = Get_check_APIS.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ResisterBinding.inflate(layoutInflater)


        var name: String? = null
        var id: String? = null
        var psw1: String? = null
        var psw2: String? = null
        var gender: String? = null
        var student_nember: Int? = null
        var campus: String? = null
        val view = binding.root

        binding.editEmail.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                email =binding.editEmail.text.toString()+"@"+(campus.toString()) //이메일 주소 세팅
                api_check.getcheckInfo("email", "$email").enqueue(object : Callback<check_data> {
                    override fun onResponse(
                        call: Call<check_data>,
                        response: Response<check_data>
                    ) {
                        getDataType(response.body())
                        binding.emailCheck.setText(response.body()?.message.toString())
                        if(response.body()?.result==200){ //이메일 증복 체크
                            check_duplication_email = true
                        }
                        else{
                            check_duplication_email = false}
                    }

                    override fun onFailure(call: Call<check_data>, t: Throwable) {

                    }

                })
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        binding.editPass.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(binding.editPass.text.toString().length<8)
                {
                    binding.passCheck.setText("8자리 이상으로 설정해 주세요")
                    check_psw = false
                }
                else{
                    if(binding.editPass.text.toString().equals(binding.editPass2.text.toString()))
                    {
                        binding.passCheck.setText("일치")
                        check_psw = true
                    }
                    else{
                        binding.passCheck.setText("일치하지 않음")
                        check_psw = false
                    }
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        binding.editPass2.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(binding.editPass.text.toString().length<8)
                {
                    binding.passCheck.setText("8자리 이상으로 설정해 주세요")
                    check_psw = false
                }
                else{
                    if(binding.editPass.text.toString().equals(binding.editPass2.text.toString()))
                    {
                        binding.passCheck.setText("일치")
                        check_psw = true
                    }
                    else{
                        binding.passCheck.setText("일치하지 않음")
                        check_psw = false
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        binding.editId.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(binding.editId.text.toString().length<6){
                    binding.idCheck.setText("6자리 미만")
                    check_id = false
                }
                else{
                    var idch = binding.editId.text.toString()
                    api_check.getcheckInfo("id", "$idch").enqueue(object : Callback<check_data> {
                        override fun onResponse(
                            call: Call<check_data>,
                            response: Response<check_data>
                        ) {
                            getDataType(response.body())
                            binding.emailCheck.setText(response.body()?.message.toString())
                            if (response.body()?.result == 200) { //아이디 증복 체크
                                binding.idCheck.setText(response.body()?.message)
                                check_id = true
                            } else {
                                binding.idCheck.setText(response.body()?.message)
                                check_id = false
                            }

                        }

                        override fun onFailure(call: Call<check_data>, t: Throwable) {

                        }
                    })

                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        var i :String?=null
        binding.id3.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(binding.id3.text.toString().length==2){

                    i = binding.id3.text.toString()
                    if((binding.id3.text.toString().toInt()<10)or(binding.id3.text.toString().toInt()>23))
                    {
                        binding.studentNumcheck.setText("잘못된 년도입니다")
                        check_studentnum = false
                    }
                    else{
                        check_studentnum = true
                    }
                }
                else if(binding.id3.text.toString().length>2){
                    binding.id3.setText(i)
                    check_studentnum = false
                }
                else{
                    check_studentnum = false
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}})

        binding.btnCheckEmail.setOnClickListener{
            random_number = Random().nextInt(899999)+100000
            if(check_duplication_email == true){
                GMailSender().sendEmail(email.toString(), random_number.toString())
                equal_email()
                var dialog = AlertDialog.Builder(this@ResisterActivity)
                dialog.setTitle("성공")
                dialog.setMessage("인증번호가 발급되었습니다")
                dialog.show()
            }
            else{
                var dialog = AlertDialog.Builder(this@ResisterActivity)
                dialog.setTitle("실패")
                dialog.setMessage("잘못된 이메일 주소입니다")
                dialog.show()
            }

        }

        binding.id2.addTextChangedListener(object : TextWatcher{ //이메일 인증 진행
            override fun afterTextChanged(s: Editable?) {
                equal_email()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        setContentView(view)
        var sex = findViewById<Spinner>(R.id.sexual) //성별 스피너
        var campus_spinner = findViewById<Spinner>(R.id.campus) //대학교 목록 스피너
        sex.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.sexual,
            android.R.layout.simple_spinner_item
        ) //성별 스피너 구현
        campus_spinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.campus_name,
            android.R.layout.simple_spinner_item
        )

        sex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(sex.selectedItemId.toString().equals("성별")){
                    check_gender = false
                }
                else
                    check_gender = true

            }override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        campus_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(campus_spinner.selectedItemId.toString().equals("학교")){
                    check_campus = false
                }
                else
                    check_campus = true
                campus = campus_spinner.selectedItem.toString()
                api_school.getSchoolInfo(campus.toString())?.enqueue(object : Callback<school_data> {
                    override fun onResponse(
                        call: Call<school_data>,
                        response: Response<school_data>
                    ) {
                        getDataType(response.body())
                        if(binding.campus.selectedItem.toString().equals("학교")){
                            binding.textView11.setText("학교 이메일 주소")
                        }
                        else{
                            binding.textView11.setText(response.body()?.school_list?.get(0)?.mail.toString())
                            campus = response.body()?.school_list?.get(0)?.mail.toString()
                        }

                    }
                    override fun onFailure(call: Call<school_data>, t: Throwable) {

                    }

                })
            }override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.btnResist.setOnClickListener {
            if(check_id == true and check_gender == true and check_campus == true and check_duplication_email == true and check_email == true and check_psw == true and check_studentnum == true){
                resister()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1500)
            }
            else{
                var dialog = AlertDialog.Builder(this@ResisterActivity)
                dialog.setTitle("실패")
                dialog.setMessage("회원정보를 확인해주세요")
                dialog.show()
            }
        }
    }

    fun equal_email() { //이메일 인증 진행 함수
        if(binding.id2.text.toString().equals(random_number.toString())){
            binding.emailnumberCheck.setText("일치")
            check_email = true
        }
        else{
            binding.emailnumberCheck.setText("일치하지 않음")
            check_email = false
        }
    }
    fun getDataType(data: Any?): String { //데이터 타입 출력 함수
        // [리턴 변수 선언]
        var returnData = ""
        // [로직 처리 실시]
        try {

            if (data != null){
                returnData = data.javaClass.simpleName.uppercase()
            }
            else {
                returnData = "NULL"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        // [로그 출력 실시]
        //*
        Log.d("type","================================================")
        Log.d("type","-----------------------------------------")
        Log.d("type","[INPUT :: " + data.toString() + "]")
        Log.d("type","-----------------------------------------")
        Log.d("type","[RETURN :: $returnData]")
        Log.d("type","================================================")
        // */
        // [리턴 반환 실시]
        return returnData
    }
    /** 문자열필터(EditText Filter) */

   //앱 비밀번호 : rugh hfaw eggj rhnf
    private fun putExtra(extraEmail: String, arrayOf: Array<String>) {

    }
    fun resister(){
        var num : Int?=null
        num = binding.id3.text.toString().toInt()
//        val data_resister = Resister_Post(
//            binding.editName.text.toString(),
//            binding.campus.selectedItem.toString(),
//            num,
//            binding.sexual.selectedItem.toString(),
//            binding.editId.text.toString(),
//            binding.editPass.text.toString(),
//            email.toString()
//        )
        val data_resister = Resister_Post(
            binding.editName.text.toString(),
            binding.campus.selectedItem.toString(),
            num,
            binding.sexual.selectedItem.toString(),
            binding.editId.text.toString(),
            binding.editPass.text.toString(),
            email.toString()
        )
        if(check_id == true and check_gender == true and check_campus == true and check_duplication_email == true and check_email == true and check_psw == true and check_studentnum == true){
            api_resister.post_users(data_resister).enqueue(object : Callback<Resister_Post_result> {
                override fun onResponse(
                    call: Call<Resister_Post_result>,
                    response: Response<Resister_Post_result>
                ) {
                    if(response.body()?.result==200) {
                        var dialog = AlertDialog.Builder(this@ResisterActivity)
                        dialog.setTitle("성공")
                        dialog.setMessage("회원가입에 성공하였습니다")
                        dialog.show()
                    }
                    else{
//                        var dialog = AlertDialog.Builder(this@ResisterActivity)
//                        dialog.setTitle("실패")
//                        dialog.setMessage("response.body = "+ response.body()?.toString() +"check_id = " + check_id.toString()+" check_gender = "+
//                                check_gender.toString()+ "  check_campus = "+check_campus.toString()+
//                                "  check_duplication_email = "+check_duplication_email.toString()+
//                                " check_email = "+check_email.toString()+" check_studentnum = "
//                                +check_studentnum.toString()+ "check_psw = "+check_psw.toString()
//                        +"name = " +binding.editName.text.toString()+"campus = "+binding.campus.selectedItem.toString()
//                        +"student_nem = "+ binding.id3.text.toString().toInt()+"gender = "+binding.sexual.selectedItem.toString()
//                        + "id = "+ binding.editId.text.toString()+"psw = "+binding.editPass.text.toString()+"email = "+email.toString()
//                        +"data+resister= "+data_resister
//                        )
//                        dialog.show()
                        var dialog = AlertDialog.Builder(this@ResisterActivity)
                        dialog.setTitle("실패")
                        dialog.setMessage("회원정보를 확인해주세요")
                        dialog.show()
                    }
                }

                override fun onFailure(call: Call<Resister_Post_result>, t: Throwable) {

                }

        })

        }
        else{
//            var dialog = AlertDialog.Builder(this@ResisterActivity)
//            dialog.setTitle("실패")
//            dialog.setMessage("check_id = " + check_id.toString()+" check_gender = "+
//                    check_gender.toString()+ "  check_campus = "+check_campus.toString()+
//                    "  check_duplication_email = "+check_duplication_email.toString()+
//                    " check_email = "+check_email.toString()+" check_studentnum = "
//                    +check_studentnum.toString())
//            dialog.show()
            var dialog = AlertDialog.Builder(this@ResisterActivity)
            dialog.setTitle("실패")
            dialog.setMessage("회원정보를 확인해주세요")
            dialog.show()
        }

    }


}


