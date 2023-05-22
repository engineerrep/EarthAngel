package com.earth.libhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.base.Constance
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.baseentity.BasePagePocketEntity
import com.earth.libbase.baseentity.BasePocketEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.view.BaseNoScrollRecyclerView
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R

class HomePackingBagAdapter (
    var mContext: Context,
    commentEntity: MutableList<BasePocketEntity>?,
    var upDade: (str: String,main: Int,group: Int) -> Unit = { s: String, i: Int, i1: Int -> }
) : BaseQuickAdapter<BasePocketEntity, BaseViewHolder>(
    R.layout.item_libhome_packed,
    commentEntity
) {

    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun convert(holder: BaseViewHolder, item: BasePocketEntity) {

        holder.setText(R.id.ItemLibHomePacketName,item.nickName)
        var ivRlv: BaseNoScrollRecyclerView? = holder.getView(R.id.ItemLibHomePacketRlv)
        var ivBtn: TextView? = holder.getView(R.id.ItemLibHomePacketInt)

        item?.poketList?.let {
            var mAdapter= HomePackingBagDetailsAdapter (mContext,it) { str: String, position: Int ->
                upDade(str,holder.layoutPosition,position)
            }
            val layoutManager1 = LinearLayoutManager(mContext).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            ivRlv?.layoutManager=layoutManager1
            ivRlv?.adapter=mAdapter
            mAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id", it[position].releaseRecordId)
                        .withString("userid", it[position].userId)
                        .navigation()
                }
            }
            if(it.size>1){
                ivBtn?.setText(mContext?.getString(R.string.base_I_Want_These))
            }else{
                ivBtn?.setText(mContext?.getString(R.string.base_I_Want_This))

            }
        }

        ivBtn?.setOnClickListener {
            if (BaseDateUtils.isFastClick()){
                ARouter.getInstance().build(Constance.ChatListActivityURL)
                    .withString("userid",item.userId)
                    .withString("chatName",item.nickName)
                    .withString("want", "want")
                    .navigation()
            }

        }
    }


}