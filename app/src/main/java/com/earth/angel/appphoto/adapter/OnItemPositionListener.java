package com.earth.angel.appphoto.adapter;

public interface  OnItemPositionListener {
    //交换
    void onItemSwap(int from, int target);

    //滑动
    void onItemMoved(int position);
}
