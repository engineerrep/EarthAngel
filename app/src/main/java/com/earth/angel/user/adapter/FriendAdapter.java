package com.earth.angel.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.earth.angel.R;
import com.earth.angel.user.side.SortModel;
import com.earth.libbase.view.ShapedImageView;

import java.util.List;

/**
 * @author: xp
 * @date: 2017/7/19
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<SortModel> mData;
    private Context mContext;
    private FriendClickListener mFriendClickListener;

    public FriendAdapter(Context context, List<SortModel> data,FriendClickListener mFriendClickListener) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
        this.mFriendClickListener=mFriendClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvTag = (TextView) view.findViewById(R.id.tag);
        viewHolder.tvName = (TextView) view.findViewById(R.id.name);
        viewHolder.iv = (ShapedImageView) view.findViewById(R.id.iv);
        viewHolder.item = (LinearLayout) view.findViewById(R.id.item);
        viewHolder.ivSelect = (ImageView) view.findViewById(R.id.ivSelect);
        viewHolder.id = (TextView) view.findViewById(R.id.numId);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText(mData.get(position).getLetters());
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

        }

        holder.tvName.setText(this.mData.get(position).getName());
        holder.id.setText("ID:"+this.mData.get(position).getUserid());

        if(this.mData.get(position).getImgUrl().isEmpty()){
            Glide.with(mContext)
                    .load(R.mipmap.head_common)
                    // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                    .into(  holder.iv);
        }else {
            Glide.with(mContext)
                    .load(this.mData.get(position).getImgUrl())
                    .placeholder(R.mipmap.head_common)
                    // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                    .into(  holder.iv);
        }
        SortModel bean=this.mData.get(position);
        holder.ivSelect.setVisibility(View.INVISIBLE);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFriendClickListener.onItemClick(bean);
                //Toast.makeText(mContext, mData.get(position).getName(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //**************************************************************

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvName,id;
        LinearLayout item;
        ShapedImageView iv;
        ImageView ivSelect;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 提供给Activity刷新数据
     * @param list
     */
    public void updateList(List<SortModel> list){
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return mData.get(position).getLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }
    public void update(){

    }
}
