package com.earth.angel.mine.adapter

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.angel.util.TimeForUtils
import com.earth.angel.view.GiftPhotoActivity
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.entity.PicTureEntity

class MyEcoGiftAdapter(
    userProfileBeans: MutableList<GiftEntity>?,
    var upedit: (item: GiftEntity) -> Unit = { item: GiftEntity -> },
    var upport: (item: GiftEntity) -> Unit = { item: GiftEntity -> },
    var uprepot: (item: GiftEntity) -> Unit = { item: GiftEntity -> },
    var updelete: (item: GiftEntity) -> Unit = { item: GiftEntity -> },
    var upsend: (item: GiftEntity) -> Unit = { item: GiftEntity -> }
) : BaseQuickAdapter<GiftEntity, BaseViewHolder>(
    R.layout.item_my_eco_gift,
    userProfileBeans
) {

    override fun convert(holder: BaseViewHolder, item: GiftEntity) {


        var tv_content: TextView? = holder.getView(R.id.tv_content)
        var time: TextView? = holder.getView(R.id.time)
        var ivGiftShare: ImageView? = holder.getView(R.id.ivGiftShare)
        var rlv: RecyclerView? = holder.getView(R.id.rlv)

        var llgift: LinearLayout? = holder.getView(R.id.llgift)
        var gift_edit: ImageView? = holder.getView(R.id.gift_edit)
        var gift_port: ImageView? = holder.getView(R.id.gift_port)
        var gift_report: ImageView? = holder.getView(R.id.gift_report)
        var gift_delete: ImageView? = holder.getView(R.id.gift_delete)
        var gift_send: ImageView? = holder.getView(R.id.gift_send)

        item?.pictureResources?.let { itList ->
            var listPicTureEntity: ArrayList<PicTureEntity> = ArrayList()

            when {
                itList.size > 3 -> {
                    listPicTureEntity.add(itList[0])
                    listPicTureEntity.add(itList[1])
                    listPicTureEntity.add(itList[2])

                }
                itList.size == 3 -> {
                    listPicTureEntity.add(itList[0])
                    listPicTureEntity.add(itList[1])
                    listPicTureEntity.add(itList[2])
                }
                itList.size < 3 -> {
                    listPicTureEntity.addAll(itList)
                }
            }


            var atapter = GiftPhotoAdapter(listPicTureEntity, itList)
            rlv?.layoutManager = GridLayoutManager(context, 3)

            atapter.setOnItemClickListener { adapter, view, position ->
                GiftDetailsActivity.openGiftDetailsActivityFromUser(
                    context,
                    item
                )
                val intent =
                    Intent(context, GiftPhotoActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(
                    GiftPhotoActivity.IMAGE_LIST,
                    item
                ).putExtra("position", position)
                context.startActivity(intent)
            }
            rlv?.adapter = atapter
        }

        item?.description?.let {
            tv_content?.setText(it)
        }
        item?.createTime?.let {
            time?.text = TimeForUtils.getGiftDtailsTime(it.toLong())
        }



        item?.isExchange?.let {
            if (it == 0) {
                ivGiftShare?.visibility = View.GONE
                item?.releaseStatus.let {
                    when (it) {
                        0 -> {
                            gift_edit?.visibility = View.VISIBLE
                            gift_port?.visibility = View.VISIBLE
                            gift_report?.visibility = View.GONE
                            gift_delete?.visibility = View.GONE
                            gift_send?.visibility = View.VISIBLE
                        }
                        1 -> {

                            gift_edit?.visibility = View.GONE
                            gift_port?.visibility = View.GONE
                            gift_report?.visibility = View.GONE
                            gift_delete?.visibility = View.VISIBLE
                            gift_send?.visibility = View.GONE
                        }
                        2 -> {

                            gift_edit?.visibility = View.GONE
                            gift_port?.visibility = View.GONE
                            gift_report?.visibility = View.VISIBLE
                            gift_delete?.visibility = View.VISIBLE
                            gift_send?.visibility = View.GONE
                        }
                    }
                }
                gift_send?.setImageResource(R.mipmap.gift_send)
            } else {
                gift_edit?.visibility = View.GONE
                gift_port?.visibility = View.GONE
                gift_report?.visibility = View.GONE
                gift_delete?.visibility = View.GONE
                gift_send?.setImageResource(R.mipmap.gift_send_yes)
                ivGiftShare?.visibility = View.VISIBLE
            }
        }
        gift_edit?.setOnClickListener {
            upedit(item)
        }
        gift_port?.setOnClickListener {
            upport(item)
        }
        gift_report?.setOnClickListener {
            uprepot(item)
        }
        gift_delete?.setOnClickListener {
            updelete(item)
        }
        gift_send?.setOnClickListener {
            upsend(item)
        }
        /*      item?.description.let {
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
                      ivBianJi?.setImageResource(R.mipmap.gift_reboot)

                      tvEdit?.text = context.getString(R.string.lab_Repost)
                      tvUnlisted?.text = context.getString(R.string.lab_Delete)
                      llBianji?.setOnClickListener {
                          upEdit(item,2)
                      }
                      lydelete?.setOnClickListener {
                          upDade(holder.layoutPosition,2)
                      }
                  } else {
                      ivBianJi?.setImageResource(R.mipmap.bianji_gift)
                      tvEdit?.text = context.getString(R.string.lab_Edit)
                      tvUnlisted?.text = context.getString(R.string.lab_Unlist)
                      llBianji?.setOnClickListener {
                          upEdit(item,1)
                      }
                      lydelete?.setOnClickListener {
                          upDade(holder.layoutPosition,1)
                      }

                  }
              }*/

    }


}