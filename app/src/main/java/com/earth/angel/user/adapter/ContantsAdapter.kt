package com.earth.angel.user.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.PhotoContactsEntity
import com.earth.libbase.util.PreferencesHelper

class ContantsAdapter(
    mContext: Context?,
    followingBeans: MutableList<PhotoContactsEntity>?,
    var upDade: (user: PhotoContactsEntity) -> Unit = {},
    var upJoin: (user: PhotoContactsEntity) -> Unit = {}
) : BaseQuickAdapter<PhotoContactsEntity, BaseViewHolder>(
    R.layout.item_contants_user,
    followingBeans
) {
    override fun convert(holder: BaseViewHolder, item: PhotoContactsEntity) {
        var tvname: TextView? = holder.getView(R.id.tv)
        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var tvNum: TextView? = holder.getView(R.id.tvNum)
        var lyJoined: Layer? = holder.getView(R.id.lyJoined)
        var tvJoined: TextView? = holder.getView(R.id.tvJoined)

        if (item.nickName.isNullOrEmpty()) {
            lyJoined?.visibility = View.VISIBLE
            tvNum?.visibility = View.GONE
            item.friendMobilePhoneName?.let {
                tvname?.setText(it)
            }
            Glide.with(context)
                .load(R.mipmap.head_common)
                // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                .into(ivHeader!!)
            tvJoined?.text = "Invite"
            var drawable = context.resources.getDrawable(R.mipmap.common_invite)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            tvJoined?.setCompoundDrawables(drawable, null, null, null)
            lyJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
            lyJoined?.setOnClickListener {
                if(tvJoined?.text.toString() != "Invited"){
                    upDade(item)
                    tvJoined?.text = "Invited"
                    lyJoined?.setBackgroundResource(R.drawable.shape_corner_group_invited)
                }

            }
        } else {
            tvNum?.visibility = View.VISIBLE
            item.nickName?.let {
                tvname?.setText(it)
            }
            item.friendUserId?.let {
                tvNum?.text = it
            }
            item?.headImgUrl?.let {
                Glide.with(context)
                    .load(it)
                    // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                    .into(ivHeader!!)
            }
            item?.isConcern?.let {
                if (it) {

                    lyJoined?.visibility = View.GONE
                    tvJoined?.text = ""
                    lyJoined?.setBackgroundResource(R.drawable.shape_corner_group_invited)

                } else {

                    lyJoined?.visibility = View.VISIBLE
                    tvJoined?.setCompoundDrawables(null, null, null, null)

                    if (PreferencesHelper.getUserId(context).equals(item?.friendUserId)) {
                        lyJoined?.visibility = View.GONE
                    } else {
                        lyJoined?.visibility = View.VISIBLE
                    }

                    if(item?.requestFrendsStatus==null){
                        tvJoined?.text=context.getString(R.string.lab_Add)
                        lyJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
                    }else{
                        if(item?.requestFrendsStatus!!){
                            tvJoined?.text=context.getString(R.string.lab_Cancel_Request)
                            lyJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
                        }else{
                            tvJoined?.text=context.getString(R.string.lab_Add)
                            lyJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
                        }
                    }

                }
            }
            lyJoined?.setOnClickListener {
                upJoin(item)
            }
        }



    }
}