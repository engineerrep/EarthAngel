package com.earth.angel.gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.gift.ui.UserMainActivity
import com.earth.angel.util.DateUtils
import com.earth.libbase.entity.UserLocationEntity
import com.earth.libbase.util.PreferencesHelper

class UserLikedapter(
    mContext: Context?,
    userProfileBeans: MutableList<UserLocationEntity>?,
    var upDade: (houseNumber: Int) -> Unit = {}
) : BaseQuickAdapter<UserLocationEntity, BaseViewHolder>(
    R.layout.item_user_like,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: UserLocationEntity) {

        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var tvname: TextView? = holder.getView(R.id.tv)
        var tvNum: TextView? = holder.getView(R.id.tvNum)
        var llItem: ConstraintLayout? = holder.getView(R.id.llItem)

        llItem?.setOnClickListener {
            if (DateUtils.isFastClick()){
                UserMainActivity.openUserMainActivity(context, item)

            }
        }

        item?.headImgUrl?.let {
            Glide.with(context)
                .load(it)
                // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                .into(ivHeader!!)
        }
        item?.nickName?.let {
            tvname?.text = it
        }

        PreferencesHelper.getLocation(context)?.let {
            if (it) {
                item?.distance?.let{
                    tvNum?.text =it.toString()+"m"
                }
            } else {
                item?.id?.let {
                    tvNum?.text = "ID:$it"
                }
            }
        }


    }


}