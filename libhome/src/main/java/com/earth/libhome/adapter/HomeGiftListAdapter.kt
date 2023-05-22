package com.earth.libhome.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity

import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.util.BaseScreenUtil
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R
import org.jetbrains.anko.dip


class HomeGiftListAdapter(
    var mContext: Activity?,
    commentEntity: MutableList<BaseGiftEntity>?,
    var upDade: (str: BaseGiftEntity) -> Unit = {}
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_libhome_homegiftlist,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {
        holder.setText(R.id.tvText,item.itemTitle)
        var tvPointText: TextView? = holder.getView(R.id.tvPointText)
        if(item.score==0){
            tvPointText?.text= mContext?.getString(R.string.label_Free)
        }else{
            tvPointText?.text= item.score.toString()
        }

        var ivHeader: ShapedImageView? = holder.getView(R.id.ivImage)
        item?.pictureResources?.let {
            if(it.size>0){
                Glide.with(ivHeader!!.context)
                    .load(it[0])
                    .placeholder(R.mipmap.base_comm_img)
                    .into(ivHeader)
            }
        }
        if (holder.layoutPosition % 2 != 0) {
            //1
            val lp: ConstraintLayout.LayoutParams =
                ivHeader?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(mContext?.dip(18)!!, 0, mContext?.dip(9)!!, 0)
            ivHeader?.layoutParams = lp

        } else {
            //2
            val lp: ConstraintLayout.LayoutParams =
                ivHeader?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(mContext?.dip(9)!!, 0, mContext?.dip(18)!!, 0)
            ivHeader?.layoutParams = lp
        }
    }


}