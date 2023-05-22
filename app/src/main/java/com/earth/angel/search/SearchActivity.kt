package com.earth.angel.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.base.*
import com.earth.angel.databinding.ActivitySearchBinding
import com.earth.angel.dialog.DialogContacts
import com.earth.angel.dialog.dialoginterface.DialogContactsInterface
import com.earth.angel.gift.adapter.GroupsAdapter
import com.earth.angel.gift.ui.GroupDetailsActivity
import com.earth.angel.gift.ui.UserMainActivity
import com.earth.angel.message.ui.NotificationActivity
import com.earth.angel.search.SearchListActivity.Companion.openSearchListActivity
import com.earth.angel.search.adapter.SearchUserdapter
import com.earth.angel.user.UserModel
import com.earth.angel.util.*
import com.earth.libbase.Config
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.HouseListEntity
import com.earth.libbase.entity.SearchDetailEntity
import com.earth.libbase.network.request.PhotoAddRequest
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.util.SoftKeyboardUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    private val mSearchModel by viewModel<SearchModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val userModle by viewModel<UserModel>()

    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var mPosition = 0
    private var JsonOldstr: String? = null
    private var listOldstr: ArrayList<HouseListEntity>? = null
    private var listUser: ArrayList<HouseListEntity>? = null
    private var listGroup: ArrayList<HouseListEntity>? = null
    private var listSearch: ArrayList<SearchDetailEntity>? = null
    private var listUserTemb: ArrayList<HouseListEntity>? = ArrayList()
    private var listGroupTemb: ArrayList<HouseListEntity>? = ArrayList()
    private var adapterUser: GroupsAdapter? = null
    private var adapterGroup: GroupsAdapter? = null
    private var adapterSearch: SearchUserdapter?=null
    private var mPW: DialogContacts? = null

    private var senSensorManager: SensorManager? = null

    override fun getLayoutId(): Int = R.layout.activity_search
    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mAppViewModel.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog.dismiss()
        })
        mBinding?.run {


            etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        SoftKeyboardUtils.closeInoutDecorView(this@SearchActivity)
                        DataReportUtils.getInstance().report("Search_keyboard")
                        listSearch?.clear()
                        adapterSearch?.notifyDataSetChanged()
                        var etStr = etSearch.text.toString().trim()
                        if(etStr.isEmpty()){
                            toast(getString(R.string.lab_phone_userid_empty))
                            return true
                        }
                        search(etStr)
                        return true
                    }
                    return false
                }
            })
            etSearch?.run {
                doOnTextChanged { text, _, _, _ ->
                    listSearch?.clear()
                    adapterSearch?.notifyDataSetChanged()
                    getData()
                }
            }

            listUser = ArrayList<HouseListEntity>()
            adapterUser = GroupsAdapter(this@SearchActivity, listUser, null)
            val layoutManager1 = LinearLayoutManager(this@SearchActivity)
            rlvUser.layoutManager = layoutManager1
            rlvUser.adapter = adapterUser
            rlvGroup.adapter = adapterGroup
            adapterUser?.setOnItemClickListener { _, _, position ->
                if(listUser!![position].number == Config.ADMIN_NAME){
                    startActivity.launch(
                        Intent(
                            this@SearchActivity,
                            NotificationActivity::class.java
                        ).putExtra("userId", listUser!![position].number)
                    )
                }else{
                    UserMainActivity.openUserMainActivity(this@SearchActivity, listUser!![position])
                }
            }

            listGroup = ArrayList<HouseListEntity>()
            adapterGroup = GroupsAdapter(this@SearchActivity, listGroup, null)
            val layoutManager2 = LinearLayoutManager(this@SearchActivity)
            rlvGroup.layoutManager = layoutManager2
            rlvGroup.adapter = adapterGroup
            adapterGroup?.setOnItemClickListener { _, _, position ->
                //    GroupDetailsActivity.openGroupDetailsActivity(requireActivity(),listGroup[position])
                if (DateUtils.isFastClick()){
                    startActivity.launch(
                        Intent(
                            this@SearchActivity,
                            GroupDetailsActivity::class.java
                        ).putExtra("giftHouse", listGroup!![position])
                    )
                }

            }
            listSearch = ArrayList<SearchDetailEntity>()
            adapterSearch = SearchUserdapter(this@SearchActivity, listSearch,upDade = {
                if(it.requestFrendsStatus){
                    deleteJoin(it)
                }else{
                    join(it)
                }

            })
            val layoutManager3 = LinearLayoutManager(this@SearchActivity)
            searchRlv.layoutManager = layoutManager3
            searchRlv.adapter = adapterSearch
            lyUser.setOnClickListener {
                openSearchListActivity(this@SearchActivity,1)
            }
            lyGroup.setOnClickListener {
                openSearchListActivity(this@SearchActivity,0)
            }
            llsearch.setOnClickListener {
                listSearch?.clear()
                adapterSearch?.notifyDataSetChanged()
                var etStr = etSearch.text.toString().trim()

                search(etStr)
            }
            tvCancel.setOnClickListener {
                finish()
            }
            if (PreferencesHelper.getContacts(this@SearchActivity) == false) {
                lyConnect.visibility= View.VISIBLE
                lyConnect.setOnClickListener {
                    if (DateUtils.isFastClick()){
                        getPermissions()
                    }
                }
            }
        }
        JsonOldstr = PreferencesHelper.getJson(this@SearchActivity)
        val gson = Gson()
        listOldstr =
            gson.fromJson(JsonOldstr, object : TypeToken<List<HouseListEntity?>?>() {}.type)
    }
    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_CONTACTS)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                if(mPW==null){
                    mPW = DialogContacts(this@SearchActivity)
                        .setOnInterface(object : DialogContactsInterface {
                            override fun cancel() {
                                mPW=null
                            }
                            override fun ok() {

                                mBinding?.lyConnect?.visibility = View.GONE
                                var bean =
                                    PhotoAddRequest(ContactUtils.getAllContacts(this@SearchActivity))
                                //   requireActivity().toast("success"+bean.insertDTOList?.size)
                                //新建一个联系人实例
                                if(bean.insertDTOList?.size==0){
                                    toast("The contact is empty")
                                }else{
                                    upData(bean)
                                }
                            }
                        })
                        .create()
                    mPW?.show()
                }

            }
            onPermissionsDenied = {
                mBinding?.lyConnect?.visibility = View.VISIBLE
            }
            onPermissionsNeverAsked = {
                mBinding?.lyConnect?.visibility = View.GONE

            }
        }
    }
    private fun upData(photoAddRequest: PhotoAddRequest) {
        launch {
            userModle.mobilePhoneAddressBookAdd(photoAddRequest).catch {}
                .onStart { mAppViewModel.showLoading() }
                .onCompletion {
                    mPW?.disMiss()
                    mPW=null
                    mAppViewModel.dismissLoading()
                }
                .collectLatest {
                    if (it.isOk(this@SearchActivity)) {
                        PreferencesHelper.saveContacts(this@SearchActivity,true)
                        toast(getString(R.string.lab_Success))
                    }
                }
        }
    }
    private fun getData() {
        var etStr = etSearch.text.toString().trim()
        mBinding?.tvSearchContentEnd.text= etStr

        listUser?.clear()
        adapterUser?.notifyDataSetChanged()
        listGroup?.clear()
        adapterGroup?.notifyDataSetChanged()
        listUserTemb?.clear()
        listGroupTemb?.clear()
        if (!listOldstr.isNullOrEmpty()) {
            for (item in listOldstr!!) {
                if (item.type == 1) {
                    if (item.name.contains(etStr) || item.number.toString().contains(etStr)) {
                        listUserTemb?.add(item)
                    }
                }
                if (item.type == 0) {
                    if (item.name.contains(etStr) || item.number.toString().contains(etStr)) {
                        listGroupTemb?.add(item)
                    }
                }
            }
        }
        if(listUserTemb?.size==0){
            mBinding?.tvContacts.visibility = View.GONE
            mBinding?.rlvUser.visibility = View.GONE
            mBinding?.lyUser.visibility = View.GONE
        }else{
            mBinding?.tvContacts.visibility = View.VISIBLE
            mBinding?.rlvUser.visibility = View.VISIBLE
            mBinding?.lyUser.visibility = View.VISIBLE
        }
        if(listGroupTemb?.size==0){
            mBinding?.tvGroups.visibility = View.GONE
            mBinding?.rlvGroup.visibility = View.GONE
            mBinding?.lyGroup.visibility = View.GONE
        }else{
            mBinding?.tvGroups.visibility = View.VISIBLE
            mBinding?.rlvGroup.visibility = View.VISIBLE
            mBinding?.lyGroup.visibility = View.VISIBLE
        }
        if (listUserTemb?.size!! <= 3) {
            mBinding?.lyUser.visibility = View.GONE
            listUser?.addAll(listUserTemb!!)
        } else {
            listUser?.add(listUserTemb!![0])
            listUser?.add(listUserTemb!![1])
            listUser?.add(listUserTemb!![2])
            mBinding?.lyUser.visibility = View.VISIBLE
        }
        if (listGroupTemb?.size!! <= 3) {
            listGroup?.addAll(listGroupTemb!!)
            mBinding?.lyGroup.visibility = View.GONE
        } else {
            listGroup?.add(listGroupTemb!![0])
            listGroup?.add(listGroupTemb!![1])
            listGroup?.add(listGroupTemb!![2])
            mBinding?.lyGroup.visibility = View.VISIBLE
        }
        adapterUser?.notifyDataSetChanged()
        adapterGroup?.notifyDataSetChanged()

    }
    private fun join(bean: SearchDetailEntity){
        launch {
            bean.number?.let {
                userModle.requestFriend(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@SearchActivity)) {
                            toast(getString(R.string.lab_Success))
                            bean.requestFrendsStatus=true
                            adapterSearch?.notifyDataSetChanged()
                        }
                    }
            }

        }
    }
    private fun deleteJoin(bean: SearchDetailEntity){
        launch {
            bean.number?.let {
                userModle.deleteAgreeToBeFriends(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@SearchActivity)) {
                            toast(getString(R.string.lab_Success))
                            bean.requestFrendsStatus=false
                            adapterSearch?.notifyDataSetChanged()
                        }
                    }
            }

        }
    }

    private fun search(str: String){

        if(str.isEmpty()){
            toast(getString(R.string.lab_phone_userid_empty))
            return
        }
        launch {
            mSearchModel.search(str, searchAll, 1, 10)
                .onStart { mAppViewModel.showLoading()}
                .onCompletion { mAppViewModel.dismissLoading()}
                .catch {
                }.collectLatest {
                    if (it.isOk(this@SearchActivity)) {
                        if( it.data.isNullOrEmpty()){
                            toast("User not Found")
                        }else{
                            listUser?.clear()
                            adapterUser?.notifyDataSetChanged()
                            listGroup?.clear()
                            adapterGroup?.notifyDataSetChanged()
                            mBinding?.tvContacts.visibility = View.GONE
                            mBinding?.rlvUser.visibility = View.GONE
                            mBinding?.lyUser.visibility = View.GONE
                            mBinding?.tvGroups.visibility = View.GONE
                            mBinding?.rlvGroup.visibility = View.GONE
                            mBinding?.lyGroup.visibility = View.GONE
                            it.data?.let {
                                listSearch?.addAll(it)
                                adapterSearch?.notifyDataSetChanged()
                            }
                        }
                    }
                }
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

    companion object {
        fun openSearchActivity(context: Context?) {
            val intent = Intent(context, SearchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }
}