package com.earth.angel.appphoto.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.CategoryEntity

class DetailsTextAdapter(
    data: MutableList<CategoryEntity>,
    var choose: (position: Int) -> Unit = {},
    ) :
    BaseQuickAdapter<CategoryEntity, BaseViewHolder>(R.layout.item_details_text, data) {

    override fun convert(holder: BaseViewHolder, item: CategoryEntity) {
        val mChooseIv: ImageView = holder.getView(R.id.ChooseIv)
        val mChooseTv: TextView = holder.getView(R.id.ChooseTv)
        val mChooseCl: ConstraintLayout = holder.getView(R.id.ChooseCl)

        item?.let {
            it.choose?.let {
                if(it){
                    mChooseIv.setImageResource(R.mipmap.photo_text)
                }else{
                    mChooseIv.setImageResource(R.mipmap.photo_text_un)
                }
            }

            it.description.let {
                mChooseTv.text=it
            }
        }
        mChooseCl.setOnClickListener {
            choose(holder.layoutPosition)
        }
    }

}
