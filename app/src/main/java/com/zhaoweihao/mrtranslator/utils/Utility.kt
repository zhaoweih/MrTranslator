package com.zhaoweihao.mrtranslator.utils

import com.google.gson.Gson
import com.zhaoweihao.mrtranslator.gson.Translate

/**
 * Created by Zhaoweihao on 2017/4/13.
 */
object Utility {
    fun handleTranslateResponse(response: String?): Translate {
        val gson = Gson()
        return gson.fromJson(response, Translate::class.java)
    }
}