package com.earth.libmine.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.earth.libbase.base.*
import com.earth.libbase.baseentity.BaseBestGiftEntity
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.dialog.BaseDialogCommon
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.request.AddGiftRequest
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.PocketDeleteRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.BaseScreenUtil
import com.earth.libbase.util.PreferencesHelper
import com.earth.libmine.R
import com.earth.libmine.adapter.MineOffGiftItemAdapter
import com.earth.libmine.adapter.MineUserFreeItemAdapter
import com.earth.libmine.adapter.UserOffGiftItemAdapter
import com.earth.libmine.adapter.UserOffItemDecoration
import com.earth.libmine.databinding.ActivityLibmineUserBinding
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.dip
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

@Route(path = Constance.LibMineUserActivityURL)
class LibMineUserActivity : BaseActivity<ActivityLibmineUserBinding>() {
    @Autowired(name = "userId")
    @JvmField
    var userId: String? = null

    private var nikeName: String? = null

    private val mMineModle by viewModel<LibMineModle>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    private var headView: View? = null
    private var footerView: View? = null
    private var mLibMineHead: ImageView? = null
    private var mLibMineName: TextView? = null
    private var mLibMineLocation: TextView? = null
    private var mLibMineTvGifted: TextView? = null
    private var mLibMineGiftedTvFollowers: TextView? = null
    private var mLibMineUserSaveImage: ImageView? = null
    private var tvLibMineHistory: TextView? = null

    private var mImageBoolean: Boolean? = false
    private var pageNum: Int = 1

    private var listPaket: ArrayList<BaseGiftEntity> = ArrayList()

    private var mMineUserFreeItemAdapter: MineUserFreeItemAdapter? = null
    private var listString: ArrayList<BaseGiftEntity> = ArrayList()

    private var mMineOffGiftItemAdapter: MineOffGiftItemAdapter? = null
    private var listOffGift: ArrayList<BaseGiftEntity> = ArrayList()

    private var mHistoryAdapter: UserOffGiftItemAdapter? = null
    private var listHistory: ArrayList<BaseGiftEntity> = ArrayList()

    private var mMinePopPoketWindowUtil: MinePopPoketWindowUtil? = null

    override fun getLayoutId(): Int = R.layout.activity_libmine_user

    override fun initActivity(savedInstanceState: Bundle?) {
        hideActionBar()
        mBinding.run {
            ARouter.getInstance().inject(this@LibMineUserActivity)
            mAppViewModel?.showLoadingProgress.observe(
                this@LibMineUserActivity,
                androidx.lifecycle.Observer {
                    if (it) mLoadingDialog?.showAllowStateLoss(
                        supportFragmentManager,
                        mLoadingDialog::class.simpleName!!
                    )
                    else mLoadingDialog?.dismiss()
                })
            headView = layoutInflater.inflate(R.layout.libmine_head_user, null)
            footerView = layoutInflater.inflate(R.layout.libmine_footer_user, null)
            mHistoryAdapter =
                UserOffGiftItemAdapter(this@LibMineUserActivity, listHistory)
            var gridLayoutManager = GridLayoutManager(this@LibMineUserActivity, 2)
            libMineRlv.layoutManager = gridLayoutManager
            libMineRlv.adapter = mHistoryAdapter
            mHistoryAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id", listHistory[position].releaseRecordId)
                        .withString("userid", listHistory[position].userId)
                        .navigation()
                }
            }

            libMineUserSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listOffGift.clear()
                    mMineOffGiftItemAdapter?.notifyDataSetChanged()
                    listString.clear()
                    mMineUserFreeItemAdapter?.notifyDataSetChanged()
                    getMine()
                    listHistory.clear()
                    mHistoryAdapter?.notifyDataSetChanged()
                    pageNum = 1
                    libMineUserSrl?.setEnableLoadMore(true)
                    getGiftList()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getGiftList()
                }
            })


            if (mMinePopPoketWindowUtil == null) {
                mMinePopPoketWindowUtil = MinePopPoketWindowUtil()
            }
            libMineAddTvIntention.setOnClickListener {
                if (userId != null) {
                    if (nikeName == null) {
                        ARouter.getInstance().build(Constance.ChatListActivityURL)
                            .withString("userid", userId)
                            .withString("chatName", "")
                            .withString("want", "want")
                            .navigation()
                    } else {
                        ARouter.getInstance().build(Constance.ChatListActivityURL)
                            .withString("userid", userId)
                            .withString("chatName", nikeName)
                            .withString("want", "want")
                            .navigation()
                    }
                }
            }
            headView?.let {

                mMineUserFreeItemAdapter = MineUserFreeItemAdapter(
                    this@LibMineUserActivity,
                    listString,
                    addNeed = true,
                    upDade = {
                        //加入用户口袋
                        if(!it.isPoket){
                            poketAdd(it)
                        }
                    })
                val mLibMineHeadBank: ImageView = it.findViewById(R.id.LibMineHeadBank)
                val lp: ConstraintLayout.LayoutParams =
                    mLibMineHeadBank.layoutParams as ConstraintLayout.LayoutParams
                lp.setMargins(
                    dip(18),
                    BaseScreenUtil.getActivityMessageHeight(),
                    0,
                    0
                )
                mLibMineHeadBank?.layoutParams = lp
                mLibMineHeadBank?.setOnClickListener {
                    finish()
                }
                val layoutManager1 = LinearLayoutManager(this@LibMineUserActivity).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                val mLibMineFreeItemRlv: RecyclerView = it.findViewById(R.id.LibMineFreeItemRlv)
                mLibMineFreeItemRlv.layoutManager = layoutManager1
                mLibMineFreeItemRlv.adapter = mMineUserFreeItemAdapter
                mMineUserFreeItemAdapter?.setOnItemClickListener { _, _, position ->
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                            .withString("id", listString[position].releaseRecordId)
                            .withString("userid", listString[position].userId)
                            .navigation()
                    }
                }
                mMineOffGiftItemAdapter =
                    MineOffGiftItemAdapter(this@LibMineUserActivity, listOffGift)
                val layoutManager2 = LinearLayoutManager(this@LibMineUserActivity).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                val mLibMineOffGiftRlv: RecyclerView = it.findViewById(R.id.LibMineOffGiftRlv)
                mLibMineOffGiftRlv.layoutManager = layoutManager2
                mLibMineOffGiftRlv.adapter = mMineOffGiftItemAdapter

                mMineOffGiftItemAdapter?.setOnItemClickListener { _, _, _ ->
                    ARouter.getInstance().build(Constance.LibMineCollectActivityURL).navigation()
                }
                mHistoryAdapter?.addHeaderView(it)
                mLibMineHead = it.findViewById(R.id.LibMineHead)
                mLibMineName = it.findViewById(R.id.LibMineUserName)
                mLibMineLocation = it.findViewById(R.id.LibMineLocation)
                mLibMineTvGifted = it.findViewById(R.id.LibMineTvGifted)
                mLibMineGiftedTvFollowers = it.findViewById(R.id.LibMineGiftedTvFollowers)
                mLibMineUserSaveImage = it.findViewById(R.id.LibMineUserSaveImage)
                tvLibMineHistory= it.findViewById(R.id.tvLibMineHistory)

                val mLibMineFreeItemViewAll: TextView =
                    it.findViewById(R.id.tvLibMineFreeItemViewAll)
                mLibMineFreeItemViewAll.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance()
                            .build(Constance.HomeNewGiftActivityURL)
                            .withString("Type", "FreeProducts")
                            .withString("userId", userId)
                            .navigation()
                    }
                }

                mLibMineUserSaveImage?.setOnClickListener {
                    if (mImageBoolean!!) {
                        //    concernedDelete()
                    } else {
                        concernedAdd()
                    }
                }
            }
            footerView?.let {
                mHistoryAdapter?.addFooterView(it)
            }

            BaseApplication.myBaseUser?.userId?.let {
                userId?.let { itUserID ->
                    if (it == itUserID) {
                        mLibMineUserSaveImage?.visibility=View.GONE
                        libMineShoppLl?.visibility = View.GONE
                    } else {
                        mLibMineUserSaveImage?.visibility=View.VISIBLE
                        libMineShoppLl?.visibility = View.VISIBLE
                    }
                }
            }

            libMineShoppLl.setOnClickListener {

                if (!mMinePopPoketWindowUtil!!.show()) {
                    listPaket.clear()
                    mMinePopPoketWindowUtil?.showPopupWindow(
                        this@LibMineUserActivity,
                        LibMineShoppCl,
                        object : MineOnItemClick {
                            override fun onItemClick() {
                            }
                            override fun onItemDelete(str: BaseGiftEntity?) {
                                str?.let {
                                    poketDelete(str)
                                }
                            }
                            override fun onItemDeleteALL(list: ArrayList<Long>?) {
                                list?.let {
                                    var blockDialog = BaseDialogCommon(
                                        title = getString(R.string.base_Delete_All),
                                        onBlockClick = {
                                            poketDeleteAll(list)
                                        })
                                    blockDialog.show(
                                        supportFragmentManager, ""
                                    )
                                }
                            }
                        })
                    selfPokedPageList()
                }


            }
            selfPokedPageList()
            getMine()
            getGiftList()
            getUser()
        }
    }

    private fun getUser() {
        launch {
            userId?.let {
                mMineModle.userSelectOne(CommonRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@LibMineUserActivity)) {
                            it.data?.run {
                                headImgUrl?.let {
                                    Glide.with(this@LibMineUserActivity)
                                        .load(it)
                                        .into(mLibMineHead!!)
                                }
                                nickName?.let {
                                    mLibMineName?.text = "Hi. I’m $it"
                                }
                                liveIn?.let {
                                    mLibMineLocation?.text = it
                                }
                                fansNumber?.let {
                                    mLibMineGiftedTvFollowers?.text = it.toString()
                                }
                                postNumber?.let {
                                    mLibMineTvGifted?.text = it.toString()
                                }
                                isConcern?.let { itBoo ->
                                    mImageBoolean = itBoo
                                    if (itBoo) {
                                        mLibMineUserSaveImage?.setImageResource(R.mipmap.user_saved)
                                    } else {
                                        mLibMineUserSaveImage?.setImageResource(R.mipmap.user_save)
                                    }
                                }
                                this@LibMineUserActivity.nikeName = nickName
                            }
                        }

                    }
            }
        }
    }

    fun getMine() {
        userId?.let {
            launch {
                mMineModle.statusPageList(CommonRequest(1, 10, userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest { it ->
                        if (it.isOk(this@LibMineUserActivity)) {
                            it.data?.run {
                                //toast(""+it.freeItems.size)
                                list?.let {
                                    listString.addAll(it)
                                    mMineUserFreeItemAdapter?.notifyDataSetChanged()
                                }
                                /*  offItems?.let {
                                      listOffGift.addAll(it)
                                      mMineOffGiftItemAdapter?.notifyDataSetChanged()
                                  }*/
                            }
                        }
                    }
            }
        }

    }

    private fun getGiftList() {
        launch {
            userId?.let {
                mMineModle.statusPageList(
                    CommonRequest(
                        pageNum = pageNum,
                        pageSize = 10,
                        userId = it.toLong(),
                        releaseStatus = CommonRequest.EXCHANGED
                    )
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        mBinding.libMineUserSrl.let {
                            it.finishRefresh(true)
                            it.finishLoadMore(true)
                        }
                        if (it.isOk(this@LibMineUserActivity)) {
                            it.data?.list?.let {
                                listHistory.addAll(it)
                                mHistoryAdapter?.notifyDataSetChanged()
                            }
                            if (listString.size == it.data?.total) {
                                mBinding?.libMineUserSrl?.setEnableLoadMore(false)
                            }
                            if(listHistory.size>0){
                                tvLibMineHistory?.visibility=View.VISIBLE
                            }
                        }
                    }
            }

        }
    }


    fun poketAdd(mBaseGiftEntity: BaseGiftEntity) {

        mBaseGiftEntity.let {
            it.releaseRecordId?.let {
                launch {
                    mMineModle.poketAdd(AddGiftRequest(it.toDouble()))
                        .onStart {
                            mAppViewModel.showLoading()
                        }.catch {
                        }.onCompletion {
                            mAppViewModel.dismissLoading()
                        }.collectLatest {
                            if(it.code==200){
                                toast(getString(R.string.label_Added_Successfully))
                                mBaseGiftEntity.isPoket=true
                                mMineUserFreeItemAdapter?.notifyDataSetChanged()
                                listPaket.clear()
                                selfPokedPageList()
                            }else{
                                toast(getString(R.string.label_Added_Already))
                            }
                        }
                }
            }
        }

    }
    // 删除某一个
    fun poketDelete(bean: BaseGiftEntity) {
        var listPocketNum: ArrayList<Long> = ArrayList()
        listPocketNum.add(bean.releaseRecordId.toLong())
        launch {
            mMineModle.poketDelete(PocketDeleteRequest(listPocketNum))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@LibMineUserActivity)) {
                        mMinePopPoketWindowUtil?.remove(bean)
                        listPaket.remove(bean)
                        initFreeProducts(bean.releaseRecordId)
                        mBinding?.run {
                            libMineAddTvPaketNum?.text = listPaket.size.toString()
                            initProvatesTv(listPaket.size)
                            libMineAddTvNum.run {
                                visibility = View.VISIBLE
                                text = listPaket.size.toString()
                            }
                        }
                        getMyPocketNum()
                    }
                }
        }
    }
    // 删除全部
    fun poketDeleteAll( listPocketNum: ArrayList<Long>) {
        launch {
            mMineModle.poketDelete(PocketDeleteRequest(listPocketNum))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@LibMineUserActivity)) {
                        mMinePopPoketWindowUtil?.removeALL()
                        listPaket.clear()
                        initFreeProducts(null)
                        mBinding?.run {
                            libMineAddTvPaketNum?.text = listPaket.size.toString()
                            initProvatesTv(listPaket.size)
                            libMineAddTvNum.run {
                                visibility = View.VISIBLE
                                text = listPaket.size.toString()
                            }
                        }
                    }
                }
        }
    }

    private fun initFreeProducts(releaseRecordId : String?){

        if(releaseRecordId.isNullOrEmpty()){
            for(item in listString){
               item.isPoket=false
            }
        }else{
            for(item in listString){
                if(item.releaseRecordId==releaseRecordId){
                    item.isPoket=false
                }
            }
        }
        mMineUserFreeItemAdapter?.notifyDataSetChanged()
    }

    private fun initProvatesTv(num: Int) {
        mBinding?.run {
            if (num > 1) {
                tvlibMineAddTvPaketNum.text = getString(R.string.base_Products)
                libMineAddTvIntention.text = getString(R.string.base_I_Want_These)
            } else {
                tvlibMineAddTvPaketNum.text = getString(R.string.base_Product)
                libMineAddTvIntention.text = getString(R.string.base_I_Want_This)
            }
        }
    }

    private fun concernedAdd() {
        userId?.let {
            launch {
                mMineModle.concernedAdd(BaseUserRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@LibMineUserActivity)) {
                            mLibMineUserSaveImage?.setImageResource(R.mipmap.user_saved)
                            mImageBoolean = true
                        }
                    }
            }
        }
    }

    private fun concernedDelete() {
        userId?.let {
            launch {
                mMineModle.concernedDelete(BaseUserRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@LibMineUserActivity)) {
                            mLibMineUserSaveImage?.setImageResource(R.mipmap.user_save)
                            mImageBoolean = false
                        }
                    }
            }
        }


    }

    private fun selfPokedPageList() {
        userId?.let {
            launch {
                mMineModle.selfPokedPageList(
                    CommonRequest(
                        userId = it.toLong()
                    )
                )
                    .onStart {
                        mAppViewModel.showLoading()
                    }.catch {
                    }.onCompletion {
                        mAppViewModel.dismissLoading()
                    }.collectLatest {
                        if (it.isOk(this@LibMineUserActivity)) {
                            it.data?.let {
                                listPaket.addAll(it.poketList)
                                mMinePopPoketWindowUtil?.adapterList(listPaket)
                                mBinding?.run {
                                    libMineAddTvPaketNum.text =listPaket.size.toString()
                                    initProvatesTv(listPaket.size)
                                    libMineAddTvNum.run {
                                        visibility = View.VISIBLE
                                        text = listPaket.size.toString()
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun getMyPocketNum() {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mMineModle.userSelectOne(CommonRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@LibMineUserActivity)) {
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK){

            return if( mMinePopPoketWindowUtil!!.isShow()){
                mMinePopPoketWindowUtil?.closed()
                true
            }else{
                return super.onKeyDown(keyCode, event)

            }

        }
        return super.onKeyDown(keyCode, event)

    }
}