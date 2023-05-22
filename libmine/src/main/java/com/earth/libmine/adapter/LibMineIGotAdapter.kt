package com.earth.libmine.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.entity.CategoryEntity
import com.earth.libbase.entity.RecordEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R

class LibMineIGotAdapter(var mContext: Context,
                         list: MutableList<RecordEntity>,
                         var upMessage: (position: Int) -> Unit = {},
                         var upDelete: (position: Int) -> Unit = {},
) : BaseQuickAdapter<RecordEntity, BaseViewHolder>(
    R.layout.item_libmine_got,
    list
) {
    override fun convert(holder: BaseViewHolder, item: RecordEntity) {
        var itemLibMineGotName: TextView? = holder.getView(R.id.itemLibMineGotName)
        item?.nickName?.let{
            itemLibMineGotName?.text=it
        }
        var ivlibHomeGiftImage: ShapedImageView? = holder.getView(R.id.ivlibHomeGiftImage)
        item?.pictureResources?.let {
            if(it.size>0){
                Glide.with(mContext)
                    .load(it[0])
                    .placeholder(R.mipmap.base_comm_img)
                    .into(ivlibHomeGiftImage!!)
            }
        }
        item?.itemTitle?.let{
            holder.setText(R.id.tvLibHomeContent,it)
        }
        item?.score?.let {
            if(it==0){
                holder.setText(R.id.ivLibHomePoint,"Free")
            }else{
                holder.setText(R.id.ivLibHomePoint,it.toString())
            }
        }

        var ivLibHomeMessage: ImageView? = holder.getView(R.id.ivLibHomeMessage)
        ivLibHomeMessage?.setOnClickListener {
            upMessage(holder.layoutPosition)
        }
        var ivLibHomeDelete: ImageView? = holder.getView(R.id.ivLibHomeDelete)
        ivLibHomeDelete?.setOnClickListener {
            upDelete(holder.layoutPosition)
        }
    }
}