package com.earth.angel.user.ui.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.FragmentContactsBinding
import com.earth.angel.dialog.DialogContacts
import com.earth.angel.dialog.dialoginterface.DialogContactsInterface
import com.earth.angel.event.ContactsEvent
import com.earth.angel.search.SearchModel
import com.earth.angel.user.UserModel
import com.earth.angel.user.adapter.ContantsAdapter
import com.earth.angel.util.ContactUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.requestPermissions
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
import org.koin.androidx.viewmodel.ext.android.viewModel


class ContactsFragment : BaseFragment<FragmentContactsBinding>() {

    private var mPW: DialogContacts? = null
    private val mViewModel by viewModel<SearchModel>()
    private val userModle by viewModel<UserModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()
    private var adapterCont: ContantsAdapter? = null
    private var listUser: ArrayList<PhotoContactsEntity> = ArrayList()
    private var pageNum: Int = 1

    override fun getLayoutId(): Int = R.layout.fragment_contacts

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            handler = this@ContactsFragment
            EventBus.getDefault().register(this@ContactsFragment)
            adapterCont = ContantsAdapter(requireContext(), listUser, upDade = {
                invite(it)
            }, upJoin = {
                join(it)
            })
            val layoutPager = LinearLayoutManager(requireContext())
            rvList.layoutManager = layoutPager
            rvList.setAdapter(adapterCont)
            rvList.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum = 1
                    listUser.clear()
                    adapterCont?.notifyDataSetChanged()
                    getData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getData()
                }
            })
            rvList.setLoadMoreEnable(false)
            if (PreferencesHelper.getContacts(requireActivity()) == false) {
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


        }

    }


    override fun onResume() {
        super.onResume()
        /*    var permissionsList: MutableList<String> = mutableListOf()
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
                mPW = DialogContacts(requireActivity())
                    .setOnInterface(object : DialogContactsInterface {
                        override fun cancel() {

                        }

                        override fun ok() {
                            listUser.clear()
                            adapterCont?.notifyDataSetChanged()
                            mBinding?.llConnectSet?.visibility = View.GONE
                            mBinding?.lyConnect?.visibility = View.GONE
                            var bean =
                                PhotoAddRequest(ContactUtils.getAllContacts(requireActivity()))
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

    private fun join(photoAddRequest: PhotoContactsEntity) {
        launch {
            photoAddRequest.friendUserId?.let {
                userModle.requestFriend(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(activity)) {
                            requireActivity().toast(requireActivity().getString(R.string.test_Success))
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
                    if (it.isOk(activity)) {
                        mPW?.disMiss()
                        PreferencesHelper.saveContacts(requireActivity(),true)
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
                    if (it.isOk(activity)) {

                    }
                }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: ContactsEvent) {
        if (PreferencesHelper.getContacts(requireActivity()) == false) {
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
            userModle.mobilePhoneAddressGet().catch {}
                .onStart { }
                .onCompletion { }
                .collectLatest {
                    if (it.isOk(activity)) {
                        it.data?.let {
                            listUser.addAll(it)
                            adapterCont?.notifyDataSetChanged()
                            mBinding?.rvList?.let { it1 ->
                                it1.finishRefresh(true)
                                it1.finishLoadMore(true)
                            }
                        }
                    }
                }
        }
    }
}