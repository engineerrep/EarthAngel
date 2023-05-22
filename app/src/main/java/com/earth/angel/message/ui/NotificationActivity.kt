package com.earth.angel.message.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.chat.CustomHelloMessage
import com.earth.angel.databinding.ActivityNotificationBinding
import com.earth.angel.message.adapter.NotificationListAdapter
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.util.OnGroupListener
import com.google.gson.Gson
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMValueCallback
import kotlinx.android.synthetic.main.include_top_bar.*

class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {
    protected val MSG_PAGE_COUNT = 20

    var lastTIMMsg: V2TIMMessage? = null
    var userId: String? = null
    var mNotificationListAdapter : NotificationListAdapter?=null
    private var listV2TIMMessage: ArrayList<V2TIMMessage> = ArrayList()

    override fun getLayoutId(): Int= R.layout.activity_notification

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding.run {
                tvTitleCenter.text = getString(R.string.lab_Notification)
                mNotificationListAdapter=NotificationListAdapter(this@NotificationActivity,
                listV2TIMMessage,object :
                        OnGroupListener {
                override fun onItemClick(position: Int) {
                    val mV2TIMMessage: V2TIMMessage = listV2TIMMessage.get(position)
                    val elem = mV2TIMMessage.customElem
                    // 自定义的json数据，需要解析成bean实例
                    var data: CustomHelloMessage? = null
                    data = Gson().fromJson(String(elem.data), CustomHelloMessage::class.java)
               /*     when(data?.customCellType ){
                        "7" ->{
                            startActivity(
                                Intent(
                                    this@NotificationActivity,
                                    UserFollowingFragment::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                        "6" ->{
                            if(! data.identificationID.isNullOrEmpty()){
                                startActivity(
                                    Intent(
                                        this@NotificationActivity,
                                        GiftDetailsActivity::class.java
                                    ).putExtra("GoodId", data.identificationID)
                                )
                            }
                        }
                        "3" ->{
                            if(! data.identificationID.isNullOrEmpty()){
                                startActivity(
                                    Intent(
                                        this@NotificationActivity,
                                        GiftDetailsActivity::class.java
                                    ).putExtra("GoodId", data.identificationID)
                                )
                            }
                        }
                    }*/
                }
                override fun onDeleteClick(position: Int) {
                }
            })
            val layoutManager1 = LinearLayoutManager(this@NotificationActivity)
            rlvMessage.layoutManager = layoutManager1
            rlvMessage.setAdapter(mNotificationListAdapter)
            userId=intent.getStringExtra("userId")
            V2TIMManager.getMessageManager().getC2CHistoryMessageList(
                userId,
                MSG_PAGE_COUNT,
                lastTIMMsg,
                object : V2TIMValueCallback<List<V2TIMMessage?>?> {
                    override fun onError(code: Int, desc: String) {
                    }
                    override fun onSuccess(v2TIMMessages: List<V2TIMMessage?>?) {
                        v2TIMMessages?.let {
                            for (item in it){
                                listV2TIMMessage.add(item!!)
                            }
                            mNotificationListAdapter?.notifyDataSetChanged()
                        }
                    }
                })
            V2TIMManager.getMessageManager().markC2CMessageAsRead(userId,object : V2TIMCallback {
                override fun onSuccess() {

                }

                override fun onError(code: Int, desc: String?) {

                }
            })
        }


    }
}