package com.earth.angel.chat

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.chat.adapter.ChatListAdapter
import com.earth.angel.chat.adapter.ChatListHeWantsAdapter
import com.earth.angel.databinding.ActivityChatListBinding
import com.earth.angel.event.SendGiftEvent
import com.earth.angel.gift.ui.UserMainActivity.Companion.USER_SHARE
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.util.ClickUtils
import com.earth.angel.util.Constants
import com.earth.angel.util.TimeForUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.PocketDeleteRequest
import com.earth.libbase.network.request.RecordDoneRequest
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.qcloud.tim.uikit.base.IMEventListener
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.include_top_bar.tvTitleCenter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChatListActivity : BaseActivity<ActivityChatListBinding>() {
    @Autowired(name = "userid")
    @JvmField
    var userid: String? = null
    @Autowired(name = "chatName")
    @JvmField
    var chatName: String? = null
    @Autowired(name = "releaseRecordId")
    @JvmField
    var releaseRecordId: String? = null
    @Autowired(name = "want")
    @JvmField
    var want: String? = null

    private val mPAdapter by lifecycleScope.inject<ChatPopularityPagingAdapter>()
    private val mViewModel by viewModel<ContactListViewModel>()
    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var mChatFragment: ChatFragment? = null
    private var mChatInfo: ChatInfo? = null
    private var mGetUserJob: Job? = null
    private var flag: Boolean? = true
    private var typeInt: Int? = null
    private var mIWantAdapter: ChatListAdapter? = null
    private var listIWantEntity: ArrayList<BaseGiftEntity>? = ArrayList()
    private var layoutPager1: LinearLayoutManager? = null
    private var layoutPager2: LinearLayoutManager? = null
    private var mHWantAdapter: ChatListHeWantsAdapter? = null
    private var listHWantEntity: ArrayList<BaseGiftEntity>? = ArrayList()


    private val mIMEventListener: IMEventListener = object : IMEventListener() {
        override fun onForceOffline() {
            //    ToastUtil.toastLongMessage(EarthAngelApp.instance.getString(R.string.repeat_login_tip));
            ChatBaseActivity.logout(EarthAngelApp.instance)
        }

        override fun onUserSigExpired() {
            //   ToastUtil.toastLongMessage(EarthAngelApp.instance.getString(R.string.expired_login_tip));
            ChatBaseActivity.logout(EarthAngelApp.instance)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_chat_list
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initActivity(savedInstanceState: Bundle?) {

        EventBus.getDefault().register(this)
        ARouter.getInstance().inject(this@ChatListActivity)
        fitSystemBar()
        chat()
        getPhoto()
        mBinding?.run {
            setViewActionBarHight(rlTopToolbar)
            mAppViewModel?.showLoadingProgress.observe(this@ChatListActivity, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
            tvLeftTool.setOnClickListener {
                finish()
            }
            mIWantAdapter = ChatListAdapter(this@ChatListActivity, listIWantEntity)
            layoutPager1 = LinearLayoutManager(
                this@ChatListActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            iWantList.layoutManager = layoutPager1
            iWantList.adapter = mIWantAdapter
            mHWantAdapter =
                ChatListHeWantsAdapter(this@ChatListActivity, listHWantEntity, upDade = {
                    deleteCounterparty(it)
                })
            layoutPager2 = LinearLayoutManager(
                this@ChatListActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            heWantsList.layoutManager = layoutPager2
            heWantsList.adapter = mHWantAdapter
            ChatAddSend.setOnClickListener {
                send()
            }
            ChatAddress.setOnClickListener {
            }
            ChatListExangeIv.setOnClickListener {
            }
            userid?.let {
                if (!isNumeric(it)) {
                    cl_bg.visibility = View.GONE
                } else {
                    cl_bg.visibility = View.VISIBLE
                }
            }
            ChatListIWantCl?.setOnClickListener {
                ChatListIWant.setTextColor(
                    ContextCompat.getColor(
                        this@ChatListActivity,
                        com.earth.libhome.R.color.BaseThemColor
                    )
                )
                ChatListIWantLine.visibility = View.VISIBLE
                iWantList.visibility = View.VISIBLE
                heWantsList.visibility = View.GONE
                ChatListHeWants.setTextColor(
                    ContextCompat.getColor(
                        this@ChatListActivity,
                        com.earth.libhome.R.color.base_login_un
                    )
                )
                ChatListHeWantsLine.visibility = View.INVISIBLE
                ChatAddSend.visibility = View.GONE
                ChatAddress.visibility = View.GONE
            }
            ChatListHeWantsCl?.setOnClickListener {
                ChatListIWant.setTextColor(
                    ContextCompat.getColor(
                        this@ChatListActivity,
                        com.earth.libhome.R.color.base_login_un
                    )
                )
                ChatListIWantLine.visibility = View.INVISIBLE
                iWantList.visibility = View.GONE
                heWantsList.visibility = View.VISIBLE
                ChatListHeWants.setTextColor(
                    ContextCompat.getColor(
                        this@ChatListActivity,
                        com.earth.libhome.R.color.BaseThemColor
                    )
                )
                ChatListHeWantsLine.visibility = View.VISIBLE
                ChatAddSend.visibility = View.VISIBLE
                ChatAddress.visibility = View.GONE
            }
            releaseRecordId?.let {
                getGiftList()
            }
        }
        selfPokedPageList()
        otherPageList()
    }
    private fun getGiftList() {
        launch {
            releaseRecordId?.let {
                mViewModel.releaseGistOne(
                    CommentRequest(
                        releaseRecordId = it.toLong()
                    )
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@ChatListActivity)) {
                            it.data?.let {
                                var listCustomList: ArrayList<BaseGiftEntity>? = ArrayList()
                                listCustomList?.add(it)
                                mChatFragment?.sendCustom(listCustomList, getString(R.string.base_head_WantProduct))
                            }
                        }

                    }
            }

        }
    }

    private fun send() {
        var listSend: ArrayList<String>? = ArrayList()
        var listCustomList: ArrayList<BaseGiftEntity>? = ArrayList()
        for (item in listHWantEntity!!) {
            if (item.releaseStatus == BaseGiftEntity.DOING) {
                listSend?.add(item.releaseRecordId)
                listCustomList?.add(item)
            }
        }
        if (listSend?.size == 0) {
            toast(""+getString(R.string.base_tips_NoGifts))
            return
        }
        var num = listSend?.size
        var str = if (num == 1) {
            "Are You Sure To Give Away $num Product?"
        } else {
            "Are You Sure To Give Away $num Products?"
        }
        launch {
            mEcoGiftGorupsModel.shoppingAddressIndex()
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@ChatListActivity)) {
                        if (it.data == null) {
                            ARouter.getInstance().build(Constance.LibMineShippingAddressActivityURL)
                                .navigation()
                        } else {
                            it.data?.let {
                                var str = "My Address:"+ "\n" +
                                         it.nickName + "\n" +
                                        it.streetAddress + "\n" +
                                        it.district + "\n" +
                                        it.province + "\n" +
                                        "(" + it.zipCode + ")" + it.phoneNumber+ "\n" +
                                            "Please pick-up from this address."

                                mChatFragment?.sendAddressCustom(it,"My Address:")
                                userid?.let {
                                    if (isNumeric(it)) {
                                        launch {
                                            mViewModel.releaseRecordDone(
                                                RecordDoneRequest(exchangePersonId = it.toLong(),ids = listSend))
                                                .onStart { mAppViewModel.showLoading() }
                                                .onCompletion { mAppViewModel.dismissLoading() }
                                                .catch { }
                                                .collectLatest {
                                                    if (it.isOk(this@ChatListActivity)) {
                                                        mChatFragment?.sendCustom(listCustomList, getString(R.string.base_head_Agreed))
                                                        listHWantEntity?.clear()
                                                        mHWantAdapter?.notifyDataSetChanged()
                                                        otherPageList()
                                                    }
                                                }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
        }

    }

    private fun deleteCounterparty(mBaseGiftEntity: BaseGiftEntity) {
        mBaseGiftEntity?.releaseRecordId?.let {
            var listPocketNum: java.util.ArrayList<Long> = java.util.ArrayList()
            listPocketNum.add(mBaseGiftEntity.releaseRecordId.toLong())
            launch {
                mViewModel.deleteCounterparty(
                    PocketDeleteRequest(
                        ownerId = mBaseGiftEntity.ownerId.toLong(),
                        releaseRecordIds = listPocketNum
                    )
                )
                    .onStart {
                        mAppViewModel.showLoading()
                    }.catch {
                    }.onCompletion {
                        mAppViewModel.dismissLoading()
                    }.collectLatest {
                        if (it.isOk(this@ChatListActivity)) {

                            var listHWant: ArrayList<BaseGiftEntity>? = ArrayList()
                            listHWant?.add(mBaseGiftEntity)
                            mChatFragment?.sendCustom(
                                listHWant,
                                getString(R.string.base_head_Unavailable)
                            )
                            listHWantEntity?.remove(mBaseGiftEntity)
                            mHWantAdapter?.notifyDataSetChanged()
                            initRlv()
                        }
                    }
            }

        }

    }

    private fun selfPokedPageList() {
        userid?.let {
            if (isNumeric(it)) {
                launch {
                    mViewModel.selfPokedPageList(
                        CommonRequest(
                            pageNum = 1,
                            pageSize = 100,
                            userId = it.toLong()
                        )
                    )
                        .onStart {
                        }.catch {
                        }.onCompletion {
                        }.collectLatest {
                            if (it.isOk(this@ChatListActivity)) {
                                it.data?.poketList?.let {
                                    listIWantEntity?.addAll(it)
                                    mIWantAdapter?.notifyDataSetChanged()
                                    want?.let { itwant->
                                        if(it.size>1){
                                            mChatFragment?.sendCustom(
                                                listIWantEntity,
                                                getString(R.string.base_head_WantTheseProduct)
                                            )
                                        }
                                        if(it.size==1){
                                            mChatFragment?.sendCustom(
                                                listIWantEntity,
                                                getString(R.string.base_head_WantProduct)
                                            )
                                        }


                                    }
                                }

                                initRlv()
                            }
                        }
                }
            }

        }
    }


    private fun otherPageList() {
        userid?.let {
            if (isNumeric(it)) {
                launch {
                    mViewModel.otherPageList(
                        CommonRequest(
                            pageNum = 1,
                            pageSize = 100,
                            userId = it.toLong()
                        )
                    )
                        .onStart {
                        }.catch {
                        }.onCompletion {
                        }.collectLatest {
                            if (it.isOk(this@ChatListActivity)) {
                                it.data?.poketList?.let {
                                    listHWantEntity?.addAll(it)
                                    mHWantAdapter?.notifyDataSetChanged()
                                }
                                initRlv()
                            }
                        }
                }
            }

        }
    }

    fun isNumeric(str: String): Boolean {
        for (i in str.indices) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }

    private fun initRlv() {
        mBinding?.run {
            if (listIWantEntity?.size == 0 && listHWantEntity?.size == 0) {
                cl_bg.visibility = View.GONE
                ChatAddSend.visibility = View.GONE
                ChatAddress.visibility = View.GONE
            } else if (listIWantEntity?.size!! > 0 && listHWantEntity?.size == 0) {
                cl_bg.visibility = View.VISIBLE
                ChatListIWantOnly.visibility = View.VISIBLE
                ChatListHeWantOnly.visibility = View.GONE
                chatAllLy.visibility = View.GONE

                iWantList.visibility = View.VISIBLE
                heWantsList.visibility = View.GONE

                ChatAddSend.visibility = View.GONE
                ChatAddress.visibility = View.GONE

            } else if (listIWantEntity?.size == 0 && listHWantEntity?.size!! > 0) {
                cl_bg.visibility = View.VISIBLE
                ChatListIWantOnly.visibility = View.GONE
                ChatListHeWantOnly.visibility = View.VISIBLE
                chatAllLy.visibility = View.GONE

                iWantList.visibility = View.GONE
                heWantsList.visibility = View.VISIBLE

                ChatAddSend.visibility = View.VISIBLE
                ChatAddress.visibility = View.GONE
            } else if (listIWantEntity!!.size > 0 && listHWantEntity?.size!! > 0) {
                cl_bg.visibility = View.VISIBLE
                ChatListIWantOnly.visibility = View.GONE
                ChatListHeWantOnly.visibility = View.GONE
                chatAllLy.visibility = View.VISIBLE

                ChatListIWant.setTextColor(
                    ContextCompat.getColor(
                        this@ChatListActivity,
                        com.earth.libhome.R.color.BaseThemColor
                    )
                )
                ChatListIWantLine.visibility = View.VISIBLE

                ChatListHeWants.setTextColor(
                    ContextCompat.getColor(
                        this@ChatListActivity,
                        com.earth.libhome.R.color.base_login_un
                    )
                )
                ChatListHeWantsLine.visibility = View.GONE

                iWantList.visibility = View.VISIBLE
                heWantsList.visibility = View.GONE

                ChatAddSend.visibility = View.GONE
                ChatAddress.visibility = View.GONE
            }
            mChatFragment?.scrollToEnd()
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getPopularityList() {
        mGetUserJob?.cancel()
        mGetUserJob = launch {
            mViewModel.loadPopularityData()
                .catch {
                }
                .collectLatest {
                    if (flag == true) {
                        mChatFragment?.scrollToEnd()
                        flag = false
                    }
                    mPAdapter.submitData(it)
                }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: SendGiftEvent?) {
        if (event?.send == true) {
        /*    sendGift(
                event?.CustomHelloMessage?.identificationID!!.toLong(),
                event.CustomHelloMessage?.formUser!!.toLong(),
                event?.CustomHelloMessage!!
            )*/
        } else {
          /*  checkSendGift(
                event?.CustomHelloMessage?.identificationID!!.toLong(),
                event?.CustomHelloMessage!!
            )*/
        }
    }

    private fun sendGift(
        exchangePersonId: Long,
        id: Long,
        mCustomHelloMessage: CustomHelloMessage
    ) {
        launch {
            mEcoGiftGorupsModel.sendGift(id, exchangePersonId)
                .catch { }
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .collectLatest {
                    if (it.isOk(this@ChatListActivity)) {
                        EventBus.getDefault().post(mCustomHelloMessage)

                    } else {
                        toast(it.msg)
                    }
                }
        }
    }

    private fun checkSendGift(id: Long, mCustomHelloMessage: CustomHelloMessage) {
        launch {
            mEcoGiftGorupsModel.checkSendGift(id)
                .catch { }
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .collectLatest {
                    if (it.isOk(this@ChatListActivity)) {
                        it.data?.let {
                            if (it.isExchange == 1) {
                                toast("Sorry, this item has been exchanged")
                            } else {
                                EventBus.getDefault().post(mCustomHelloMessage)
                            }
                        }
                        //  EventBus.getDefault().post(mCustomHelloMessage)
                    }
                }
        }
    }

    private fun chat() {

        /*   final OfflineMessageBean bean = OfflineMessageDispatcher.parseOfflineMessage(intent);
        if (bean != null) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.cancelAll();
            }
        }
*/
        if (V2TIMManager.getInstance().loginStatus != V2TIMManager.V2TIM_STATUS_LOGINED) {
            //       startSplashActivity(bundle)
            finish()
            return
        }
        //   mChatInfo = bundle!!.getSerializable(Constants.CHAT_INFO) as ChatInfo?

        if (userid != null) {
            val chatInfo = ChatInfo()
            chatInfo.type = V2TIMConversation.V2TIM_C2C
            chatInfo.id = userid
            mUserid = userid
            chatName?.let {
                chatInfo.chatName = it
            }
            mChatInfo = chatInfo
        }
        val bundle = Bundle()
        bundle.putSerializable(Constants.CHAT_INFO, mChatInfo)

        if (mChatInfo != null) {
            mChatFragment = ChatFragment()
            mChatFragment!!.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.empty_list_view, mChatFragment!!)
                .commitAllowingStateLoss()
            tvTitleCenter.text = mChatInfo!!.chatName
            //tvName.text = mChatInfo!!.chatName + "’s eco gifts"
            if (TimeForUtils.isNumericZidai(mChatInfo!!.id)) {
                //getOneData(mChatInfo!!.id.toLong())
            }
            mChatInfo = null
        } else {
            finish()
        }
    }

    private fun updateAddress() {
        launch {
            mEcoGiftGorupsModel.shoppingAddressIndex()
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@ChatListActivity)) {
                        if (it.data == null) {
                            ARouter.getInstance().build(Constance.LibMineShippingAddressActivityURL)
                                .navigation()
                        } else {
                            it.data?.let {
                                var str = it.nickName + "\n" +
                                        it.streetAddress + "\n" +
                                        it.district + "\n" +
                                        it.province + "\n" +
                                        "(" + it.zipCode + ")" + it.phoneNumber
                                mChatFragment?.sendAddressCustom(it,"My Address:")
                            }

                        }
                    }
                }
        }
    }

    companion object {

        var mUserid: String? = null
        fun startActivity(userid: String?, chatName: String?) {
            val chatInfo = ChatInfo()
            chatInfo.type = V2TIMConversation.V2TIM_C2C
            chatInfo.id = userid
            mUserid = userid
            chatInfo.chatName = chatName
            val intent = Intent(EarthAngelApp.instance, ChatListActivity::class.java)
            intent.putExtra(Constants.CHAT_INFO, chatInfo)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            EarthAngelApp.instance.startActivity(intent)
        }

        fun startActivity(userid: String?, chatName: String?, type: String?) {
            val chatInfo = ChatInfo()
            chatInfo.type = V2TIMConversation.V2TIM_C2C
            chatInfo.id = userid
            mUserid = userid
            chatInfo.chatName = chatName
            val intent = Intent(EarthAngelApp.instance, ChatListActivity::class.java)
            intent.putExtra(Constants.CHAT_INFO, chatInfo)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(USER_SHARE, type)
            EarthAngelApp.instance.startActivity(intent)
        }

        fun startActivity(userid: String?, chatName: String?, bean: GiftEntity?) {
            val chatInfo = ChatInfo()
            chatInfo.type = V2TIMConversation.V2TIM_C2C
            chatInfo.id = userid
            mUserid = userid
            chatInfo.chatName = chatName
            val intent = Intent(EarthAngelApp.instance, ChatListActivity::class.java)
            intent.putExtra(Constants.CHAT_INFO, chatInfo)
            intent.putExtra(Constants.CHAT_CUSTOM, bean)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            EarthAngelApp.instance.startActivity(intent)
        }
    }

    private fun getPhoto() {
        val permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsList.add(1, Manifest.permission.CAMERA)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ClickUtils.clear()
    }
}