package com.earth.libmine.point

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.entity.PointRecordEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.view.loadingstatus.LoadingStatusCode
import com.earth.libmine.R
import com.earth.libmine.adapter.PointRecordAdapter
import com.earth.libmine.databinding.ActivityPointBinding
import com.earth.libmine.ui.LibMineModle
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.PointActivityURL)
class PointActivity : BaseActivity<ActivityPointBinding>() {

    private val mMineModle by viewModel<LibMineModle>()
    private var mAdapter: PointRecordAdapter? = null
    private var listString: ArrayList<PointRecordEntity> = ArrayList()
    private var pageNum: Int = 1

    override fun getLayoutId(): Int = R.layout.activity_point

    override fun initActivity(savedInstanceState: Bundle?) {
        fitSystemBar()
        mBinding.run {
            LibArrowRight.setOnClickListener {
                finish()
            }
            var mLinearLayoutManager =
                LinearLayoutManager(this@PointActivity, LinearLayoutManager.VERTICAL, false)
            mAdapter=PointRecordAdapter(this@PointActivity,listString)
            rlvPoint.run {
                layoutManager = mLinearLayoutManager
                adapter = mAdapter
            }
            setViewActionBarHight(LibArrowRight)
            libHomeSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum = 1
                    libHomeSrl?.setEnableLoadMore(true)
                    listString.clear()
                    getScoreData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getScoreData()
                }
            })

        }
        mBinding?.statusCode = LoadingStatusCode.Loading
        getData()
        getScoreData()
    }

    private fun getData() {
        launch {
            mMineModle.myScore()
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@PointActivity)) {
                        it.data?.score?.let {
                            mBinding.tvPoint.text = it.toString()
                        }
                    }
                }
        }
    }
    private fun getScoreData() {
        launch {
            mMineModle.myScorePageList(CommentRequest(pageNum = pageNum,pageSize = 10))
                .onStart {
                }.catch {
                    mBinding?.statusCode = LoadingStatusCode.Error
                }.onCompletion {
                }.collectLatest {
                    mBinding?.statusCode = LoadingStatusCode.Succeed
                    if (it.isOk(this@PointActivity)) {
                        it.data?.list?.let {
                            listString.addAll(it)
                            mAdapter?.notifyDataSetChanged()
                        }
                    }
                    if(listString.size ==0){
                        mBinding?.statusCode = LoadingStatusCode.Empty
                    }
                    mBinding.libHomeSrl.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                }
        }
    }
}