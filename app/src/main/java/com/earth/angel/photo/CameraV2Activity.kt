package com.earth.angel.photo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.databinding.ActivityCameraV2Binding
import com.earth.angel.photo.gallery.GalleryModel
import com.earth.angel.util.DateUtils
import com.earth.angel.util.EventBusHelper
import com.earth.angel.util.compresshelper.CompressHelper
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.base.delayLaunch
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.util.*
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.activity_camera_v2.*
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


@Route(path = Constance.CameraV2ActivityURL)
class CameraV2Activity : BaseActivity<ActivityCameraV2Binding>() {

    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    private var imgAdapter: PhotoListAdapter? = null
    private var photos = mutableListOf<GalleryModel>()
    private var photoType: Int = 1
    private var layoutPager: LinearLayoutManager? = null

    @Autowired(name = "SIZE")
    @JvmField
    var SIZE: Int? = 0

    companion object {
        const val Max_PHOTO = "max_selected_image_size"
        const val Max_Max_PHOTO_SIZE = 9
    }

    private var maxSelectedSize = Max_Max_PHOTO_SIZE

    override fun getLayoutId(): Int = R.layout.activity_camera_v2

    override fun initActivity(savedInstanceState: Bundle?) {
        ActivityStackManager.addShareActivity(this@CameraV2Activity)
        mBinding.run {
            ARouter.getInstance().inject(this@CameraV2Activity)
            val lp: LinearLayout.LayoutParams =
                PhotoLine.layoutParams as LinearLayout.LayoutParams
            lp.setMargins(0, BaseScreenUtil.getActivityMessageHeight(), 0, 0)
            PhotoLine.layoutParams = lp

            maxSelectedSize = SIZE!!
            hideActionBar()
            mAppViewModel.showLoadingProgress.observe(this@CameraV2Activity) {
                if (it) mLoadingDialog.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog.dismiss()
            }
            mSurfaceView.audio= Audio.OFF
            mSurfaceView.open()
            ivPhoto.setOnClickListener {
                if (mSurfaceView.facing == Facing.BACK) {
                    mSurfaceView.facing = Facing.FRONT
                } else {
                    mSurfaceView.facing = Facing.BACK
                }
            }
            photoRl.setOnClickListener {
                photoRect.visibility = View.VISIBLE
                delayLaunch(1000) {
                    block = {
                        photoRect.visibility = View.GONE
                    }
                }
            }
            tvBank.setOnClickListener {
                finishCameraActivity()
            }
            imgAdapter = PhotoListAdapter(this@CameraV2Activity, photos, upDade = {
                photos.remove(it)
                imgAdapter?.notifyDataSetChanged()
                initView()
            })

            imgAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    var list = ArrayList<String>()
                    for (item in photos) {
                        item.path?.let {
                            list.add(it)
                        }
                    }
                    ARouter.getInstance().build(Constance.GiftPhotoActivity)
                        .withInt("position", position)
                        .withStringArrayList("ImgList", list)
                        .navigation()
                }
            }

            layoutPager = LinearLayoutManager(
                this@CameraV2Activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            photoRlv.layoutManager = layoutPager
            photoRlv.adapter = imgAdapter
            initCamera()
            takePhotoButton.setOnClickListener {
                if (takeTvPhotos.visibility == View.VISIBLE) {
                    PhotoListAdapter.selectPosition = -1
                    imgAdapter?.notifyDataSetChanged()
                    takePhoto(true)
                    return@setOnClickListener
                }
                var longth = photos.size
                if (longth < maxSelectedSize) {
                    mAppViewModel.showLoading()
                    mSurfaceView.takePictureSnapshot()
                } else {
                    toast("Add up to $maxSelectedSize photos")
                }
            }
            llPhoto.setOnClickListener {
                if (DateUtils.isFastClick()) {

                    //自动进入选照片界面
                    EasyPhotos.createAlbum(
                        this@CameraV2Activity,
                        false,
                        false,
                        BaseGlideEngine.getInstance()
                    )
                        .setPuzzleMenu(false)
                        .setCleanMenu(false)
                        .setCount(maxSelectedSize)
                        .start(101) //也可以选择链式调用写法
                }
            }

            /*    photoButton.setOnClickListener {
                    val intent = Intent(this@CameraV2Activity, AddPhotoActivity::class.java)
                        .putExtra(AddPhotoActivity.SUPPORT_MUTABLE, true)
                        .putExtra(AddPhotoActivity.PHOTO_TYPE, PhotoUploadType.PUBLIC)
                        .putExtra(AddPhotoActivity.Max_SELECTED_IMAGE_SIZE_KEY, maxSelectedSize)
                    startActivity(intent)
                    finish()
                }*/
            tvConfirm.setOnClickListener {
                // PhotoEditActivity.openPhotoEditActivity(this@CameraActivity)
                EventBusHelper.post(SelectedMutablePhotoEvent(photos, photoType))
                finish()
            }
        }
    }

    private fun initCamera() {
        mBinding?.mSurfaceView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                mAppViewModel.dismissLoading()
                //通过jpeg获取图片的旋转角度orientation jpeg中包含了照片角度信息
                val orientation = Exif.getOrientation(result.data)
                var bitmap = BitmapFactory.decodeByteArray(result.data, 0, result.data.size)
                //有些机型会将照片旋转90度
                val m = Matrix()
                m.postRotate(orientation.toFloat())
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true);
                if (bitmap != null) {
                    val file = Storage.getImageGalleryFile(this@CameraV2Activity, bitmap!!)
                    /*     val newFile: File = CompressHelper.getDefault(this)
                             .compressToFile(file)*/
                    val newFile = CompressHelper.Builder(this@CameraV2Activity)
                        .setMaxWidth(720f) // 默认最大宽度为720
                        .setMaxHeight(960f) // 默认最大高度为960
                        .setQuality(80) // 默认压缩质量为80
                        .setFileName(System.currentTimeMillis().toString()) // 设置你需要修改的文件名
                        .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                        .setDestinationDirectoryPath(
                            getSDPath()
                        )
                        .build()
                        .compressToFile(file)
                    newFile?.let {
                        val galleryModel = GalleryModel().apply {
                            path = it.path
                            photo = GalleryModel.TYPE_PHOTO
                        }
                        photos.add(galleryModel)
                    }
                    imgAdapter?.notifyDataSetChanged()
                    layoutPager?.scrollToPositionWithOffset(
                        imgAdapter?.itemCount!! - 1,
                        Integer.MIN_VALUE
                    )
                    initView()
                }
            }
        })
    }

    private fun takePhoto(take: Boolean) {
        mBinding.run {
            if (take) {
                takeTvPhotos.visibility = View.GONE
                ivPhotoBg.visibility = View.GONE
            } else {
                takeTvPhotos.visibility = View.VISIBLE
                ivPhotoBg.visibility = View.VISIBLE
            }
        }

    }

    private fun initView() {
        when {
            photos.size > 0 -> {
                tvConfirm.visibility = View.VISIBLE
                tvConfirm.text = "Upload(" + photos.size + ")"
                llPhoto.visibility = View.GONE
                photoRlvBg.visibility=View.VISIBLE
            }
            photos.size == 0 -> {
                tvConfirm.visibility = View.GONE
                llPhoto.visibility = View.VISIBLE
                photoRlvBg.visibility=View.GONE

            }
            else -> {
                tvConfirm.visibility = View.VISIBLE
                llPhoto.visibility = View.VISIBLE
                photoRlvBg.visibility=View.GONE
            }
        }

    }

    private fun getSDPath(): String {
        var sdDir: File? = null
        sdDir = EarthAngelApp.instance.getExternalFilesDir(null)
        return sdDir.toString()
    }

    override fun onResume() {
        super.onResume()
        if (!mBinding.mSurfaceView.isOpened) {
            mBinding.mSurfaceView.open()
        }
    }

    override fun onPause() {
        super.onPause()
        mBinding.mSurfaceView.close() //退出界面 比如点击home时 停掉相机 以onResume重启
    }

    override fun onDestroy() {
        super.onDestroy()
        //销毁相机释放资源
        mBinding.mSurfaceView.destroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishCameraActivity()
        }
        return false

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK == resultCode) {
            if (requestCode == 101) {
                    if(data!=null){
                        //返回对象集合：如果你需要了解图片的宽、高、大小、用户是否选中原图选项等信息，可以用这个
                        val resultPhotos: ArrayList<Photo> =
                            data!!.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)!!
                          resultPhotos.let {
                            for (item in resultPhotos) {
                         /*       var bean = GalleryModel()
                                bean.fileName = item.name
                                bean.path = item.path
                                bean.uri = item.uri
                                bean.height = item.height
                                bean.width = item.width
                                photos.add(bean)*/
                                val mFile=File(item.path)
                                val newFile = CompressHelper.Builder(this@CameraV2Activity)
                                    .setMaxWidth(720f) // 默认最大宽度为720
                                    .setMaxHeight(960f) // 默认最大高度为960
                                    .setQuality(80) // 默认压缩质量为80
                                    .setFileName(System.currentTimeMillis().toString()) // 设置你需要修改的文件名
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                                    .setDestinationDirectoryPath(
                                        getSDPath()
                                    )
                                    .build()
                                    .compressToFile(mFile)
                                newFile?.let {
                                    val galleryModel = GalleryModel().apply {
                                        path = it.path
                                        photo = GalleryModel.TYPE_PHOTO
                                    }
                                    photos.add(galleryModel)
                                }
                            }
                              imgAdapter?.notifyDataSetChanged()

                            EventBusHelper.post(SelectedMutablePhotoEvent(photos, photoType))
                            finish()
                        }
                    }


            }

        }
    }

    // 判断是否照相了
    private fun finishCameraActivity() {
        var intent: Intent? = Intent()
        if (photos.size == 0) {
            intent?.putExtra("CA", 0)
        } else {
            intent?.putExtra("CA", 1)
        }
        setResult(100, intent)
        finish()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {

            }
        }

        return super.onTouchEvent(event)

    }
}