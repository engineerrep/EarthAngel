package com.earth.angel.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.earth.angel.R;

public class PopupWindowUtil {

    public static void showPopupWindow(Context context, View v,OnItemClick onItemClick){

        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_eco_gift, null);
       // Button button = (Button) contentView.findViewById(R.id.btn);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);

     /*   button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "onclick==action",
                        Toast.LENGTH_SHORT).show();
            }
        });*/

        //检测屏幕消失的事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                onItemClick.onItemClick();
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        //获取自身的长宽高
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = contentView.getMeasuredHeight();
        int popupWidth = contentView.getMeasuredWidth();

        //获取控件在屏幕上的位置，并赋值给location数组
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        //在控件上方显示
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 50, location[1] - popupHeight);

        // 如果要在控件下方显示，则使用这个方法
        //popupWindow.showAsDropDown(v);
    }

}
