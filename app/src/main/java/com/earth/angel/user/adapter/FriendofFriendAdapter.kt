package com.earth.angel.user.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.UserEntity

class FriendofFriendAdapter(
    mContext: Context?,
    followingBeans: MutableList<UserEntity>?,
    var upDade: (user: UserEntity) -> Unit = {}
) : BaseQuickAdapter<UserEntity, BaseViewHolder>(
    R.layout.item_search_user,
    followingBeans
) {
    override fun convert(holder: BaseViewHolder, item: UserEntity) {
        var tvname: TextView? = holder.getView(R.id.tv)
        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var tvNum: TextView? = holder.getView(R.id.tvNum)
        var ivJoined: Layer? = holder.getView(R.id.lyJoined)
        var tvJoined: TextView? = holder.getView(R.id.tvJoined)

        item?.nickName?.let {
            tvname?.setText(it)
        }
        item?.headImgUrl?.let {
            Glide.with(context)
                .load(it)
                // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                .into(ivHeader!!)
        }
      /*  item?.userId?.let { itUserid ->
            tvNum?.text = "ID:$itUserid"
            ivJoined?.setOnClickListener {
                upDade(item)
            }
        }*/

        tvJoined?.text = "Following"
        tvJoined?.setTextColor(ContextCompat.getColor(context, R.color.themColor))
        ivJoined?.setBackgroundResource(R.drawable.shape_corner_save)

    }
}