package com.earth.angel.dialog.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.gift.ui.UserMainActivity
import com.earth.libbase.entity.MemberEntity
import com.earth.libbase.util.PreferencesHelper

class DialogUserdapter (
    mContext: Context?,
    userProfileBeans: MutableList<MemberEntity>?,
    var upDade: (userid: Long,position: Int) -> Unit = { l: Long, i: Int -> }
    ) : BaseQuickAdapter<MemberEntity, BaseViewHolder>(
    R.layout.item_dialog_user,
    userProfileBeans
    ) {
        override fun convert(holder: BaseViewHolder, item: MemberEntity) {
            var ivHeader: ImageView? = holder.getView(R.id.iv)
            var tvname: TextView? = holder.getView(R.id.tv)
            var tvNum: TextView? = holder.getView(R.id.tvNum)
            var ivJoined: Layer? = holder.getView(R.id.lyJoined)
            var tvJoined: TextView? = holder.getView(R.id.tvJoined)

            ivHeader?.setOnClickListener {
                UserMainActivity.openUserMainActivity(context, item)
            }

            item?.headImgUrl?.let {
                Glide.with(context)
                    .load(it)
                    // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                    .into(ivHeader!!)
            }
            item?.nickName?.let {
                tvname?.text=it
            }
            item?.userId?.let {
                tvNum?.text="ID:$it"
            }


            item?.isConcern?.let {
                if(it){
                    tvJoined?.visibility=View.GONE
                    ivJoined?.setBackgroundResource(0)
                }else{

                    if( item?.requestFrendsStatus){
                        tvJoined?.visibility=View.VISIBLE
                        tvJoined?.text=context.getString(R.string.lab_Cancel_Request)
                        ivJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
                    }else{
                        tvJoined?.visibility=View.VISIBLE
                        tvJoined?.text=context.getString(R.string.lab_Add)
                        ivJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
                    }

                }
            }

            item?.userId.let { userid ->
                if(PreferencesHelper.getUserId(context)== userid.toString()){
                    tvJoined?.visibility= View.GONE
                }else{
                    tvJoined?.visibility= View.VISIBLE
                }

                ivJoined?.setOnClickListener {
                    upDade(userid,holder.layoutPosition)
                }

            }
        }


    }