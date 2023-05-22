package com.earth.angel.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.earth.angel.R;
import com.earth.angel.chat.CustomHelloMessage;
import com.earth.libbase.util.OnGroupListener;
import com.earth.libbase.util.leftslidelib.ItemSlideHelper;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMMessage;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.RemindViewHolder>
        implements ItemSlideHelper.Callback {

    private Context context;
    private List<V2TIMMessage> mDatas = new ArrayList<V2TIMMessage>();

    private RecyclerView mRecyclerView;
    private OnGroupListener onGroupListener;

    public NotificationListAdapter(Context context, List<V2TIMMessage> mDatas, OnGroupListener onGroupListener) {
        this.context = context;
        this.mDatas = mDatas;
        this.onGroupListener = onGroupListener;
    }

    @Override
    public RemindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
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
        V2TIMMessage mV2TIMMessage=mDatas.get(position);

        V2TIMCustomElem elem = mV2TIMMessage.getCustomElem();
        // 自定义的json数据，需要解析成bean实例
        CustomHelloMessage data = null;
  /*      try {
            data = new Gson().fromJson(new String(elem.getData()), CustomHelloMessage.class);

            if(data.getCustomCellType().equals("3")||data.getCustomCellType().equals("4")||data.getCustomCellType().equals("6")){
                holder.llWebInfo.setVisibility(View.GONE);
                holder.llBtn.setVisibility(View.GONE);
                CustomUserEntity customUserEntity=null;
                customUserEntity= new Gson().fromJson(data.getUserInfo(), CustomUserEntity.class);
                Glide.with(context)
                        .load(customUserEntity.getUserFace())
                        .placeholder(R.mipmap.head_common)
                        .into(holder.ivHead);
                holder.tvTex.setText(data.getText());

            }else if(data.getCustomCellType().equals("5")) {
                String s=new String(elem.getData());
                MessageWebEntity mCustomMessage=new Gson().fromJson(s,MessageWebEntity.class);
                WebUserInfoEntity userInfo=mCustomMessage.getData();
                holder.llWebInfo.setVisibility(View.VISIBLE);
                holder.tvShareNameContent.setText(userInfo.getName());
                holder.tvContactInfoContent.setText(userInfo.getContactInfo());
                holder.tvMessageContent.setText(userInfo.getMessage());
                holder.llBtn.setVisibility(View.GONE);
                holder.llBtn.setVisibility(View.GONE);
                holder.tvTex.setText(data.getText());
                Glide.with(context)
                        .load("")
                        .placeholder(R.mipmap.head_common)
                        .into(holder.ivHead);
            }else if(data.getCustomCellType().equals("7")) {
                holder.llWebInfo.setVisibility(View.GONE);
                holder.llBtn.setVisibility(View.GONE);
                CustomUserEntity customUserEntity=null;
                customUserEntity= new Gson().fromJson(data.getUserInfo(), CustomUserEntity.class);
                Glide.with(context)
                        .load(customUserEntity.getUserFace())
                        .placeholder(R.mipmap.head_common)
                        .into(holder.ivHead);
                holder.tvTex.setText(data.getText());

                holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGroupListener.onDeleteClick(position);
                    }
                });
            }else {
                holder.llWebInfo.setVisibility(View.GONE);

            }


        } catch (Exception e) {
            //   DemoLog.w(TAG, "invalid json: " + new String(elem.getData()) + " " + e.getMessage());
        }*/
        Long time=mDatas.get(position).getTimestamp();
        if(time==0){
            holder.tvTime.setText("");
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String timeStr = sdf.format(new Date(time*1000));// 时间戳转换成时间
            holder.tvTime.setText(timeStr);
        }
        holder.clItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Toast.makeText(context,""+ finalData.getLink(),Toast.LENGTH_SHORT).show();

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
        ImageView ivHead;
        TextView tvTex;
        TextView tvTime;

        TextView tvConfirm;
        TextView tvDelete;

        TextView delete;
        LinearLayout llItem;
        LinearLayout llBtn;
        ConstraintLayout clItem;
        LinearLayout llWebInfo;

        TextView tvShareNameContent;
        TextView tvContactInfoContent;
        TextView tvMessageContent;
        public RemindViewHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.ivHead);
            tvTex = itemView.findViewById(R.id.tv);
            tvTime = itemView.findViewById(R.id.tvTime);

            tvConfirm = itemView.findViewById(R.id.tvConfirm);
            tvDelete = itemView.findViewById(R.id.tvDelete);

            delete = itemView.findViewById(R.id.tv_msg_remind_delete);
            llItem=itemView.findViewById(R.id.ll_msg_remind_main);
            llBtn=itemView.findViewById(R.id.llBtn);
            clItem=itemView.findViewById(R.id.clItem);

            llWebInfo=itemView.findViewById(R.id.llWebInfo);
            tvShareNameContent=itemView.findViewById(R.id.tvShareNameContent);
            tvContactInfoContent=itemView.findViewById(R.id.tvContactInfoContent);
            tvMessageContent=itemView.findViewById(R.id.tvMessageContent);

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

