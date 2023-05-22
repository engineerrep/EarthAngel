package com.earth.angel.mine.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.databinding.ActivityEditProfileBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.mine.MineModel
import com.earth.angel.photo.SelectedMutablePhotoEvent
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.angel.util.EventBusHelper
import com.earth.libbase.Config
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.OSSManager
import com.earth.libbase.service.OssInterface
import com.earth.libbase.service.OssService
import com.earth.libbase.util.BaseGlideEngine
import com.earth.libbase.util.Storage
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>(), OssInterface {
    private val mMineModel by viewModel<MineModel>()
    private val maxTextNum = 16//最多显示的字数

    //OSS的上传下载
    private var mPath: String? = ""
    private var mOldPath: String? = ""
    private var mName: String? = ""
    private var mOldName: String? = ""
    private var mOssService: OssService? = null

    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    override fun getLayoutId(): Int = R.layout.activity_edit_profile

    override fun initActivity(savedInstanceState: Bundle?) {
        EventBusHelper.register(this)
        mBinding?.run {
            handler = this@EditProfileActivity
            tvTitleCenter.text = "Update Profile"
            toolbarTabRight.text = "Save"
            toolbarTabRight.setOnClickListener {
                save()
            }
            tvLeftToolUser.setOnClickListener {
                exit()
            }
            showActionBar()
            mOssService = OSSManager.initOSS(this@EditProfileActivity, Config.OSS_ENDPOINT)
            mOssService?.setInterface(this@EditProfileActivity)

            llPhoto.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    DataReportUtils.getInstance().report("Update_profile_photo")
                    getPhoto()
                }

            }
            etName.run {
                doOnTextChanged { text, _, _, _ ->
                    mName = text!!.toString().trim()
                }
            }
            etName?.run {
                var wordNum: CharSequence? = null
                var selectionStart: Int
                var selectionEnd: Int
                doOnTextChanged { text, _, _, _ ->
                    text?.let {
                        wordNum = it.toString().trim()
                    }
                }
                doAfterTextChanged {
                    maxTextNum - it!!.length
                    mName = text!!.toString().trim()
                    selectionStart = getSelectionStart()
                    selectionEnd = getSelectionEnd()
                    if (wordNum!!.length > maxTextNum) {
                        it.delete(selectionStart - 1, selectionEnd)
                        val tempSelection = selectionEnd
                        setSelection(tempSelection)
                    }
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

        select()

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
        }
        return false

    }

    private fun checkExit(): Boolean {
        var exitBoolean = false
        if (mPath != mOldPath) {
            exitBoolean = true
        }
        if (mName != mOldName) {
            exitBoolean = true
        }
        return exitBoolean
    }

    private fun exit() {
        if (checkExit()) {
            var blockDialog = DialogCommon(title = "Confirm to save the updated content?",
                onBlockClick = {
                    save()
                },
                onDissMissClick = {
                    finish()
                })
            blockDialog.show(
                supportFragmentManager, ""
            )
        } else {
            finish()
        }
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

                EasyPhotos.createAlbum(this@EditProfileActivity, true, false, BaseGlideEngine.getInstance())
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
                val resultPhotos: java.util.ArrayList<Photo> =
                    data!!.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)!!
                resultPhotos?.let {
                    for (item in resultPhotos){
                        item.path?.let { it1 ->
                            mAppViewModel.showLoading()
                            mOssService?.asyncPutImage(Storage.getFileSufix(it1), it1)
                        }
                    }

                }

            }

        }
    }
    @ExperimentalCoroutinesApi
    @Subscribe
    fun onEvent(event: SelectedMutablePhotoEvent) {
        if (event.photos.isNotEmpty()) {
            for (item in event.photos) {
                item.path?.let { it1 ->
                    mAppViewModel.showLoading()
                    mOssService?.asyncPutImage(Storage.getFileSufix(it1), it1)
                }
            }
        }

    }


    private fun select() {
        launch {
            mMineModel.selectMine().catch { }.collectLatest {
                if (it.isOk(this@EditProfileActivity)) {
                    it.data?.let { mine ->

                        mBinding?.run {
                            if (mine.nickName.length > 15) {
                                etName.setText(mine.nickName.substring(0, 14))
                            } else {
                                etName.setText(mine.nickName)
                            }
                            etName.setSelection(etName.text.toString().length)
                            mName = mine.nickName
                            mOldName = mine.nickName
                            mine.headImgUrl?.let {
                                mPath = it
                                mOldPath = it
                                Glide.with(this@EditProfileActivity)
                                    .load(it)
                                    .into(ivHead)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun save() {
        var name = mBinding.etName.text.toString().trim()
        if (name.isNullOrEmpty()) {
            return
        }
        if (name.length < 3 || name.length > 15) {
            toast(getString(R.string.label_Username_Length))
            return
        }
        DataReportUtils.getInstance().report("Update_profile_username")
        if (mPath.isNullOrEmpty()) {
            return
        }
        launch {
            mMineModel.updateOne(mPath!!, name).catch { }.collectLatest {
                if (it.isOk(this@EditProfileActivity)) {
                    setResult(100)
                    finish()
                }
            }
        }
    }


    override fun onBankString(path: String, str: String) {
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
                    Glide.with(this@EditProfileActivity)
                        .load(mPath)
                        .placeholder(R.drawable.brvah_sample_footer_loading)
                        .into(mBinding?.ivHead)
                }
                // 这里的else相当于Java中switch的default;
                else -> {

                }
            }
        }
    }

}