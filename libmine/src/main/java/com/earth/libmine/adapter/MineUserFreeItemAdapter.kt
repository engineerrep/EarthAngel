package com.earth.libmine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.baseentity.BaseGiftEntity

import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R


class MineUserFreeItemAdapter(
    var mContext: Context?,
    commentEntity: MutableList<BaseGiftEntity>?,
    var addNeed: Boolean = true,
    var upDade: (str: BaseGiftEntity) -> Unit = {}
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_libmine_user_free,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {
        holder.setText(R.id.tvText, item.itemTitle)
        var ivHeader: ShapedImageView? = holder.getView(R.id.ivImage)
        var addPocket: LinearLayout? = holder.getView(R.id.item_libmine_addpocket)
        var addGift: ImageView? = holder.getView(R.id.add_gift)
        var ivImageFlag: ImageView? = holder.getView(R.id.ivImageFlag)
        var addGifttv: TextView? = holder.getView(R.id.add_gifttv)

        item?.isPoket?.let {
            if(it){
                ivImageFlag?.visibility=View.VISIBLE
                addGift?.setImageResource(R.mipmap.is_add)
                addGifttv?.text=mContext?.getString(R.string.base_AddEd)
            }else{
                ivImageFlag?.visibility=View.GONE
                addGift?.setImageResource(R.mipmap.add_gift)
                addGifttv?.text=mContext?.getString(R.string.base_Add_to_cart)
            }
        }

        item?.pictureResources?.let {
            if (it.size > 0) {
                Glide.with(ivHeader!!.context)
                    .load(it[0])
                    .placeholder(R.mipmap.base_comm_img)
                    .into(ivHeader)
            }
        }


        var llAdd: LinearLayout? = holder.getView(R.id.item_libmine_addpocket)
        if (addNeed) {
            llAdd?.visibility = View.VISIBLE
        } else {
            llAdd?.visibility = View.GONE
        }
        llAdd?.setOnClickListener {
            if (BaseDateUtils.isFastClick()) {
                upDade(item)
            }
        }
        BaseApplication.myBaseUser?.userId?.let {
            item.userId?.let { itUserID ->
                if (it == itUserID) {
                    addPocket?.visibility=View.GONE
                }else{
                    addPocket?.visibility=View.VISIBLE
                }
            }
        }


    }


}