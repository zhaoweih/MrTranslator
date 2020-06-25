package com.zhaoweihao.mrtranslator.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.widget.Toast
import com.zhaoweihao.mrtranslator.R
import com.zhaoweihao.mrtranslator.constant.Constant
import com.zhaoweihao.mrtranslator.gson.Translate
import com.zhaoweihao.mrtranslator.service.ClipboardService
import com.zhaoweihao.mrtranslator.utils.HttpUtil.sendOkHttpRequest
import com.zhaoweihao.mrtranslator.utils.Utility.handleTranslateResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by ZhaoWeihao on 2017/8/15.
 * 点按翻译 服务
 */
class ClipboardService : Service() {
    private var manager: ClipboardManager? = null
    var handler: Handler? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        handler = Handler()
        manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager!!.addPrimaryClipChangedListener(listener)
    }

    private val listener = OnPrimaryClipChangedListener {
        if (manager!!.hasPrimaryClip()) {
            val data = manager!!.primaryClip
            handleClipData(data.getItemAt(0).text.toString())
        }
    }

    private fun handleClipData(clipData: String) {
        val url = Constant.YOUDAO_URL
        sendOkHttpRequest(url + clipData, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread(Runnable { Toast.makeText(applicationContext, R.string.translate_fail, Toast.LENGTH_SHORT).show() })
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body().string()
                val translate = handleTranslateResponse(responseData)
                runOnUiThread(Runnable {
                    if (translate.errorCode == 0) {
                        showTranslateInfo(translate)
                    } else if (translate.errorCode == 20) {
                        Toast.makeText(applicationContext, R.string.translate_overlong, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, R.string.translate_fail, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })
    }

    private fun showTranslateInfo(translate: Translate) {
        val result = translate.translation!![0]
        val mBuilder = NotificationCompat.Builder(this@ClipboardService)
                .setSmallIcon(R.drawable.small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(result)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setStyle(android.support.v4.app.NotificationCompat.BigTextStyle().bigText(result)) as NotificationCompat.Builder
        val shareIntent = Intent().setAction(Intent.ACTION_SEND).setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, result)
        val sharePi = PendingIntent.getActivity(this@ClipboardService, 0, shareIntent, 0)
        mBuilder.addAction(R.drawable.ic_share_white_24dp, getString(R.string.share), sharePi)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, mBuilder.build())
    }

    private fun runOnUiThread(runnable: Runnable) {
        handler!!.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (manager != null) {
            manager!!.removePrimaryClipChangedListener(listener)
        }
    }

    companion object {
        private val TAG = ClipboardService::class.java.simpleName
    }
}