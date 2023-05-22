package com.earth.libhome.group

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Location
import android.os.*
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.*
import com.earth.libbase.dialog.LocationServiceDialog
import com.earth.libbase.entity.GroupEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.util.AppUtils
import com.earth.libbase.util.BaseDateUtils
import com.earth.libhome.R
import com.earth.libhome.adapter.GroupListAdapter
import com.earth.libhome.adapter.GroupPageAdapter
import com.earth.libhome.databinding.ActivityGrouplistBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.maps.SupportMapFragment
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.libhome_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


@Route(path = Constance.GroupListActivityURL)
class GroupListActivity : BaseActivity<ActivityGrouplistBinding>() , Observer{


    private val mGroupApiModle by viewModel<GroupApiModle>()
    private var listGroupEntity: ArrayList<GroupEntity> = ArrayList()
    private var listNear: ArrayList<GroupEntity> = ArrayList()
    private var pageNum: Int = 1
    private var code = -1

    private var head: View? = null
    private var foot: View? = null

    private var adapter: GroupListAdapter? = null
    private var adapterPage: GroupPageAdapter? = null
    private var llMyGroupsCreate: LinearLayout? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var result: ArrayList<Address> = ArrayList()
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var rlvMyGroups: RecyclerView? = null
    private var mIvEmpty: ImageView? = null
    override fun getLayoutId(): Int = R.layout.activity_grouplist

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        MessageObservable.messageObservable.addObserver(this)
        head = layoutInflater.inflate(R.layout.head_grouplist, null)
        foot = layoutInflater.inflate(R.layout.group_foot, null)
        head?.let {
            rlvMyGroups = it.findViewById(R.id.rlvMyGroups)
            llMyGroupsCreate = it.findViewById(R.id.llMyGroupsCreate)
            val tvCreateNow: TextView? = it.findViewById(R.id.tvCreateNow)
            tvCreateNow?.setOnClickListener {
                jump()
            }
            adapter = GroupListAdapter(listGroupEntity)
            rlvMyGroups?.layoutManager = LinearLayoutManager(
                this@GroupListActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            rlvMyGroups?.adapter = adapter
            adapter?.setOnItemClickListener { adapter, view, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance()
                        .build(Constance.GroupMainActivityURL)
                        .withString("GroupID", listGroupEntity[position].id)
                        .navigation()
                }
            }
        }
        foot?.let {
            mIvEmpty = it.findViewById(R.id.ivEmpty)
            mIvEmpty?.setOnClickListener {
                getPermissions()
            }
        }
        mBinding.run {
            tvLibHomeLeftTool.setOnClickListener {
                finish()
            }
            tvLibMinTitleCenter.text = getString(R.string.label_Community_list)
            ivLibHomeRightTool.setBackgroundResource(R.mipmap.group_create)
            ivLibHomeRightTool.setOnClickListener {
                jump()
            }

            rlvMyGroupsList.layoutManager = LinearLayoutManager(
                this@GroupListActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapterPage = GroupPageAdapter(listNear)
            rlvMyGroupsList.adapter = adapterPage
            adapterPage?.addHeaderView(head!!)
            adapterPage?.addFooterView(foot!!)
            adapterPage?.setOnItemClickListener { adapter, view, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance()
                        .build(Constance.GroupMainActivityURL)
                        .withString("GroupID", listNear[position].id)
                        .navigation()
                }
            }
            MyGroupsSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    dataRefresh()
                }
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    nearbyCommunityList()
                }
            })
            code = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this@GroupListActivity)
            if (code == ConnectionResult.SUCCESS) {
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this@GroupListActivity)
            }
        }
        communityList()

        if (AppUtils.isLocationServiceEnable(this@GroupListActivity)) {
            getPermissions()
        } else {
            val blockDialog = LocationServiceDialog(
                onBlockClick = {
                    AppUtils.foToOpenGps(this@GroupListActivity)
                })
            blockDialog.show(supportFragmentManager, "LocationServiceDialog")
        }

    }

    private fun dataRefresh(){
        pageNum = 1
        mBinding.MyGroupsSrl.setEnableLoadMore(true)
        listGroupEntity.clear()
        adapter?.notifyDataSetChanged()
        communityList()
        listNear.clear()
        adapterPage?.notifyDataSetChanged()
        nearbyCommunityList()
    }


    @SuppressLint("MissingPermission")
    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionsList.add(1, Manifest.permission.ACCESS_FINE_LOCATION)
        delayLaunch(500) {
            block = {
                requestPermissions {
                    permissions = permissionsList
                    onAllPermissionsGranted = {
                        if (code == ConnectionResult.SUCCESS) {
                            mIvEmpty?.visibility = View.GONE
                            fusedLocationClient?.lastLocation
                                .addOnSuccessListener { location: Location? ->
                                    // Got last known location. In some rare situations this can be null.

                                    if (location != null) {
                                        Thread {
                                            try {
                                                latitude = location.latitude
                                                longitude = location.longitude
                                                mHandler.sendEmptyMessage(1)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }.start()

                                    } else {

                                        val locationRequest: LocationRequest =
                                            LocationRequest.create()
                                        locationRequest.priority =
                                            LocationRequest.PRIORITY_HIGH_ACCURACY;
                                        locationRequest.interval = 2000
                                        locationRequest.fastestInterval = 1000
                                        fusedLocationClient.requestLocationUpdates(
                                            locationRequest,
                                            mLocationCallback,
                                            Looper.getMainLooper()
                                        )

                                    }


                                }
                        }
                    }
                    onPermissionsDenied = {
                        mIvEmpty?.isClickable = true
                        mIvEmpty?.visibility = View.VISIBLE
                        mIvEmpty?.setImageResource(R.mipmap.group_perssion)

                    }
                    onPermissionsNeverAsked = {
                    }
                }
            }
        }
    }

    var currentLocation: Location? = null

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult?.lastLocation == null) {
                return
            }
            listNear.clear()
            adapterPage?.notifyDataSetChanged()
            //获取到位置信息
            currentLocation = locationResult.lastLocation
            Thread {
                try {
                    latitude = currentLocation!!.latitude
                    longitude = currentLocation!!.longitude
                    mHandler.sendEmptyMessage(1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    // 创建一个Handler
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    fusedLocationClient.removeLocationUpdates(mLocationCallback)
                    nearbyCommunityList()
                }
                // 这里的else相当于Java中switch的default;
                else -> {
                }
            }
        }
    }

    private fun communityList() {
        launch {
            mGroupApiModle.communityList(CommentRequest(pageNum = 1, pageSize = 100))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@GroupListActivity)) {
                        it.data?.list?.let {
                            listGroupEntity.addAll(it)
                            adapter?.notifyDataSetChanged()
                        }
                    }
                    if (listGroupEntity.size == 0) {
                        rlvMyGroups?.visibility = View.GONE
                        llMyGroupsCreate?.visibility = View.VISIBLE
                    } else {
                        rlvMyGroups?.visibility = View.VISIBLE
                        llMyGroupsCreate?.visibility = View.GONE
                    }
                }
        }
    }

    private fun nearbyCommunityList() {
        launch {
            mGroupApiModle.nearbyCommunityList(
                CommentRequest(
                    pageNum = pageNum,
                    pageSize = 10,
                    latitude = latitude,
                    longitude = longitude
                )
            )
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@GroupListActivity)) {
                        it.data?.list?.let {
                            listNear.addAll(it)
                            adapterPage?.notifyDataSetChanged()
                        }
                    }
                    if (listNear.size == 0) {
                        mIvEmpty?.isClickable = false
                        mIvEmpty?.visibility = View.VISIBLE
                        mIvEmpty?.setImageResource(R.mipmap.group_empty)
                    } else {
                        mIvEmpty?.visibility = View.GONE
                    }
                    mBinding?.MyGroupsSrl?.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                }
        }
    }

    private fun jump() {
        if (BaseDateUtils.isFastClick()) {
            ARouter.getInstance()
                .build(Constance.GroupCreateActivityURL)
                .navigation()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        MessageObservable.messageObservable.deleteObserver(this)
    }
    override fun update(o: Observable?, arg: Any?) {
        o?.let {
            if (it is MessageObservable) {
                arg?.let {
                    var bean = it as UpdateEntity
                    bean.let {
                        // 更新列表
                        it.groupList?.let {
                            dataRefresh()
                        }
                    }
                }
            }
        }
    }
}