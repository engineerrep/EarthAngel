package com.earth.angel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.earth.angel.appphoto.PhotoEditActivity
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.chat.ContactListViewModel
import com.earth.angel.chat.push.ThirdPushTokenMgr
import com.earth.angel.databinding.ActivityMainPhotoBinding
import com.earth.angel.event.*
import com.earth.angel.gift.adapter.MainListPagingAdapter
import com.earth.angel.gift.adapter.RecommendMainAdapter
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.gift.ui.fragment.EcoGiftGroupsFragment
import com.earth.angel.mine.MineFragment
import com.earth.angel.mine.MineModel
import com.earth.angel.share.ShareActivity
import com.earth.angel.user.UserModel
import com.earth.angel.user.ui.fragment.UserFollowingFragment
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.angel.util.ScreenUtil
import com.earth.angel.util.TimeForUtils
import com.earth.angel.view.CustomPopupWindow
import com.earth.angel.view.OnItemClick
import com.earth.angel.view.PopupWindowUtil
import com.earth.libbase.Config
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.entity.CustomMessage
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.entity.UserInfoAddEntity
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import com.hjq.xtoast.XToast
import com.tencent.imsdk.v2.*
import com.tencent.qcloud.tim.uikit.TUIKit
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainPhotoActivity : BaseActivity<ActivityMainPhotoBinding>() {

    private val mMainListPAdapter by lifecycleScope.inject<MainListPagingAdapter>()
    private val mViewModel by viewModel<ContactListViewModel>()
    private val userModle by viewModel<UserModel>()
    private val mMineModel by viewModel<MineModel>()
    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()
    private var mChatListAdapter: RecommendMainAdapter? = null
    private var listGiftEntity: ArrayList<GiftEntity>? = ArrayList()
    private var customPopupWindow: CustomPopupWindow? = null
    private  var position = 0
    private var mIntent: Intent? = null
    override fun getLayoutId(): Int = R.layout.activity_main_photo
    override fun initActivity(savedInstanceState: Bundle?) {
        hideActionBar()
        ScreenUtil.setAndroidNativeLightStatusBar(this@MainPhotoActivity, true)
        mBinding.run {
            handler = this@MainPhotoActivity
            val lp: ConstraintLayout.LayoutParams =
                llMainPhoto.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(0, ScreenUtil.getActivityMessageHeight(this@MainPhotoActivity), 0, 0)
            llMainPhoto.layoutParams = lp
            ivEditPhoto.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    DataReportUtils.getInstance().report("Click_post")
                    PhotoEditActivity.openPhotoEditActivity(this@MainPhotoActivity, true)
                }
            }
            lyMain.visibility=View.VISIBLE
            llTabOne.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    mIntent = Intent(this@MainPhotoActivity, EcoGiftGroupsFragment::class.java)
                    mIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(mIntent)
                }
            }
            llPost.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    PhotoEditActivity.openPhotoEditActivity(this@MainPhotoActivity, true)
                }
            }
            llTabFive.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    mIntent = Intent(this@MainPhotoActivity, MineFragment::class.java)
                    mIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(mIntent)
                }
            }
            llShare.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    EarthAngelApp.myProfileEntity.let {
                        mIntent = Intent(this@MainPhotoActivity, ShareActivity::class.java)
                        mIntent?.putExtra("mUserID", it?.id)
                        mIntent?.putExtra("mUserNAme", it?.nickName)
                        mIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(mIntent)
                    }

                }
            }
            customPopupWindow = CustomPopupWindow.Builder(this@MainPhotoActivity)
                .setContentView(R.layout.pop_eco_gift)
                .setwidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setheight(LinearLayout.LayoutParams.WRAP_CONTENT)
                .build()
            TimeForUtils.setText(
                this@MainPhotoActivity, getString(R.string.text_Upload), tvUpload,
                7, 15,
                View.OnClickListener {
                   /* customPopupWindow?.showAsDropDown(
                        tvUpload,
                        0, -0, 0
                    )
                    toast(""+customPopupWindow?.getheight())*/
                    PopupWindowUtil.showPopupWindow(this@MainPhotoActivity,tvUpload, OnItemClick {
                        viewTransparent.visibility=View.GONE

                    })
                    viewTransparent.visibility=View.VISIBLE
                }

            )
         /*
            TimeForUtils.setText(
                this@MainPhotoActivity, getString(R.string.text_shape), tvShape,
                14, 24, 37, getString(R.string.text_shape).length,
                null, null
            )*/
            select()
            val layoutManager1 = LinearLayoutManager(this@MainPhotoActivity).apply {
                  orientation = LinearLayoutManager.HORIZONTAL
              }
          /*  val layoutManager1: LinearLayoutManager =
                object : LinearLayoutManager(this@MainPhotoActivity, HORIZONTAL, false) {

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }*/
            mChatListAdapter = RecommendMainAdapter(this@MainPhotoActivity, listGiftEntity,
                upDade = {
                    if (position < listGiftEntity!!.size) {
                        position++
                        rvOnePopularity.scrollToPosition(position)
                    } else {
                        position = 0
                        rvOnePopularity.scrollToPosition(position)
                    }
                })
            rvOnePopularity.layoutManager = layoutManager1
            rvOnePopularity.adapter = mMainListPAdapter
            getPopularityList()
            mChatListAdapter?.setOnItemClickListener { adapter, view, position ->
                startActivity(
                    Intent(
                        this@MainPhotoActivity,
                        GiftDetailsActivity::class.java
                    ).putExtra("GoodId", listGiftEntity!![position].id)
                )
            }

        }
        login()
        friendList()
        getShareShow()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this@MainPhotoActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@MainPhotoActivity)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: MessageEvent) {
        friendList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShareShowEvent(event: ShareShowEvent) {
        getShareShow()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMainMessageEvent(event: MainMessageEvent) {
        if (event.message == 0) {
            mBinding?.tvNumFriendOne.visibility = View.GONE
        } else {
            mBinding?.tvNumFriendOne.visibility = View.VISIBLE
            mBinding?.tvNumFriendOne.text = event.message.toString()
        }
    }



    private fun friendList() {
        launch {
            userModle.following(1, 100).catch { }
                .collect {
                    if (it.data?.list.isNullOrEmpty()) {
                        mBinding?.tvNumFriend?.visibility = View.GONE
                    } else {
                        mBinding?.tvNumFriend?.visibility = View.VISIBLE
                        mBinding?.tvNumFriend?.text = it.data?.list?.size.toString()
                    }
                }
        }
    }

    private fun select() {
        launch {
            mMineModel.selectMine()
                .catch {}.collectLatest {
                    if (it.isOk(this@MainPhotoActivity)) {
                        it.data?.let { mine ->
                            mine.nickName?.let {
                                PreferencesHelper.saveName(this@MainPhotoActivity, it)
                            }
                            mine.id?.let {
                                PreferencesHelper.saveUserId(this@MainPhotoActivity, it)
                            }
                            mine.headImgUrl?.let {
                                PreferencesHelper.saveHead(this@MainPhotoActivity, it)
                            }
                            PreferencesHelper.saveMyProfileCache(
                                this@MainPhotoActivity,
                                Gson().toJson(mine)
                            )
                        }
                    }
                }
        }
    }

    // 分享按钮是 否显示
    private fun getShareShow() {
        mBinding.llShare.visibility = View.INVISIBLE
        launch {
            mMineModel.tradablePageList(1, 4, null)
                .catch {
                }.collectLatest {
                    if (it.isOk(this@MainPhotoActivity)) {
                        it.data?.let { list ->
                            if (list.isNotEmpty()) {
                                for (item in list) {
                                    if (item.isExchange == 0) {
                                        mBinding.llShare.visibility = View.VISIBLE
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun login() {
        TUIKit.login(
            PreferencesHelper.getUserId(this@MainPhotoActivity),
            PreferencesHelper.getImtoken(this@MainPhotoActivity),
            object : IUIKitCallBack {
                override fun onSuccess(data: Any?) {
                    prepareThirdPushToken()
                    V2TIMManager.getMessageManager()
                        .addAdvancedMsgListener(object : V2TIMAdvancedMsgListener() {
                            override fun onRecvNewMessage(msg: V2TIMMessage?) {
                                super.onRecvNewMessage(msg)
                                var cus = msg?.customElem
                                cus?.data?.let {
                                    val s = String(cus?.data!!)
                                    val user: CustomMessage =
                                        Gson().fromJson(s, CustomMessage::class.java)
                                            if(user.customCellType=="7"){
                                                user?.userInfo?.let {
                                                    if (it == "") {
                                                        return
                                                    }
                                                    val userInfo: UserInfoAddEntity =
                                                        Gson().fromJson(it, UserInfoAddEntity::class.java)
                                                    if (msg?.sender == Config.ADMIN_NAME) {
                                                        //     mBinding?.tvNumFriend?.visibility = View.VISIBLE
                                                        val intent = Intent(
                                                            this@MainPhotoActivity,
                                                            UserFollowingFragment::class.java
                                                        )
                                                        val pendingIntent =
                                                            PendingIntent.getActivity(
                                                                this@MainPhotoActivity,
                                                                0,
                                                                intent,
                                                                0
                                                            )
                                                        showNotification(
                                                            this@MainPhotoActivity,
                                                            getString(R.string.lab_Friend_Requests),
                                                            user.text,
                                                            pendingIntent
                                                        )
                                                        EventBus.getDefault().post(MessageEvent())
                                                        showToast(userInfo, user.text)

                                                    }
                                                }
                                            }
                                }
                            }
                        })
                    V2TIMManager.getConversationManager()
                        .setConversationListener(object : V2TIMConversationListener() {
                            override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>?) {
                                super.onConversationChanged(conversationList)
                                checkMessage()
                                EventBus.getDefault().post(ConversationListChangeEvent())
                            }
                        })
                }

                override fun onError(module: String?, errCode: Int, errMsg: String?) {
                }
            })
    }

    private fun prepareThirdPushToken() {
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM()
    }

    private fun showNotification(
        context: Context,
        contentTitle: String,
        contentText: String,
        intent: PendingIntent
    ) {
        val channelId: Int = Random().nextInt(543254)
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId.toString())
        builder.setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(intent)
            .setTicker("") //通知首次出现在通知栏，带上升动画效果的
            .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
            .setAutoCancel(true) //设置这个标志当用户单击面板就可以让通知将自动取消
            .setOngoing(false) //ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            .setDefaults(Notification.DEFAULT_VIBRATE) //向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "EarthAngel"
            val mChannel =
                NotificationChannel(channelId.toString(), name, NotificationManager.IMPORTANCE_HIGH)
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel)
                builder.setChannelId(channelId.toString())
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.small_icon) //需要使用背景透明，图标为纯白色的图标。不然在很多机型上，如果直接使用应用icon,会直接显示纯白色图标，体验会不好。
        } else {
            builder.setSmallIcon(R.mipmap.small_icon)
        }
        notificationManager?.notify(channelId * 10, builder.build())
    }

    private var exitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            toast(R.string.exitapp)
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

    private fun checkMessage() {
        var sum = 0
        V2TIMManager.getConversationManager().getConversationList(
            0,
            50,
            object : V2TIMValueCallback<V2TIMConversationResult?> {
                override fun onSuccess(t: V2TIMConversationResult?) {
                    t?.conversationList?.let {
                        for (item in it) {
                            sum += item.unreadCount
                        }
                        if (sum > 0) {
                            mBinding?.tvNumFriendOne?.visibility = View.VISIBLE
                            mBinding?.tvNumFriendOne?.text = sum.toString()
                        } else {
                            mBinding?.tvNumFriendOne?.visibility = View.GONE
                        }
                    }
                }
                override fun onError(code: Int, desc: String?) {
                }
            })
    }

    open fun showToast(mUserInfoAddEntity: UserInfoAddEntity, text: String) {
        mUserInfoAddEntity?.let {
            XToast<XToast<*>>(ActivityStackManager.getTopActivity()).apply {
                setContentView(R.layout.toast_addfriend)
                // 设置成可拖拽的
                //setDraggable()
                // 设置显示时长
                setDuration(3000)
                // 设置动画样式
                //setAnimStyle(android.R.style.Animation_Translucent)
                // 设置外层是否能被触摸
                //setOutsideTouchable(false)
                // 设置窗口背景阴影强度
                //setBackgroundDimAmount(0.5f)
                // setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                setText(R.id.tvName, it.userName)
                setText(R.id.tvNameID, text)
                Glide.with(EarthAngelApp.instance)
                    .load(it.userFace)
                    .into(decorView.findViewById(R.id.ivHead))
                setOnClickListener(
                    R.id.btSaveShare,
                    XToast.OnClickListener<TextView?> { toast: XToast<*>, view: TextView? ->
                        // 点击这个 View 后消失
                        like(it.userId)
                        toast.cancel()
                        // 跳转到某个Activity
                        // toast.startActivity(intent);
                    })
                setOnClickListener(
                    R.id.btDelete,
                    XToast.OnClickListener<TextView?> { toast: XToast<*>, view: TextView? ->
                        // 点击这个 View 后消失
                        toast.cancel()
                        // 跳转到某个Activity
                        // toast.startActivity(intent);
                    })
            }.show()
        }

    }

    private fun like(userid: String) {
        launch {
            userid.let {
                userModle.agreeToBeFriends(it.toLong()).catch {}
                    .onStart { }
                    .onCompletion { }
                    .collectLatest {
                        if (it.isOk(this@MainPhotoActivity)) {

                            EventBus.getDefault().post(MessageEvent())
                            EventBus.getDefault().post(GroupCreatEvent())

                        }
                    }
            }

        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getPopularityList() {
       launch {
           mViewModel.loadMainListData()
                .catch {
                }
                .collectLatest {
                    mMainListPAdapter.submitData(it)
                }
        }
    }
}