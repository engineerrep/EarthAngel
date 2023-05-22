package com.earth.libhome.ui

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.base.delayLaunch
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.LocationUtil
import com.earth.libbase.util.PreferencesHelper
import com.earth.libhome.R
import com.earth.libhome.adapter.HomeNewGiftListAdapter
import com.earth.libhome.adapter.HomeNewNearByAdapter
import com.earth.libhome.databinding.ActivityHomeNearbyBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

@Route(path = Constance.HomeNearbyActivityURL)
class HomeNearbyActivity : BaseActivity<ActivityHomeNearbyBinding>() {

    private var mNearByAdapter: HomeNewNearByAdapter? = null
    private var listNearBy: ArrayList<BaseGiftEntity> = ArrayList()
    private var pageNum: Int = 1
    private val mHomeModel by viewModel<HomeModle>()
    private var longitude: Double? = null
    private var latitude: Double? = null


    override fun getLayoutId(): Int =R.layout.activity_home_nearby


    override fun initActivity(savedInstanceState: Bundle?) {

        mBinding?.run {
            fitSystemBar()
            mNearByAdapter = HomeNewNearByAdapter(this@HomeNearbyActivity, listNearBy)
            val layoutManager1 = LinearLayoutManager(this@HomeNearbyActivity)
                .apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            libHomeNearByRlv.layoutManager = layoutManager1
            libHomeNearByRlv.adapter = mNearByAdapter
            mNearByAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()){
                    ARouter.getInstance().build(Constance.LibMineUserActivityURL)
                        .withString("userId",listNearBy[position].userId)
                        .navigation()
                }
            }

            LibHomeNearByBank.setOnClickListener {
                finish()
            }
            var mLocation= PreferencesHelper.getLocationName(this@HomeNearbyActivity)
            if(mLocation.isNullOrEmpty()){
                LibHomeNearByLocationLL.visibility= View.GONE
            }else{
                LibHomeNearByLocationLL.visibility= View.VISIBLE
                LibHomeNearByLocationTv.text=mLocation
            }
            libHomeNearBySrl?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listNearBy.clear()
                    mNearByAdapter?.notifyDataSetChanged()
                    pageNum=1
                    libHomeNearBySrl?.setEnableLoadMore(true)
                    getData()
                }
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getData()
                }
            })
            getPermissions()
        }
    }
    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionsList.add(1, Manifest.permission.ACCESS_FINE_LOCATION)

        delayLaunch(500) {
            block = {
                requestPermissions {
                    permissions = permissionsList
                    onAllPermissionsGranted = {
                        PreferencesHelper.saveLocation(this@HomeNearbyActivity, true)
                        if (LocationUtil().getLocationByNet(this@HomeNearbyActivity) != null) {
                            latitude =
                                LocationUtil().getLocationByNet(this@HomeNearbyActivity).doublelist[0]
                            longitude =
                                LocationUtil().getLocationByNet(this@HomeNearbyActivity).doublelist[1]
                            if (latitude != null && longitude != null) {
                                getData()
                            }
                        }
                    }
                    onPermissionsDenied = {

                    }
                    onPermissionsNeverAsked = {

                    }

                }
            }
        }

    }


    private fun getData() {
        launch {
            mHomeModel.nearbyItemGifts(CommonRequest(pageNum, 10,latitude=latitude,longitude = longitude))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    mBinding?.libHomeNearBySrl?.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                    if (it.isOk(this@HomeNearbyActivity)) {
                        if (it.data == null) {
                            mBinding?.libHomeNearBySrl?.setEnableLoadMore(false)
                        } else {
                            it.data?.list?.let {
                                listNearBy.addAll(it)
                                mNearByAdapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
        }


    }

}