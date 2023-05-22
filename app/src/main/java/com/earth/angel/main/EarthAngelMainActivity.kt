package com.earth.angel.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.chat.push.ThirdPushTokenMgr
import com.earth.angel.databinding.ActivityEarthAngelMainBinding
import com.earth.angel.mine.MineModel
import com.earth.libbase.base.*
import com.earth.libbase.entity.CustomMessage
import com.earth.libbase.entity.SingEntity
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.GroupRequest
import com.earth.libbase.network.request.UserUpdateRequest
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.BaseScreenUtil
import com.earth.libbase.util.LogUtils
import com.earth.libbase.util.PreferencesHelper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.tencent.imsdk.v2.*
import com.tencent.qcloud.tim.uikit.TUIKit
import com.tencent.qcloud.tim.uikit.base.IMEventListener
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack
import com.tencent.qcloud.tim.uikit.utils.ToastUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.sp
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


@Route(path = Constance.EarthAngelMainActivityURL)
class EarthAngelMainActivity : BaseActivity<ActivityEarthAngelMainBinding>() {

    private val mMineModel by viewModel<MineModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var navController: NavController
    private var count: TextView? = null

    private var code = -1
    private var result: ArrayList<Address> = ArrayList()

    override fun getLayoutId(): Int = R.layout.activity_earth_angel_main

    override fun initActivity(savedInstanceState: Bundle?) {
        fitSystemBar()
        mBinding.run {
            navView.itemIconTintList = null
            //添加自定义的FixFragmentNavigator
            /*          navController =
                          Navigation.findNavController(this@EarthAngelMainActivity, R.id.nav_host_fragment)
                      val fragment =
                          supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                      val fragmentNavigator =
                          FixFragmentNavigator(
                              this@EarthAngelMainActivity,
                              supportFragmentManager,
                              fragment!!.id
                          )
                      navController.navigatorProvider.addNavigator(fragmentNavigator)
                      navController.setGraph(R.navigation.mobile_navigation)

                      navView.setupWithNavController(navController)*/
            val navController = findNavController(R.id.nav_host_fragment)

            navView.setupWithNavController(navController)


          /*  val menuView: BottomNavigationMenuView =
                navView.getChildAt(0) as BottomNavigationMenuView
            val tab: View = menuView.getChildAt(2)
            val itemView: BottomNavigationItemView = tab as BottomNavigationItemView
            val badge: View =
                LayoutInflater.from(this@EarthAngelMainActivity)
                    .inflate(R.layout.menu_badge, menuView, false)
            itemView.addView(badge)

            count = badge.findViewById(R.id.tvNumFriendOne)*/
            //判断是否能用google服务
            code = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this@EarthAngelMainActivity)
            if (code == ConnectionResult.SUCCESS) {
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this@EarthAngelMainActivity)
            }

            val lp: ConstraintLayout.LayoutParams =
                ivMainMessageNext.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(
                0,
                BaseScreenUtil.getActivityMessageHeight(),
                this@EarthAngelMainActivity.sp(18),
                0
            )
            ivMainMessageNext.layoutParams = lp
            initNext()

        }
        val intent = intent
        val data: Uri? = intent.data //


        val goodsId: String? = data?.getQueryParameter("groupId")
        goodsId?.let {
            joinCommunity(GroupRequest(communityId = it))
        }
        login()
        getPermissions()
        signIn()
    }


    //检查是否引导
    private fun initNext() {
       if (PreferencesHelper.getFirstArt(this@EarthAngelMainActivity) == false) {
            // 第一次进
            mBinding.run {
                viewFirst.visibility=View.VISIBLE
                mainButton.visibility = View.VISIBLE
                mBinding.mainButton1.visibility=View.VISIBLE
                lyPost.visibility= View.VISIBLE
                btnPost.setOnClickListener {
                    // 关闭第一个 ，打开第二个
                    lyPost.visibility= View.GONE
                    lyGroup.visibility= View.VISIBLE
                    btnPost.visibility=View.GONE
                }
                btnGroup.setOnClickListener {
                    // 关闭第二个 ，打开第三个
                    lyGroup.visibility= View.GONE
                    lyChat.visibility= View.VISIBLE
                    btnGroup.visibility=View.GONE
                }
                btnMessage.setOnClickListener {
                    // 关闭第三个 ，标记已读
                    lyChat.visibility= View.GONE
                    mainButton?.visibility = View.GONE
                    btnMessage.visibility=View.GONE
                    PreferencesHelper.saveFirstArt(this@EarthAngelMainActivity,true)
                }
            }
        }else{
            //屏蔽
           mBinding.mainButton.visibility=View.GONE
           mBinding.mainButton1.visibility=View.GONE

       }

    }

    private fun joinCommunity(bean: GroupRequest) {
        launch {
            mMineModel.joinCommunity(bean)
                .onStart {
                }.catch {
                    ARouter.getInstance()
                        .build(Constance.GroupMainActivityURL)
                        .withString("GroupID", bean.communityId)
                        .navigation()
                }.onCompletion {
                }.collectLatest {
                    ARouter.getInstance()
                        .build(Constance.GroupMainActivityURL)
                        .withString("GroupID", bean.communityId)
                        .navigation()
                }
        }
    }

    fun signIn() {
        launch {
            mMineModel.signIn()
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@EarthAngelMainActivity)) {
                        it.data?.let {
                            it.whetherNeed?.let { whetherNeed ->
                                if(whetherNeed== SingEntity.NEED){
                                    ARouter.getInstance()
                                        .build(Constance.PointDialogActivityURL)
                                        .withSerializable("SIGN",it)
                                        .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .withFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                        .navigation(this@EarthAngelMainActivity)
                                }
                            }
                        }
                    }
                }
        }
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
                            fusedLocationClient?.lastLocation
                                .addOnSuccessListener { location: Location? ->
                                    // Got last known location. In some rare situations this can be null.
                                    launch {
                                        try {
                                            if (location != null) {
                                                val gc = Geocoder(
                                                    this@EarthAngelMainActivity,
                                                    Locale.getDefault()
                                                )
                                                gc.getFromLocation(
                                                    location.latitude,
                                                    location.longitude, 1
                                                ).let {
                                                    result.clear()
                                                    result.addAll(it)
                                                    withContext(Dispatchers.Main) {
                                                        if (result.size > 0) {
                                                            result[0]?.let { itAddre ->
                                                                if (itAddre.adminArea != null && itAddre.locality != null && itAddre.subLocality != null) {
                                                                    var locationStr =
                                                                        itAddre.subLocality + "." + itAddre.locality + "." + itAddre.adminArea
                                                                    PreferencesHelper.saveLocationName(
                                                                        this@EarthAngelMainActivity,
                                                                        locationStr
                                                                    )
                                                                    updateUser(locationStr,itAddre.latitude,itAddre.longitude)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            PreferencesHelper.saveLocation(this@EarthAngelMainActivity, true)
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

    // 获取地址信息
    fun getAddress(context: Context?, location: Location?): List<Address?>? {
        var result: List<Address?>? = null
        try {
            if (location != null) {
                val gc = Geocoder(context, Locale.getDefault())
                result = gc.getFromLocation(
                    location.latitude,
                    location.longitude, 1
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun updateUser(liveIn: String,latitude: Double,longitude: Double) {
        launch {
            mMineModel.updateUser(UserUpdateRequest(liveIn = liveIn,latitude = latitude,longitude = longitude))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                }
        }
    }

    private fun login() {
        var userid = BaseApplication.myBaseUser?.userId
        var token = PreferencesHelper.getImtoken(this@EarthAngelMainActivity)
        TUIKit.login(userid, token,
            object : IUIKitCallBack {
                override fun onSuccess(data: Any?) {
                    ThirdPushTokenMgr.getInstance().setPushTokenToTIM()
                    LogUtils.error("IMSuccess")
                    TUIKit.addIMEventListener(mIMEventListener)
                    V2TIMManager.getMessageManager()
                        .addAdvancedMsgListener(object : V2TIMAdvancedMsgListener() {
                            override fun onRecvNewMessage(msg: V2TIMMessage?) {
                                super.onRecvNewMessage(msg)
                                var cus = msg?.customElem
                                cus?.data?.let {
                                    val s = String(cus?.data!!)
                                    val user: CustomMessage =
                                        Gson().fromJson(s, CustomMessage::class.java)
                                    if (user.text == getString(R.string.base_head_Agreed) || user.text == getString(
                                            R.string.base_head_Agreeds
                                        )
                                    ) {
                                        getMyPocketNum()
                                    }
                                }
                            }
                        })

                    V2TIMManager.getConversationManager().getConversationList(0,50, object :
                        V2TIMValueCallback<V2TIMConversationResult?> {
                        override fun onSuccess(t: V2TIMConversationResult?) {
                            t?.conversationList?.let {
                                updateConversation(it, true);
                            }
                        }
                        override fun onError(code: Int, desc: String?) {
                        }
                    })
                    V2TIMManager.getConversationManager().setConversationListener(object : V2TIMConversationListener() {
                        override fun onNewConversation(conversationList: MutableList<V2TIMConversation>?) {
                            super.onNewConversation(conversationList)
                            conversationList?.let {
                                updateConversation(conversationList, true)
                            }
                        }
                        override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>?) {
                            super.onConversationChanged(conversationList)
                            conversationList?.let {
                                updateConversation(conversationList, true)
                            }
                        }
                    })
                }

                override fun onError(module: String?, errCode: Int, errMsg: String?) {
                    LogUtils.error("IMSuccessError" + errMsg)
                }
            })
    }
    private fun updateConversation( convList: List<V2TIMConversation>,  needSort: Boolean) {
        var sum=0
        for (index in convList.indices){
            val conv = convList[index]
            sum += conv.unreadCount
        }
        PreferencesHelper.saveMessage(BaseApplication.instance, sum.toString())

        MessageObservable.messageObservable.newMessage(
            UpdateEntity(
                messageNum = sum.toString()
            )
        )
    }


        // 查找包里的物品
    private fun getMyPocketNum() {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mMineModel.userSelectOne(CommonRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@EarthAngelMainActivity)) {
                            it.data?.let { ituser ->
                                PreferencesHelper.saveMyProfileCache(
                                    BaseApplication.instance,
                                    Gson().toJson(ituser)
                                )
                                ituser.poketNumber?.let {
                                    MessageObservable.messageObservable.newMessage(
                                        UpdateEntity(
                                            pockNum = it
                                        )
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }

    /*    override fun onBackPressed() {

    //        如果调用 super.onBackPressed()，navigation会操作回退栈,切换到之前显示的页面，导致 页面叠加错乱
    //        super.onBackPressed()

            val id = navController.currentDestination?.id!!
            val homeNavi = navController.graph[R.id.navigation_Article].id
            if (id != homeNavi) {
                //mBinding?.navView.selectedItemId = R.id.navigation_Article
                finish()
            } else {
                finish()
            }
        }*/
    private var exitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            toast(R.string.exitapp)
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }


    // 监听做成静态可以让每个子类重写时都注册相同的一份。
    private val mIMEventListener: IMEventListener = object : IMEventListener() {
        override fun onForceOffline() {
            ToastUtil.toastLongMessage("Your account has been logged in on another device.")
            ActivityStackManager.finishAll()
            BaseApplication.myBaseUser = null
            PreferencesHelper.clear(this@EarthAngelMainActivity)
            ARouter.getInstance().build(Constance.RegistActivityURL).navigation()

        }

        override fun onUserSigExpired() {
            //   ToastUtil.toastLongMessage(EarthAngelApp.instance.getString(R.string.expired_login_tip));

        }
    }

}