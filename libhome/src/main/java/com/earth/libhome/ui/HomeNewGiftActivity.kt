package com.earth.libhome.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.Constance
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.PreferencesHelper
import com.earth.libhome.R
import com.earth.libhome.adapter.HomeNewGiftAdapter
import com.earth.libhome.adapter.HomeNewGiftListAdapter
import com.earth.libhome.databinding.ActivityHomeNewgiftBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_home_nearby.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

@Route(path = Constance.HomeNewGiftActivityURL)
class HomeNewGiftActivity : BaseActivity<ActivityHomeNewgiftBinding>() {
    @Autowired(name = "Type")
    @JvmField
    var mType: String? = null
    @Autowired(name = "userId")
    @JvmField
    var userId: String? = null
    private var mNewGiftAdapter: HomeNewGiftListAdapter? = null
    private var listNewGift: ArrayList<BaseGiftEntity> = ArrayList()

    private var pageNum: Int = 1
    private var mBoolean: Boolean? = false

    private val mHomeModel by viewModel<HomeModle>()

    override fun getLayoutId(): Int = R.layout.activity_home_newgift

    override fun initActivity(savedInstanceState: Bundle?) {
        fitSystemBar()
        ARouter.getInstance().inject(this@HomeNewGiftActivity)

        mBinding?.run {
            mType?.let {
                when (it) {
                    "FreeProducts" -> {
                        LibHomeNewGiftTV.text = "Free Products"
                        mBoolean=true
                    }
                    "MineFree" -> {
                        LibHomeNewGiftTV.text = "Free Products"
                        mBoolean=false

                    }
                    "MineOff" -> {
                        LibHomeNewGiftTV.text = "Off Product"
                        mBoolean=false

                    }
                    else -> {
                        LibHomeNewGiftTV.text = ""
                    }
                }
            }
            mNewGiftAdapter = HomeNewGiftListAdapter(this@HomeNewGiftActivity, listNewGift,mBoolean = mBoolean)
            val layoutManager1 = LinearLayoutManager(this@HomeNewGiftActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            LibHomeNewGiftBank.setOnClickListener {
                finish()
            }
            libHomeNewGiftRlv.layoutManager = layoutManager1
            libHomeNewGiftRlv.adapter = mNewGiftAdapter
            mNewGiftAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()){
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id",listNewGift[position].releaseRecordId)
                        .withString("userid",listNewGift[position].userId)
                        .navigation()
                }
            }

            libHomeNewGiftSrl?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listNewGift.clear()
                    mNewGiftAdapter?.notifyDataSetChanged()
                    pageNum = 1
                    libHomeNewGiftSrl?.setEnableLoadMore(true)
                    getMyData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getMyData()
                }
            })
            getMyData()
        }

    }

    private fun getMyData() {
        mType?.let {
            when (it) {
                "FreeProducts" -> {
                    getData()
                }
                "MineFree" -> {
                    getGiftList(CommonRequest.DOING)
                }
                "MineOff" -> {
                    getGiftList(CommonRequest.REMOVE)
                }
            }
        }
    }

    private fun getGiftList(releaseStatus: Int?) {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mHomeModel.userPageGiftList(
                    CommonRequest(
                        pageNum = pageNum,
                        pageSize = 10,
                        releaseStatus = releaseStatus
                    )
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        mBinding?.libHomeNewGiftSrl?.let {
                            it.finishRefresh(true)
                            it.finishLoadMore(true)
                        }
                        if (it.isOk(this@HomeNewGiftActivity)) {
                            it.data?.list?.let {
                                listNewGift.addAll(it)
                                mNewGiftAdapter?.notifyDataSetChanged()
                            }
                            if (listNewGift.size == it.data?.total) {
                                mBinding?.libHomeNewGiftSrl?.setEnableLoadMore(false)
                            }

                        }
                    }
            }

        }
    }

    private fun getData() {
        launch {
            mHomeModel.userPageGiftList(    CommonRequest(
                pageNum = pageNum,
                pageSize = 10,
                userId = userId?.toLong(),
                releaseStatus = CommonRequest.DOING
            ))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    mBinding?.libHomeNewGiftSrl?.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                    if (it.isOk(this@HomeNewGiftActivity)) {
                        it.data?.list?.let {
                            listNewGift.addAll(it)
                            mNewGiftAdapter?.notifyDataSetChanged()
                        }

                    }
                    if (listNewGift.size == it.data?.total) {
                        mBinding?.libHomeNewGiftSrl?.setEnableLoadMore(false)
                    }
                }
        }


    }
}