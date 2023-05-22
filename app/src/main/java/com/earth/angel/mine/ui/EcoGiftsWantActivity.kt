package com.earth.angel.mine.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.ActivityEcoGiftsWantBinding
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.angel.mine.MineModel
import com.earth.angel.mine.adapter.EcoGiftWantAdapter
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

class EcoGiftsWantActivity : BaseActivity<ActivityEcoGiftsWantBinding>(),
    OnRefreshLoadMoreListener {
    private val mMineModel by viewModel<MineModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var pageNum: Int = 1
   // var listGift: ArrayList<GiftWantMessageEntity> = ArrayList()
    private var listGift: ArrayList<GiftEntity> = ArrayList()

    private var mEcoGiftWantAdapter: EcoGiftWantAdapter? = null

    override fun getLayoutId(): Int = R.layout.activity_eco_gifts_want

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            handler = this@EcoGiftsWantActivity
            tvTitleCenter.text = getString(R.string.text_Eco_Gifts_I_Wanted)
            mEcoGiftWantAdapter = EcoGiftWantAdapter(this@EcoGiftsWantActivity,listGift)
            val layoutPager = LinearLayoutManager(this@EcoGiftsWantActivity)
            rvList.layoutManager = layoutPager
            rvList.setAdapter(mEcoGiftWantAdapter)

            mEcoGiftWantAdapter?.setOnItemClickListener { _, _, position ->
                GiftDetailsActivity.openGiftDetailsActivityFromUser(
                    this@EcoGiftsWantActivity,
                    listGift[position]
                )
            }
            onRefreshLoadMoreListener = this@EcoGiftsWantActivity
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

    private fun selectData(releaseRecordId: Long) {
        launch {
            mMineModel.releaseRecordOne(releaseRecordId).onStart {
                mAppViewModel.showLoading()

            }.onCompletion {
                mAppViewModel.dismissLoading()
            }
                .catch { }.collectLatest {
                    if (it.isOk(this@EcoGiftsWantActivity)) {
                        it.data?.let {
                            GiftDetailsActivity.openGiftDetailsActivity(this@EcoGiftsWantActivity,it)
                        }
                    }
                }
        }


    }

    companion object {
        fun openEcoGiftsWantActivity(context: Context?) {
            val intent = Intent(context, EcoGiftsWantActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }

    private fun getData() {
        launch {
            mMineModel.selfPageListV2(pageNum, 10)
                .onStart {
                    mAppViewModel.showLoading()

                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }
                .catch {

                }.collectLatest {
                    if (it.isOk(this@EcoGiftsWantActivity)) {
                        it.data?.list?.let {

                            listGift.addAll(it)
                            mEcoGiftWantAdapter?.notifyDataSetChanged()

                        }

                    }
                }
            mBinding?.rvList?.let { it1 ->
                it1.finishRefresh(true)
                it1.finishLoadMore(true)

                if (pageNum > 1 && (listGift == null || listGift.size <= 0)) {
                    it1.setLoadMoreEnable(false)
                } else {
                    it1.setLoadMoreEnable(true)
                }
            }
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