package com.earth.angel.mine.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.ActivityConsideringGiftBinding
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.angel.mine.MineModel
import com.earth.angel.mine.adapter.EcoGiftConsiderAdapter
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GiftEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConsideringGiftActivity : BaseActivity<ActivityConsideringGiftBinding>(),
    OnRefreshLoadMoreListener {

    private val mMineModel by viewModel<MineModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var mEcoGiftConsiderAdapter: EcoGiftConsiderAdapter? = null
    private var pageNum: Int = 1
    private var listGift: ArrayList<GiftEntity> = ArrayList()
    override fun getLayoutId(): Int = R.layout.activity_considering_gift

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            handler = this@ConsideringGiftActivity
            tvTitleCenter.text = getString(R.string.lab_considering)
            mEcoGiftConsiderAdapter = EcoGiftConsiderAdapter(listGift)
            val layoutPager = LinearLayoutManager(this@ConsideringGiftActivity)
            rvList.layoutManager = layoutPager
            rvList.setAdapter(mEcoGiftConsiderAdapter)
            onRefreshLoadMoreListener = this@ConsideringGiftActivity
            mEcoGiftConsiderAdapter?.setOnItemClickListener { _, _, position ->
                GiftDetailsActivity.openGiftDetailsActivity(
                    this@ConsideringGiftActivity,
                    listGift[position]
                )
            }
        }
        mAppViewModel?.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })
        getData()
    }

    private fun getData() {
        launch {
            mMineModel.likeList(pageNum, 10)
                .catch {

                }.onStart {
                  //  mAppViewModel.showLoading()
                }
                .onCompletion {
                  //  mAppViewModel.dismissLoading()
                }
                .collectLatest {
                    if (it.isOk(this@ConsideringGiftActivity)) {
                        it.data?.list?.let { itList ->
                            listGift.addAll(itList)
                            mEcoGiftConsiderAdapter?.notifyDataSetChanged()

                        }
                    }
                    mBinding?.rvList?.let { it1 ->
                        it1.finishRefresh(true)
                        it1.finishLoadMore(true)
                    }
                }
        }

    }

    companion object {
        fun openConsideringGiftActivity(context: Context?) {
            val intent = Intent(context, ConsideringGiftActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        listGift.clear()
        pageNum = 1
        getData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        getData()
    }
}