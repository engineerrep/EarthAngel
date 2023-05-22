package com.earth.libmine.set

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.earth.libbase.Config
import com.earth.libbase.base.*
import com.earth.libbase.dialog.BaseDialogCommon
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.OSSManager
import com.earth.libbase.network.request.UserUpdateRequest
import com.earth.libbase.service.OssInterface
import com.earth.libbase.service.OssService
import com.earth.libbase.util.*
import com.earth.libmine.R
import com.earth.libmine.databinding.ActivityLibMineSetBinding
import com.earth.libmine.ui.LibMineModle
import com.google.gson.Gson
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import kotlinx.android.synthetic.main.activity_lib_mine_set.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

@Route(path = Constance.LibMineSetActivityURL)
class LibMineSetActivity : BaseActivity<ActivityLibMineSetBinding>(), Observer, OssInterface {
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private val mMineModle by viewModel<LibMineModle>()

    private var mOssService: OssService? = null

    override fun getLayoutId(): Int = R.layout.activity_lib_mine_set

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        MessageObservable.messageObservable.addObserver(this)
        mBinding.run {
            LibMineSetLeft.setOnClickListener {
                finish()
            }
            mOssService = OSSManager.initOSS(this@LibMineSetActivity, Config.OSS_ENDPOINT)
            mOssService?.setInterface(this@LibMineSetActivity)
            initNocation()
            LibMineSetNotificationLy.setOnClickListener {
                    AppUtils.gotoSet(this@LibMineSetActivity)
            }
            BaseApplication.myBaseUser?.run {
                headImgUrl?.let { itHead ->
                    Glide.with(LibMineSetHeadImage.context)
                        .load(itHead)
                        .into(LibMineSetHeadImage)
                }
            }
            PreferencesHelper.getEmail(BaseApplication.instance).let {
                LibMineSetEmailClv?.showContent(it)
            }
            LibMineSetHeadNameLy.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    getPhoto()
                }
            }
            LibMinePasswordClv.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.ChangePasswordActivityURL)
                        .navigation()
                }
            }
            LibMineSetAddAddressClv.setOnClickListener {
                ARouter.getInstance().build(Constance.LibMineShippingAddressActivityURL)
                    .navigation()
            }
            LibMineSetNameClv.setOnClickListener {
                ARouter.getInstance().build(Constance.LibMineEtNameActivityURL)
                    .navigation()
            }
            ivLibBaseRightTool.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.HomePackingBagActivityURL)
                        .navigation()
                }
            }
            LibMinePrivacyPolicyClv.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.Html5ActivityURL)
                        .withString("path", Config.Privacy)
                        .withString("title", getString(R.string.label_PrivacyPolicy))
                        .navigation()
                }
            }
            LibMineTermsClv.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.Html5ActivityURL)
                        .withString("path", Config.Terms)
                        .withString("title", getString(R.string.label_TermsofUse))
                        .navigation()
                }
            }
            LibMineSetLogOut.setOnClickListener {
                exitApp()
            }
            LibMineSetDelete.setOnClickListener {
                val blockDialog = BaseDialogCommon(
                    title = getString(R.string.label_Delete),
                    onBlockClick = {
                        deleteUser()
                    })
                blockDialog.show(supportFragmentManager, getString(R.string.label_Delete))
            }
            LibMineSetVersionTv.text = getVersionName(this@LibMineSetActivity)
            BaseApplication.myBaseUser?.let {
                it.poketNumber?.let {
                    if (it == "0") {
                        LibMineSetBarPacketNum?.visibility = View.GONE
                        LibMineSetBarPacketNum?.text = it
                        } else {
                        LibMineSetBarPacketNum?.visibility = View.VISIBLE
                        LibMineSetBarPacketNum?.text = it
                    }
                }
            }
            mAppViewModel?.showLoadingProgress.observe(
                this@LibMineSetActivity,
                androidx.lifecycle.Observer {
                    if (it) mLoadingDialog?.showAllowStateLoss(
                        supportFragmentManager,
                        mLoadingDialog::class.simpleName!!
                    )
                    else mLoadingDialog?.dismiss()
                })
        }

    }

    override fun onRestart() {
        super.onRestart()
        initNocation()
    }

    private fun exitApp(){
        ActivityStackManager.finishAll()
        BaseApplication.myBaseUser = null
        PreferencesHelper.clear(this@LibMineSetActivity)
        ARouter.getInstance().build(Constance.RegistActivityURL).navigation()
        V2TIMManager.getInstance().logout(object : V2TIMCallback {
            override fun onError(code: Int, desc: String) {
            }
            override fun onSuccess() {
            }
        })
    }

    private fun getPhoto() {
        val permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsList.add(1, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                /*           val intent = Intent(this@EditProfileActivity, AddPhotoActivity::class.java)
                               .putExtra(AddPhotoActivity.SUPPORT_MUTABLE, false)
                               .putExtra(AddPhotoActivity.PHOTO_TYPE, PhotoUploadType.PHOTO)
                               .putExtra(AddPhotoActivity.SUPPORT_EDIT, false)
                               .putExtra(AddPhotoActivity.Max_SELECTED_IMAGE_SIZE_KEY, 10)
                           startActivity(intent)*/
                EasyPhotos.createAlbum(
                    this@LibMineSetActivity,
                    true,
                    false,
                    BaseGlideEngine.getInstance())
                    .setPuzzleMenu(false)
                    .setCleanMenu(false)
                    .setFileProviderAuthority("com.earth.angel.fileProvider")
                    .start(101) //也可以选择链式调用写法

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK == resultCode) {
            if (requestCode == 101) {
                //返回对象集合：如果你需要了解图片的宽、高、大小、用户是否选中原图选项等信息，可以用这个
                if(data!=null){
                    val resultPhotos: ArrayList<Photo> =
                        data!!.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)!!
                    resultPhotos?.let {
                        for (item in resultPhotos) {
                            item.path?.let { it1 ->
                                mAppViewModel.showLoading()
                                mOssService?.asyncPutImage(Storage.getFileSufix(it1), it1)
                            }
                        }
                    }
                }
            }
        }
    }

    fun initNocation() {
        mBinding.run {
            AppUtils.isNotificationEnabled(this@LibMineSetActivity)?.let {
                if (it) {
                    LibMineSetNotify.setImageResource(R.mipmap.base_notify_yes)
                } else {
                    LibMineSetNotify.setImageResource(R.mipmap.base_notify_no)
                }
            }
        }
    }

    fun getVersionName(context: Context): String? {
        // 包管理者
        val mg: PackageManager = context.packageManager
        try {
            // getPackageInfo(packageName 包名, flags 标志位（表示要获取什么数据）);
            // 0表示获取基本数据
            val info: PackageInfo = mg.getPackageInfo(context.packageName, 0)
            return info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
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
                        it.pockNum?.let {
                            if (it == "0") {
                                LibMineSetBarPacketNum?.visibility = View.GONE
                                LibMineSetBarPacketNum?.text = it
                            } else {
                                LibMineSetBarPacketNum?.visibility = View.VISIBLE
                                LibMineSetBarPacketNum?.text = it
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBankString(path: String, str: String) {
        if (str.isNotEmpty()) {
            updateUser(str)
        }
    }

    fun updateUser(head: String) {
        launch {
            mMineModle.updateUser(UserUpdateRequest(headImgUrl = head))
                .onStart {
                }.catch {
                }.onCompletion {
                    mAppViewModel?.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@LibMineSetActivity)) {
                        it?.data?.let {
                            PreferencesHelper.saveMyProfileCache(
                                BaseApplication.instance,
                                Gson().toJson(it)
                            )
                            finish()
                        }
                        head?.let {
                            Glide.with(this@LibMineSetActivity)
                                .load(head)
                                .placeholder(R.mipmap.base_comm_img)
                                .into(mBinding?.LibMineSetHeadImage)
                            MessageObservable.messageObservable.newMessage(UpdateEntity(headUrl = head))
                        }
                    }
                }
        }
    }
    fun deleteUser() {
        launch {
            mMineModle.deleteAccount()
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@LibMineSetActivity)) {
                        PreferencesHelper.saveEmail(this@LibMineSetActivity,"")
                        exitApp()
                    }
                }
        }
    }
    override fun filed() {
        mAppViewModel?.dismissLoading()
    }
}