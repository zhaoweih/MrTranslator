package com.zhaoweihao.mrtranslator.utils

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by Zhaoweihao on 2017/4/13.
 */
object HttpUtil {
    fun sendOkHttpRequest(address: String?, callback: Callback?) {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(address)
                .build()
        client.newCall(request).enqueue(callback)
    }
}