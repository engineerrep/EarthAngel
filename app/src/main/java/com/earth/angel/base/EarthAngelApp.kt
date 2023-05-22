package com.earth.angel.base


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.earth.angel.appphoto.BasePhotoSaveEntity
import com.earth.angel.chat.HelloChatController
import com.earth.angel.chat.push.ThirdPushTokenMgr
import com.earth.angel.module.*
import com.earth.angel.util.MessageNotification
import com.earth.angel.util.PreferencesAppHelper
import com.earth.libbase.Config.SDKAPPID
import com.earth.libbase.Config.sharerUserId
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.BaseContext
import com.earth.libbase.entity.FilterEntity
import com.earth.libbase.entity.MineEntity
import com.earth.libbase.util.PreferencesHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.mmkv.MMKV
import com.tencent.qcloud.tim.uikit.TUIKit
import com.tencent.qcloud.tim.uikit.base.IMEventListener
import com.tencent.qcloud.tim.uikit.base.TUIKitListenerManager
import com.tencent.qcloud.tim.uikit.config.CustomFaceConfig
import com.tencent.qcloud.tim.uikit.config.GeneralConfig
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit.MessageUnreadWatcher
import com.tencent.qcloud.tim.uikit.utils.TUIKitUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class EarthAngelApp : BaseApplication() {

    private val devKey = "5GukTiRRbAugGCr5cHENTN"

    /// api地址
    private val IP_API = "https://open-im.rentsoft.cn"
    /// websocket地址
    private val IP_WS = "wss://open-im.rentsoft.cn/wss"

    override fun onCreate() {
        super.onCreate()
        instance = applicationContext
        BaseContext.instance.init(this)
        ARouter.init(this)
        MMKV.initialize(this)
        myProfileEntity = PreferencesHelper.fetchMyProfileCache(this).run {
            Gson().fromJson(this, MineEntity::class.java)
        }

        mFilterEntity= PreferencesHelper.getFilterCache(this).run {
            Gson().fromJson(this, FilterEntity::class.java)
        }

        mBasePhotoRequest = PreferencesAppHelper.fetchPostCache(this).run {
            Gson().fromJson(this, BasePhotoSaveEntity::class.java)
        }

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@EarthAngelApp)
            androidFileProperties()
            modules(
                dataSourceModule, repositoryModule, viewModelModule,
                fragmentModule, dialogModule, adapterModule
            )
        }
        val conversionDataListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let { cvData ->
                    cvData.map {

                        Log.i("EarthAngelApp", "conversion_attribute:  ${it.key} = ${it.value}")
                        if(it.key=="my_custom_param"){
                            sharerUserId= it.value as String?
                        }
                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                Log.e("EarthAngelApp", "error onAttributionFailure :  $error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    Log.d("EarthAngelApp", "onAppOpen_attribute: ${it.key} = ${it.value}")
                }
            }

            override fun onAttributionFailure(error: String?) {
                Log.e("EarthAngelApp", "error onAttributionFailure :  $error")
            }
        }

        AppsFlyerLib.getInstance().init(devKey, conversionDataListener, applicationContext)
        // 配置 Config，请按需配置
        // 配置 Config，请按需配置
        val configs = TUIKit.getConfigs()
        configs.sdkConfig = V2TIMSDKConfig()
        configs.customFaceConfig = CustomFaceConfig()

        var mGeneralConfig= GeneralConfig()
        mGeneralConfig.isShowRead = true
        configs.generalConfig =mGeneralConfig

        TUIKit.init(this, SDKAPPID, configs)
        registerCustomListeners()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
            //    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            //  val msg = getString(R.string.msg_token_fmt, token)
           // Log.d(TAG, token.toString())
            ThirdPushTokenMgr.getInstance().thirdPushToken = token

        })

        registerActivityLifecycleCallbacks(StatisticActivityLifecycleCallback())

    }
    internal class StatisticActivityLifecycleCallback :
        ActivityLifecycleCallbacks {
        private var foregroundActivities = 0
        private var isChangingConfiguration = false
        private val mIMEventListener: IMEventListener = object : IMEventListener() {
            override fun onNewMessage(msg: V2TIMMessage) {
                val imSdkVersion = V2TIMManager.getInstance().version
                // IMSDK 5.0.1及以后版本 doBackground 之后同时会离线推送
                if (TUIKitUtils.compareVersion(imSdkVersion, "5.0.1") < 0) {
                    val notification: MessageNotification = MessageNotification.getInstance()
                    notification.notify(msg)
                }
            }
        }
        private val mUnreadWatcher =
            MessageUnreadWatcher { count -> // 华为离线推送角标
             //   HUAWEIHmsMessageService.updateBadge(this@DemoApplication, count)
            }

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
          /*  DemoLog.i(
                com.tencent.qcloud.tim.demo.DemoApplication.TAG,
                "onActivityCreated bundle: $bundle"
            )
            if (bundle != null) { // 若bundle不为空则程序异常结束
                // 重启整个程序
                val intent = Intent(activity, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }*/
        }

        override fun onActivityStarted(activity: Activity) {
            foregroundActivities++
            if (foregroundActivities == 1 && !isChangingConfiguration) {
                // 应用切到前台
             /*   DemoLog.i(
                    com.tencent.qcloud.tim.demo.DemoApplication.TAG,
                    "application enter foreground"
                )*/
                V2TIMManager.getOfflinePushManager().doForeground(object : V2TIMCallback {
                    override fun onError(code: Int, desc: String) {
                      /*  DemoLog.e(
                            com.tencent.qcloud.tim.demo.DemoApplication.TAG,
                            "doForeground err = $code, desc = $desc"
                        )*/
                    }

                    override fun onSuccess() {
                    /*    DemoLog.i(
                            com.tencent.qcloud.tim.demo.DemoApplication.TAG,
                            "doForeground success"
                        )*/
                    }
                })
                TUIKit.removeIMEventListener(mIMEventListener)
                ConversationManagerKit.getInstance().removeUnreadWatcher(mUnreadWatcher)
                MessageNotification.getInstance().cancelTimeout()
            }
            isChangingConfiguration = false
        }

        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {
            foregroundActivities--
            if (foregroundActivities == 0) {
                // 应用切到后台
             /*   DemoLog.i(
                    com.tencent.qcloud.tim.demo.DemoApplication.TAG,
                    "application enter background"
                )*/
                val unReadCount = ConversationManagerKit.getInstance().unreadTotal
                V2TIMManager.getOfflinePushManager()
                    .doBackground(unReadCount, object : V2TIMCallback {
                        override fun onError(code: Int, desc: String) {
                         /*   DemoLog.e(
                                com.tencent.qcloud.tim.demo.DemoApplication.TAG,
                                "doBackground err = $code, desc = $desc"
                            )*/
                        }

                        override fun onSuccess() {
                         /*   DemoLog.i(
                                com.tencent.qcloud.tim.demo.DemoApplication.TAG,
                                "doBackground success"
                            )*/
                        }
                    })
                // 应用退到后台，消息转化为系统通知
                TUIKit.addIMEventListener(mIMEventListener)
                ConversationManagerKit.getInstance().addUnreadWatcher(mUnreadWatcher)
            }
            isChangingConfiguration = activity.isChangingConfigurations
        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    private fun registerCustomListeners() {
        TUIKitListenerManager.getInstance().addChatListener(HelloChatController())
        TUIKitListenerManager.getInstance()
            .addConversationListener(HelloChatController.HelloConversationController())
        TUIKitListenerManager.getInstance()
    }


    companion object {
        const val IM_TAG = "TX_IM"
        var myProfileEntity: MineEntity? = null
        var mFilterEntity: FilterEntity? = null
        var mBasePhotoRequest: BasePhotoSaveEntity? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Context
        init {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                ClassicsHeader(
                    context
                )
            }
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                ClassicsFooter(
                    context
                )
            }
        }
    }

}