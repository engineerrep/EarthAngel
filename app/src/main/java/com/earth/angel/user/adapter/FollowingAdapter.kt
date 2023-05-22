package com.earth.angel.user.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.FriendRequestUserEntity

class FollowingAdapter(
    mContext: Context?,
    followingBeans: MutableList<FriendRequestUserEntity>?,
    var upDade: (user: FriendRequestUserEntity) -> Unit = {},
    var upDelete: (user: FriendRequestUserEntity) -> Unit = {}
) : BaseQuickAdapter<FriendRequestUserEntity, BaseViewHolder>(
    R.layout.item_following_user,
    followingBeans
) {
    override fun convert(holder: BaseViewHolder, item: FriendRequestUserEntity) {
        var tvname: TextView? = holder.getView(R.id.tv)
        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var tvNum: TextView? = holder.getView(R.id.tvNum)
        var tvJoined: TextView? = holder.getView(R.id.tvJoined)
        var tvDelete: TextView? = holder.getView(R.id.tvDelete)

        item?.frendUserNickName?.let {
            tvname?.setText(it)
        }
        item?.frendUserHeadImgUrl?.let {
            Glide.with(context)
                .load(it)
                // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                .into(ivHeader!!)
        }
        item?.frendUserId?.let { itUserid ->
            tvNum?.text = "ID:$itUserid"
            tvJoined?.setOnClickListener {
                upDade(item)
            }
            tvDelete?.setOnClickListener {
                upDelete(item)
            }
        }
        tvJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)

    }
}