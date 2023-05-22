package com.earth.angel.search.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.base.searchGorup
import com.earth.angel.databinding.FragmentListItemBinding
import com.earth.angel.event.GroupCreatEvent
import com.earth.angel.event.SearchEvent
import com.earth.angel.gift.ui.GroupDetailsActivity
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.search.SearchModel
import com.earth.angel.search.adapter.SearchGroupsAdapter
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.entity.SearchDetailEntity
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

class ExchangesFragment : BaseFragment<FragmentListItemBinding>() , OnRefreshLoadMoreListener {
    private var atapter: SearchGroupsAdapter?=null
    var listGroup: ArrayList<SearchDetailEntity> = ArrayList()
    private val mLoginViewModel by viewModel<EcoGiftGorupsModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()
    private var pageNum: Int = 1
    private var text: String = ""

    private val mViewModel by viewModel<SearchModel>()

    override fun getLayoutId(): Int = R.layout.fragment_list_item

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        atapter=SearchGroupsAdapter(requireContext(),listGroup,upDade = {
            if(it.status){
                exitGroup(it)
            }else{
                joinGroup(it)
            }
        })
        atapter?.setOnItemClickListener { adapter, view, position ->
            if(listGroup[position].status){
                startActivity.launch(
                    Intent(
                        requireContext(),
                        GroupDetailsActivity::class.java
                    ).putExtra("SearchDetailEntity", listGroup[position])
                        )
            }
        }
        mBinding?.run {
            handler=this@ExchangesFragment
            val layoutPager = LinearLayoutManager(requireContext())
            rvList.layoutManager = layoutPager


            rvList.setAdapter(atapter)
            onRefreshLoadMoreListener=this@ExchangesFragment
        }


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: SearchEvent) {
        if(event.type==searchGorup){
            pageNum=1
            text = event.etstr
            listGroup.clear()
            //giftHouse()
        }

    }
    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                100 -> {

                }
                101 -> {

                }
            }
        }
    private fun joinGroup(bean: SearchDetailEntity) {
        bean.number?.let {
            launch {
                mLoginViewModel.joinGroup(it.toLong()).onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if(it.code==200){
                        bean.status=true
                        atapter?.notifyDataSetChanged()
                        EventBus.getDefault().post(GroupCreatEvent())
                    }
                }
            }
        }
    }
    private fun exitGroup(bean: SearchDetailEntity) {
        bean.number?.let {
            launch {
                mLoginViewModel.quitGroup(it.toLong()).onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if(it.code==200){
                        bean.status=false
                        atapter?.notifyDataSetChanged()
                        EventBus.getDefault().post(GroupCreatEvent())
                    }
                }
            }
        }
    }
/*    private fun giftHouse() {
        launch {
            mViewModel.searchHouse(text,searchGorup, pageNum, 10)
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .catch {
                }.collectLatest {
                    SoftKeyboardUtils.closeInoutDecorView(requireActivity())
                    if(it.code==200){
                        it.data?.list?.let { list ->
                            if (list.isNotEmpty()) {
                                listGroup.addAll(it.data?.list!!)
                                atapter?.notifyDataSetChanged()
                            }
                        }
                        mBinding?.rvList?.let { it1 ->
                            it1.finishRefresh(true)
                            it1.finishLoadMore(true)
                        }
                    }
                }
        }
    }*/
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        listGroup.clear()
        atapter?.notifyDataSetChanged()
        pageNum = 1
       // giftHouse()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
       // giftHouse()
    }

}