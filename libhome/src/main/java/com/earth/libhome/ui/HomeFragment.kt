package com.earth.libhome.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.earth.libbase.base.*
import com.earth.libbase.baseentity.BaseBestGiftEntity
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R
import com.earth.libhome.adapter.ArticleHomeAdapter
import com.earth.libhome.adapter.HomeGiftListAdapter
import com.earth.libhome.databinding.FragmentHomeBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class HomeFragment : BaseFragment<FragmentHomeBinding>(), Observer {

    private val mHomeModel by viewModel<HomeModle>()
    private var pageNum: Int = 1
    private var headView: View? = null
    private var footerView: View? = null
    private var mHomeGiftListAdapter: HomeGiftListAdapter? = null
    private var listString: ArrayList<BaseGiftEntity> = ArrayList()

    private var listArticle: ArrayList<ArticleEntity> = ArrayList()
    private var mArticledapter: ArticleHomeAdapter? = null

    private var ivlibHomeHeadImage: ShapedImageView? = null
    private var ivLibHomeName: TextView? = null
    private var mLibHomeNewGiftRlv: RecyclerView? = null
    private var tvlibLogo: TextView? = null // 环保分值
    private var ivMainMessageNum: TextView? = null

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        MessageObservable.messageObservable.addObserver(this)
        mBinding?.run {
            setViewActionBarHight(libHomeSrl)
            headView = layoutInflater.inflate(R.layout.head_home, null)
            footerView = layoutInflater.inflate(R.layout.homefooterview, null)
            mHomeGiftListAdapter = HomeGiftListAdapter(activity, listString)
            var gridLayoutManager = GridLayoutManager(activity, 2)
            libHomeRlv.layoutManager = gridLayoutManager
            libHomeRlv.adapter = mHomeGiftListAdapter
            mHomeGiftListAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id", listString[position].releaseRecordId)
                        .withString("userid", listString[position].userId)
                        .navigation()
                }
            }
            footerView?.let {
                val mLibLLHomeFooterForFree: LinearLayout =
                    it.findViewById(R.id.libLLHomeFooterForFree)
                mLibLLHomeFooterForFree.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.CameraV2ActivityURL).navigation()
                    }
                }
                val libLLHomeFooterForFree: LinearLayout =
                    it.findViewById(R.id.libLLHomeFooterForFree)
                libLLHomeFooterForFree.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.PhotoPostActivityURL).navigation()
                    }
                }
            }

            headView?.let {
                mHomeGiftListAdapter?.addHeaderView(it)
                ivlibHomeHeadImage = it.findViewById(R.id.ivlibHomeHeadImage)
                ivLibHomeName = it.findViewById(R.id.ivLibHomeName)
                tvlibLogo= it.findViewById(R.id.tvlibLogo)
                ivMainMessageNum=it.findViewById(R.id.ivMainMessageNum)
                val ivlibTree: ImageView= it.findViewById(R.id.ivlibTree)
                Glide.with(requireActivity())
                    .asGif()
                    .load(R.mipmap.tree)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivlibTree)

                PreferencesHelper.getMessage(BaseApplication.instance).let {
                    if (it == "0"||it == "") {
                        ivMainMessageNum?.visibility = View.GONE
                        ivMainMessageNum?.text = it
                    } else {
                        ivMainMessageNum?.visibility = View.VISIBLE
                        ivMainMessageNum?.text = it
                    }
                }
                val mivMainMessage: ImageView= it.findViewById(R.id.ivMainMessage)
                mivMainMessage.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance()
                            .build(Constance.ChatMainFragmentURL)
                            .navigation()
                    }
                }
                val mLibMineTree: ConstraintLayout = it.findViewById(R.id.LibMineTree)
                mLibMineTree.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.PointActivityURL).navigation()
                    }
                }
                val mLibMineSaved: ImageView = it.findViewById(R.id.LibMineSaved)
                mLibHomeNewGiftRlv = it.findViewById(R.id.LibHomeNewGiftRlv)

                mArticledapter = ArticleHomeAdapter(requireContext(), listArticle)
                val mLinearLayoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                mLibHomeNewGiftRlv?.run {
                    layoutManager = mLinearLayoutManager
                    adapter = mArticledapter
                }
                mArticledapter?.setOnItemClickListener { adapter, view, position ->
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance()
                            .build(Constance.ArticleTextActivityURL)
                            .withSerializable("ArticleEntity", listArticle[position])
                            .navigation()
                    }
                }

                mLibMineSaved.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.LibMineCollectActivityURL)
                            .navigation()
                    }
                }
                BaseApplication.myBaseUser?.run {
                    headImgUrl?.let { itHead ->
                        ivlibHomeHeadImage?.let {
                            Glide.with(ivlibHomeHeadImage!!.context)
                                .load(itHead)
                                .placeholder(R.mipmap.base_comm_head)
                                .into(ivlibHomeHeadImage!!)
                        }
                    }
                    nickName?.let { itName ->
                        ivLibHomeName?.text = getString(R.string.base_head_Name) + itName
                    }
                }
                val mtvLibHomeNewGiftViewAll: TextView =
                    it.findViewById(R.id.tvLibHomeNewGiftViewAll)
                mtvLibHomeNewGiftViewAll.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.LibArticleListActivityURL)
                            .navigation()
                    }
                }

            }
            libHomeSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum = 1
                    libHomeSrl.setEnableLoadMore(true)
                    listString.clear()
                    getGiftList()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getGiftList()
                }
            })
        }
        getGiftList()
        getArticlesData()
        getPointData()
    }


    private fun concernedAdd(userid: BaseBestGiftEntity) {

        launch {
            mHomeModel.concernedAdd(BaseUserRequest(userId = userid.userId.toLong()))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(activity)) {
                        userid.isConcern = true
                        //mHomeBestAdapter?.notifyDataSetChanged()
                    }
                }
        }
    }

    private fun concernedDelete(userid: BaseBestGiftEntity) {
        launch {
            mHomeModel.concernedDelete(BaseUserRequest(userId = userid.userId.toLong()))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(activity)) {
                        userid.isConcern = false
                        //  mHomeBestAdapter?.notifyDataSetChanged()
                    }
                }
        }
    }

    private fun getGiftList() {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mHomeModel.statusPageList(
                    CommonRequest(
                        pageNum = pageNum,
                        pageSize = 10,
                        releaseStatus = 0
                    )
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        mBinding?.libHomeSrl?.let {
                            it.finishRefresh(true)
                            it.finishLoadMore(true)
                        }
                        if (it.isOk(activity)) {
                            it.data?.list?.let {
                                listString.addAll(it)
                                mHomeGiftListAdapter?.notifyDataSetChanged()
                            }
                            if (listString.size == it.data?.total) {

                                mBinding?.libHomeSrl?.setEnableLoadMore(false)
                                footerView?.let {
                                    if (!mHomeGiftListAdapter!!.hasFooterLayout()) {
                                        mHomeGiftListAdapter?.addFooterView(it)
                                    }
                                }
                            } else {
                                if (mHomeGiftListAdapter!!.hasFooterLayout()) {
                                    footerView?.let {
                                        if (mHomeGiftListAdapter!!.hasFooterLayout()) {
                                            mHomeGiftListAdapter?.removeAllFooterView()
                                        }
                                    }
                                }
                            }
                        }
                    }
            }

        }
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
                        it.headUrl?.let { itUrl ->
                            ivlibHomeHeadImage?.let {
                                Glide.with(requireContext())
                                    .load(itUrl)
                                    .into(ivlibHomeHeadImage!!)
                            }
                        }
                        it.nikeName?.let {
                            ivLibHomeName?.text = "Hi. $it"
                        }
                        it.messagePoint?.let {
                            getPointData()
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

    private fun getArticlesData() {
        launch {
            mHomeModel.pageArticlesList(CommentRequest(pageSize = 10, pageNum = pageNum))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(activity)) {
                        it.data?.list?.let {
                            listArticle.addAll(it)
                            mArticledapter?.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    private fun getPointData() {
        launch {
            mHomeModel.myScore()
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(activity)) {
                        it.data?.score?.let {
                            tvlibLogo?.text=it.toString()
                        }
                    }
                }
        }
    }

}