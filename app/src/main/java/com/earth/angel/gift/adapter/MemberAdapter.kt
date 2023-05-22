package com.earth.angel.gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.util.DateUtils
import com.earth.libbase.entity.MemberEntity


class MemberAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<MemberEntity>?,
    var upDade: (memberEntity: MemberEntity) -> Unit = {}
) : BaseQuickAdapter<MemberEntity, BaseViewHolder>(
    R.layout.item_chat_popularity,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: MemberEntity) {
        var ivHeader: ImageView? = holder.getView(R.id.sivHeadimg)
        var ivJoined: TextView? = holder.getView(R.id.chativ)

        if (item.nickName.isNullOrEmpty()) {

            Glide.with(context)
                .load(R.mipmap.user_add)
                .into(ivHeader!!)
            ivJoined?.text = ""

        } else {

            item?.headImgUrl?.let {
                Glide.with(context)
                    .load(it)
                    .placeholder(R.mipmap.head_common)
                    .into(ivHeader!!)
            }
            item?.nickName?.let {
                ivJoined?.text = it
            }
        }

        ivHeader?.setOnClickListener {
            if (DateUtils.isFastClick()){
                upDade(item)

            }
        }
    }


}