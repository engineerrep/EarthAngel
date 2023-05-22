package com.earth.angel.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.ActivitySearchListBinding
import com.earth.angel.gift.adapter.GroupsAdapter
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.entity.HouseListEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchListActivity : BaseActivity<ActivitySearchListBinding>() {

    private var adapterUser: GroupsAdapter? = null
    private var listOldstr: ArrayList<HouseListEntity>? = ArrayList()
    private val mEcoGiftModel by viewModel<EcoGiftGorupsModel>()
    private var pageNum: Int = 1
    private var type: Int = 0

    override fun getLayoutId(): Int = R.layout.activity_search_list

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            handler = this@SearchListActivity
            type = intent.getIntExtra("type", 0)
            listOldstr = ArrayList<HouseListEntity>()
            adapterUser = GroupsAdapter(this@SearchListActivity, listOldstr, null)
            val layoutManager1 = LinearLayoutManager(this@SearchListActivity)
            xrlv.layoutManager = layoutManager1
            xrlv.setAdapter(adapterUser)
            xrlv.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listOldstr?.clear()
                    adapterUser?.notifyDataSetChanged()
                    getJoined()
                }
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getJoined()
                }
            })
        }
        getJoined()
    }

    private fun getJoined() {

        launch {
            mEcoGiftModel.indexPageList(pageNum, 100).onStart {
            }.catch {
            }.onCompletion {
            }.collectLatest {
                if (it.isOk(this@SearchListActivity)) {
                    it.data?.let {
                        for (item in it) {
                            if (item.type == type) {
                                listOldstr?.add(item)
                            }
                        }
                    }
                    adapterUser?.notifyDataSetChanged()
                }
                mBinding?.xrlv?.let {
                    it.finishRefresh(true)
                    it.finishLoadMore(true)
                }
            }
        }
    }

    companion object {
        fun openSearchListActivity(context: Context, type: Int) {
            val intent = Intent(context, SearchListActivity::class.java)
            intent.putExtra("type", type)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }
}