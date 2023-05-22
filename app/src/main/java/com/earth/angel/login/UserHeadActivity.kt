package com.earth.angel.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.earth.angel.MainPhotoActivity
import com.earth.angel.R
import com.earth.angel.databinding.ActivityUserHeadBinding
import com.earth.angel.login.adapter.UserHeadAdapter
import com.earth.angel.mine.MineModel
import com.earth.angel.util.DataReportUtils
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
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.util.Storage
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel


class UserHeadActivity : BaseActivity<ActivityUserHeadBinding>(), OssInterface {
    private var atapterHead: UserHeadAdapter? = null
    private var mOssService: OssService?=null

    private val mMineModel by viewModel<MineModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    // private var listHead= listOf(R.mipmap.head_1,R.mipmap.head_1,R.mipmap.head_1,R.mipmap.head_1,R.mipmap.head_1,R.mipmap.head_1)
    private var listHead: ArrayList<Int>? = null

    //OSS的上传下载
    private var mPath: String? = ""
    private var mHeadPath: String? = null
    override fun getLayoutId(): Int = R.layout.activity_user_head
    private var mName: String? = ""

    override fun initActivity(savedInstanceState: Bundle?) {
        DataReportUtils.getInstance().report("Signup_photo")
            showActionBar()
        tvTitleCenter.text="PHOTO"
        mOssService = OSSManager.initOSS(this@UserHeadActivity, Config.OSS_ENDPOINT)
        mOssService?.setInterface(this@UserHeadActivity)
        mName = intent.getStringExtra("name")
        mBinding?.run {
            listHead = ArrayList()
            listHead!!.add(R.mipmap.head_1)
            listHead!!.add(R.mipmap.head_2)
            listHead!!.add(R.mipmap.head_3)
            listHead!!.add(R.mipmap.head_4)
            listHead!!.add(R.mipmap.head_5)
            listHead!!.add(R.mipmap.head_6)
            listHead!!.add(R.mipmap.head_7)
            listHead!!.add(R.mipmap.head_8)
            atapterHead = UserHeadAdapter(listHead)
            val gridLayoutManager = GridLayoutManager(this@UserHeadActivity, 4)
            rlHead.layoutManager = gridLayoutManager
            rlHead.adapter = atapterHead

          /*  var result = Random().nextInt(9)
            when (result) {
                1 -> {
                    headImg = R.mipmap.head_1
                }
                2 -> {
                    headImg = R.mipmap.head_2
                }
                3 -> {
                    headImg = R.mipmap.head_3
                }
                4 -> {
                    headImg = R.mipmap.head_4
                }
                5 -> {
                    headImg = R.mipmap.head_5
                }
                6 -> {
                    headImg = R.mipmap.head_6
                }
                7 -> {
                    headImg = R.mipmap.head_7
                }
                8 -> {
                    headImg = R.mipmap.head_8
                }
                else -> {
                    headImg = R.mipmap.head_1
                }
            }*/

          /*  atapterHead?.setOnItemClickListener { adapter, view, position ->
                headImg = listHead!![position]
                Glide.with(this@UserHeadActivity)
                    .load(headImg)
                    .into(ivHead)
            }*/
            ivHead.setOnClickListener {
                getPhoto()
            }
            btNext.setOnClickListener {
                save()
            }
            mAppViewModel?.showLoadingProgress.observe(this@UserHeadActivity, androidx.lifecycle.Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
        }
    }

    private fun getPhoto() {
        val permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsList.add(1, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                EasyPhotos.createAlbum(this@UserHeadActivity, true, false, BaseGlideEngine.getInstance())
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
                    mAppViewModel.showLoading()

                    for (item in resultPhotos){
                        item.path?.let { it1 ->
                            mOssService?.asyncPutImage(Storage.getFileSufix(it1), it1)
                        }
                    }

                }

            }

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)

    }

    @SuppressLint("ResourceType")
    private fun save() {
        DataReportUtils.getInstance().report("Upload_photo")
        if (mPath.isNullOrEmpty()) {

           /* if (headImg==null){
                toast(getString(R.string.lab_avatar))
                return
            }*/
            toast(getString(R.string.lab_avatar))
            return
     /*       flag = 1
            val `is`: InputStream = resources.openRawResource(headImg!!)
            val mBitmap = BitmapFactory.decodeStream(`is`)
            var psth = Storage.saveImage(mBitmap)
            EarthAngelApp.mService?.asyncPutImage(Storage.getFileSufix(psth), psth)*/

            return
        }
        launch {
            mMineModel.updateOne(mPath!!, mName!!).catch { }.collectLatest {
                if (it.isOk(this@UserHeadActivity)) {
                    finish()
                    PreferencesHelper.saveFirstAuth(this@UserHeadActivity, false)
                    val intent = Intent(this@UserHeadActivity, MainPhotoActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }
    }

    override fun onBankString(path: String,str: String) {
        mAppViewModel.dismissLoading()
        if (str.isNotEmpty()) {
            val mMessage = Message()
            mMessage.obj = str
            mMessage.what = 1
            mHandler.sendMessage(mMessage)
        }
    }

    // 创建一个Handler
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    mPath = msg.obj.toString()
                    Glide.with(this@UserHeadActivity)
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

    override fun filed() {
        mAppViewModel.dismissLoading()

    }
}