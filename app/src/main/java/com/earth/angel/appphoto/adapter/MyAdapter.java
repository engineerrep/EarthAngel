package com.earth.angel.appphoto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.earth.angel.R;
import com.earth.libbase.entity.PhotoCutEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> implements OnItemPositionListener {
    private ArrayList<PhotoCutEntity> mDatas;
    private Context context;
    private MyAdapterListener myAdapterListener;

    public MyAdapter(Context context, ArrayList<PhotoCutEntity> mDatas,MyAdapterListener myAdapterListener) {
        this.context=context;
        this.mDatas=mDatas;
        this.myAdapterListener=myAdapterListener;
    }
    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_edit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyHolder holder, int position) {
        PhotoCutEntity mPhotoCutEntity=mDatas.get(position);
        if(mPhotoCutEntity.getSdPath().equals("")){
            holder.llEpty.setVisibility(View.VISIBLE);
            holder.iv_delete.setVisibility(View.GONE);
            Glide.with(context)
                    .load("")
                    .placeholder(R.drawable.shape_corner_edit)
                    .into( holder.drag);
        }else {
            holder.llEpty.setVisibility(View.GONE);
            holder.iv_delete.setVisibility(View.VISIBLE);
      /*      if(mPhotoCutEntity.getCheck()){
                Glide.with(context)
                        .load(mPhotoCutEntity.getChangePath())
                                .placeholder(R.drawable.brvah_sample_footer_loading)
                                .into( holder.drag);
            }else {
                Glide.with(context)
                        .load(mPhotoCutEntity.getPath())
                        .placeholder(R.drawable.brvah_sample_footer_loading)
                        .into( holder.drag);
            }*/
            Glide.with(context)
                    .load(mPhotoCutEntity.getSdPath())
                    .placeholder(R.drawable.brvah_sample_footer_loading)
                    .into( holder.drag);
        }
        if(position==0){
            if(mDatas.get(position).getSdPath().equals("")){
                holder.tvcover.setVisibility(View.GONE);
            }else {
                holder.tvcover.setVisibility(View.VISIBLE);
            }
        }else {
            holder.tvcover.setVisibility(View.GONE);
        }
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapterListener.delete(position);
            }
        });
        holder.drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapterListener.add(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();

    }

    @Override
    public void onItemSwap(int from, int target) {
//            Collections.swap(mDatas, from, target);
        //交换数据
        PhotoCutEntity s = mDatas.get(from);
        mDatas.remove(from);
        mDatas.add(target,s);
        for (int i=0;i<mDatas.size();i++){
            if(mDatas.get(i).getSdPath().equals("")){
                mDatas.remove(i);
            }
        }
        if(mDatas.size()<9){
            mDatas.add(new PhotoCutEntity(""));
        }
        notifyItemMoved(from, target);
           notifyDataSetChanged();
    }

    @Override
    public void onItemMoved(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    protected class MyHolder extends RecyclerView.ViewHolder {

        public ImageView iv_delete;

        public ImageView drag;
        public LinearLayout llEpty;
        public TextView tvcover;

        public MyHolder(View itemView) {
            super(itemView);
            drag = itemView.findViewById(R.id.iv_thumb);
            llEpty= itemView.findViewById(R.id.llEpty);
            tvcover= itemView.findViewById(R.id.tvcover);
            iv_delete= itemView.findViewById(R.id.iv_delete);
//                drag.setOnTouchListener(this);

        }

/*          @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    helper.startDrag(this);
                }
                return false;
            }*/
    }

}
