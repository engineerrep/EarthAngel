package com.earth.angel.dialog

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.libbase.entity.TypeEntity


class DialogTypedapter (
    userProfileBeans: MutableList<TypeEntity>?,
    var upDade: (houseNumber: Int) -> Unit = {}
    ) : BaseQuickAdapter<TypeEntity, BaseViewHolder>(
    R.layout.item_doalog_type,
    userProfileBeans
    ) {

        override fun convert(holder: BaseViewHolder, item: TypeEntity) {

            var tvname: TextView? = holder.getView(R.id.tv)
            item.englishDescribe?.let {
                tvname?.text=it
            }
            if (EarthAngelApp.mFilterEntity==null){
                tvname?.setBackgroundResource(R.drawable.shape_corner_edit)
                tvname?.setTextColor(ContextCompat.getColor(context, R.color.rank_un))
            }else{
                if(EarthAngelApp.mFilterEntity?.releaseType==item.releaseType){
                    tvname?.setBackgroundResource(R.drawable.shape_corner_8sp_themcoclor)
                    tvname?.setTextColor(ContextCompat.getColor(context, R.color.white))
                }else{
                    tvname?.setBackgroundResource(R.drawable.shape_corner_edit)
                    tvname?.setTextColor(ContextCompat.getColor(context, R.color.rank_un))

                }
            }
        }


    }