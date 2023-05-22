package com.earth.angel.user.ui

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.databinding.ActivityContactsBinding
import com.earth.angel.dialog.DialogContacts
import com.earth.angel.dialog.dialoginterface.DialogContactsInterface
import com.earth.angel.gift.ui.UserMainActivity.Companion.openUserMainActivity
import com.earth.angel.user.UserModel
import com.earth.angel.user.adapter.ContantsAdapter
import com.earth.angel.util.ContactUtils
import com.earth.angel.util.DateUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.PhotoContactsEntity
import com.earth.libbase.network.request.PhotoAddRequest
import com.earth.libbase.util.PreferencesHelper
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactsActivity : BaseActivity<ActivityContactsBinding>() {

    private var mPW: DialogContacts? = null
    private val userModle by viewModel<UserModel>()
    private var adapterCont: ContantsAdapter? = null
    private var listUser: ArrayList<PhotoContactsEntity> = ArrayList()
    private var pageNum: Int = 1

    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    override fun getLayoutId(): Int = R.layout.activity_contacts

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            handler = this@ContactsActivity
            tvTitleCenter.text = getString(R.string.lab_Contacts)
            adapterCont = ContantsAdapter(this@ContactsActivity, listUser, upDade = {
                invite(it)
            }, upJoin = {
                if (it.requestFrendsStatus == true) {
                    deleteJoin(it)
                } else {
                    join(it)
                }
            })
            adapterCont?.setOnItemClickListener { _, _, position ->

                var bean = listUser[position]
                if (!bean.nickName.isNullOrEmpty()) {
                    openUserMainActivity(
                        this@ContactsActivity,
                        bean.nickName!!, bean.ownerUserId!!, bean.headImgUrl!!
                    )
                }
            }
            val layoutPager = LinearLayoutManager(this@ContactsActivity)
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

            if (PreferencesHelper.getContacts(this@ContactsActivity) == false) {
                lyConnect.visibility = View.VISIBLE
                lyConnect.setOnClickListener {
                    if (DateUtils.isFastClick()) {
                        getPermissions()
                    }
                }
            } else {
                lyConnect.visibility = View.GONE
                pageNum = 1
                listUser.clear()
                adapterCont?.notifyDataSetChanged()
                getData()
            }
            mAppViewModel?.showLoadingProgress.observe(this@ContactsActivity, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
        }
    }


    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_CONTACTS)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                if (mPW == null) {
                    mPW = DialogContacts(this@ContactsActivity)
                        .setOnInterface(object : DialogContactsInterface {
                            override fun cancel() {
                                mPW = null
                            }

                            override fun ok() {
                                listUser.clear()
                                adapterCont?.notifyDataSetChanged()
                                mBinding.llConnectSet?.visibility = View.GONE
                                mBinding.lyConnect?.visibility = View.GONE
                                var bean =
                                    PhotoAddRequest(ContactUtils.getAllContacts(this@ContactsActivity))
                                //   requireActivity().toast("success"+bean.insertDTOList?.size)
                                //新建一个联系人实例
                                if (bean.insertDTOList?.size == 0) {
                                    toast("The contact is empty")
                                } else {
                                    upData(bean)
                                }
                            }
                        })
                        .create()
                    mPW?.show()
                }

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

    private fun deleteJoin(photoAddRequest: PhotoContactsEntity) {
        launch {
            photoAddRequest.friendUserId?.let {
                userModle.deleteAgreeToBeFriends(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@ContactsActivity)) {
                            toast(getString(R.string.lab_Success))
                            photoAddRequest.requestFrendsStatus = false
                            adapterCont?.notifyDataSetChanged()

                        }

                    }
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
                        if (it.isOk(this@ContactsActivity)) {
                            toast(getString(R.string.lab_Success))
                            photoAddRequest.requestFrendsStatus = true
                            adapterCont?.notifyDataSetChanged()

                        }
                    }
            }
        }
    }

    private fun upData(photoAddRequest: PhotoAddRequest) {
        launch {
            userModle.mobilePhoneAddressBookAdd(photoAddRequest).catch {}
                .onStart { mAppViewModel.showLoading() }
                .onCompletion {
                    mPW?.disMiss()
                    mPW = null
                    mAppViewModel.dismissLoading()
                }
                .collectLatest {
                    if (it.isOk(this@ContactsActivity)) {
                        toast(getString(R.string.lab_Success))
                        getData()
                    }
                }
        }
    }
    private val USERADDRESS = "https://www.earthangel.app:13002/share/index.html?link=HSGI"

    private fun invite(photoAddRequest: PhotoContactsEntity) {



        photoAddRequest?.let {
            launch {
                userModle.mobilePhoneAddressInvite(it.id,"https://earthangel.onelink.me/HSGl/97d046db?pid=networkx_int&c=winter&af_adset=coats&af_ad=cashmere&my_custom_param="+EarthAngelApp.myProfileEntity?.id).catch {}
                    .onStart { mAppViewModel.showLoading()}
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@ContactsActivity)) {
                            toast(getString(R.string.lab_Success))
                        }
                    }
            }

        }

    }

    @ExperimentalCoroutinesApi
    private fun getData() {
        launch {
            userModle.mobilePhoneAddressGet().catch {}
                .onStart { }
                .onCompletion { }
                .collectLatest {
                    PreferencesHelper.saveContacts(this@ContactsActivity, true)

                    if (it.isOk(this@ContactsActivity)) {
                        it.data?.let {
                            listUser.addAll(it)
                            adapterCont?.notifyDataSetChanged()
                            mBinding.rvList?.let { it1 ->
                                it1.finishRefresh(true)
                                it1.finishLoadMore(true)
                            }
                            if (listUser.size == 0) {
                                mBinding.llEmpty.visibility = View.VISIBLE
                            } else {
                                mBinding.llEmpty.visibility = View.GONE
                            }
                        }
                    }
                }
        }
    }
}