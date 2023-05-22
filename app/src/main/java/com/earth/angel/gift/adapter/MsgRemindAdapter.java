package com.earth.angel.gift.adapter;

import static com.earth.libbase.Config.ADMIN_NAME;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.earth.angel.R;
import com.earth.libbase.entity.HouseListEntity;
import com.earth.libbase.util.OnGroupListener;
import com.earth.libbase.util.leftslidelib.ItemSlideHelper;
import com.nine.palace.headpic.NinePalaceImageView;
import com.nine.palace.headpic.NinePalaceImageViewAdapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MsgRemindAdapter extends RecyclerView.Adapter<MsgRemindAdapter.RemindViewHolder>
        implements ItemSlideHelper.Callback {

    private Context context;
    private List<HouseListEntity> mDatas = new ArrayList<HouseListEntity>();

    private RecyclerView mRecyclerView;
    private OnGroupListener onGroupListener;

    public MsgRemindAdapter(Context context, List<HouseListEntity> mDatas, OnGroupListener onGroupListener) {
        this.context = context;
        this.mDatas = mDatas;
        this.onGroupListener = onGroupListener;
    }

    @Override
    public RemindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newgroup, parent, false);
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
        NinePalaceImageViewAdapter adapter = new NinePalaceImageViewAdapter<String>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String s) {
                Glide.with(context).load(s).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);
            }
        };
        HouseListEntity mHouseListEntity= mDatas.get(position);
        if (!mHouseListEntity.getImgUrl().isEmpty()) {
            if (mHouseListEntity.getType() == 0) {
                holder.ivHeader.setVisibility(View.VISIBLE);
                holder.ivHead.setVisibility(View.INVISIBLE);
                holder.ivHeader.setAdapter(adapter);
                holder.ivHeader.setImagesData(mHouseListEntity.getImgUrl());
            } else {
                holder.ivHeader.setVisibility(View.INVISIBLE);
                holder.ivHead.setVisibility(View.VISIBLE);
                if (!mHouseListEntity.getImgUrl().get(0).isEmpty()) {
                    Glide.with(context)
                            .load(mHouseListEntity.getImgUrl().get(0))
                            .placeholder(R.mipmap.head_common)
                            .into(holder.ivHead);
                }else {
                    if(mHouseListEntity.getNumber().equals(ADMIN_NAME)){
                        Glide.with(context)
                                .load("https://yt-bullet.oss-cn-shenzhen.aliyuncs.com/earthangel.png")
                                .placeholder(R.mipmap.head_common)
                                .into(holder.ivHead);
                    }
                }

            }
        }
        holder.tvName.setText(mHouseListEntity.getName());
        Long time = mHouseListEntity.getReleaseTime();
        Long mMessageTime = mHouseListEntity.getMessageTime();

        if (mHouseListEntity.getType() == 1) {
            if (null != mHouseListEntity.getLastMesssage()) {
                holder.tvmessage.setText(mHouseListEntity.getLastMesssage());
            } else {
                holder.tvmessage.setText("");
            }
            if(mMessageTime==0){
                holder.tvTime.setText("");
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String timeStr = sdf.format(new Date(mMessageTime*1000));// 时间戳转换成时间
                holder.tvTime.setText(timeStr+"");
            }

        } else if(mHouseListEntity.getType() == 0){
            if (mHouseListEntity.getMembers() == 0) {
                holder.tvmessage.setText("");
            } else if (mHouseListEntity.getMembers() == 1) {
                holder.tvmessage.setText("[ " +mHouseListEntity.getMembers()+ " member ]");
            } else {
                holder.tvmessage.setText("[ " +mHouseListEntity.getMembers()+ " members ]");
            }
            if (time == 0) {
                holder.tvTime.setText("");
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String timeStr = sdf.format(new Date(mHouseListEntity.getReleaseTime()));// 时间戳转换成时间
                holder.tvTime.setText(timeStr);
            }
        }else {
            holder.tvTime.setText("");
        }

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGroupListener.onItemClick(position);
            }
        });
        //删除监听
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGroupListener.onDeleteClick(position);
            }
        });
        if (mHouseListEntity.getMessage() == 0) {
            holder.message.setVisibility(View.GONE);
        } else {
            holder.message.setVisibility(View.VISIBLE);
        }
        holder.message.setText(mHouseListEntity.getMessage() + "");
   /*     //消息主体监听，这里我是让他添加一条数据，替换成你需要的操作即可
        holder.llMsgRemindMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("okhttp" ,"点击位置 position = "+position);
            }
        });
        //标记已读监听
        holder.tvMsgRemindCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isCheck = mDatas.get(position).isChecked();
                if (isCheck) {
                    mDatas.get(position).setChecked(false);
                } else {
                    mDatas.get(position).setChecked(true);
                }
                notifyItemChanged(position);
            }
        });
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
        NinePalaceImageView ivHeader;
        ImageView ivHead;
        TextView tvmessage;
        TextView tvName;
        TextView tvNum;
        TextView tvTime;
        LinearLayout llItem;
        TextView delete;
        TextView message;

        public RemindViewHolder(View itemView) {
            super(itemView);
            ivHeader = itemView.findViewById(R.id.iv);
            ivHead = itemView.findViewById(R.id.ivHead);
            tvmessage = itemView.findViewById(R.id.tvmessage);
            tvName = itemView.findViewById(R.id.tv);
            tvNum = itemView.findViewById(R.id.tvNum);
            tvTime = itemView.findViewById(R.id.tvJoined);
            llItem = itemView.findViewById(R.id.ll_msg_remind_main);
            delete = itemView.findViewById(R.id.tv_msg_remind_delete);
            message = itemView.findViewById(R.id.tvMessageNum);
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

