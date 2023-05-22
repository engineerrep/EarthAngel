package com.earth.libbase.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

public class BaseNoScrollRecyclerView extends RecyclerView {

    public BaseNoScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseNoScrollRecyclerView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
    }

    public BaseNoScrollRecyclerView(Context context) {
        super(context);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
