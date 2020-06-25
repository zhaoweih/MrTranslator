package com.zhaoweihao.mrtranslator.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.preference.PreferenceManager
import com.zhaoweihao.mrtranslator.service.ClipboardService

/**
 * Created by Zhao Weihao on 2017/8/15.
 */
class StartClipboardServiceAtBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (sharedPreferences.getBoolean("tap_translate", false)) {
                val serviceIntent = Intent(context, ClipboardService::class.java)
                context.startService(serviceIntent)
            }
        }
    }
}