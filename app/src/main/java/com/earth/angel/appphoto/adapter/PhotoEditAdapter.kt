package com.earth.angel.appphoto.adapter

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R

class PhotoEditAdapter(
    data: MutableList<String>?,
    var addImg: () -> Unit = {},
    var upDade: (position: Int) -> Unit = {}
) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_photo_edit, data) {

    override fun convert(holder: BaseViewHolder, item: String) {
        val ivDelete: ImageView = holder.getView(R.id.iv_delete)
        val ivContent: ImageView = holder.getView(R.id.iv_thumb)
        val llEpty: LinearLayout = holder.getView(R.id.llEpty)
        val tvcover: TextView = holder.getView(R.id.tvcover)
        if(holder.layoutPosition==0){
            if(item.isEmpty()){
                tvcover.visibility=View.GONE
            }else{
                tvcover.visibility=View.VISIBLE
            }
        }else{
            tvcover.visibility=View.GONE
        }

        if (item.isEmpty()) {
            llEpty.visibility = View.VISIBLE
            ivDelete.visibility = View.GONE
            ivContent.setImageResource(R.drawable.shape_corner_edit)
        } else {
            llEpty.visibility = View.GONE
            ivDelete.visibility = View.VISIBLE
            item?.let {
                Glide.with(context)
                    .load(it)
                    .placeholder(R.drawable.brvah_sample_footer_loading)
                    .into(ivContent)
            }
        }
        ivDelete.setOnClickListener {
            data.remove(item)
            if (data.size > 1) {
                if (data[data.size - 1].isNotEmpty()) {
                    data.add("")
                }
            }
            notifyDataSetChanged()
        }
    }

}
