package com.earth.angel.chat

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.chat.adapter.ChatMainListAdapter
import com.earth.angel.databinding.FragmentChatMainBinding
import com.earth.libbase.base.*
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.PreferencesHelper
import com.tencent.imsdk.v2.*
import kotlinx.android.synthetic.main.fragment_chat_main.*
import org.jetbrains.anko.toast
import java.util.*

@Route(path = Constance.ChatMainFragmentURL)
class ChatMainFragment : BaseActivity<FragmentChatMainBinding>(), Observer {

    private var mChatMainListAdapter: ChatMainListAdapter? = null

    private var layoutPager: LinearLayoutManager? = null

    private var  uiConvList: ArrayList<V2TIMConversation>?= ArrayList()

    override fun getLayoutId(): Int =R.layout.fragment_chat_main


    private fun updateConversation( convList: List<V2TIMConversation>,  needSort: Boolean) {
        var sum=0
        for (index in convList.indices){
            val conv = convList[index]
            var isExit = false
            sum += conv.unreadCount
            for(j in uiConvList!!.indices){
                val uiConv = uiConvList!![j]
                if (uiConv.conversationID.equals(conv.conversationID)) {
                    uiConvList?.set(j, conv)
                    isExit = true
                }
            }
            // UI 会话列表没有该会话，则新增
            if (!isExit) {
                uiConvList?.add(conv);
            }
        }
        PreferencesHelper.saveMessage(BaseApplication.instance, sum.toString())

        if (needSort) {

        }
        MessageObservable.messageObservable.newMessage(
            UpdateEntity(
                messageNum = sum.toString()
            )
        )
        if(uiConvList!!.isEmpty()){
            mBinding?.LibMessageEmpty?.visibility=View.VISIBLE
        }else{
            mBinding?.LibMessageEmpty?.visibility=View.GONE
        }
        mChatMainListAdapter?.setList(uiConvList)

        }
    override fun onDestroy() {
        super.onDestroy()
        MessageObservable.messageObservable.deleteObserver(this)
    }
    override fun update(o: Observable?, arg: Any?) {
        o?.let {
            if (it is MessageObservable) {
                arg?.let {
                    var bean = it as UpdateEntity
                    bean.let {
                        it.pockNum?.let {
                            if (it == "0") {
                                LibChatPacketNum?.visibility = View.GONE
                                LibChatPacketNum?.text = it
                            } else {
                                LibChatPacketNum?.visibility = View.VISIBLE
                                LibChatPacketNum?.text = it
                            }
                        }
                       it.permission?.let {
                           toast(""+it)
                       }
                    }

                }
            }
        }
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding.run {
            MessageObservable.messageObservable.addObserver(this@ChatMainFragment)
            tvChatMainLogo.setOnClickListener {
                finish()
            }
            layoutPager = LinearLayoutManager(this@ChatMainFragment,
                LinearLayoutManager.VERTICAL,
                false
            )
            mChatMainListAdapter=ChatMainListAdapter(this@ChatMainFragment,uiConvList)
            ChatMainRlv.layoutManager=layoutPager
            ChatMainRlv.adapter=mChatMainListAdapter
            mChatMainListAdapter?.setOnItemClickListener { _, _, position ->
                    uiConvList?.let {
                        ARouter.getInstance().build(Constance.ChatListActivityURL)
                            .withString("userid", it[position].userID)
                            .withString("chatName",it[position].showName)
                            .navigation()
                        /*          val chatInfo = ChatInfo()
                                  chatInfo.type = V2TIMConversation.V2TIM_C2C
                                  chatInfo.id = uiConvList!![position].userID
                                  chatInfo.chatName= uiConvList!![position].showName
                                  val intent = Intent(EarthAngelApp.instance, ChatActivity::class.java)
                                  intent.putExtra(Constants.CHAT_INFO, chatInfo)
                                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                  startActivity(intent)*/
                    }

            }
            baseLiwu.setOnClickListener {
                if (BaseDateUtils.isFastClick()){
                    ARouter.getInstance().build(Constance.HomePackingBagActivityURL)
                        .navigation()
                }
            }
            BaseApplication.myBaseUser?.let {
                it.poketNumber.let {
                    if (it == "0") {
                        LibChatPacketNum?.visibility = View.GONE
                        LibChatPacketNum?.text = it
                    } else {
                        LibChatPacketNum?.visibility = View.VISIBLE
                        LibChatPacketNum?.text = it
                    }
                }
            }
            V2TIMManager.getConversationManager().setConversationListener(object : V2TIMConversationListener() {
                override fun onNewConversation(conversationList: MutableList<V2TIMConversation>?) {
                    super.onNewConversation(conversationList)
                    conversationList?.let {
                        updateConversation(conversationList, true)
                    }
                }
                override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>?) {
                    super.onConversationChanged(conversationList)
                    conversationList?.let {
                        updateConversation(conversationList, true)
                    }
                }
            })
            V2TIMManager.getConversationManager().getConversationList(0,50, object : V2TIMValueCallback<V2TIMConversationResult?> {
                override fun onSuccess(t: V2TIMConversationResult?) {
                    t?.conversationList?.let {
                        updateConversation(it, true);
                    }
                    t?.let {
                        if (!it.isFinished) {
                            V2TIMManager.getConversationManager().getConversationList(
                                t.nextSeq, 50,
                                object : V2TIMValueCallback<V2TIMConversationResult> {
                                    override fun onError(code: Int, desc: String) {}
                                    override fun onSuccess(v2TIMConversationResult: V2TIMConversationResult) {
                                        // 拉取成功，更新 UI 会话列表
                                        updateConversation(
                                            v2TIMConversationResult.conversationList,
                                            false
                                        )
                                    }
                                })
                        }
                    }
                }
                override fun onError(code: Int, desc: String?) {
                }
            })
        }


    }

}