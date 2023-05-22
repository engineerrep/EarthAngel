package com.earth.libbase.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class AndroidBottomSoftBar {
    private View mViewObserved;//被监听的视图
    private int usableHeightPrevious;//视图变化前的可用高度
    private ViewGroup.LayoutParams frameLayoutParams;

    private AndroidBottomSoftBar(View viewObserving, final Activity activity) {
        mViewObserved = viewObserving;
        //给View添加全局的布局监听器
        mViewObserved.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                resetLayoutByUsableHeight(activity);
            }
        });
        frameLayoutParams = mViewObserved.getLayoutParams();
    }


    /**
     * 关联要监听的视图
     */
    public static void assistActivity(View viewObserving, Activity activity) {
        new AndroidBottomSoftBar(viewObserving, activity);
    }

    private void resetLayoutByUsableHeight(Activity activity) {

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        // 计算视图可视高度
       /* Rect r = new Rect();
        mViewObserved.getWindowVisibleDisplayFrame(r);
        int virtualKeyHeight = 40;
        int usableHeightNow = (r.bottom - r.top)
                + CommonUtils.dip2px(WDApplication.getInstance().getApplicationContext(), virtualKeyHeight);*/

//        int usableHeightNow = height - getHasVirtualKey(activity);
        int usableHeightNow = height;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity.getWindow().setNavigationBarColor(Color.RED);
//            activity.getWindow().setNavigationBarColor(Color.parseColor("#1bb5d7"));
//        }

        //比较布局变化前后的View的可用高度
        if (usableHeightNow != usableHeightPrevious) {
            //如果两次高度不一致
            //将当前的View的可用高度设置成View的实际高度
//            frameLayoutParams.height = usableHeightNow;
            mViewObserved.setPadding(0, 0, 0, getHasVirtualKey(activity));
            mViewObserved.requestLayout();//请求重新布局
            usableHeightPrevious = usableHeightNow;
        }
    }


    /**
     * dpi 通过反射，获取包含虚拟键的整体屏幕高度
     * height 获取屏幕尺寸，但是不包括虚拟功能高度
     *
     * @return
     */
    public static int getHasVirtualKey(Activity activity) {
        int dpi = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }


        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        return dpi - height;
    }

}
