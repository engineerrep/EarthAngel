package com.earth.angel.gift.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.OSS
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.base.PhotoUploadType
import com.earth.angel.databinding.ActivityAddGroupBinding
import com.earth.angel.event.GroupCreatEvent
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.photo.AddPhotoActivity
import com.earth.angel.photo.SelectedMutablePhotoEvent
import com.earth.angel.util.EventBusHelper
import com.earth.libbase.Config
import com.earth.libbase.Config.OSS_ACCESS_KEY_ID
import com.earth.libbase.Config.OSS_ACCESS_KEY_SECRET
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.OSSManager
import com.earth.libbase.service.OssInterface
import com.earth.libbase.service.OssService
import com.earth.libbase.util.Storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddGroupActivity : BaseActivity<ActivityAddGroupBinding>(), OssInterface {

    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()

    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var mOssService: OssService?=null

    //OSS的上传下载
    override fun getLayoutId(): Int = R.layout.activity_add_group
    private var mPath: String? = ""
    override fun initActivity(savedInstanceState: Bundle?) {

        EventBusHelper.register(this)
        showActionBar()
        mBinding?.run {
            handler = this@AddGroupActivity
            mOssService = OSSManager.initOSS(this@AddGroupActivity, Config.OSS_ENDPOINT)
            mOssService?.setInterface(this@AddGroupActivity)
            addGroupIv.setOnClickListener {
                getPhoto()
            }
            btSave.isEnabled = false
            btSave.setOnClickListener {
                save()
            }
            etGroupName?.run {
                doAfterTextChanged {
                    checkCanUpdate()
                }
            }
        }

        mAppViewModel.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })

    }

    private fun save() {
        launch {
            mEcoGiftGorupsModel.creatGroup(mBinding?.etGroupName?.text.toString(), mPath!!,null)
                .catch { }.collectLatest {
                    if (it.isOk(this@AddGroupActivity)) {
                    finish()
                    EventBus.getDefault().post(GroupCreatEvent())
                }
            }
        }
    }

    private fun checkCanUpdate() {
        mBinding?.btSave?.isEnabled = when {
            mBinding?.etGroupName?.text.isNullOrEmpty() -> {
                false
            }
            mBinding?.etGroupName?.text.length>40 -> {
                false
            }
            else -> {
                true
            }
        }
    }

    private fun getPhoto() {
        val permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_EXTERNAL_STORAGE)

        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                val intent = Intent(this@AddGroupActivity, AddPhotoActivity::class.java)
                    .putExtra(AddPhotoActivity.SUPPORT_MUTABLE, false)
                    .putExtra(AddPhotoActivity.PHOTO_TYPE, PhotoUploadType.PHOTO)
                    .putExtra(AddPhotoActivity.Max_SELECTED_IMAGE_SIZE_KEY, 10)
                startActivity(intent)
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Subscribe
    fun onEvent(event: SelectedMutablePhotoEvent) {
        when (event.photoType) {
            PhotoUploadType.PHOTO -> {
                if (event.photos.isNotEmpty()) {
                    for (item in event.photos) {
                        item.path?.let { it1 ->
                            mAppViewModel.showLoading()
                            mOssService?.asyncPutImage(Storage.getFileSufix(it1), it1)
                        }
                    }

                }
            }
        }
    }

    private fun initOSS(endpoint: String?, bucket: String?): OssService? {
        val credentialProvider =
            OSSPlainTextAKSKCredentialProvider(OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET)
        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求书，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        val oss: OSS = OSSClient(this@AddGroupActivity, endpoint, credentialProvider, conf)
        OSSLog.enableLog()
        return OssService(oss, Config.BUCKET_NAME)
    }

    companion object {
        fun openAddGroupActivity(context: Context?) {
            val intent = Intent(context, AddGroupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }

    override fun onBankString(path: String,str: String) {

        mAppViewModel?.dismissLoading()
        if (str.isNotEmpty()) {
            val mMessage = Message()
            mMessage.obj = str
            mMessage.what = 1
            mHandler.sendMessage(mMessage)
        }


    }

    override fun filed() {
        mAppViewModel.dismissLoading()
        mPath = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)

    }

    // 创建一个Handler
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    mPath = msg.obj.toString()
                    Glide.with(this@AddGroupActivity)
                        .load(mPath)
                        .placeholder(R.drawable.brvah_sample_footer_loading)
                        .into(mBinding?.addGroupIv)
                }
                // 这里的else相当于Java中switch的default;
                else -> {

                }
            }
        }
    }

}