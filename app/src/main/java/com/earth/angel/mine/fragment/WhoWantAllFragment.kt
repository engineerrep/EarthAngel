package com.earth.angel.mine.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.appphoto.PhotoEditActivity
import com.earth.angel.databinding.FragmentWhoWantAllBinding
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.angel.mine.MineModel
import com.earth.angel.mine.adapter.WhoWantAllAdapter
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.entity.GiftEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class WhoWantAllFragment : BaseFragment<FragmentWhoWantAllBinding>() {

    private val mMineModel by viewModel<MineModel>()
    private var listGift: ArrayList<GiftEntity> = ArrayList()
    private var pageNum: Int = 1
    private var adapter: WhoWantAllAdapter? = null
    override fun getLayoutId(): Int = R.layout.fragment_who_want_all

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            handler = this@WhoWantAllFragment
            val layoutPager = LinearLayoutManager(requireContext())
            rvList.layoutManager = layoutPager
            adapter = WhoWantAllAdapter(requireContext(), listGift, upDade = {
                deleteData(listGift[it].id.toLong(), it)
            }, upEdit = {
                startActivity.launch(
                    Intent(
                        requireActivity(),
                        PhotoEditActivity::class.java
                    ).putExtra("giftEntity", it)
                )
            })
            adapter?.setOnItemClickListener { _, _, position ->
                GiftDetailsActivity.openGiftDetailsActivityFromUser(
                    requireActivity(),
                    listGift[position]
                )
            }
            rvList.setAdapter(adapter)
            srf?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    mBinding?.srf?.setEnableLoadMore(true)
                    listGift.clear()
                    pageNum = 1
                    giftData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    giftData()
                }
            })

        }
        giftData()
    }

    private fun deleteData(id: Long, position: Int) {
        launch {
            mMineModel.unload(id)
                .catch {
                }.collectLatest {
                    if (it.isOk(activity)) {
                        listGift[position].releaseStatus = 2
                        adapter?.notifyDataSetChanged()
                    }
                }
        }
    }

    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == 100) {
                listGift.clear()
                pageNum = 1
                giftData()
            }
        }

    private fun giftData() {

        launch {
            mMineModel.readPageList(pageNum, 10)
                .catch {

                }.collectLatest {
                    if (it.isOk(activity)) {

                        it.data?.list?.let { list ->
                            if (list.isNotEmpty()) {
                                listGift.addAll(it.data?.list!!)
                                adapter?.notifyDataSetChanged()
                                if(list.size<10&&pageNum==1){
                                    mBinding?.srf?.setEnableLoadMore(false)
                                }
                            }
                        }
                        mBinding?.srf?.let {
                           it. finishRefresh(true)
                            it.finishLoadMore()
                        }

                    }

                }

        }
    }


}