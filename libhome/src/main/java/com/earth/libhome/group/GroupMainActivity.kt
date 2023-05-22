package com.earth.libhome.group

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.earth.libbase.Config
import com.earth.libbase.base.*
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.baseentity.GroupStepEntity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GroupEntity
import com.earth.libbase.network.OSSManager
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.GroupRequest
import com.earth.libbase.service.OssInterface
import com.earth.libbase.service.OssService
import com.earth.libbase.util.*
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R
import com.earth.libhome.adapter.GroupListAdapter
import com.earth.libhome.adapter.GroupMainAdapter
import com.earth.libhome.adapter.GroupStepAdapter
import com.earth.libhome.databinding.ActivityGroupMainBinding
import com.earth.libhome.ui.GroupMoreClick
import com.earth.libhome.ui.GroupMoreUtil
import com.earth.libhome.ui.HomeOnItemClick
import com.earth.libhome.ui.HomePopMoreUtil
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.dip
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

@Route(path = Constance.GroupMainActivityURL)
class GroupMainActivity : BaseActivity<ActivityGroupMainBinding>(), OssInterface, Observer {
    @Autowired(name = "GroupID")
    @JvmField
    var id: String? = null
    private val mGroupApiModle by viewModel<GroupApiModle>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var adapterGroup: GroupMainAdapter? = null
    private var listGift: ArrayList<BaseGiftEntity> = ArrayList()
    private var adapterGroupStepAdapter: GroupStepAdapter? = null
    private var listStep: ArrayList<GroupStepEntity> = ArrayList()
    private var mPath: String? = ""
    private var mUserid: String? = ""
    private var head: View? = null
    private var foot: View? = null
    private var tvName: TextView? = null
    private var tvGroupNum: TextView? = null
    private var mLibMineTvGifted: TextView? = null
    private var tvLocationNameContent: TextView? = null
    private var rlvBuild: RecyclerView? = null
    private var tvBuild: TextView? = null
    private var tvFill: TextView? = null
    private var tvInvite: TextView? = null
    private var itemHead1: ShapedImageView? = null
    private var tvMainPost: Button? = null
    private var mPhotoModify: TextView? = null
    private var mLibHomeGiftMoreIv: ImageView? = null
    private var mLibHomeGiftRlv: ImageView? = null
    private var mlyBuild: Layer? = null
    private var pageNum: Int = 1
    private var mOssService: OssService? = null

    override fun getLayoutId(): Int = R.layout.activity_group_main

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {
            fitSystemBar()
            ARouter.getInstance().inject(this@GroupMainActivity)
            // 图片上传初始
            mOssService = OSSManager.initOSS(this@GroupMainActivity, Config.OSS_ENDPOINT)
            mOssService?.setInterface(this@GroupMainActivity)

            var gridLayoutManager = GridLayoutManager(this@GroupMainActivity, 2)
            adapterGroup = GroupMainAdapter(this@GroupMainActivity, listGift)
            rlvProduct.layoutManager = gridLayoutManager
            rlvProduct.adapter = adapterGroup
            adapterGroup?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id", listGift[position].releaseRecordId)
                        .withString("userid", listGift[position].userId)
                        .navigation()
                }
            }
            MyGroupsSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    communityDetail()
                    pageNum = 1
                    MyGroupsSrl?.setEnableLoadMore(true)
                    listGift.clear()
                    communityList()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    communityList()
                }
            })

            head = layoutInflater.inflate(R.layout.head_groupmain, null)
            foot = layoutInflater.inflate(R.layout.foot_groupmain, null)

            head?.let {
                val mLibHomeUserArrowRight: ImageView = it.findViewById(R.id.LibHomeUserArrowRight)
                val lp: ConstraintLayout.LayoutParams =
                    mLibHomeUserArrowRight.layoutParams as ConstraintLayout.LayoutParams
                lp.setMargins(
                    dip(18),
                    BaseScreenUtil.getActivityMessageHeight(),
                    0,
                    0
                )
                mLibHomeUserArrowRight.layoutParams = lp
                mLibHomeUserArrowRight.setOnClickListener {
                    finish()
                }
                tvName = it.findViewById(R.id.tvName)
                mLibHomeGiftRlv = it.findViewById(R.id.groupMainBg)
                tvGroupNum = it.findViewById(R.id.tvGroupNum)
                mLibMineTvGifted = it.findViewById(R.id.LibMineTvGifted)
                tvLocationNameContent = it.findViewById(R.id.tvLocationNameContent)
                tvFill = it.findViewById(R.id.tvFill)
                tvBuild = it.findViewById(R.id.tvBuild)
                adapterGroup?.addHeaderView(it)
                rlvBuild = it.findViewById(R.id.rlvBuild)
                tvInvite = it.findViewById(R.id.tvInvite)
                itemHead1 = it.findViewById(R.id.itemHead1)
                tvMainPost = it.findViewById(R.id.tvMainPost)
                mPhotoModify = it.findViewById(R.id.PhotoModify)
                mlyBuild = it.findViewById(R.id.lyBuild)
                mLibHomeGiftMoreIv = it.findViewById(R.id.LibHomeGiftMoreIv)


                mPhotoModify?.setOnClickListener {
                    jumpFill()
                }
                tvFill?.setOnClickListener {
                    jumpFill()
                }
                tvMainPost?.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.PhotoPostActivityURL)
                            .withString("GroupID", id)
                            .navigation()
                    }

                }
                listStep.add(GroupStepEntity(R.mipmap.group_photo, "Edit cover photo", false))
                listStep.add(
                    GroupStepEntity(
                        R.mipmap.group_detalis,
                        "Introduce your community",
                        false
                    )
                )
                listStep.add(GroupStepEntity(R.mipmap.group_image, "Post a new product", false))
                listStep.add(
                    GroupStepEntity(
                        R.mipmap.group_friend,
                        "Invite friends to join",
                        false
                    )
                )
                adapterGroupStepAdapter = GroupStepAdapter(this@GroupMainActivity, listStep)
                rlvBuild?.layoutManager = LinearLayoutManager(
                    this@GroupMainActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                rlvBuild?.adapter = adapterGroupStepAdapter
                adapterGroupStepAdapter?.setOnItemClickListener { _, _, position ->
                    when (position) {
                        0 -> {
                            if (!listStep[position].down) {
                                getPhoto()
                            }
                        }
                        1 -> {
                            if (!listStep[position].down) {
                                jumpFill()
                            }
                        }
                        2 -> {
                            if (!listStep[position].down) {
                                if (BaseDateUtils.isFastClick()) {
                                    ARouter.getInstance().build(Constance.PhotoPostActivityURL)
                                        .navigation()
                                }
                            }
                        }
                        3 -> {
                            share()
                        }
                    }

                }
            }
            MessageObservable.messageObservable.addObserver(this@GroupMainActivity)
            mAppViewModel.showLoadingProgress.observe(
                this@GroupMainActivity,
                androidx.lifecycle.Observer {
                    if (it) mLoadingDialog?.showAllowStateLoss(
                        supportFragmentManager,
                        mLoadingDialog::class.simpleName!!
                    )
                    else mLoadingDialog?.dismiss()
                })
            communityDetail()
            communityList()
        }
    }

    private fun jumpFill() {
        BaseApplication.myBaseUser?.userId?.let {
            if (mUserid == it) {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance()
                        .build(Constance.PhotoEtidContentActivityURL)
                        .withString("TYPE", "Group")
                        .withString("Content", tvFill?.text.toString())
                        .navigation(this@GroupMainActivity, 1)
                }
            }
        }
    }

    private fun share() {
        if (BaseDateUtils.isFastClick()) {
            ShareUtil.shareGroup(this@GroupMainActivity, id)
        }
    }

    private fun getPhoto() {
        val permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsList.add(1, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                /*           val intent = Intent(this@EditProfileActivity, AddPhotoActivity::class.java)
                               .putExtra(AddPhotoActivity.SUPPORT_MUTABLE, false)
                               .putExtra(AddPhotoActivity.PHOTO_TYPE, PhotoUploadType.PHOTO)
                               .putExtra(AddPhotoActivity.SUPPORT_EDIT, false)
                               .putExtra(AddPhotoActivity.Max_SELECTED_IMAGE_SIZE_KEY, 10)
                           startActivity(intent)*/

                EasyPhotos.createAlbum(
                    this@GroupMainActivity,
                    true,
                    false,
                    BaseGlideEngine.getInstance()
                )
                    .setPuzzleMenu(false)
                    .setCleanMenu(false)
                    .setFileProviderAuthority("com.earth.angel.fileProvider")
                    .start(101) //也可以选择链式调用写法

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK == resultCode) {
            if (requestCode == 101) {
                //返回对象集合：如果你需要了解图片的宽、高、大小、用户是否选中原图选项等信息，可以用这个
                val resultPhotos: java.util.ArrayList<Photo> =
                    data!!.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)!!
                resultPhotos?.let {
                    for (item in resultPhotos) {
                        item.path?.let { it1 ->
                            mAppViewModel.showLoading()
                            mOssService?.asyncPutImage(Storage.getFileSufix(it1), it1)
                        }
                    }

                }
            }
        }
        if (resultCode == 1) {
            when (requestCode) {
                1 -> {
                    data?.let {
                        mAppViewModel.showLoading()
                        val ss = data.getStringExtra("content")
                        communityUpdate(GroupRequest(description = ss, id = id))
                    }
                }
                2 -> {
                    data?.let {
                        val ss = data.getStringExtra("content")
                        tvName?.text = ss
                    }
                }
            }
        }
    }

    //获取社群详情
    private fun communityDetail() {
        id?.let {
            launch {
                mGroupApiModle.communityDetail(CommentRequest(id = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@GroupMainActivity)) {
                            it.data?.run {
                                tvName?.text = communityName
                                tvGroupNum?.text = userNum.toString()
                                var strItem = if (itemNum == 0 || itemNum == 1) {
                                    "$itemNum New Product"
                                } else {
                                    "$itemNum New Products"
                                }

                                headImgUrl?.let {
                                    Glide.with(this@GroupMainActivity)
                                        .load(headImgUrl)
                                        .placeholder(R.mipmap.base_comm_head)
                                        .into(itemHead1!!)
                                }
                                mUserid = userId
                                mLibHomeGiftMoreIv?.setOnClickListener {
                                    val mGroupMoreUtil = GroupMoreUtil()
                                    mGroupMoreUtil.showPopupWindow(
                                        this@GroupMainActivity,
                                        mBinding.bottomView,
                                        userId,
                                        GroupMoreClick {
                                            when (it.id) {
                                                R.id.LibHomeMoreDelete -> {
                                                    //name
                                                    ARouter.getInstance()
                                                        .build(Constance.GroupNameActivityURL)
                                                        .withString(
                                                            "GroupName",
                                                            communityName
                                                        )
                                                        .withString("GroupID", id)
                                                        .navigation(
                                                            this@GroupMainActivity,
                                                            2
                                                        )

                                                }
                                                R.id.LibHomeMoreOff -> {
                                                    // photo
                                                    getPhoto()

                                                }
                                                R.id.LibHomeExit -> {
                                                    // exit
                                                    exitCommunity()
                                                }
                                            }
                                        },
                                    )

                                }

                                BaseApplication.myBaseUser?.userId?.let {
                                    if (userId == it) {
                                        // 自己的社群
                                        mPhotoModify?.visibility = View.VISIBLE
                                        tvInvite?.visibility = View.VISIBLE
                                        tvInvite?.text = getString(R.string.base_Invite)
                                        mLibHomeGiftMoreIv?.visibility = View.VISIBLE
                                        tvMainPost?.visibility = View.VISIBLE
                                        tvFill?.visibility = View.VISIBLE
                                        if (description.isNullOrEmpty()) {
                                            tvFill?.text = "To fill"
                                            tvFill?.setTextColor(
                                                ContextCompat.getColor(
                                                    this@GroupMainActivity,
                                                    R.color.BaseThemColor
                                                )
                                            )
                                        } else {
                                            tvFill?.text = description
                                            tvFill?.setTextColor(
                                                ContextCompat.getColor(
                                                    this@GroupMainActivity,
                                                    R.color.base_login_un
                                                )
                                            )
                                        }
                                    } else {
                                        // 别人的社群
                                        mPhotoModify?.visibility = View.GONE
                                        if (isJoined) {
                                            // 加入的
                                            tvInvite?.visibility = View.VISIBLE
                                            tvInvite?.text = getString(R.string.base_Invite)
                                            mLibHomeGiftMoreIv?.visibility = View.VISIBLE
                                            tvMainPost?.visibility = View.VISIBLE
                                            if (description.isNullOrEmpty()) {
                                                tvFill?.visibility = View.GONE
                                                tvFill?.text = "To fill"
                                                tvFill?.setTextColor(
                                                    ContextCompat.getColor(
                                                        this@GroupMainActivity,
                                                        R.color.BaseThemColor
                                                    )
                                                )
                                            } else {
                                                tvFill?.visibility = View.VISIBLE
                                                tvFill?.text = description
                                                tvFill?.setTextColor(
                                                    ContextCompat.getColor(
                                                        this@GroupMainActivity,
                                                        R.color.base_login_un
                                                    )
                                                )
                                            }
                                        } else {
                                            // 没有加入的
                                            tvInvite?.visibility = View.VISIBLE
                                            tvInvite?.text = getString(R.string.base_Join)
                                            mLibHomeGiftMoreIv?.visibility = View.GONE
                                            tvMainPost?.visibility = View.GONE
                                            tvFill?.visibility = View.GONE


                                        }

                                    }
                                }
                                tvInvite?.setOnClickListener {
                                    BaseApplication.myBaseUser?.userId?.let {
                                        if (userId == it) {
                                            share()
                                        } else {
                                            if (isJoined) {
                                                share()
                                            } else {
                                                joinCommunity(GroupRequest(communityId = id))
                                            }
                                        }
                                    }
                                }

                                mLibMineTvGifted?.text = strItem
                                tvLocationNameContent?.text = address



                                listStep[0].down = imageUpload=="FINISHED"
                                listStep[1].down = editDescription=="FINISHED"
                                listStep[2].down = releaseGoods=="FINISHED"
                                listStep[3].down = joinCommunity=="FINISHED"


                                picUrl?.let {
                                    Glide.with(this@GroupMainActivity)
                                        .load(picUrl)
                                        .placeholder(R.mipmap.group_default)
                                        .into(mLibHomeGiftRlv!!)
                                }
                                adapterGroupStepAdapter?.notifyDataSetChanged()
                                var sum = 0
                                for (item in listStep) {
                                    if (item.down) {
                                        sum += 1
                                    }
                                }
                                tvBuild?.text = "Build Community ( $sum / 4 )"
                                if (sum == 4) {
                                    mlyBuild?.visibility = View.GONE
                                } else {
                                    mlyBuild?.visibility = View.VISIBLE
                                }
                            }
                        }

                    }
            }
        }

    }

    //获取社群礼品
    private fun communityList() {
        launch {
            id?.let {
                mGroupApiModle.communityItems(
                    CommentRequest(
                        id = it.toLong(),
                        pageNum = pageNum,
                        pageSize = 10
                    )
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@GroupMainActivity)) {
                            it.data?.list?.let {
                                listGift.addAll(it)
                                adapterGroup?.notifyDataSetChanged()
                            }
                            mBinding.MyGroupsSrl.let {
                                it.finishRefresh(true)
                                it.finishLoadMore(true)
                            }
                        }
                        if (listGift.size == 0) {
                            if (adapterGroup?.hasFooterLayout() == false) {
                                adapterGroup?.addFooterView(foot!!)
                            }
                        } else {
                            adapterGroup?.removeAllFooterView()
                        }
                    }
            }
        }
    }

    override fun onBankString(path: String, str: String) {
        if (str.isNotEmpty()) {
            val mMessage = Message()
            mMessage.obj = str
            mMessage.what = 1
            mHandler.sendMessage(mMessage)
        }
    }

    // 创建一个Handler
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    mPath = msg.obj.toString()
                    Glide.with(this@GroupMainActivity)
                        .load(mPath)
                        .into(mLibHomeGiftRlv!!)
                    communityUpdate(GroupRequest(picUrl = mPath, id = id))
                }
                // 这里的else相当于Java中switch的default;
                else -> {

                }
            }
        }
    }

    private fun communityUpdate(bean: GroupRequest) {
        launch {
            mGroupApiModle.communityUpdate(bean)
                .onStart {
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@GroupMainActivity)) {
                        communityDetail()
                    }
                }
        }
    }

    private fun joinCommunity(bean: GroupRequest) {
        launch {
            mGroupApiModle.joinCommunity(bean)
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@GroupMainActivity)) {
                        MessageObservable.messageObservable.newMessage(
                            UpdateEntity(
                                groupList = UpdateEntity.GROUP
                            )
                        )
                        communityDetail()
                    }
                }
        }
    }

    private fun exitCommunity() {
        launch {
            mGroupApiModle.exitCommunity(GroupRequest(communityId = id))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@GroupMainActivity)) {
                        MessageObservable.messageObservable.newMessage(
                            UpdateEntity(
                                groupList = UpdateEntity.GROUP
                            )
                        )
                        finish()
                    }
                }
        }
    }

    override fun filed() {
        mAppViewModel.dismissLoading()
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
                        it.postProduct?.let {
                            pageNum = 1
                            mBinding.MyGroupsSrl?.setEnableLoadMore(true)
                            listGift.clear()
                            communityList()
                        }
                    }
                }
            }
        }
    }

}