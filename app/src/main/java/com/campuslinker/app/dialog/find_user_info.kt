package com.campuslinker.app.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import com.campuslinker.app.R

class find_user_info (context: Context)
{
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog_user_info()
    {
        dialog.setContentView(R.layout.find_user_info_dialog)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

//        val edit_name = dialog.findViewById<EditText>(R.id.)

        dialog.findViewById<Button>(R.id.find_id).setOnClickListener {
            onClickListener.onClicked_id()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.find_pwd).setOnClickListener {
            onClickListener.onClicked_pwd()
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener
    {
        fun onClicked_id(
        )
        fun onClicked_pwd()
    }

}