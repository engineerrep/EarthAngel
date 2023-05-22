package com.earth.angel.chat

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.chat.adapter.ChatListAdapter
import com.earth.angel.chat.adapter.ChatListHeWantsAdapter
import com.earth.angel.databinding.ActivityChatBinding
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.util.Constants
import com.earth.libbase.base.*
import com.earth.libbase.baseentity.BaseChatPageGiftEntity
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.dialog.BaseDialogCommon
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.PocketDeleteRequest
import com.earth.libbase.network.request.RecordDoneRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo
import com.tencent.qcloud.tim.uikit.modules.chat.base.OfflineMessageBean
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.ChatListActivityURL)
class ChatActivity : BaseActivity<ActivityChatBinding>() {

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

    private val mViewModel by viewModel<ContactListViewModel>()
    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()

    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    private var mIWantAdapter: ChatListAdapter? = null
    private var listIWantEntity: ArrayList<BaseGiftEntity>? = ArrayList()
    private var layoutPager1: LinearLayoutManager? = null
    private var layoutPager2: LinearLayoutManager? = null
    private var mHWantAdapter: ChatListHeWantsAdapter? = null
    private var listHWantEntity: ArrayList<BaseGiftEntity>? = ArrayList()
    private var iWantList: RecyclerView? = null
    private var mChatFragment: ChatFragment? = null
    private var mChatInfo: ChatInfo? = null
    private var toPoint: String? = null // 对方分够不够

    override fun getLayoutId(): Int = R.layout.activity_chat

    override fun initActivity(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this@ChatActivity)
      //  getPhoto()
        initview()
        chat(intent)
    }

    private fun initview() {
        showActionBar()
        mBinding?.run {
            mAppViewModel?.showLoadingProgress.observe(this@ChatActivity, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
            mIWantAdapter = ChatListAdapter(this@ChatActivity, listIWantEntity, upDade = {
                poketBagPageList(it)
            })
            layoutPager1 = LinearLayoutManager(
                this@ChatActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            iWantList?.layoutManager = layoutPager1
            iWantList?.adapter = mIWantAdapter
            mHWantAdapter = ChatListHeWantsAdapter(this@ChatActivity, listHWantEntity, upDade = {
                deleteCounterparty(it)
            })
            layoutPager2 = LinearLayoutManager(
                this@ChatActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            heWantsList.layoutManager = layoutPager2
            heWantsList.adapter = mHWantAdapter
            ChatAddSend.setOnClickListener {
                send()
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
                        this@ChatActivity,
                        R.color.BaseThemColor
                    )
                )
                ChatListIWantLine.visibility = View.VISIBLE
                iWantList.visibility = View.VISIBLE
                heWantsList.visibility = View.GONE
                ChatListHeWants.setTextColor(
                    ContextCompat.getColor(
                        this@ChatActivity,
                        R.color.base_login_un
                    )
                )
                ChatListHeWantsLine.visibility = View.INVISIBLE
                ChatAddSend.visibility = View.GONE
                ChatAddress.visibility = View.GONE
            }
            ChatListHeWantsCl?.setOnClickListener {
                ChatListIWant.setTextColor(
                    ContextCompat.getColor(
                        this@ChatActivity,
                        R.color.base_login_un
                    )
                )
                ChatListIWantLine.visibility = View.INVISIBLE
                iWantList.visibility = View.GONE
                heWantsList.visibility = View.VISIBLE
                ChatListHeWants.setTextColor(
                    ContextCompat.getColor(
                        this@ChatActivity,
                        R.color.BaseThemColor
                    )
                )
                ChatListHeWantsLine.visibility = View.VISIBLE
                ChatAddSend.visibility = View.VISIBLE
                ChatAddress.visibility = View.GONE
            }
            tvEditDone.setOnClickListener {
                openEdit(false)
            }
            releaseRecordId?.let {
                getGiftList()
            }
        }
        selfPokedPageList()
        otherPageList()
    }

    /**
     * 我想要的物品
     */
    private fun selfPokedPageList() {
        userid?.let {
            if (isNumeric(it)) {
                launch {
                    mViewModel.selfPokedPageList(
                        CommonRequest(
                            userId = it.toLong()
                        )
                    )
                        .onStart {
                        }.catch {
                        }.onCompletion {
                        }.collectLatest {
                            if (it.isOk(this@ChatActivity)) {
                                it.data?.poketList?.let {
                                    listIWantEntity?.addAll(it)
                                    mIWantAdapter?.notifyDataSetChanged()
                                    want?.let { itwant ->
                                        if (it.size > 1) {
                                            mChatFragment?.sendCustom(
                                                listIWantEntity,
                                                getString(R.string.base_head_WantTheseProduct)
                                            )
                                        }
                                        if (it.size == 1) {
                                            mChatFragment?.sendCustom(
                                                listIWantEntity,
                                                getString(R.string.base_head_WantProduct)
                                            )
                                        }
                                    }
                                }
                                // 自己的分不够要不要编辑
                                it.data?.enough.let {
                                    if (it ==BaseChatPageGiftEntity.NOTENOUGH) {
                                        var title =
                                            "The Eco credit is insufficient, do you want to delete some products?"
                                        val blockDialog = BaseDialogCommon(
                                            title = title,
                                            onBlockClick = {
                                                openEdit(true)
                                            })
                                        blockDialog.show(supportFragmentManager, title)
                                    }
                                }

                                initRlv()
                            }
                        }
                }
            }

        }
    }

    private fun openEdit(show: Boolean) {
        if (show) {
            mBinding.tvEditDone.visibility = View.VISIBLE
            ChatListAdapter.showDelete = show
        } else {
            mBinding.tvEditDone.visibility = View.GONE
            ChatListAdapter.showDelete = show
        }
        mIWantAdapter?.notifyDataSetChanged()

    }

    /**
     * 删除背包的物品
     */
    private fun poketBagPageList(position: Int) {

        var listPocketNum: ArrayList<Long> = ArrayList()
        listPocketNum.add(listIWantEntity?.get(position)!!.releaseRecordId.toLong())
        launch {
            mViewModel.poketBagdelete(PocketDeleteRequest(listPocketNum))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@ChatActivity)) {
                        listIWantEntity?.removeAt(position)
                        mIWantAdapter?.notifyDataSetChanged()
                        initRlv()
                        getUser()
                    }
                }
        }
    }

    private fun getUser() {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mViewModel.userSelectOne(CommonRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@ChatActivity)) {
                            it.data?.let { ituser ->
                                PreferencesHelper.saveMyProfileCache(
                                    BaseApplication.instance,
                                    Gson().toJson(ituser)
                                )
                                ituser.poketNumber?.let {
                                    MessageObservable.messageObservable.newMessage(
                                        UpdateEntity(
                                            pockNum = it
                                        )
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }

    /**
     * 对方想要的物品
     */
    private fun otherPageList() {
        userid?.let {
            if (isNumeric(it)) {
                launch {
                    mViewModel.otherPageList(
                        CommonRequest(
                            userId = it.toLong()
                        )
                    )
                        .onStart {
                        }.catch {
                        }.onCompletion {
                        }.collectLatest {
                            if (it.isOk(this@ChatActivity)) {
                                it.data?.enough?.let{
                                    toPoint=it
                                }
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

    private fun initRlv() {
        mBinding?.run {

            tvLeftTool.setOnClickListener {
                finish()
            }
            chatName?.let {
                tvTitleCenter.text = it
            }
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
                        this@ChatActivity,
                        com.earth.libhome.R.color.BaseThemColor
                    )
                )
                ChatListIWantLine.visibility = View.VISIBLE

                ChatListHeWants.setTextColor(
                    ContextCompat.getColor(
                        this@ChatActivity,
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
                        if (it.isOk(this@ChatActivity)) {
                            it.data?.let {
                                var listCustomList: ArrayList<BaseGiftEntity>? = ArrayList()
                                listCustomList?.add(it)
                                mChatFragment?.sendCustom(
                                    listCustomList,
                                    getString(R.string.base_head_WantProduct)
                                )
                            }
                        }

                    }
            }

        }
    }

    private fun send() {
        // 对方分不够
        if(toPoint== BaseChatPageGiftEntity.NOTENOUGH){
            var title =
                "Its Eco credit is insufficient, do you want to change it?"
            val blockDialog = BaseDialogCommon(
                title = title,
                onBlockClick = {
                    if (BaseDateUtils.isFastClick()){
                        ARouter.getInstance().build(Constance.LibMineUserActivityURL)
                            .withString("userId",BaseApplication.myBaseUser?.userId)
                            .navigation(this,0)
                    }
                })
            blockDialog.show(supportFragmentManager, title)
        }else{

            var listSend: ArrayList<String>? = ArrayList()
            var listCustomList: ArrayList<BaseGiftEntity>? = ArrayList()
            for (item in listHWantEntity!!) {
                if (item.releaseStatus == BaseGiftEntity.DOING) {
                    listSend?.add(item.releaseRecordId)
                    listCustomList?.add(item)
                }
            }
            if (listSend?.size == 0) {
                toast("" + getString(R.string.base_tips_NoGifts))
                return
            }
            var num = listSend?.size
            var str = if (num == 1) {
                "Are You Sure To Give Away $num Product?"
            } else {
                "Are You Sure To Give Away $num Products?"
            }
            val blockDialog1 = BaseDialogCommon(
                title = str,
                onBlockClick = {
                    launch {
                        mEcoGiftGorupsModel.shoppingAddressIndex()
                            .onStart {
                                mAppViewModel.showLoading()
                            }.catch {
                            }.onCompletion {
                                mAppViewModel.dismissLoading()
                            }.collectLatest {
                                if (it.isOk(this@ChatActivity)) {
                                    if (it.data == null) {
                                        ARouter.getInstance().build(Constance.LibMineShippingAddressActivityURL)
                                            .navigation()
                                    } else {
                                        it.data?.let { address ->
                                            var str = "My Address:" + "\n" +
                                                    address.nickName + "\n" +
                                                    address.streetAddress + "\n" +
                                                    address.district + "\n" +
                                                    address.province + "\n" +
                                                    "(" + address.zipCode + ")" + address.phoneNumber + "\n" +
                                                    "Please pick-up from this address."

                                            mChatFragment?.sendAddressCustom(address, "My Address:")
                                            userid?.let {
                                                if (isNumeric(it)) {
                                                    launch {
                                                        mViewModel.releaseRecordDone(
                                                            RecordDoneRequest(
                                                                exchangePersonId = it.toLong(),
                                                                ids = listSend,
                                                                district = address.district,
                                                                nickName = address.nickName,
                                                                phoneNumber = address.phoneNumber,
                                                                streetAddress = address.streetAddress,
                                                                zipCode = address.zipCode,
                                                                province = address.province
                                                            )
                                                        )
                                                            .onStart { mAppViewModel.showLoading() }
                                                            .onCompletion { mAppViewModel.dismissLoading() }
                                                            .catch { }
                                                            .collectLatest {
                                                                if (it.isOk(this@ChatActivity)) {
                                                                    if (listCustomList?.size == 1) {
                                                                        mChatFragment?.sendCustom(
                                                                            listCustomList,
                                                                            getString(R.string.base_head_Agreed)
                                                                        )
                                                                    } else {
                                                                        mChatFragment?.sendCustom(
                                                                            listCustomList,
                                                                            getString(R.string.base_head_Agreeds)
                                                                        )
                                                                    }
                                                                    listHWantEntity?.clear()
                                                                    mHWantAdapter?.notifyDataSetChanged()
                                                                    MessageObservable.messageObservable.newMessage(
                                                                        UpdateEntity(
                                                                            postProduct = "postProduct"
                                                                        )
                                                                    )
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
                })
            blockDialog1.show(supportFragmentManager, str)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            0 ->{
                listIWantEntity?.clear()
                listHWantEntity?.clear()
                initview()
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
                        if (it.isOk(this@ChatActivity)) {

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


    fun isNumeric(str: String): Boolean {
        for (i in str.indices) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }

    override fun onNewIntent(intent: Intent) {
        //  DemoLog.i(TAG, "onNewIntent");
        super.onNewIntent(intent)
        chat(intent)
    }

    override fun onResume() {
        //   DemoLog.i(TAG, "onResume");
        super.onResume()
    }

    private fun chat(intent: Intent) {
        /*   val bundle = intent.extras
           //  DemoLog.i(TAG, "bundle: " + bundle + " intent: " + intent);
           if (bundle == null) {
               startSplashActivity(null)
           }*/
        /*   final OfflineMessageBean bean = OfflineMessageDispatcher.parseOfflineMessage(intent);
        if (bean != null) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.cancelAll();
            }
        }
*/
        /*  if (V2TIMManager.getInstance().loginStatus != V2TIMManager.V2TIM_STATUS_LOGINED) {
              startSplashActivity(bundle)
              finish()
              return
          }
  */
        if (userid != null) {
            val chatInfo = ChatInfo()
            chatInfo.type = V2TIMConversation.V2TIM_C2C
            chatInfo.id = userid
            chatName?.let {
                chatInfo.chatName = it
            }
            mChatInfo = chatInfo
        }
        val bundle = Bundle()
        bundle.putSerializable(Constants.CHAT_INFO, mChatInfo)
        //  mChatInfo = bundle!!.getSerializable(Constants.CHAT_INFO) as ChatInfo?
        /*       if (bean != null) {
            if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CALL) {
           //     DemoLog.i(TAG, "offline push  AV CALL . bean: " + bean);
                startAVCall(bean);
                finish();
                return;
            } else if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CHAT) {
                mChatInfo = new ChatInfo();
                mChatInfo.setType(bean.chatType);
                mChatInfo.setId(bean.sender);
                mChatInfo.setChatName(bean.nickname);
                bundle.putSerializable(Constants.CHAT_INFO, mChatInfo);
            //    DemoLog.i(TAG, "offline push mChatInfo: " + mChatInfo);
            }
        } if {
            mChatInfo = (ChatInfo) bundle.getSerializable(Constants.CHAT_INFO);
          //  DemoLog.i(TAG, "start chatActivity chatInfo: " + mChatInfo);
        }*/
        if (mChatInfo != null) {
            mChatFragment = ChatFragment()
            mChatFragment!!.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.empty_view, mChatFragment!!)
                .commitAllowingStateLoss()
            mChatInfo = null
        } else {
            finish()
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

    private fun startSplashActivity(bundle: Bundle?) {
        /*      Intent intent = new Intent(ChatActivity.this, SplashActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();*/
    }

    private fun startAVCall(bean: OfflineMessageBean) {
        val baseLiveListener = TUIKitLiveListenerManager.getInstance().baseCallListener
        baseLiveListener?.handleOfflinePushCall(bean)
    }

    companion object {
        private val TAG = ChatActivity::class.java.simpleName
        fun startActivity(userid: String?, chatName: String?) {
            val chatInfo = ChatInfo()
            chatInfo.type = V2TIMConversation.V2TIM_C2C
            chatInfo.id = userid
            chatInfo.chatName = chatName
            val intent = Intent(EarthAngelApp.instance, ChatActivity::class.java)
            intent.putExtra(Constants.CHAT_INFO, chatInfo)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            EarthAngelApp.instance.startActivity(intent)
        }

        fun startActivity(userid: String?, chatName: String?, bean: GiftEntity?) {
            val chatInfo = ChatInfo()
            chatInfo.type = V2TIMConversation.V2TIM_C2C
            chatInfo.id = userid
            chatInfo.chatName = chatName
            val intent = Intent(EarthAngelApp.instance, ChatActivity::class.java)
            intent.putExtra(Constants.CHAT_INFO, chatInfo)
            intent.putExtra(Constants.CHAT_CUSTOM, bean)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            EarthAngelApp.instance.startActivity(intent)
        }
    }


}