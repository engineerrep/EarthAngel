package com.earth.libmine.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.earth.libbase.base.*
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.dialog.PointDialogCommon
import com.earth.libbase.entity.SingEntity
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.util.AppUtils
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.util.ShareUtil
import com.earth.libmine.R
import com.earth.libmine.adapter.MineOffGiftItemAdapter
import com.earth.libmine.adapter.MinePageFreeItemAdapter
import com.earth.libmine.adapter.MineUserListGiftListAdapter
import com.earth.libmine.databinding.FragmentLibMineBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class LibMineFragment : BaseFragment<FragmentLibMineBinding>(), Observer {

    private val mMineModle by viewModel<LibMineModle>()
    private var headView: View? = null
    private var mHistoryAdapter: MineUserListGiftListAdapter? = null
    private var listHistory: ArrayList<BaseGiftEntity> = ArrayList()
    private var mMineUserFreeItemAdapter: MinePageFreeItemAdapter? = null
    private var listString: ArrayList<BaseGiftEntity> = ArrayList()
    private var mMineOffGiftItemAdapter: MineOffGiftItemAdapter? = null
    private var listOffGift: ArrayList<BaseGiftEntity> = ArrayList()
    private var mLibMineHead: ImageView? = null
    private var mLibMineTvGifted: TextView? = null
    private var mLibMineGiftedTvFollowers: TextView? = null
    private var mLibMineName: TextView? = null
    private var mLibMineLocation: TextView? = null
    private var mlibMineAddTvNum: TextView? = null
    private var mLibMineFreeItemLy: Layer? = null
    private var mLibMineOffGiftLy: Layer? = null
    private var mLibMineHistory: TextView? = null
    private var mLibMineIvLocationIv: ImageView? = null
    private var ivMainMessageNum: TextView? = null

    private var pageNum: Int = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun getLayoutId(): Int = R.layout.fragment_lib_mine

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        MessageObservable.messageObservable.addObserver(this)
        mBinding?.run {
            headView = layoutInflater.inflate(R.layout.libmine_head_home_user, null)
            mHistoryAdapter = MineUserListGiftListAdapter(activity, listHistory)
            mHistoryAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id", listHistory[position].releaseRecordId)
                        .withString("userid", listHistory[position].userId)
                        .navigation()
                }
            }
            var gridLayoutManager = GridLayoutManager(activity, 2)
            libMineHomeRlv.layoutManager = gridLayoutManager
            libMineHomeRlv.adapter = mHistoryAdapter
            libMineSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listOffGift.clear()
                    mMineOffGiftItemAdapter?.notifyDataSetChanged()
                    listString.clear()
                    mMineUserFreeItemAdapter?.notifyDataSetChanged()
                    getMine()
                    listHistory.clear()
                    mHistoryAdapter?.notifyDataSetChanged()
                    pageNum = 1
                    libMineSrl.setEnableLoadMore(true)
                    getGiftList()
                }
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getGiftList()
                }
            })

            headView?.let {
                mHistoryAdapter?.addHeaderView(it)
                mLibMineHead = it.findViewById(R.id.LibMineHead)
                mLibMineTvGifted = it.findViewById(R.id.LibMineTvGifted)
                mLibMineGiftedTvFollowers = it.findViewById(R.id.LibMineGiftedTvFollowers)
                mLibMineName = it.findViewById(R.id.LibMineUserName)
                mlibMineAddTvNum = it.findViewById(R.id.libMineAddTvNum)
                mLibMineLocation = it.findViewById(R.id.LibMineLocation)
                mLibMineFreeItemLy = it.findViewById(R.id.tvLibMineFreeItemLy)
                mLibMineOffGiftLy = it.findViewById(R.id.tvLibMineOffGiftLy)
                mLibMineHistory = it.findViewById(R.id.tvLibMineHistory)
                mLibMineIvLocationIv = it.findViewById(R.id.LibMineIvLocation)
                ivMainMessageNum=it.findViewById(R.id.ivMainMessageNum)
                PreferencesHelper.getMessage(BaseApplication.instance).let {
                    if (it == "0"||it == "") {
                        ivMainMessageNum?.visibility = View.GONE
                        ivMainMessageNum?.text = it
                    } else {
                        ivMainMessageNum?.visibility = View.VISIBLE
                        ivMainMessageNum?.text = it
                    }
                }
                val mtvLibMineFreeItemViewAll: TextView =
                    it.findViewById(R.id.tvLibMineFreeItemViewAll)
                val mtvLibMineOffGiftViewAll: TextView =
                    it.findViewById(R.id.tvLibMineOffGiftViewAll)
                val libMineHomeShare: ImageView = it.findViewById(R.id.libMineHomeShare)
                val mlibLLHomeForFree: LinearLayout = it.findViewById(R.id.libLLHomeForFree)
                val mLibMineSet: ImageView = it.findViewById(R.id.LibMineSet)
                setViewActionBarHight(mLibMineSet)
                val mivMainMessage: ImageView = it.findViewById(R.id.ivMainMessage)
                setViewActionBarHight(mivMainMessage)
                mivMainMessage.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance()
                            .build(Constance.ChatMainFragmentURL)
                            .navigation()
                    }
                }
                val mLibMineFreeItemRlv: RecyclerView = it.findViewById(R.id.LibMineFreeItemRlv)
                val mLibMineOffGiftRlv: RecyclerView = it.findViewById(R.id.LibMineOffGiftRlv)
                val mlibMineHomePoket: ImageView = it.findViewById(R.id.libMineHomePoket)
                val mlibMineHomeSaved: ImageView = it.findViewById(R.id.libMineHomeSaved)
                val mlibMineHomeRecord: ImageView = it.findViewById(R.id.libMineHomeRecord)

                mtvLibMineFreeItemViewAll.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.HomeNewGiftActivityURL)
                            .withString("Type", "MineFree")
                            .navigation()
                    }
                }
                mtvLibMineOffGiftViewAll.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.HomeNewGiftActivityURL)
                            .withString("Type", "MineOff")
                            .navigation()
                    }
                }
                mlibMineHomeRecord.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.LibmineRecordActivityURL)
                            .navigation()
                    }
                }
                libMineHomeShare.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        BaseApplication.myBaseUser?.userId?.let {
                            ShareUtil.shareUserText(requireContext(), it)
                        }
                        firstShare()
                    }
                }
                mlibLLHomeForFree.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.PhotoPostActivityURL)
                            .navigation()
                    }
                }
                mLibMineSet.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.LibMineSetActivityURL)
                            .navigation()
                    }
                }
                val layoutManager1 = LinearLayoutManager(activity).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                mMineUserFreeItemAdapter =
                    MinePageFreeItemAdapter(activity, listString, addNeed = true)
                mLibMineFreeItemRlv.layoutManager = layoutManager1
                mLibMineFreeItemRlv.adapter = mMineUserFreeItemAdapter
                mMineUserFreeItemAdapter?.setOnItemClickListener { _, _, position ->
                    if (BaseDateUtils.isFastClick()) {
                        if(listString.size>0){
                            ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                                .withString("id", listString[position].releaseRecordId)
                                .withString("userid", listString[position].userId)
                                .navigation()
                        }
                    }
                }
                mMineOffGiftItemAdapter = MineOffGiftItemAdapter(activity, listOffGift)
                val layoutManager2 = LinearLayoutManager(activity).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                mLibMineOffGiftRlv.layoutManager = layoutManager2
                mLibMineOffGiftRlv.adapter = mMineOffGiftItemAdapter
                mMineOffGiftItemAdapter?.setOnItemClickListener { _, _, position ->
                    if (BaseDateUtils.isFastClick()) {
                        if(listOffGift.size>0){
                            ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                                .withString("id", listOffGift[position].releaseRecordId)
                                .withString("userid", listOffGift[position].userId)
                                .navigation()
                        }

                    }
                }
                mlibMineHomePoket.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.HomePackingBagActivityURL)
                            .navigation()
                    }
                }
                mlibMineHomeSaved.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.LibMineCollectActivityURL)
                            .navigation()
                    }
                }
                if( PreferencesHelper.getLocationName(requireContext()).isNullOrEmpty()){
                    mLibMineIvLocationIv?.visibility = View.GONE
                    mLibMineLocation?.visibility = View.GONE
                }else{
                    mLibMineLocation?.text =  PreferencesHelper.getLocationName(requireContext())
                    mLibMineIvLocationIv?.visibility = View.VISIBLE
                    mLibMineLocation?.visibility = View.VISIBLE
                }

            }
        }
        getMine()
        getGiftList()
    }




    override fun onDestroy() {
        super.onDestroy()
        MessageObservable.messageObservable.deleteObserver(this)
    }


    private fun getGiftList() {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mMineModle.userPageGiftList(
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
                        mBinding?.libMineSrl?.let {
                            it.finishRefresh(true)
                            it.finishLoadMore(true)
                        }
                        if (it.isOk(activity)) {
                            it.data?.list?.let {
                                listHistory.addAll(it)
                                mHistoryAdapter?.notifyDataSetChanged()
                            }
                            if (listHistory.size == 0) {
                                mLibMineHistory?.visibility = View.GONE
                            } else {
                                mLibMineHistory?.visibility = View.VISIBLE
                            }
                            if (listString.size == it.data?.total) {
                                mBinding?.libMineSrl?.setEnableLoadMore(false)
                            }
                        }
                    }
            }
        }
    }

    fun getMine() {
        BaseApplication.myBaseUser?.userId?.let {
            launch {
                mMineModle.userIndex(CommonRequest(1, 10, userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(activity)) {
                            it.data?.run {
                                //toast(""+it.freeItems.size)
                                freeItems?.let {
                                    listString.addAll(it)
                                    mMineUserFreeItemAdapter?.notifyDataSetChanged()
                                }
                                if (listString.size == 0) {
                                    mLibMineFreeItemLy?.visibility = View.GONE
                                } else {
                                    mLibMineFreeItemLy?.visibility = View.VISIBLE
                                }
                                offItems?.let {
                                    listOffGift.addAll(it)
                                    mMineOffGiftItemAdapter?.notifyDataSetChanged()
                                }
                                if (listOffGift.size == 0) {
                                    mLibMineOffGiftLy?.visibility = View.GONE
                                } else {
                                    mLibMineOffGiftLy?.visibility = View.VISIBLE
                                }
                                if(headImgUrl.isNullOrEmpty()){
                                    Glide.with(requireContext())
                                        .load(R.mipmap.base_head_big)
                                        .into(mLibMineHead!!)
                                }else{
                                    Glide.with(requireContext())
                                        .load(headImgUrl)
                                        .placeholder(R.mipmap.base_head_big)
                                        .into(mLibMineHead!!)
                                }
                                nickName?.let {
                                    mLibMineName?.text = "Hi. I’m $it"
                                }
                                fansNumber?.let {
                                    mLibMineGiftedTvFollowers?.text = it.toString()
                                }
                                postNumber?.let {
                                    mLibMineTvGifted?.text = it.toString()
                                }
                                poketNumber?.let {
                                    if (it == "0") {
                                        mlibMineAddTvNum?.visibility = View.GONE
                                        mlibMineAddTvNum?.text = it
                                    } else {
                                        mlibMineAddTvNum?.visibility = View.VISIBLE
                                        mlibMineAddTvNum?.text = it
                                    }
                                }
                            }
                        }
                    }
            }
        }

    }

    override fun update(o: Observable?, arg: Any?) {
        o?.let {
            if (it is MessageObservable) {
                arg?.let {
                    var bean = it as UpdateEntity
                    bean.let {
                        it.pockNum?.let {
                            if (it == "0") {
                                mlibMineAddTvNum?.visibility = View.GONE
                                mlibMineAddTvNum?.text = it
                            } else {
                                mlibMineAddTvNum?.visibility = View.VISIBLE
                                mlibMineAddTvNum?.text = it
                            }
                        }
                        it.headUrl?.let { itUrl ->
                            mLibMineHead?.let {
                                Glide.with(requireContext())
                                    .load(itUrl)
                                    .into(mLibMineHead!!)
                            }
                        }
                        it.nikeName?.let {
                            mLibMineName?.text = "Hi. I’m $it"
                        }
                        it.postProduct?.let {
                            listString.clear()
                            listOffGift.clear()
                            getMine()
                        }
                        it.messageNum?.let {
                            if (it == "0"||it == "") {
                                ivMainMessageNum?.visibility = View.GONE
                                ivMainMessageNum?.text = it
                            } else {
                                ivMainMessageNum?.visibility = View.VISIBLE
                                ivMainMessageNum?.text = it
                            }
                        }
                    }
                }
            }
        }
    }
    private fun firstShare() {
        launch {
            mMineModle.shareIn()
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(requireContext())) {
                        it.data?.let {
                            it.whetherNeed.let { whetherNeed ->
                                if(whetherNeed== SingEntity.NEED){
                                    var title="Get 10 Eco credit for first \n sharing every day."
                                    val blockDialog = PointDialogCommon(
                                        content = title,
                                        onBlockClick = {
                                        })
                                    if(isActivityEnable()){
                                        blockDialog.show(requireActivity().supportFragmentManager, title)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
    fun isActivityEnable(): Boolean {
        return (activity != null && !requireActivity().isDestroyed
                && !requireActivity().isFinishing && isAdded)
    }


}