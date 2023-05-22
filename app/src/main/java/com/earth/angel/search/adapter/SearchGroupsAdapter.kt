package com.earth.angel.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.SearchDetailEntity

class SearchGroupsAdapter (
    mContext: Context?,
    userProfileBeans: MutableList<SearchDetailEntity>?,
    var upDade: (item: SearchDetailEntity ) -> Unit = {}
    ) : BaseQuickAdapter<SearchDetailEntity, BaseViewHolder>(
    R.layout.item_search_group,
    userProfileBeans
    ) {
        private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


        override fun convert(holder: BaseViewHolder, item: SearchDetailEntity) {
            var ivHeader: ImageView? = holder.getView(R.id.iv)
            var tvname: TextView? = holder.getView(R.id.tv)
            var tvNum: TextView? = holder.getView(R.id.tvNum)
            var ivJoined: Layer? = holder.getView(R.id.lyJoined)
            var tvJoined: TextView? = holder.getView(R.id.tvJoined)



            item?.imgUrl?.let {
                Glide.with(context)
                    .load(it)
                    // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                    .into(ivHeader!!)
            }
            item?.name?.let {
                tvname?.text=it
            }
            item?.number?.let {
                tvNum?.text= "Number:$it"
            }
            ivJoined?.setOnClickListener {
                upDade(item)
            }
            item?.status?.let {
                if(it){
                    tvJoined?.text="Joined"
                    ivJoined?.setBackgroundResource(R.drawable.shape_corner_group_joined)
                }else{
                    tvJoined?.text="+Join"
                    ivJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
                }
            }

        }


    }