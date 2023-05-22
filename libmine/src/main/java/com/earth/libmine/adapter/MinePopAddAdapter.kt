package com.earth.libmine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity

import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R


class MinePopAddAdapter(
    mContext: Context?,
    commentEntity: MutableList<BaseGiftEntity>?,
    var upDade: (str: BaseGiftEntity) -> Unit = {}
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_libmine_pop_add,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {
        holder.setText(R.id.tvText,item.itemTitle)
        var ivHeader: ShapedImageView? = holder.getView(R.id.ivImage)
        var ivContent: ImageView? = holder.getView(R.id.MinePopAddShiXiao)
        var ivMinePopDelete: ImageView? = holder.getView(R.id.MinePopDelete)

        item?.pictureResources?.let {
            if(it.size>0){
                Glide.with(ivHeader!!.context)
                    .load(it[0])
                    .into(ivHeader)
            }
        }
        if(item.releaseStatus!=null){
            item?.releaseStatus?.let {
                when(it){
                    0 -> {
                        ivContent?.visibility=View.GONE
                    }
                    else -> {
                        ivContent?.visibility=View.VISIBLE
                    }
                }
            }
        }else{
            ivContent?.visibility=View.GONE

        }
        ivMinePopDelete?.setOnClickListener {
            upDade(item)
        }
    }


}