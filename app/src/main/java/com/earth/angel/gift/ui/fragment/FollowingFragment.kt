package com.earth.angel.gift.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.FragmentFollowingBinding
import com.earth.angel.event.GroupCreatEvent
import com.earth.angel.gift.adapter.PageGiftAdapter
import com.earth.angel.gift.ui.GiftAddMessageActivity
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.angel.gift.ui.RepotActivity
import com.earth.angel.gift.ui.UserMainActivity
import com.earth.angel.search.SearchModel
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.entity.GiftHouseEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel


class FollowingFragment : BaseFragment<FragmentFollowingBinding>(),
    OnRefreshLoadMoreListener {

    private val mLoginViewModel by viewModel<EcoGiftGorupsModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()
    private val mSearchModel by viewModel<SearchModel>()

    private var pageNum: Int = 1
    private var position: Int? = null
    private var houseNumber: Long? = null

    private var listPageGiftList: ArrayList<GiftEntity> = ArrayList()
    private var listGroup: ArrayList<GiftHouseEntity> = ArrayList()

    override fun getLayoutId(): Int = R.layout.fragment_following

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        // showActionBar()
        EventBus.getDefault().register(this)
        mBinding?.run {
            handler = this@FollowingFragment
            adapter = PageGiftAdapter(requireContext(), listPageGiftList,
                upDade = {
                    addLike(it)
                }, upMessage = {
                    GiftAddMessageActivity.openGiftAddMessageActivity(
                        requireActivity(),
                        listPageGiftList[it]
                    )
                }, upUser = { giftEntity: GiftEntity, i: Int ->
                    position = i
                    startActivity.launch(
                        Intent(
                            requireContext(),
                            UserMainActivity::class.java
                        ).putExtra("GiftEntity", giftEntity)
                    )
                }, upfollowe = {
                    likeUser(it)
                }, upReport = {
                    startActivity(Intent(requireActivity(), RepotActivity::class.java)
                        .putExtra("giftEntity",it))
                })
            val layoutPager = LinearLayoutManager(requireContext())
            rvList.layoutManager = layoutPager
            rvList.adapter = adapter

            adapter?.setOnItemClickListener { _, _, position ->
                GiftDetailsActivity.openGiftDetailsActivity(
                    requireActivity(),
                    listPageGiftList[position]
                )
            }
            srf.setOnLoadMoreListener {
                pageNum++
                getPageList()
            }
            srf.setOnRefreshListener {
                notifyData()
            }
        }
        getJoined()
    }

    private fun likeUser(bean: GiftEntity) {
        bean.userId?.let {
            launch {
                mSearchModel.concerned(bean.userId.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(activity)) {
                            bean.isConcern = true
                            mBinding?.adapter?.notifyDataSetChanged()
                        }
                    }
            }
        }
    }

    private fun report(str: String, bean: GiftEntity) {
        bean?.let {
            launch {
                mSearchModel.report(str, bean.id.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(activity)) {
                        }
                    }
            }
        }
    }

    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                100 -> {
                    notifyData()
                }
                101 -> {
                }
            }
        }

    private fun notifyData() {
        listPageGiftList.clear()
        listGroup.clear()
        pageNum = 1
        getJoined()
    }

    private fun addLike(position: Int) {
        var relationStatus: Int = 0
        if (listPageGiftList[position].isLike) {
            relationStatus = 0
        } else {
            relationStatus = 2
        }
        launch {
            mLoginViewModel.goodsRelation(relationStatus, listPageGiftList[position].id.toLong())
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                        listPageGiftList[position].isLike =
                            listPageGiftList[position].isLike != true
                        mBinding?.adapter?.notifyDataSetChanged()
                    }
                }
        }
    }

    private fun getJoined() {
        launch {
            mLoginViewModel.getJoinGroup(1, 10)
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                        if (it.data == null) {
                            recommend()
                        } else {
                            it.data?.let {
                                if (it.size!! > 0) {
                                    listGroup.addAll(it)
                                    houseNumber = listGroup[0].houseNumber
                                    getPageList()
                                } else {
                                    recommend()
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun getPageList() {
        houseNumber?.let { itlong ->
            launch {
                mLoginViewModel.selectUser(itlong, pageNum, 10).onStart {
                    // mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    //  mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                        it.data?.list?.let {
                            if (it.isNotEmpty()) {
                                listPageGiftList.addAll(it)
                                mBinding?.adapter?.notifyDataSetChanged()
                            }

                        }
                        mBinding?.srf?.let { it1 ->
                            it1.finishRefresh(true)
                            it1.finishLoadMore()
                            if (pageNum > 1 && (listPageGiftList == null || listPageGiftList.size <= 0)) {
                                it1.finishRefresh(true)

                            } else {
                                it1.finishLoadMore()

                            }
                        }
                        if (pageNum==1&&listPageGiftList.isEmpty()){
                            mBinding?.tvEmpty?.visibility=View.VISIBLE
                        }else{
                            mBinding?.tvEmpty?.visibility=View.GONE
                        }
                    }

                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: GroupCreatEvent) {
        if (event.isConcern == null) {
            notifyData()
        } else {
            if (position == null) {
                notifyData()
            } else {
                position?.let {
                    event?.isConcern?.let { itb->
                        listPageGiftList[it].isConcern=itb
                        for(item in listPageGiftList ){
                            if(item.userId.toLong()==event.userId){
                                item.isConcern = itb
                            }
                        }
                    }

                    mBinding?.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun recommend() {
        launch {
            mLoginViewModel.recommend(1, 10)
                .catch {  }
                .collectLatest {
                Log.e("", "" + it.toString())
                if (it.isOk(activity)) {

                    it.data?.let {
                        listGroup.addAll(it)
                        if (it.isNotEmpty()) {
                            houseNumber = listGroup[0].houseNumber
                            getPageList()
                        }
                    }
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        listPageGiftList.clear()
        getPageList()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        getPageList()
    }
}