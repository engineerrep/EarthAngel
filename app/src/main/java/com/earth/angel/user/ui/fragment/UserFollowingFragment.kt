package com.earth.angel.user.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.FragmentUserFollowingBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.event.GroupCreatEvent
import com.earth.angel.event.MessageEvent
import com.earth.angel.user.UserModel
import com.earth.angel.user.adapter.FollowingAdapter
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.FriendRequestUserEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 朋友请求列表
 */
class UserFollowingFragment : BaseActivity<FragmentUserFollowingBinding>(),
    OnRefreshLoadMoreListener {
    private val userModle by viewModel<UserModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var userAdapter: FollowingAdapter? = null
    private var listUser: ArrayList<FriendRequestUserEntity> = ArrayList()
    private var pageNum: Int = 1

    override fun getLayoutId(): Int = R.layout.fragment_user_following



    private fun like(user: FriendRequestUserEntity) {
        launch {
            user?.frendUserId.let {
                userModle.agreeToBeFriends(it.toLong()).catch {}
                    .onStart {   mAppViewModel.showLoading() }
                    .onCompletion {   mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@UserFollowingFragment)) {
                            listUser.remove(user)
                            userAdapter?.notifyDataSetChanged()
                            EventBus.getDefault().post(MessageEvent())
                            EventBus.getDefault().post(GroupCreatEvent())
                            if(listUser.size==0){
                                mBinding?.llEmpty.visibility=View.VISIBLE
                            }else{
                                mBinding?.llEmpty.visibility=View.GONE
                            }
                        }
                    }
            }

        }
    }

    private fun delete(user: FriendRequestUserEntity) {
        launch {
            user?.frendUserId.let {
                userModle.deleteAgreeToBeFriends(it.toLong()).catch {}
                    .onStart {   mAppViewModel.showLoading() }
                    .onCompletion {   mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@UserFollowingFragment)) {
                            listUser.remove(user)
                            userAdapter?.notifyDataSetChanged()
                            EventBus.getDefault().post(MessageEvent())
                            if(listUser.size==0){
                                mBinding?.llEmpty.visibility=View.VISIBLE
                            }else{
                                mBinding?.llEmpty.visibility=View.GONE
                            }
                        }

                    }
            }

        }
    }
    private fun getUser() {
        launch {
            userModle.following(pageNum, 10).catch { }
                .collect {
                    it.data?.list?.let {
                        listUser.addAll(it)
                        userAdapter?.notifyDataSetChanged()
                    }
                    mBinding?.xrlv?.let { it1 ->
                        it1.finishRefresh(true)
                        it1.finishLoadMore(true)
                        if(pageNum==1){
                            if(listUser.size==0){
                                mBinding?.llEmpty.visibility=View.VISIBLE
                            }else{
                                mBinding?.llEmpty.visibility=View.GONE
                            }
                        }
                    }
                }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        listUser.clear()
        userAdapter?.notifyDataSetChanged()
        pageNum = 1
        getUser()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        getUser()
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            handler = this@UserFollowingFragment
            showActionBar()
            tvTitleCenter.text = getString(R.string.lab_Friend_Requests)
            userAdapter = FollowingAdapter(this@UserFollowingFragment, listUser, upDade = {
                like(it)
            }, upDelete = {

                var blockDialog = DialogCommon(title = getString(R.string.dialog_delete),onBlockClick = {
                    delete(it)
                })
                blockDialog.show(
                    supportFragmentManager, ""
                )
            })
            val layoutPager = LinearLayoutManager(this@UserFollowingFragment)
            xrlv.layoutManager = layoutPager
            xrlv.setAdapter(userAdapter)
            xrlv.setSmartRefreshLoadMoreListener(this@UserFollowingFragment)
            xrlv.setLoadMoreEnable(false)
            mAppViewModel?.showLoadingProgress.observe(this@UserFollowingFragment, androidx.lifecycle.Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
        }

        getUser()
    }


}