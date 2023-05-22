package com.earth.angel.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.UserLocationEntity
import com.earth.libbase.util.PreferencesHelper

class Shakedapter(
    mContext: Context?,
    userProfileBeans: MutableList<UserLocationEntity>?,
    var upDade: (item: UserLocationEntity) -> Unit = {}
) : BaseQuickAdapter<UserLocationEntity, BaseViewHolder>(
    R.layout.item_shake,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: UserLocationEntity) {

        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var tvname: TextView? = holder.getView(R.id.tv)
        var tvNum: TextView? = holder.getView(R.id.tvNum)
        var llItem: ConstraintLayout? = holder.getView(R.id.llItem)

        llItem?.setOnClickListener {
            upDade(item)
        }

        item?.headImgUrl?.let {
            Glide.with(context)
                .load(it)
                .placeholder(R.mipmap.head_common)
                .into(ivHeader!!)
        }
        item?.nickName?.let {
            tvname?.text = it
        }

        PreferencesHelper.getLocation(context)?.let {
            if (it) {
                item?.distance?.let{
                    if(it==0){
                        tvNum?.text = ""
                    }else{
                        tvNum?.text =it.toString()+"m"
                    }
                }
            } else {
                item?.id?.let {
                    tvNum?.text = "ID:$it"
                }
            }
        }


    }


}