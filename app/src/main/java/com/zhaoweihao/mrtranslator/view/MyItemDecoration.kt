package com.zhaoweihao.mrtranslator.view

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ItemDecoration
import android.view.View

/**
 * Created by Zhaoweihao on 17/7/6.
 */
class MyItemDecoration : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect[0, 0, 0] = 1
    }
}