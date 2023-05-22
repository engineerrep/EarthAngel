package com.earth.angel.mine.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.util.TimeForUtils
import com.earth.libbase.entity.GiftEntity

class WhoWantAllAdapter (
    context: Context,
    userProfileBeans: MutableList<GiftEntity>?,
    var upDade: (position: Int) -> Unit = {},
    var upEdit: (item: GiftEntity) -> Unit = {},
) : BaseQuickAdapter<GiftEntity, BaseViewHolder>(
    R.layout.item_who_want_all,
    userProfileBeans
) {
    override fun convert(holder: BaseViewHolder, item: GiftEntity) {

        var nly: RecyclerView? = holder.getView(R.id.nly)

        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var tvDestail: TextView? = holder.getView(R.id.tvDestail)
        var time: TextView? = holder.getView(R.id.time)
        var lydelete: LinearLayout? = holder.getView(R.id.lldelete)
        var tvUnlisted: TextView? = holder.getView(R.id.tvUnlisted)
        var llBianji: LinearLayout? = holder.getView(R.id.llBianji)


        var tvValue: TextView? = holder.getView(R.id.tvValue)
        var tvValueNum: TextView? = holder.getView(R.id.tvValueNum)
        var llValue: LinearLayout? = holder.getView(R.id.llValue)

        item.valuation.let {
            if (it == 0.0) {
                tvValue?.visibility = View.GONE
                tvValueNum?.visibility = View.GONE
                llValue?.visibility = View.GONE
            } else {
                tvValue?.visibility = View.VISIBLE
                tvValueNum?.visibility = View.VISIBLE
                tvValueNum?.setText("$$it")
            }

        }


        llBianji?.setOnClickListener {
            upEdit(item)
        }

        item?.description.let {
            tvDestail?.text = it
        }


        item?.pictureResources?.let {
            if (it.isNotEmpty()) {
                Glide.with(context)
                    .load(it[0].pictureUrl)
                    .into(ivHeader!!)
            }

        }
        item?.createTime?.let {
            time?.text = TimeForUtils.getGiftTime(it.toLong())
        }
        item?.releaseStatus.let {
            if (it == 1||it == 2) {
                llBianji?.visibility = View.INVISIBLE
                tvUnlisted?.text = "Unlisted"
            } else {
                llBianji?.visibility = View.VISIBLE
                tvUnlisted?.text = "Unlist"
            }
        }
        lydelete?.setOnClickListener {
            upDade(holder.layoutPosition)
        }

        item.releasedMessageRecords?.let {
            var mAdapter = GiftMessageAdapter(it)
            val layoutPager = LinearLayoutManager(context)
            layoutPager.isAutoMeasureEnabled = true
            nly?.layoutManager = layoutPager
            if (nly?.adapter == null) {
                nly?.adapter = mAdapter
            }
            mAdapter.setOnItemClickListener { _, _, _ ->
              //  ChatActivity.openChatActivity(context,item)
            }
        }

    }


}