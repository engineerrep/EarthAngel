package com.earth.libbase.base;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


        if (parent.getChildLayoutPosition(view) %2 !=0) {
            //奇数
            outRect.right = space;
        }else {
            //偶数
            outRect.left = space;

        }
    }


}


