package com.earth.angel.search.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.base.searchUser
import com.earth.angel.databinding.FragmentListUserItemBinding
import com.earth.angel.event.GroupCreatEvent
import com.earth.angel.event.SearchEvent
import com.earth.angel.search.SearchModel
import com.earth.angel.search.adapter.SearchUserdapter
import com.earth.angel.util.DataReportUtils
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

class UserFragment : BaseFragment<FragmentListUserItemBinding>(), OnRefreshLoadMoreListener {
    private val mViewModel by viewModel<SearchModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()

    private var mAtapter: SearchUserdapter? = null
    private var listGroup: ArrayList<SearchDetailEntity> = ArrayList()
    private var pageNum: Int = 1
    private var text: String = ""


    override fun getLayoutId(): Int = R.layout.fragment_list_user_item

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        mAtapter = SearchUserdapter(requireContext(), listGroup,upDade = {
        //    joinGroup(it)
        })

        mBinding?.run {
            handler=this@UserFragment
            val layoutPager = LinearLayoutManager(requireContext())
            rvList.layoutManager = layoutPager
            rvList.setAdapter(mAtapter)
            onRefreshLoadMoreListener=this@UserFragment
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: SearchEvent) {
        if (event.type == searchUser) {
            pageNum=1
            listGroup.clear()
            mAtapter?.notifyDataSetChanged()
            if(event.etstr.isNullOrEmpty()){
            //    giftHouse()
            }else{
                text = event.etstr
               // giftHouse()
            }

        }

    }
    private fun joinGroup(position: Int) {
        listGroup[position].number?.let {

            if(  listGroup[position].status){
                launch {
                    mViewModel.deleteConcerned(it.toLong()).catch {}
                        .onStart { mAppViewModel.showLoading() }
                        .onCompletion { mAppViewModel.dismissLoading() }
                        .collectLatest {
                            if(it.code==200){
                                DataReportUtils.getInstance().report("Unfollow")

                                listGroup[position].status=false
                                mAtapter?.notifyDataSetChanged()
                                EventBus.getDefault().post(GroupCreatEvent())
                            }
                        }
                }
            }else{
                launch {
                    mViewModel.concerned(it.toLong()).catch {}
                        .onStart { mAppViewModel.showLoading() }
                        .onCompletion { mAppViewModel.dismissLoading() }
                        .collectLatest {
                            if(it.code==200){
                                DataReportUtils.getInstance().report("Follow")

                                listGroup[position].status=true
                                mAtapter?.notifyDataSetChanged()
                                EventBus.getDefault().post(GroupCreatEvent())
                            }
                        }
                }
            }



        }
    }
  /*  private fun giftHouse() {

        launch {
            mViewModel.searchHouse(text, searchUser, pageNum, 10)
                .onStart { }
                .onCompletion {  }
                .catch {

                }.collectLatest {
                    SoftKeyboardUtils.closeInoutDecorView(requireActivity())

                    if (it.code == 200) {

                        it.data?.list?.let { list ->
                            if (list.isNotEmpty()) {
                                listGroup.addAll(it.data?.list!!)
                                mAtapter?.notifyDataSetChanged()
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
        pageNum = 1
      //  giftHouse()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
      //  giftHouse()
    }
}