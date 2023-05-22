package com.earth.angel.dialog

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.TypeEntity


class DialogPhotoTypedapter (
    userProfileBeans: MutableList<TypeEntity>?,
    var upDade: (houseNumber: Int) -> Unit = {}
    ) : BaseQuickAdapter<TypeEntity, BaseViewHolder>(
    R.layout.item_doalog_type,
    userProfileBeans
    ) {
    companion object {
        var selectPosition = -1

    }
        override fun convert(holder: BaseViewHolder, item: TypeEntity) {

            var tvname: TextView? = holder.getView(R.id.tv)
            item.englishDescribe?.let {
                tvname?.text=it
            }

                if(selectPosition==holder.layoutPosition){
                    tvname?.setBackgroundResource(R.drawable.shape_corner_8sp_themcoclor)
                    tvname?.setTextColor(ContextCompat.getColor(context, R.color.white))
                }else{
                    tvname?.setBackgroundResource(R.drawable.shape_corner_edit)
                    tvname?.setTextColor(ContextCompat.getColor(context, R.color.rank_un))

                }

        }


    }