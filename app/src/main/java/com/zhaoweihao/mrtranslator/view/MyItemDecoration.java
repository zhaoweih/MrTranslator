package com.zhaoweihao.mrtranslator.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Zhaoweihao on 17/7/6.
 */

public class MyItemDecoration extends RecyclerView.ItemDecoration{

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, 1);
    }
}
