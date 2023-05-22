package com.earth.libmine.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class UserOffItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public UserOffItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


        if (parent.getChildLayoutPosition(view) %2 !=0) {
            //奇数
            outRect.left = space;
            outRect.right = space/2;
        }else {
            //偶数
            outRect.left = space/2;
            outRect.right=space;
        }
    }


}


