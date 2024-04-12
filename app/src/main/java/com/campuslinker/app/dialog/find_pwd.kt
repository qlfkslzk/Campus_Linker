package com.campuslinker.app.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.campuslinker.app.R

class find_pwd(context: Context)
{
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog_pwd()
    {
        dialog.setContentView(R.layout.find_pwd_dialog)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val id = dialog.findViewById<EditText>(R.id.edit_id_find_pwd)
        val email = dialog.findViewById<EditText>(R.id.edit_email_find_pwd)
        val token = dialog.findViewById<EditText>(R.id.edit_token)
        val pwd1 = dialog.findViewById<EditText>(R.id.edit_pwd)
        val pwd2 = dialog.findViewById<EditText>(R.id.edid_match_pwd)

        dialog.findViewById<Button>(R.id.send_token).setOnClickListener {
            onClickListener.onClicked1(id.text.toString(),email.text.toString())
        }
        dialog.findViewById<Button>(R.id.change_pwd).setOnClickListener {
            onClickListener.onClicked2(id.text.toString(),token.text.toString(),pwd1.text.toString(),pwd2.text.toString())
            if(pwd1.text.toString().equals(pwd2.text.toString())){
                dialog.dismiss()
            }

        }
    }

    interface OnDialogClickListener
    {
        fun onClicked1(id:String,email: String)
        fun onClicked2(id:String,token:String,pwd1: String,pwd2:String)
    }

}