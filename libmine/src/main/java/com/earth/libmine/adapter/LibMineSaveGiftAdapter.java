package com.earth.libmine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.earth.libbase.util.OnGroupListener;
import com.earth.libbase.util.leftslidelib.ItemSlideHelper;
import com.earth.libbase.entity.HouseListEntity;
import com.earth.libmine.R;


import java.util.ArrayList;
import java.util.List;


public class LibMineSaveGiftAdapter extends RecyclerView.Adapter<LibMineSaveGiftAdapter.RemindViewHolder>
        implements ItemSlideHelper.Callback {

    private Context context;
    private List<String> mDatas = new ArrayList<String>();

    private RecyclerView mRecyclerView;
    private OnGroupListener onGroupListener;

    public LibMineSaveGiftAdapter(Context context, List<String> mDatas, OnGroupListener onGroupListener) {
        this.context = context;
        this.mDatas = mDatas;
        this.onGroupListener = onGroupListener;
    }

    @Override
    public RemindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.libmine_itemsavegift, parent, false);
        return new RemindViewHolder(view);
    }

    /**
     * 将recyclerView绑定Slide事件
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnItemTouchListener(new ItemSlideHelper(mRecyclerView.getContext(), this));
    }

    @Override
    public void onBindViewHolder(final RemindViewHolder holder, final int position) {

        /**
         * -->特别注意，敲黑板了啊！！！在执行notify的时候，取position要取holder.getAdapterPosition()，
         * 消息被删除之后，他原来的position是final的，所以取到的值不准确，会报数组越界。
         */



    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 此方法用来计算水平方向移动的距离
     *
     * @param holder
     * @return
     */
    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof LinearLayout) {
            ViewGroup viewGroup = (ViewGroup) holder.itemView;
            //viewGroup包含3个控件，即消息主item、标记已读、删除，返回为标记已读宽度+删除宽度
            return viewGroup.getChildAt(1).getLayoutParams().width
                    + viewGroup.getChildAt(2).getLayoutParams().width;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder getChildViewHolder(View childView) {
        return mRecyclerView.getChildViewHolder(childView);
    }

    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }

    /**
     * 自定义的ViewHolder
     */
    public class RemindViewHolder extends RecyclerView.ViewHolder {


        public RemindViewHolder(View itemView) {
            super(itemView);

        }
    }

    /**
     * 删除单条数据
     *
     * @param position
     */
    public void removeData(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

}

