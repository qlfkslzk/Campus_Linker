package com.campuslinker.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.RingtoneManager
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseService"
    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")
        super.onNewToken(token)

        // 토큰 값을 따로 저장해둔다.
        val pref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("token", token).apply()
        editor.commit()

        Log.i("로그: ", "성공적으로 토큰을 저장함")
    }
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "From: " + message.from)
        Log.d(TAG, "FCM Message Received: ${message.data}")
        Log.d(TAG, "FCM message.notification Received: ${message.notification}")
//        if (message.data.isNotEmpty()) {
//            val title = message.data["title"]
//            val body = message.data["body"]
//            Log.d(TAG, "Title: $title")
//            Log.d(TAG, "Body: $body")
//
//            sendNotification(title,body)
//            // 앱이 실행 중일 때 처리 로직 추가
//
//        } else {
//            Log.e(TAG, "알림 데이터가 비어 있습니다.")
//        }
//        message.notification?.let {
//            Log.d("FCM", "Message Notification Body: ${it.title}")
//            //알림 메세지 _ 포그라운드에서도 알림 받은 것 처럼 받은 정보를 가지고 notification 구현하기.
//            sendNotification(it.title, it.body)
//        }

        //데이터 메세지의 경우.
        if(message.data.isNotEmpty()) {
            Log.d("FCM", "Message Notification Body: ${message.data}")
            sendDataMessage(message.data)
        }
    }

    private fun sendNotification(title: String?, body: String?) {
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        val intent = Intent(this, ChattingActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // 사용자 정의 알림 채널 ID
        val channelId = "Campus_Linker_Alarm_Id"
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 사용자 정의 알림 채널 생성
            val channel = NotificationChannel(
                channelId,
                "Campus_Linker_Alarm",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(uniId, notificationBuilder.build())
    }
private fun createNotificationChannel(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        channel.enableLights(true)
        channel.enableVibration(true)

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }
}

    companion object {
        private const val CHANNEL_NAME = "Campus_Linker_Alarm"
        private const val CHANNEL_ID = "Campus_Linker_Alarm_Id"

    }
    private fun sendDataMessage(data: MutableMap<String, String>) {
        var x = data["purpose"]
        var y = data["purpose_num"]
        val notificationKey = "$x:$y"

        var title = data["title"].toString()
        var text = data["body"].toString()
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()
        var purpose = data["purpose_title"].toString()

// 작은 글씨를 추가할 SpannableString을 생성합니다.
        val middleText = data["purpose"].toString()
        Log.d("data", data.toString() )
        var intent = Intent(this, StartActivity::class.java)
        if(data["purpose"].toString().equals("free")){
            intent.putExtra("board_num", data["purpose_num"].toString())
            token_management.prefs.setString("board_num", data["purpose_num"].toString())
            intent = Intent(this, ReadFreeBoardActivity::class.java)
            Log.d("intent", "ReadFreeBoardActivity")
        }
        else if (data["purpose"].toString().equals("match")){
            intent.putExtra("board_num", data["purpose_num"].toString())
            token_management.prefs.setString("board_num", data["purpose_num"].toString())
            intent = Intent(this, MapsActivity::class.java)
            Log.d("intent", "MapsActivity")}
        else if(data["purpose"].toString().equals("room")){
            token_management.prefs.setString("chatting_room_num", data["purpose_num"].toString())
            var x = token_management.prefs.getString("chatting_room_num","기본값")
            intent = Intent(this, ChattingActivity::class.java)
            intent.putExtra("chatting_room_num", data["purpose_num"].toString())
            Log.d("data[\"purpose_num\"]", data["purpose_num"].toString())
            Log.d("chatting_room_num", x.toString())
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        if(data["purpose"].toString().equals("room")){
            purpose = purpose + "(채팅)"
            val spannableTitle = SpannableString("$title\n$purpose")
            val purposeStartIndex = title.length + 1
            val purposeEndIndex = purposeStartIndex + purpose.split("(").get(0).length

// 작은 글씨를 추가할 부분의 텍스트에 스타일을 적용합니다.
            spannableTitle.setSpan(StyleSpan(Typeface.ITALIC), title.length, spannableTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableTitle.setSpan(ForegroundColorSpan(Color.rgb(120,127,246)), purposeStartIndex, purposeEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val channelId = "Campus_Linker_Alarm_Id"
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.campus_linker_circle)
                .setContentTitle(spannableTitle)
                .setContentText(data["body"].toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 사용자 정의 알림 채널 생성
                val channel = NotificationChannel(
                    channelId,
                    "Campus_Linker_Alarm",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(uniId, notificationBuilder.build())
        }
        else{
            val spannableTitle = SpannableString("$title\n$purpose")
            val purposeStartIndex = title.length + 1
            val purposeEndIndex = purposeStartIndex + purpose.length

// 작은 글씨를 추가할 부분의 텍스트에 스타일을 적용합니다.
            spannableTitle.setSpan(StyleSpan(Typeface.ITALIC), title.length, spannableTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableTitle.setSpan(ForegroundColorSpan(Color.rgb(120,127,246)), purposeStartIndex, purposeEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val channelId = "Campus_Linker_Alarm_Id"
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.campus_linker_circle)
                .setContentTitle(spannableTitle)
                .setContentText(data["body"].toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 사용자 정의 알림 채널 생성
                val channel = NotificationChannel(
                    channelId,
                    "Campus_Linker_Alarm",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(uniId, notificationBuilder.build())
        }

// 중간 크기로 추가할 SpannableString을 생성합니다.






    }
    fun refreshFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 새로운 토큰을 얻음
                    val newToken: String? = task.result

                    // 새 토큰 처리 (예: 서버에서 업데이트)
                    if (newToken != null) {
                        println("새로운 FCM 토큰: $newToken")
                    }
                } else {
                    // 오류 처리
                    println("FCM 토큰 갱신 실패: ${task.exception}")
                }
            }
    }
}