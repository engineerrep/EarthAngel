package com.earth.angel


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.earth.angel.appphoto.fragment.PhotoFragment
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.chat.push.ThirdPushTokenMgr
import com.earth.angel.databinding.ActivityMainBinding
import com.earth.angel.event.MainMessageEvent
import com.earth.angel.event.MessageEvent
import com.earth.angel.gift.ui.fragment.EcoGiftGroupsFragment
import com.earth.angel.mine.MineFragment
import com.earth.angel.mine.MineModel
import com.earth.angel.search.ShakeFragment
import com.earth.angel.user.UserModel
import com.earth.angel.user.ui.fragment.UserFollowingFragment
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.ScreenUtil
import com.earth.angel.view.notify.NotificationControl
import com.earth.angel.view.notify.listener.PendingIntentListener
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.CustomMessage
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.qcloud.tim.uikit.TUIKit
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : BaseActivity<ActivityMainBinding>(), PendingIntentListener {

    private val mAppViewModel by viewModel<AppViewModel>()
    private val mMineModel by viewModel<MineModel>()
    private val userModle by viewModel<UserModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var mFragmentTransaction: FragmentTransaction? = null
    private var mGiftMainFragment: EcoGiftGroupsFragment? = null
    private var mPhotoFragment: PhotoFragment? = null
    private var mMineFragment: MineFragment? = null
    private var mShakeFragment: ShakeFragment? = null
    private var fragments: ArrayList<Fragment> = ArrayList()

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity(savedInstanceState: Bundle?) {
        //showActionBar()
        hideActionBar()
        mBinding?.run {
            handler = this@MainActivity
            NotificationControl.addPendingIntentListener(this@MainActivity)
            ScreenUtil.setAndroidNativeLightStatusBar(this@MainActivity, true)
            EventBus.getDefault().register(this@MainActivity)
            val transaction = supportFragmentManager.beginTransaction()
            if (savedInstanceState != null) {//内存重启
                mGiftMainFragment =
                    supportFragmentManager.findFragmentByTag("MainFragment") as EcoGiftGroupsFragment?
                mPhotoFragment =
                    supportFragmentManager.findFragmentByTag("PhotoFragment") as PhotoFragment?
                mShakeFragment =
                    supportFragmentManager.findFragmentByTag("ShakeFragment") as ShakeFragment?
                mMineFragment =
                    supportFragmentManager.findFragmentByTag("MineFragment") as MineFragment?
            } else {
                //mGiftMainFragment = EcoGiftGroupsFragment()
                mPhotoFragment = PhotoFragment()
                mMineFragment = MineFragment()
                mShakeFragment = ShakeFragment()
                transaction.add(R.id.fcv, mPhotoFragment!!, "MainFragment")
                transaction.add(R.id.fcv, mPhotoFragment!!, "PhotoFragment")
                transaction.add(R.id.fcv, mPhotoFragment!!, "MineFragment")
                transaction.add(R.id.fcv, mPhotoFragment!!, "mShakeFragment")

            }
            fragments.clear()
            fragments.add(mPhotoFragment!!)
            fragments.add(mPhotoFragment!!)
            fragments.add(mPhotoFragment!!)
            fragments.add(mPhotoFragment!!)

            transaction.commit()

            llTabOne.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.show(fragments.get(0))
                transaction.hide(fragments.get(1))
                transaction.hide(fragments.get(2))
                transaction.hide(fragments.get(3))
                transaction.commitAllowingStateLoss()
                ivTabOne.setImageResource(R.mipmap.gift_main_select)
                tvTabOne.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.themColor))
                ivTabThree.setImageResource(R.mipmap.post_no)
                tvTabThree.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )
                ivTabFour.setImageResource(R.mipmap.shake_main_no)
                tvTabFour.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )
                ivTabFive.setImageResource(R.mipmap.earth_my)
                tvTabFive.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )
            }
            llTabThree.setOnClickListener {
                DataReportUtils.getInstance().report("Post")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.hide(fragments.get(0))
                transaction.show(fragments.get(1))
                transaction.hide(fragments.get(2))
                transaction.hide(fragments.get(3))
                transaction.commitAllowingStateLoss()
                ivTabOne.setImageResource(R.mipmap.gift_main_unselect)
                tvTabOne.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )

                ivTabThree.setImageResource(R.mipmap.post_yes)
                tvTabThree.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.themColor
                    )
                )

                ivTabFour.setImageResource(R.mipmap.shake_main_no)
                tvTabFour.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )
                ivTabFive.setImageResource(R.mipmap.earth_my)
                tvTabFive.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )
            }
            llTabFour.setOnClickListener {
                DataReportUtils.getInstance().report("Shake")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.hide(fragments.get(0))
                transaction.hide(fragments.get(1))
                transaction.show(fragments.get(2))
                transaction.hide(fragments.get(3))
                transaction.commitAllowingStateLoss()
                ivTabOne.setImageResource(R.mipmap.gift_main_unselect)
                tvTabOne.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )

                ivTabThree.setImageResource(R.mipmap.post_no)
                tvTabThree.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )

                ivTabFour.setImageResource(R.mipmap.shake_main_yes)
                tvTabFour.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.themColor
                    )
                )
                ivTabFive.setImageResource(R.mipmap.earth_my)
                tvTabFive.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )
            }
            llTabFive.setOnClickListener {
                DataReportUtils.getInstance().report("Me")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.hide(fragments[0])
                transaction.hide(fragments[1])
                transaction.hide(fragments[2])
                transaction.show(fragments[3])
                transaction.commitAllowingStateLoss()
                ivTabOne.setImageResource(R.mipmap.gift_main_unselect)
                tvTabOne.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )

                ivTabThree.setImageResource(R.mipmap.post_no)
                tvTabThree.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )

                ivTabFour.setImageResource(R.mipmap.shake_main_no)
                tvTabFour.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.main_unselect
                    )
                )
                ivTabFive.setImageResource(R.mipmap.main_mine_yes)
                tvTabFive.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.themColor))
            }
            val transaction1 = supportFragmentManager.beginTransaction()
            transaction1.show(fragments.get(0))
            transaction1.hide(fragments.get(1))
            transaction1.hide(fragments.get(2))
            transaction1.hide(fragments.get(3))
            transaction1.commitAllowingStateLoss()
            ivTabOne.setImageResource(R.mipmap.gift_main_select)
            tvTabOne.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.themColor))
            ivTabThree.setImageResource(R.mipmap.post_no)
            tvTabThree.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.main_unselect
                )
            )
            ivTabFour.setImageResource(R.mipmap.shake_main_no)
            tvTabFour.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.main_unselect
                )
            )
            ivTabFive.setImageResource(R.mipmap.earth_my)
            tvTabFive.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.main_unselect
                )
            )
        }


        mAppViewModel?.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })
        select()
        login()
        friendList()

    }

    private fun prepareThirdPushToken() {

        ThirdPushTokenMgr.getInstance().setPushTokenToTIM()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: MessageEvent) {
        friendList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMainMessageEvent(event: MainMessageEvent) {
        if (event.message == 0) {
            mBinding?.tvNumFriendOne.visibility = View.GONE
        } else {
            mBinding?.tvNumFriendOne.visibility = View.VISIBLE
            mBinding?.tvNumFriendOne.text = event.message.toString()
        }
    }

    private fun friendList() {
        launch {
            userModle.following(1, 100).catch { }
                .collect {
                    if (it.data?.list.isNullOrEmpty()) {
                        mBinding?.tvNumFriend?.visibility = View.GONE
                    } else {
                        mBinding?.tvNumFriend?.visibility = View.VISIBLE
                        mBinding?.tvNumFriend?.text = it.data?.list?.size.toString()

                    }
                }
        }
    }


    private fun select() {
        launch {
            mMineModel.selectMine()
                .catch {}.collectLatest {
                    if (it.isOk(this@MainActivity)) {
                        it.data?.let { mine ->
                            mine.nickName?.let {
                                PreferencesHelper.saveName(this@MainActivity, it)
                            }
                            mine.id?.let {
                                PreferencesHelper.saveUserId(this@MainActivity, it)
                            }
                            mine.headImgUrl?.let {
                                PreferencesHelper.saveHead(this@MainActivity, it)
                            }
                            PreferencesHelper.saveMyProfileCache(
                                this@MainActivity,
                                Gson().toJson(mine)
                            )
                        }
                    }
                }
        }
    }


    private fun login() {
        TUIKit.login(
            PreferencesHelper.getUserId(this@MainActivity),
            PreferencesHelper.getImtoken(this@MainActivity),
            object : IUIKitCallBack {
                override fun onSuccess(data: Any?) {
                    prepareThirdPushToken()
                    V2TIMManager.getMessageManager()
                        .addAdvancedMsgListener(object : V2TIMAdvancedMsgListener() {
                            override fun onRecvNewMessage(msg: V2TIMMessage?) {
                                super.onRecvNewMessage(msg)
                                var cus = msg?.customElem
                                cus?.data?.let {
                                    val s = String(cus?.data!!)
                                    val user: CustomMessage =
                                        Gson().fromJson(s, CustomMessage::class.java)
                                    /* user?.Ext?.let {
                                                val ext: CustomExtMessage =
                                                    Gson().fromJson(it, CustomExtMessage::class.java)
                                                if (msg?.sender == ADMIN_NAME) {
                                                    mBinding?.tvNumFriend?.visibility = View.VISIBLE
                                                    val intent = Intent(this@MainActivity, UserFollowingFragment::class.java)
                                                    val pendingIntent =
                                                        PendingIntent.getActivity(this@MainActivity, 0, intent, 0)
                                                    showNotification(this@MainActivity, getString(R.string.lab_Friend_Requests), ext.content, pendingIntent)
                                                }
                                                EventBus.getDefault().post(MessageEvent())
                                            }*/
                                }

                            }
                        })

                }

                override fun onError(module: String?, errCode: Int, errMsg: String?) {
                }
            })
    }

    private var exitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            toast(R.string.exitapp)
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

    override fun onClick(notifyId: Int, viewId: Int) {
        val intent = Intent(EarthAngelApp.instance, UserFollowingFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showNotification(
        context: Context,
        contentTitle: String,
        contentText: String,
        intent: PendingIntent
    ) {
        val channelId: Int = Random().nextInt(543254)
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId.toString())
        builder.setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(intent)
            .setTicker("") //通知首次出现在通知栏，带上升动画效果的
            .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
            .setAutoCancel(true) //设置这个标志当用户单击面板就可以让通知将自动取消
            .setOngoing(false) //ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            .setDefaults(Notification.DEFAULT_VIBRATE) //向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "yourname"
            val mChannel =
                NotificationChannel(channelId.toString(), name, NotificationManager.IMPORTANCE_HIGH)
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel)
                builder.setChannelId(channelId.toString())
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.small_icon) //需要使用背景透明，图标为纯白色的图标。不然在很多机型上，如果直接使用应用icon,会直接显示纯白色图标，体验会不好。
        } else {
            builder.setSmallIcon(R.mipmap.small_icon)
        }
        notificationManager?.notify(channelId * 10, builder.build())
    }
}