package com.earth.angel.gift.ui.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.chat.CustomHelloMessage
import com.earth.angel.databinding.FragmentEcogiftgroupsBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.dialog.DialogContacts
import com.earth.angel.dialog.dialoginterface.DialogContactsInterface
import com.earth.angel.event.ConversationListChangeEvent
import com.earth.angel.event.GroupCreatEvent
import com.earth.angel.event.MainMessageEvent
import com.earth.angel.gift.adapter.MsgRemindAdapter
import com.earth.angel.gift.ui.*
import com.earth.angel.message.ui.NotificationActivity
import com.earth.angel.search.SearchActivity
import com.earth.angel.user.UserModel
import com.earth.angel.user.ui.FriendListActivity
import com.earth.angel.util.ContactUtils
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.angel.util.ScreenUtil.getActivityMessageHeight
import com.earth.angel.view.CustomPopupWindow
import com.earth.libbase.Config.ADMIN_NAME
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.HouseListEntity
import com.earth.libbase.network.request.PhotoAddRequest
import com.earth.libbase.util.OnGroupListener
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.tencent.imsdk.v2.*
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_TEXT
import kotlinx.android.synthetic.main.fragment_ecogiftgroups.*
import kotlinx.android.synthetic.main.head_ecogiftgroups.view.*
import kotlinx.android.synthetic.main.search_layout.*
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
import java.util.*


class EcoGiftGroupsFragment : BaseActivity<FragmentEcogiftgroupsBinding>() {

    private val mEcoGiftModel by viewModel<EcoGiftGorupsModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private val userModle by viewModel<UserModel>()
    private var adapterGroups: MsgRemindAdapter? = null
    private var listGroup: ArrayList<HouseListEntity> = ArrayList()
    private var pageNum: Int = 1
    private var position: Int? = null
    private var headView: View? = null
    private var JsonOldstr: String? = null
    private var listOldstr: List<HouseListEntity>? = null
    private var textTitle: String? = null
    private var customPopupWindow: CustomPopupWindow? = null
    private var mPW: DialogContacts? = null

    override fun getLayoutId(): Int = R.layout.fragment_ecogiftgroups
    override fun initActivity(savedInstanceState: Bundle?) {
        hideActionBar()
        EventBus.getDefault().register(this)
        mBinding?.run {
            handler = this@EcoGiftGroupsFragment
            mAppViewModel?.showLoadingProgress.observe(this@EcoGiftGroupsFragment, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
            tvLeftTool.setOnClickListener {
                finish()
            }
            val lp: LinearLayout.LayoutParams = rlEcoTopToolbar.layoutParams as LayoutParams
            lp.setMargins(0, getActivityMessageHeight(this@EcoGiftGroupsFragment), 0, 0)
            rlEcoTopToolbar.layoutParams = lp
            //requireActivity().toast(""+getActivityMessageHeight(this@EcoGiftGroupsFragment))
            headView = layoutInflater.inflate(R.layout.head_ecogiftgroups, null)
            customPopupWindow = CustomPopupWindow.Builder(this@EcoGiftGroupsFragment)
                .setContentView(R.layout.pop_add)
                .setwidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setheight(LinearLayout.LayoutParams.WRAP_CONTENT)
                .build()
            val llchat = customPopupWindow?.getItemView(R.id.ll_chat) as LinearLayout
            llchat.setOnClickListener {
                customPopupWindow?.dismiss()
                if (DateUtils.isFastClick()) {
                    startActivity.launch(
                        Intent(
                            this@EcoGiftGroupsFragment,
                            FriendListActivity::class.java
                        ).putExtra("type", FriendListActivity.group)
                            .putExtra("houseNumber", 0)
                    )
                }
            }
            val llSearch = customPopupWindow?.getItemView(R.id.ll_search) as LinearLayout
            llSearch.setOnClickListener {
                customPopupWindow?.dismiss()
                if (DateUtils.isFastClick()) {
                    return@setOnClickListener
                }
                DataReportUtils.getInstance().report("Search")
                SearchActivity.openSearchActivity(this@EcoGiftGroupsFragment)
            }
            // 加入的
            val layoutManager1 = LinearLayoutManager(this@EcoGiftGroupsFragment)
            rlvList.layoutManager = layoutManager1
            adapterGroups =
                MsgRemindAdapter(this@EcoGiftGroupsFragment, listGroup, object :
                    OnGroupListener {
                    override fun onItemClick(position: Int) {
                        if (DateUtils.isFastClick()) {
                            if (listGroup[position].type == 0) {
                                startActivity.launch(
                                    Intent(
                                        this@EcoGiftGroupsFragment,
                                        GroupDetailsActivity::class.java
                                    ).putExtra("giftHouse", listGroup[position])
                                        .putExtra("textTitle", textTitle)
                                )
                            } else {
                                if (listGroup[position].number == ADMIN_NAME) {
                                    startActivity.launch(
                                        Intent(
                                            this@EcoGiftGroupsFragment,
                                            NotificationActivity::class.java
                                        ).putExtra("userId", listGroup[position].number)
                                    )
                                } else {
                                    UserMainActivity.openUserMainActivity(
                                        this@EcoGiftGroupsFragment,
                                        listGroup[position]
                                    )
                                }

                            }
                        }

                    }

                    override fun onDeleteClick(position: Int) {
                        if (listGroup[position].type == 0) {
                            var blockDialog =
                                DialogCommon(
                                    title = getString(R.string.dialog_delete),
                                    onBlockClick = {
                                        quitGroup(listGroup[position].number.toLong(), position)
                                    })
                            blockDialog.show(
                                this@EcoGiftGroupsFragment.supportFragmentManager, ""
                            )
                        } else {
                            //删除
                            var blockDialog =
                                DialogCommon(
                                    title = getString(R.string.dialog_delete),
                                    onBlockClick = {
                                        if (listGroup[position].number != ADMIN_NAME) {
                                            deleteFriend(
                                                listGroup[position].number.toLong(),
                                                position
                                            )
                                        }
                                    })
                            blockDialog.show(
                                this@EcoGiftGroupsFragment.supportFragmentManager, ""
                            )

                        }
                    }
                })
            rlvList.adapter = adapterGroups
            srl?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    notifyData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getJoined()
                }
            })
            clSearch.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    DataReportUtils.getInstance().report("Search")
                    SearchActivity.openSearchActivity(this@EcoGiftGroupsFragment)
                }

            }
            ivEcoRightTool.setOnClickListener {
                //  AddGroupActivity.openAddGroupActivity(requireActivity())
                customPopupWindow?.showAsDropDown(
                    ivEcoRightTool,
                    -100, 0, 0
                )
            }

            initContacts()
        }
        mAppViewModel.showLoading()
        getJoined()
    }

    private fun initTitle(sum: Int) {
        if (sum == 0) {
            mBinding?.tvEcoTitleCenter?.text = "EARTHANGEL"
        } else {
            mBinding?.tvEcoTitleCenter?.text = "EARTHANGEL($sum)"
        }
    }

    private fun initContacts() {

        mBinding?.run {
            if (PreferencesHelper.getContacts(this@EcoGiftGroupsFragment) == false) {
                lyConnect.visibility = View.VISIBLE
            } else {
                lyConnect.visibility = View.GONE
            }
            lyConnect.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    getPermissions()
                }
            }
        }
    }

    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_CONTACTS)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                if (mPW == null) {
                    mPW = DialogContacts(this@EcoGiftGroupsFragment)
                        .setOnInterface(object : DialogContactsInterface {
                            override fun cancel() {
                                mPW = null
                            }

                            override fun ok() {
                                var bean =
                                    PhotoAddRequest(ContactUtils.getAllContacts(this@EcoGiftGroupsFragment))
                                //   requireActivity().toast("success"+bean.insertDTOList?.size)
                                //新建一个联系人实例
                                if (bean.insertDTOList?.size == 0) {
                                    toast("The contact is empty")
                                } else {
                                    upData(bean)
                                }
                            }
                        })
                        .create()
                    mPW?.show()
                }

            }
            onPermissionsDenied = {

            }
            onPermissionsNeverAsked = {

            }
        }
    }

    private fun upData(photoAddRequest: PhotoAddRequest) {
        launch {
            userModle.mobilePhoneAddressBookAdd(photoAddRequest).catch {
            }
                .onStart { mAppViewModel.showLoading() }
                .onCompletion {
                    mPW?.disMiss()
                    mPW = null
                    mAppViewModel.dismissLoading()
                }
                .collectLatest {
                    PreferencesHelper.saveContacts(this@EcoGiftGroupsFragment, true)
                    clConnect.visibility = View.GONE

                    if (it.isOk(this@EcoGiftGroupsFragment)) {
                        toast(getString(R.string.lab_Success))
                    }
                }
        }
    }


    private fun notifyData() {
        listGroup.clear()
        adapterGroups?.notifyDataSetChanged()
        pageNum = 1
        getJoined()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: GroupCreatEvent) {
        if (event.isConcern == null) {
            notifyData()
        } else {
            if (position == null) {
                notifyData()
            } else {
                position?.let {
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConversationListChangeEvent(msg: ConversationListChangeEvent) {
        checkMessage()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

    }

    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                100 -> {
                    notifyData()
                }
                101 -> {
                }
                102 -> {

                }
            }
        }

    private fun deleteFriend(userid: Long, position: Int) {
        launch {
            userid?.let {
                userModle.deleteFriend(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@EcoGiftGroupsFragment)) {
                            adapterGroups?.removeData(position)
                            adapterGroups?.notifyDataSetChanged()
                        }
                    }
            }

        }
    }

    private fun quitGroup(houseNumber: Long, position: Int) {
        launch {
            mEcoGiftModel.quitGroup(houseNumber).catch { }
                .collectLatest {
                    if (it.isOk(this@EcoGiftGroupsFragment)) {
                        adapterGroups?.removeData(position)
                        adapterGroups?.notifyDataSetChanged()
                    }
                }
        }
    }

    private fun getJoined() {
        JsonOldstr = PreferencesHelper.getJson(this@EcoGiftGroupsFragment)
        val gson = Gson()
        listOldstr =
            gson.fromJson(JsonOldstr, object : TypeToken<List<HouseListEntity?>?>() {}.type)
        // adapterGroups?.setList(listOldstr)
        launch {
            mEcoGiftModel.indexPageList(pageNum, 100).onStart {
            }.catch {
            }.onCompletion {
                mAppViewModel?.dismissLoading()
            }.collectLatest {

                if (it.isOk(this@EcoGiftGroupsFragment)) {

                    it.data?.let {
                        listGroup.addAll(it)
                        val gson = Gson()
                        val user = gson.toJson(listGroup)
                        PreferencesHelper.saveJson(this@EcoGiftGroupsFragment, user)
                        adapterGroups?.notifyDataSetChanged()
                        checkMessage()
                    }

                    mBinding?.srl?.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                }
            }
        }
    }

    private fun checkMessage() {
        V2TIMManager.getConversationManager().getConversationList(
            0,
            50,
            object : V2TIMValueCallback<V2TIMConversationResult?> {
                override fun onSuccess(t: V2TIMConversationResult?) {
                    var sum1 = 0
                    var sum2 = 0
                    var sumAll = 0
                    t?.conversationList?.let {
                        // 把未读消息  放到数据上去，再按时间排序 把ADMIN_NAME 排除了，下面有计算
                        for (item in it) {
                            for (bean in listGroup) {
                                if (item.userID == bean.number && bean.number != ADMIN_NAME) {
                                    bean.message = item.unreadCount
                                    item.lastMessage?.timestamp?.let {
                                        bean.messageTime = it
                                    }
                                    when (item.lastMessage.elemType) {

                                        V2TIM_ELEM_TYPE_TEXT -> {
                                            bean.lastMesssage = item.lastMessage.textElem.text
                                        }

                                        V2TIM_ELEM_TYPE_CUSTOM -> {
                                            item.lastMessage.customElem.data.let {
                                                val s = String(item.lastMessage.customElem?.data!!)
                                                val mCustomMessage: CustomHelloMessage =
                                                    Gson().fromJson(
                                                        s,
                                                        CustomHelloMessage::class.java
                                                    )
                                           /*     if (mCustomMessage.isSend.isNullOrEmpty()) {
                                                    if (mCustomMessage.customCellType.equals("0")) {
                                                        bean.lastMesssage = "I want this"
                                                    } else if (mCustomMessage.customCellType.equals(
                                                            "1"
                                                        )
                                                    ) {
                                                        bean.lastMesssage =
                                                            "Check my eco gifts for you"
                                                    }
                                                } else {
                                                    if (mCustomMessage.isSend.equals("0")) {
                                                        bean.lastMesssage = "No,I won't."
                                                    } else if (mCustomMessage.isSend.equals("1")) {
                                                        bean.lastMesssage = "Yes,I will."
                                                    }
                                                }*/
                                            }
                                        }
                                    }
                                    sum1 += item.unreadCount // 总的未读数
                                }
                            }
                            // 遍历会话列表
                        }

                        for (item in it) {
                            if (item.userID == ADMIN_NAME) {
                                sum2 += item.unreadCount // 总的未读数

                                // 把notification取出来
                                var mHouseListEntity = HouseListEntity(
                                    type = 1,
                                    name = item.showName,
                                    imgUrl = arrayListOf(item.faceUrl),
                                    number = item.userID,
                                    releaseTime = 0,
                                    releasesNumber = 0,
                                    requestFrendsStatus = true,
                                    members = 0,
                                    message = sum2,
                                    lastMesssage = "",
                                    messageTime = 0
                                )

                                // 把最后一条消息选出来
                                var lsatmessage = ""
                                when (item.lastMessage.elemType) {
                                    V2TIM_ELEM_TYPE_CUSTOM -> {
                                        item.lastMessage.customElem.data.let {
                                            val s = String(item.lastMessage.customElem?.data!!)
                                            val mCustomMessage: CustomHelloMessage =
                                                Gson().fromJson(s, CustomHelloMessage::class.java)
                                            lsatmessage = mCustomMessage.text
                                        }

                                    }
                                }
                                //遍历列表如果没有就放进去
                                var flagNotication = false//
                                for (bean in listGroup) {
                                    if (bean.number == ADMIN_NAME) {
                                        bean.message = sum2
                                        bean.lastMesssage = lsatmessage
                                        item.lastMessage?.timestamp?.let {
                                            bean.messageTime = it
                                        }
                                        flagNotication = true
                                        break
                                    }
                                }
                                if (!flagNotication) {
                                    mHouseListEntity.lastMesssage = lsatmessage
                                    item.lastMessage?.timestamp?.let {
                                        mHouseListEntity.messageTime = it
                                    }
                                    listGroup.add(0, mHouseListEntity)
                                }
                            }

                        }
                        listGroup.sort()
                        adapterGroups?.notifyDataSetChanged()
                        sumAll = sum1 + sum2
                        EventBus.getDefault().post(MainMessageEvent(sumAll))
                        initTitle(sumAll)
                        if (listGroup.isEmpty()) {
                            mBinding?.rlvList?.visibility = View.GONE
                            mBinding?.llContacts?.visibility = View.VISIBLE
                        } else {
                            mBinding?.llContacts?.visibility = View.GONE
                            mBinding?.rlvList?.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onError(code: Int, desc: String?) {

                }
            })
    }

}