package com.earth.angel.mine.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.FragmentIwantUnreadBinding
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.angel.mine.MineModel
import com.earth.angel.mine.adapter.EcoGiftWantAdapter
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.entity.GiftEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class IWantAllFragment : BaseFragment<FragmentIwantUnreadBinding>()  {
    private val mMineModel by viewModel<MineModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private var pageNum: Int = 1
    // var listGift: ArrayList<GiftWantMessageEntity> = ArrayList()
    private var listGift: ArrayList<GiftEntity> = ArrayList()

    private var mEcoGiftWantAdapter: EcoGiftWantAdapter? = null


    override fun getLayoutId(): Int =R.layout.fragment_iwant_unread

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            handler = this@IWantAllFragment

            mEcoGiftWantAdapter = EcoGiftWantAdapter(requireContext(),listGift)
            val layoutPager = LinearLayoutManager(requireContext())
            rvList.layoutManager = layoutPager
            rvList.setAdapter(mEcoGiftWantAdapter)

            mEcoGiftWantAdapter?.setOnItemClickListener { _, _, position ->
                GiftDetailsActivity.openGiftDetailsActivityFromUser(
                    requireContext(),
                    listGift[position]
                )
            }
            srf?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listGift.clear()
                    pageNum = 1
                    getData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getData()
                }
            })
        }
        getData()
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
                    if (it.isOk(activity)) {
                        it.data?.list?.let {

                            listGift.addAll(it)
                            mEcoGiftWantAdapter?.notifyDataSetChanged()

                        }

                    }
                    if(listGift.size<10&&pageNum==1){
                        mBinding?.srf?.setEnableLoadMore(false)
                    }
                    mBinding?.srf?.let {
                        it. finishRefresh(true)
                        it.finishLoadMore()
                    }
                }

        }

    }




}