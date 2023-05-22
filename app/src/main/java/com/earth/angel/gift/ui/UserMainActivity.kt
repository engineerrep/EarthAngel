package com.earth.angel.gift.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.chat.ChatListActivity
import com.earth.angel.databinding.ActivityUserMainBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.event.ShakeEvent
import com.earth.angel.event.ShareSuccessEvent
import com.earth.angel.gift.adapter.UserMainAdapter
import com.earth.angel.mine.MineModel
import com.earth.angel.search.SearchModel
import com.earth.angel.share.modle.ShareModel
import com.earth.angel.user.UserModel
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.angel.util.EventBusHelper
import com.earth.angel.util.ScreenUtil
import com.earth.angel.view.CustomPopupWindow
import com.earth.angel.view.GiftPhotoActivity
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.*
import com.earth.libbase.network.request.SelectUserRequest
import com.earth.libbase.network.request.ShareRequest
import com.earth.libbase.util.PreferencesHelper
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.tencent.imsdk.v2.V2TIMConversationResult
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback
import kotlinx.android.synthetic.main.activity_user_main.*
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


class UserMainActivity : BaseActivity<ActivityUserMainBinding>(), OnRefreshLoadMoreListener {

    private val mMineModel by viewModel<MineModel>()
    private val mViewModel by viewModel<SearchModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val userModle by viewModel<UserModel>()
    private val mShareModel by viewModel<ShareModel>()

    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var customBottomWindow: CustomPopupWindow? = null
    private var mUserMainAdapter: UserMainAdapter? = null
    private var pageNum: Int = 1
    private var userId: Long? = null
    private var nikeName: String? = null

    private var list: ArrayList<GiftEntity>? = ArrayList()
    private var bean: SelectUserRequest? = null
    private var mGiftEntity: GiftEntity? = null
    private var mMemberEntity: MemberEntity? = null
    private var mHouseListEntity: HouseListEntity? = null
    private var mSearchDetailEntity: SearchDetailEntity? = null
    private var mUserEntity: UserLocationEntity? = null

    private var mUserId: Long? = null
    private var mnikeName: String? = null
    private var mhead: String? = null
    private var tvLink: TextView? = null

    private var mheadUrl: String? = null

    override fun getLayoutId(): Int = R.layout.activity_user_main

    @SuppressLint("SetTextI18n")
    override fun initActivity(savedInstanceState: Bundle?) {
        hideActionBar()
        val lp: LinearLayout.LayoutParams =
            rlUSerTopToolbar.layoutParams as LinearLayout.LayoutParams
        lp.setMargins(0, ScreenUtil.getActivityMessageHeight(this@UserMainActivity), 0, 0)
        rlUSerTopToolbar.layoutParams = lp
        ScreenUtil.setAndroidNativeLightStatusBar(this@UserMainActivity, true)

        mGiftEntity = intent.getSerializableExtra("GiftEntity") as GiftEntity?
        mMemberEntity = intent.getSerializableExtra("MemberEntity") as MemberEntity?
        mHouseListEntity = intent.getSerializableExtra("HouseListEntity") as HouseListEntity?
        mUserEntity = intent.getSerializableExtra("UserLocationEntity") as UserLocationEntity?
        mnikeName = intent.getStringExtra("name")
        mSearchDetailEntity = intent.getSerializableExtra("SearchDetailEntity") as SearchDetailEntity?

        mBinding?.run {
            handler = this@UserMainActivity
            mGiftEntity?.run {
                tv.text = nickName
                tvTitleCenter.text = "$nickName'S ECO PAGE"
                tvNum.text = "ID: $userId"
                headImgUrl?.let {
                    mheadUrl=it
                    Glide.with(this@UserMainActivity)
                        .load(it)
                        .placeholder(R.mipmap.head_common)
                        .into(iv)
                }
                initAdd(isConcern, requestFrendsStatus)
                this@UserMainActivity.userId = userId.toLong()
                this@UserMainActivity.nikeName = nickName
                getUser()

            }
            mMemberEntity?.run {
                tv.text = nickName

                tvTitleCenter.text = "$nickName'S ECO PAGE"
                tvNum.text = "ID: $userId"
                headImgUrl?.let {
                    mheadUrl=it
                    Glide.with(this@UserMainActivity)
                        .load(it)
                        .placeholder(R.mipmap.head_common)
                        .into(iv)
                }
                initAdd(isConcern, requestFrendsStatus)

                this@UserMainActivity.userId = userId
                this@UserMainActivity.nikeName = nickName
                getUser()
            }
            mHouseListEntity?.run {
                tv.text = name
                tvTitleCenter.text = "$name'S ECO PAGE"
                tvNum.text = "ID: $number"
                imgUrl?.let {
                    if (imgUrl.isNotEmpty()) {
                        mheadUrl=it[0]
                        Glide.with(this@UserMainActivity)
                            .load(it[0])
                            .placeholder(R.mipmap.head_common)
                            .into(iv)
                    }
                }
                tvJoined.text = getString(R.string.lab_Messages)
                tvJoined.setBackgroundResource(R.drawable.shape_corner_group_joined)
                tvJoined?.setTextColor(
                    ContextCompat.getColor(
                        this@UserMainActivity,
                        R.color.themColor
                    )
                )
                ivAddFriend.visibility = View.GONE
                this@UserMainActivity.userId = number.toLong()
                this@UserMainActivity.nikeName = name
                getUser()
            }

            mSearchDetailEntity?.run {
                tv.text = name
                tvTitleCenter.text = "$name'S ECO PAGE"
                tvNum.text = "ID: $number"
                imgUrl?.let {
                    mheadUrl=it

                    Glide.with(this@UserMainActivity)
                        .load(it)
                        .placeholder(R.mipmap.head_common)
                        .into(iv)
                }

                initAdd(status, requestFrendsStatus)

                this@UserMainActivity.userId = number.toLong()
                this@UserMainActivity.nikeName = name
                getUser()
            }
            mnikeName?.let {
                mUserId = intent?.getSerializableExtra("userid").toString().toLong()
                mhead = intent?.getStringExtra("head")

                this@UserMainActivity.userId = mUserId
                this@UserMainActivity.nikeName = it


                mBinding?.tv.text = it
                mBinding?.tvTitleCenter.text = "$it'S ECO PAGE"
                mBinding?.tvNum.text = "ID: $mUserId"
                mhead?.let {
                    mheadUrl=it
                    Glide.with(this@UserMainActivity)
                        .load(it)
                        .placeholder(R.mipmap.head_common)
                        .into(iv)
                }
                mBinding?.tvJoined.text = getString(R.string.lab_Messages)
                mBinding?.tvJoined.setBackgroundResource(R.drawable.shape_corner_group_joined)
                mBinding?.tvJoined?.setTextColor(
                    ContextCompat.getColor(
                        this@UserMainActivity,
                        R.color.themColor
                    )
                )
                ivAddFriend.visibility = View.GONE

                list?.clear()
                mUserMainAdapter?.notifyDataSetChanged()
                if (PreferencesHelper.getUserId(this@UserMainActivity) == this@UserMainActivity.userId.toString()) {
                    tvJoined.visibility = View.GONE
                    tvForward.visibility = View.GONE
                }
                getUser()
            }
            mUserEntity?.run {
                tv.text = nickName
                tvTitleCenter.text = "$nickName'S ECO PAGE"
                tvNum.text = "ID: $id"
                headImgUrl?.let {
                    mheadUrl=it

                    Glide.with(this@UserMainActivity)
                        .load(it)
                        .placeholder(R.mipmap.head_common)
                        .into(iv)
                }
                initAdd(isConcern, requestFrendsStatus)
                this@UserMainActivity.userId = id.toLong()
                this@UserMainActivity.nikeName = nickName
                getUser()
            }
            onChatIntent()
            tvJoined.setOnClickListener {
                //likeUser()
                if (tvJoined.text.toString() == getString(R.string.lab_Messages)) {
                    ChatListActivity.startActivity(
                        this@UserMainActivity.userId.toString(),
                        this@UserMainActivity.nikeName
                    )
                } else if (tvJoined.text.toString() == getString(R.string.lab_Add)) {
                    join()
                } else if (tvJoined.text.toString() == getString(R.string.lab_Cancel_Request)) {
                    delete()
                }
                /*    val info: MessageInfo = MessageInfoUtil.buildCustomMessage(data)
                    val messageSender = TUIKitListenerManager.getInstance().messageSender
                    messageSender?.sendMessage(info, null, chatInfoId,
                        chatType === V2TIMConversation.V2TIM_GROUP, false, object : IUIKitCallBack {
                            override fun onSuccess(data: Any) {
                                Log.i("CustomChatController", "send success")
                            }

                            override fun onError(module: String, errCode: Int, errMsg: String) {
                                Log.i("CustomChatController", "send failed")
                            }
                        })*/


            }
            if (PreferencesHelper.getUserId(this@UserMainActivity) == this@UserMainActivity.userId.toString()) {
                llAddFriend.visibility = View.GONE
                tvForward.visibility = View.GONE
            }
            var gridLayoutManager = GridLayoutManager(this@UserMainActivity, 2)
            mUserMainAdapter = UserMainAdapter(this@UserMainActivity, list,upDade = {
                    if(!it.isConcern){
                        var blockDialog = DialogCommon(title = getString(R.string.dialog_tell),onBlockClick = {
                            join()
                        })
                        blockDialog.show(
                            supportFragmentManager, ""
                        )
                    }else{
                        ChatListActivity.startActivity(it.userId,it.nickName,it)
                    }
            })
            xlv.layoutManager = gridLayoutManager
            xlv.setAdapter(mUserMainAdapter)
            onRefreshLoadMoreListener = this@UserMainActivity
            mUserMainAdapter?.setOnItemClickListener { _, _, position ->
                if (DateUtils.isFastClick()){
                    DataReportUtils.getInstance().report("Listing_details_photo")
                    GiftDetailsActivity.openGiftDetailsActivity(
                        this@UserMainActivity,
                        list!![position]
                    )
                }

            }
            tvForward.setOnClickListener {
                ChatListActivity.startActivity(
                    this@UserMainActivity.userId.toString(),
                    this@UserMainActivity.nikeName,
                    "Share"
                )
            }
            ivRightTool.setBackgroundResource(R.mipmap.share)
            ivRightTool.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    customBottomWindow?.showAtLocation(
                        ivRightTool,
                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0
                    )
                }
            }
            initBottom()
            shareVersionAdd()
           iv.setOnClickListener {
               if (DateUtils.isFastClick()) {
                   mheadUrl?.let {
                    val intent = Intent(this@UserMainActivity, GiftPhotoActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra(GiftPhotoActivity.IMG, it)
                    startActivity(intent)
                }
               }
           }
        }

        mAppViewModel?.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })
    }
    private fun initBottom() {
        customBottomWindow = CustomPopupWindow.Builder(this@UserMainActivity)
            .setContentView(R.layout.pop_share_bottom)
            .setwidth(LinearLayout.LayoutParams.MATCH_PARENT)
            .setheight(LinearLayout.LayoutParams.WRAP_CONTENT)
            .build()
        val llCopyLink = customBottomWindow?.getItemView(R.id.llCopyLink) as LinearLayout
        tvLink = customBottomWindow?.getItemView(R.id.tvLink) as TextView

        val llShareVia = customBottomWindow?.getItemView(R.id.llShareVia) as LinearLayout
        val ivDelete = customBottomWindow?.getItemView(R.id.ivDelete) as ImageView
        llCopyLink.setOnClickListener {
            val cm = baseContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText(null, tvLink?.text.toString()))
            toast(getString(R.string.lab_Success))
        }
        llShareVia.setOnClickListener {
       //     ShareUtil.shareText(this@UserMainActivity, tvLink?.text.toString(), "EarthAngel")
        }
        ivDelete.setOnClickListener {
            customBottomWindow?.dismiss()
        }
    }
    private fun shareVersionAdd() {
        this@UserMainActivity.nikeName?.let {
            var sendStr = "Here are $it’s eco gifts for you"
            launch {
                mShareModel.shareVersionAdd(ShareRequest(userId =this@UserMainActivity.userId!!.toLong(),textContent = sendStr)).catch { }
                    .collectLatest {
                        if (it.isOk(this@UserMainActivity)) {
                            tvLink?.text = it.data?.url
                            tvLink?.visibility=View.GONE
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        V2TIMManager.getConversationManager().getConversationList(
            0,
            100,
            object : V2TIMValueCallback<V2TIMConversationResult?> {
                override fun onSuccess(t: V2TIMConversationResult?) {
                    t?.conversationList?.let {
                        for (item in it) {
                            if (item.userID == this@UserMainActivity.userId.toString()) {
                                if(item.unreadCount>0){
                                    mBinding?.tvMessageNum.visibility=View.VISIBLE
                                    mBinding?.tvMessageNum.text=item.unreadCount.toString()
                                }else{
                                    mBinding?.tvMessageNum.visibility=View.GONE
                                }

                                break
                            }
                        }
                    }
                }
                override fun onError(code: Int, desc: String?) {
                }
            })
    }

    private fun initAdd(isConcern: Boolean, requestFrendsStatus: Boolean) {
        if (isConcern) {
            tvJoined.text = getString(R.string.lab_Messages)
            llAddFriend.setBackgroundResource(R.drawable.shape_corner_group_joined)
            tvJoined?.setTextColor(ContextCompat.getColor(this@UserMainActivity, R.color.themColor))
            ivAddFriend.visibility = View.GONE
        } else {
            ivAddFriend.visibility = View.VISIBLE
            if (requestFrendsStatus) {
                tvJoined.text = getString(R.string.lab_Cancel_Request)
                llAddFriend.setBackgroundResource(R.drawable.shape_corner_group_join)
                tvJoined?.setTextColor(ContextCompat.getColor(this@UserMainActivity, R.color.white))
                tvForward.visibility = View.GONE
            } else {
                tvJoined.text = getString(R.string.lab_Add)
                llAddFriend.setBackgroundResource(R.drawable.shape_corner_group_join)
                tvJoined?.setTextColor(ContextCompat.getColor(this@UserMainActivity, R.color.white))
                tvForward.visibility = View.GONE
            }
        }
    }

    private fun onChatIntent() {
        mnikeName = intent?.getStringExtra("name")
        mnikeName?.let {
            mUserId = intent?.getSerializableExtra("userid").toString().toLong()
            mhead = intent?.getStringExtra("head")
            this@UserMainActivity.userId = mUserId
            this@UserMainActivity.nikeName = it
            mBinding?.tv.text = it
            mBinding?.tvTitleCenter.text = "$it'S ECO PAGE"
            mBinding?.tvNum.text = "ID :$mUserId"
            mhead?.let {
                Glide.with(this@UserMainActivity)
                    .load(it)
                    .placeholder(R.mipmap.head_common)
                    .into(iv)
            }
            mBinding?.tvJoined.text = getString(R.string.lab_Messages)
            mBinding?.tvJoined.setBackgroundResource(R.drawable.shape_corner_group_joined)
            mBinding?.tvJoined?.setTextColor(
                ContextCompat.getColor(
                    this@UserMainActivity,
                    R.color.themColor
                )
            )
            list?.clear()
            mUserMainAdapter?.notifyDataSetChanged()
            if (PreferencesHelper.getUserId(this@UserMainActivity) == this@UserMainActivity.userId.toString()) {
                tvJoined.visibility = View.GONE
                tvForward.visibility = View.GONE
            }
            getUser()
        }
    }

    private fun join() {
        launch {
            this@UserMainActivity.userId?.let {
                userModle.requestFriend(it).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@UserMainActivity)) {
                            toast(getString(R.string.lab_Success))
                            tvJoined.text = getString(R.string.lab_Cancel_Request)
                            llAddFriend.setBackgroundResource(R.drawable.shape_corner_group_join)
                            tvJoined?.setTextColor(
                                ContextCompat.getColor(
                                    this@UserMainActivity,
                                    R.color.white
                                )
                            )
                            tvForward.visibility = View.GONE
                            eventShake(true)
                        }
                    }
            }

        }
    }

    private fun delete() {
        launch {
            this@UserMainActivity.userId?.let {
                userModle.deleteAgreeToBeFriends(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@UserMainActivity)) {
                            toast(getString(R.string.lab_Success))
                            tvJoined.text = getString(R.string.lab_Add)
                            llAddFriend.setBackgroundResource(R.drawable.shape_corner_group_join)
                            tvJoined?.setTextColor(
                                ContextCompat.getColor(
                                    this@UserMainActivity,
                                    R.color.white
                                )
                            )
                            tvForward.visibility = View.GONE
                            eventShake(false)
                        }

                    }
            }

        }
    }


    private fun setTopMargin(view: View, topMargin: Int) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(
            layoutParams.leftMargin, topMargin,
            layoutParams.rightMargin, layoutParams.bottomMargin
        )
        view.layoutParams = layoutParams
    }
    private fun eventShake( requestFrendsStatus: Boolean){
        mUserEntity?.run {
            EventBusHelper.post(ShakeEvent(id,requestFrendsStatus))
        }
    }


    companion object {
        const val USER_SHARE = "user_share"
        fun openUserMainActivity(context: Context?, mGiftEntity: GiftEntity) {
            val intent = Intent(context, UserMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("GiftEntity", mGiftEntity)
            context?.startActivity(intent)
        }

        fun openUserMainActivity(context: Context?, mMemberEntity: MemberEntity) {
            val intent = Intent(context, UserMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("MemberEntity", mMemberEntity)
            context?.startActivity(intent)
        }

        fun openUserMainActivity(context: Context?, mHouseListEntity: HouseListEntity) {
            val intent = Intent(context, UserMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("HouseListEntity", mHouseListEntity)
            context?.startActivity(intent)
        }

        fun openUserMainActivity(context: Context?, mSearchDetailEntity: SearchDetailEntity) {
            val intent = Intent(context, UserMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("SearchDetailEntity", mSearchDetailEntity)
            context?.startActivity(intent)
        }

        fun openUserMainActivity(context: Context?, userEntity: UserLocationEntity) {
            val intent = Intent(context, UserMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("UserLocationEntity", userEntity)
            context?.startActivity(intent)
        }
        fun openUserMainActivity(context: Context?, mnikeName: String,userid: String,head: String) {
            val intent = Intent(context, UserMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("name", mnikeName)
            intent.putExtra("userid", userid)
            intent.putExtra("head", head)

            context?.startActivity(intent)
        }

    }

    private fun getUser() {
        userId?.let {
            launch {
                mMineModel.ownPageList(pageNum, 10, it).catch { }.collectLatest {
                    if (it.isOk(this@UserMainActivity)) {
                        it.data?.let {
                            list?.addAll(it)
                            mUserMainAdapter?.notifyDataSetChanged()
                        }
                    }
                    mBinding?.xlv?.let { it1 ->
                        it1.finishRefresh(true)
                        it1.finishLoadMore(true)
                        if (pageNum > 1 && (list == null || list!!.size <= 0)) {
                            it1.setLoadMoreEnable(false)
                        } else {
                            it1.setLoadMoreEnable(true)
                        }
                    }
                    if (pageNum == 1) {
                        if (list?.size == 0) {
                            mBinding?.llEmpty.visibility = View.VISIBLE
                        } else {
                            mBinding?.llEmpty.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventEvent(event: ShareSuccessEvent) {
        mAppViewModel?.dismissLoading()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            //注册
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) {
            //注销
            EventBus.getDefault().unregister(this)
        }
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        list?.clear()
        pageNum = 1
        getUser()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        getUser()
    }
}