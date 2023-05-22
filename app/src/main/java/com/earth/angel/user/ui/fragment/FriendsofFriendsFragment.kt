package com.earth.angel.user.ui.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.FragmentFriendsofFriendsBinding
import com.earth.angel.dialog.DialogContacts
import com.earth.angel.dialog.dialoginterface.DialogContactsInterface
import com.earth.angel.event.ContactsEvent
import com.earth.angel.search.SearchModel
import com.earth.angel.user.UserModel
import com.earth.angel.user.adapter.ContantsAdapter
import com.earth.angel.util.ContactUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.PhotoContactsEntity
import com.earth.libbase.network.request.PhotoAddRequest
import com.earth.libbase.util.PreferencesHelper
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
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel


class FriendsofFriendsFragment : BaseActivity<FragmentFriendsofFriendsBinding>() {

    private var mPW: DialogContacts? = null
    private val mViewModel by viewModel<SearchModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private val userModle by viewModel<UserModel>()
    private var adapterCont: ContantsAdapter?=null
    private var listUser: ArrayList<PhotoContactsEntity> = ArrayList()
    private var pageNum: Int = 1

    override fun getLayoutId(): Int =R.layout.fragment_friendsof_friends

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            handler=this@FriendsofFriendsFragment
            EventBus.getDefault().register(this@FriendsofFriendsFragment)

            lyConnect.setOnClickListener {
                getPermissions()
            }
            adapterCont=ContantsAdapter(this@FriendsofFriendsFragment,listUser,upDade = {
                invite(it)
            },upJoin = {
                join(it)
            })
            val layoutPager = LinearLayoutManager(this@FriendsofFriendsFragment)
            rvffList.layoutManager = layoutPager
            rvffList.setAdapter(adapterCont)
            rvffList.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum=1
                    listUser.clear()
                    adapterCont?.notifyDataSetChanged()
                    getData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getData()
                }
            })
            rvffList.setLoadMoreEnable(false)
            if (PreferencesHelper.getContacts(this@FriendsofFriendsFragment) == false) {
                lyConnect.visibility=View.VISIBLE
                lyConnect.setOnClickListener {
                    getPermissions()
                }
            }else{
                lyConnect.visibility=View.GONE
                pageNum = 1
                listUser.clear()
                adapterCont?.notifyDataSetChanged()
                getData()
            }
            mAppViewModel?.showLoadingProgress.observe(this@FriendsofFriendsFragment, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })


        }
   /*     var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_CONTACTS)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                listUser.clear()
                adapterCont?.notifyDataSetChanged()
                mBinding?.llConnectSet?.visibility = View.GONE
                mBinding?.lyConnect?.visibility = View.GONE
                var bean = PhotoAddRequest(ContactUtils.getAllContacts(requireActivity()))
                //   requireActivity().toast("success"+bean.insertDTOList?.size)
                //新建一个联系人实例
               upData(bean)
            }
            onPermissionsDenied = {
                requireActivity().toast("filed")
                mBinding?.llConnectSet?.visibility = View.GONE
                mBinding?.lyConnect?.visibility = View.VISIBLE
            }
            onPermissionsNeverAsked = {
                requireActivity().toast("neverask")
                mBinding?.llConnectSet?.visibility = View.VISIBLE
                mBinding?.lyConnect?.visibility = View.GONE

            }
        }*/


    }

    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_CONTACTS)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                mPW = DialogContacts(this@FriendsofFriendsFragment)
                    .setOnInterface(object : DialogContactsInterface {
                        override fun cancel() {

                        }

                        override fun ok() {
                            listUser.clear()
                            adapterCont?.notifyDataSetChanged()
                            mBinding?.llConnectSet?.visibility = View.GONE
                            mBinding?.lyConnect?.visibility = View.GONE
                            var bean = PhotoAddRequest(ContactUtils.getAllContacts(this@FriendsofFriendsFragment))
                            //   requireActivity().toast("success"+bean.insertDTOList?.size)
                            //新建一个联系人实例
                            upData(bean)
                        }
                    })
                    .create()
                mPW?.show()
            }
            onPermissionsDenied = {
                mBinding?.llConnectSet?.visibility = View.GONE
                mBinding?.lyConnect?.visibility = View.VISIBLE
            }
            onPermissionsNeverAsked = {
                mBinding?.llConnectSet?.visibility = View.VISIBLE
                mBinding?.lyConnect?.visibility = View.GONE

            }
        }
    }
    private fun join(photoAddRequest: PhotoContactsEntity){
        launch {
            photoAddRequest.friendUserId?.let {
                userModle.requestFriend(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if(it.code==200){
                            toast(getString(R.string.test_Success))
                        }
                    }
            }

        }
    }
    private fun upData(photoAddRequest: PhotoAddRequest) {
        launch {
            userModle.mobilePhoneAddressBookAdd(photoAddRequest).catch {}
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .collectLatest {

                    if (it.isOk(this@FriendsofFriendsFragment)) {
                        mPW?.disMiss()
                        PreferencesHelper.saveContacts(this@FriendsofFriendsFragment,true)
                        EventBus.getDefault().post(ContactsEvent())

                    }
                }
        }
    }
    private fun invite(photoAddRequest: PhotoContactsEntity) {
        launch {
            userModle.mobilePhoneAddressInvite(photoAddRequest.id,"").catch {}
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .collectLatest {
                    if (it.isOk(this@FriendsofFriendsFragment)) {

                    }
                }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: ContactsEvent) {
        if (PreferencesHelper.getContacts(this@FriendsofFriendsFragment) == false) {
            mBinding?.lyConnect?.visibility=View.VISIBLE
            mBinding?.lyConnect?.setOnClickListener {
                getPermissions()
            }
        }else{
            mBinding?.lyConnect?.visibility=View.GONE
            pageNum = 1
            listUser.clear()
            adapterCont?.notifyDataSetChanged()
            getData()
        }
    }
    private fun getData() {
        launch {
            userModle.friendsOfFriends().catch {}
                .onStart { }
                .onCompletion { }
                .collectLatest {
                    if (it.isOk(this@FriendsofFriendsFragment)) {
                        it.data?.let {
                            listUser.addAll(it)
                            adapterCont?.notifyDataSetChanged()
                            mBinding?.rvffList?.let { it1 ->
                                it1.finishRefresh(true)
                                it1.finishLoadMore(true)
                            }
                        }
                    }
                }
        }
    }

}