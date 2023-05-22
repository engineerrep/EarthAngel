package com.earth.angel.view;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;


public class EncyclopediaAutoScrollView extends ScrollView {
    private boolean scrolledToTop = true;
    private boolean scrolledToBottom = false;
    private int paddingTop = 0;
    private int tePpaddingTop = 0;

    private final int MSG_SCROLL = 10;
    private final int MSG_SCROLL_Loop = 11;
    private final int MSG_SCROLL_GO = 12;

    //是否能滑动
    private boolean scrollAble = true;
    //是否自动滚动
    private boolean autoToScroll = true;
    //是否循环滚动
    private boolean scrollLoop = false;
    //多少秒后开始滚动，默认5秒
    private int fistTimeScroll = 5000;
    //多少毫秒滚动一个像素点
    private int scrollRate = 20;

    private boolean start = true;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SCROLL:
                    if (scrollAble && autoToScroll) {

                        if (start) {
                            //发生偏移
                            scrollTo(0, paddingTop);
                            paddingTop += 4;
                            Log.e("ScrollView","paddingTop"+paddingTop);

                            mHandler.removeMessages(MSG_SCROLL);
                            mHandler.sendEmptyMessageDelayed(MSG_SCROLL, scrollRate);
                        }
                    }
                    break;
                case MSG_SCROLL_Loop:
                    paddingTop = 0;
                    autoToScroll = true;
                    mHandler.sendEmptyMessageDelayed(MSG_SCROLL, fistTimeScroll);
                    break;
                case MSG_SCROLL_GO:
                    paddingTop=getScrollY();
                    mHandler.sendEmptyMessageDelayed(MSG_SCROLL, fistTimeScroll);
                    start=true;
                    break;

                default:
                    break;

            }

        }
    };

    public EncyclopediaAutoScrollView(Context context) {
        this(context, null);
    }

    public EncyclopediaAutoScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EncyclopediaAutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EncyclopediaAutoScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }


    private ISmartScrollChangedListener mSmartScrollChangedListener;

    /**
     * 定义监听接口
     */
    public interface ISmartScrollChangedListener {
        //滑动到底部
        void onScrolledToBottom();

        //滑动到顶部
        void onScrolledToTop();

    }


    /**
     * 设置滑动到顶部或底部的监听
     *
     * @param smartScrollChangedListener
     */
    public void setScanScrollChangedListener(ISmartScrollChangedListener smartScrollChangedListener) {
        mSmartScrollChangedListener = smartScrollChangedListener;
    }

    /**
     * ScrollView内的视图进行滑动时的回调方法，据说是API 9后都是调用这个方法，但是我测试过并不准确
     */
    @Override
    protected void onOverScrolled(int scrollXaxis, int scrollYaxis, boolean clampedXaxis, boolean clampedYaxis) {
        super.onOverScrolled(scrollXaxis, scrollYaxis, clampedXaxis, clampedYaxis);
        Log.e("ScrollView","scrollYaxis"+scrollYaxis+"clampedYaxis"+clampedYaxis);

        if (scrollYaxis == 0) {
            scrolledToTop = clampedYaxis;
            scrolledToBottom = false;
        } else {
            scrolledToTop = false;
            scrolledToBottom = clampedYaxis;//系统回调告诉你什么时候滑动到底部
        }

        notifyScrollChangedListeners();
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        View view = getChildAt(0);
        int height = view.getMeasuredHeight();
        Log.e("ScrollView","view.getMeasuredHeight()"+height);
        Log.e("ScrollView","getMeasuredHeight()"+getMeasuredHeight());
        Log.e("ScrollView","getScrollY()"+getScrollY());

        height-= (getMeasuredHeight() + getScrollY());
        Log.e("ScrollView","height"+height);

        if (height <= 10){
            start=false;
            mHandler.removeMessages(MSG_SCROLL);
        }



    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                start=false;
                break;
            case MotionEvent.ACTION_UP:

                mHandler.sendEmptyMessageDelayed(MSG_SCROLL_GO, 1000);

                break;

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断是否滑到底部
     */
    private void notifyScrollChangedListeners() {
        if (scrolledToTop) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToTop();
            }
        } else if (scrolledToBottom) {
            mHandler.removeMessages(MSG_SCROLL);
            if (!scrollLoop) {
                scrollAble = false;
            }
            if (scrollLoop) {
                mHandler.sendEmptyMessageDelayed(MSG_SCROLL_Loop, fistTimeScroll);
            }
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToBottom();
            }
        }
    }



    /**
     * 设置是否可以滚动
     *
     * @param autoToScroll
     */
    public void setAutoToScroll(boolean autoToScroll) {
        this.autoToScroll = autoToScroll;
    }

    /**
     * 设置开始滚动的时间
     *
     * @param fistTimeScroll
     */
    public void setFistTimeScroll(int fistTimeScroll) {
        this.fistTimeScroll = fistTimeScroll;
        mHandler.removeMessages(MSG_SCROLL);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLL, fistTimeScroll);
    }

    /**
     * 设置滚动的速度
     *
     * @param scrollRate
     */
    public void setScrollRate(int scrollRate) {
        this.scrollRate = scrollRate;
    }

    /**
     * 设置是否循环滚动
     *
     * @param scrollLoop
     */
    public void setScrollLoop(boolean scrollLoop) {

        this.scrollLoop = scrollLoop;

    }

    public void startScroll() {

        this.start = true;

        mHandler.sendEmptyMessageDelayed(MSG_SCROLL_GO, 1000);

    }

    public void stopScroll() {

        start=false;
        mHandler.removeMessages(MSG_SCROLL);

    }
    public void destroyScroll() {
        mHandler.removeMessages(MSG_SCROLL);

        scrollAble=false;
        autoToScroll=false;
        start=false;

    }
    public Boolean getScroll() {
        return this.start;
    }
}
