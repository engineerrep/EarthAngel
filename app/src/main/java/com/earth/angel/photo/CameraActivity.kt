package com.earth.angel.photo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.util.SparseIntArray
import android.view.KeyEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.base.PhotoUploadType
import com.earth.angel.databinding.ActivityCameraBinding
import com.earth.angel.photo.gallery.GalleryModel
import com.earth.angel.util.EventBusHelper
import com.earth.angel.util.compresshelper.CompressHelper
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.util.Storage
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.nio.ByteBuffer
import kotlin.math.abs


class CameraActivity : BaseActivity<ActivityCameraBinding>() {
    companion object {
        const val Max_PHOTO = "max_selected_image_size"
        const val Max_Max_PHOTO_SIZE = 9

    }

    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private val ORIENTATIONS = SparseIntArray()
    private var imgAdapter: PhotoListAdapter? = null
    private var photos = mutableListOf<GalleryModel>()
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCameraDevice: CameraDevice? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var childHandler: Handler? = null
    private var mainHandler: Handler? = null
    private var mImageReader: ImageReader? = null
    private var mCameraID //摄像头Id 0 为后  1 为前
            : String? = "0"
    private var mCameraManager //摄像头管理器
            : CameraManager? = null
    private var photoType: Int = 1
    private var maxSelectedSize = Max_Max_PHOTO_SIZE
    private var layoutPager: LinearLayoutManager? = null
    override fun getLayoutId(): Int = R.layout.activity_camera

    override fun initActivity(savedInstanceState: Bundle?) {
        hideActionBar()
        ORIENTATIONS.append(Surface.ROTATION_0, 0)
        ORIENTATIONS.append(Surface.ROTATION_90, 90)
        ORIENTATIONS.append(Surface.ROTATION_180, 180)
        ORIENTATIONS.append(Surface.ROTATION_270, 270)
        mBinding?.run {
            maxSelectedSize =
                intent.extras?.getInt(Max_PHOTO) ?: Max_Max_PHOTO_SIZE

            mAppViewModel.showLoadingProgress.observe(this@CameraActivity,
                {
                    if (it) mLoadingDialog.showAllowStateLoss(
                        supportFragmentManager,
                        mLoadingDialog::class.simpleName!!
                    )
                    else mLoadingDialog.dismiss()
                })
            imgAdapter = PhotoListAdapter(this@CameraActivity, photos, upDade = {
                photos.remove(it)
                imgAdapter?.notifyDataSetChanged()
                when {
                    photos.size > 0 -> {
                        tvConfirm.visibility = View.VISIBLE
                        tvConfirm.text = "Confirm (+" + photos.size + ")"
                        llPhoto.visibility = View.GONE
                    }
                    photos.size == 0 -> {
                        tvConfirm.visibility = View.GONE
                        llPhoto.visibility = View.VISIBLE
                    }
                    else -> {
                        tvConfirm.visibility = View.VISIBLE
                        llPhoto.visibility = View.VISIBLE
                    }
                }
            })
            layoutPager = LinearLayoutManager(
                this@CameraActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            photoRlv.layoutManager = layoutPager
            photoRlv.adapter = imgAdapter
            initsufView()
            takePhotoButton.setOnClickListener {

                var longth = photos.size
                if (longth < maxSelectedSize) {
                    mAppViewModel.showLoading()
                    takePicture()
                } else {
                    toast("Add up to $maxSelectedSize photos")
                }
            }
            photoButton.setOnClickListener {
                val intent = Intent(this@CameraActivity, AddPhotoActivity::class.java)
                    .putExtra(AddPhotoActivity.SUPPORT_MUTABLE, true)
                    .putExtra(AddPhotoActivity.PHOTO_TYPE, PhotoUploadType.PUBLIC)
                    .putExtra(AddPhotoActivity.Max_SELECTED_IMAGE_SIZE_KEY, maxSelectedSize)
                startActivity(intent)
                finish()

            }
            tvConfirm.setOnClickListener {
                // PhotoEditActivity.openPhotoEditActivity(this@CameraActivity)
                EventBusHelper.post(SelectedMutablePhotoEvent(photos, photoType))
                finish()

            }
            ivPhoto.setOnClickListener {
                if (mCameraID == "0") {
                    mCameraID = "1"
                } else {
                    mCameraID = "0"
                }
                initCamera2()
            }

        }
        tvBank.setOnClickListener {

            finishCameraActivity()
        }
    }

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

    private fun initsufView() {
        mSurfaceHolder = mSurfaceView.holder
        mSurfaceHolder?.setKeepScreenOn(true)
        mSurfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) { //SurfaceView创建
                // 初始化Camera
                initCamera2()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) { //SurfaceView销毁
                // 释放Camera资源
                if (null != mCameraDevice) {
                    mCameraDevice?.close()
                    mCameraDevice = null
                }
            }
        })
    }


    private fun initCamera2() {
        val handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        childHandler = Handler(handlerThread.looper)
        mainHandler = Handler(mainLooper)

        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1)
        mImageReader!!.setOnImageAvailableListener({ reader ->
            mAppViewModel.dismissLoading()
            //可以在这里处理拍照得到的临时照片 例如，写入本地
            //    mCameraDevice!!.close()
            //    mSurfaceView.setVisibility(View.GONE)
            //    iv_show.setVisibility(View.VISIBLE)
            // 拿到拍照照片数据
            val image: Image = reader.acquireNextImage()
            val buffer: ByteBuffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes) //由缓冲区存入字节数组
            var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bitmap != null) {
                //    iv_show.setImageBitmap(bitmap)
                var newBitMsmp: Bitmap? = null
                bitmap?.run {
                    if (width < height) {
                        newBitMsmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.width)
                    } else {
                        val matrix = Matrix()
                        matrix.postRotate(90F)
                        newBitMsmp = Bitmap.createBitmap(
                            bitmap,
                            0,
                            0,
                            bitmap.height,
                            bitmap.height,
                            matrix,
                            true
                        )
                    }
                }
                if (newBitMsmp == null) {
                    return@setOnImageAvailableListener
                }
                val file = Storage.getImageGalleryFile(this, newBitMsmp!!)
                /*     val newFile: File = CompressHelper.getDefault(this)
                         .compressToFile(file)*/
                val newFile = CompressHelper.Builder(this)
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
                image.close()
                when {
                    photos.size > 0 -> {
                        tvConfirm.visibility = View.VISIBLE
                        tvConfirm.text = "Confirm (+" + photos.size + ")"
                        llPhoto.visibility = View.GONE
                    }
                    photos.size == 0 -> {
                        tvConfirm.visibility = View.GONE
                        llPhoto.visibility = View.VISIBLE
                    }
                    else -> {
                        tvConfirm.visibility = View.VISIBLE
                        llPhoto.visibility = View.VISIBLE
                    }
                }
            }
        }, mainHandler)
        //获取摄像头管理
        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager?
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            //打开摄像头
            mCameraManager!!.openCamera(mCameraID!!, stateCallback, mainHandler)


        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun getSDPath(): String {

        var sdDir: File? = null
        sdDir = EarthAngelApp.instance.getExternalFilesDir(null)
        return sdDir.toString()
    }

    /**
     * 摄像头创建监听
     */
    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) { //打开摄像头
            mCameraDevice = camera
            //开启预览
            takePreview()
        }

        override fun onDisconnected(camera: CameraDevice) { //关闭摄像头
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                mCameraDevice = null
            }
        }

        override fun onError(camera: CameraDevice, error: Int) { //发生错误
            Toast.makeText(this@CameraActivity, "摄像头开启失败", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 开始预览
     */
    private fun takePreview() {
        try {
            // 创建预览需要的CaptureRequest.Builder
            val previewRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(mSurfaceHolder!!.surface)
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice!!.createCaptureSession(
                listOf(
                    mSurfaceHolder!!.surface,
                    mImageReader!!.surface
                ), object : CameraCaptureSession.StateCallback( // ③
                ) {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        if (null == mCameraDevice) return
                        // 当摄像头已经准备好时，开始显示预览
                        mCameraCaptureSession = cameraCaptureSession
                        try {
                            // 自动对焦
                            previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_AUTO
                            )
                            // 打开闪光灯
                            previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                            )
                            // 显示预览
                            val previewRequest = previewRequestBuilder.build()
                            mCameraCaptureSession!!.setRepeatingRequest(
                                previewRequest,
                                null,
                                childHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        Toast.makeText(this@CameraActivity, "配置失败", Toast.LENGTH_SHORT).show()
                    }
                }, childHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 拍照
     */
    private fun takePicture() {
        if (mCameraDevice == null) return
        // 创建拍照需要的CaptureRequest.Builder
        val captureRequestBuilder: CaptureRequest.Builder
        try {
            captureRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader!!.surface)
            // 自动对焦
            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            // 自动曝光
            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
            // 获取手机方向
            val rotation = windowManager.defaultDisplay.rotation
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))
            //拍照
            val mCaptureRequest = captureRequestBuilder.build()
            mCameraCaptureSession?.capture(mCaptureRequest, null, childHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishCameraActivity()

        }
        return false

    }

    private fun getSize(): Size? {
        var selectSize: Size? = null
        var cameraCharacteristics = mCameraManager!!.getCameraCharacteristics(mCameraID!!)
        val streamConfigurationMap =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val sizes = streamConfigurationMap!!.getOutputSizes(ImageFormat.JPEG)
        val displayMetrics = resources.displayMetrics //因为我这里是将预览铺满屏幕,所以直接获取屏幕分辨率

        val deviceWidth = displayMetrics.widthPixels //屏幕分辨率宽

        val deviceHeigh = displayMetrics.heightPixels //屏幕分辨率高
        for (j in 1..40) {
            for (i in 0..sizes.size) {
                val itemSize = sizes[i]
                //判断当前Size高度小于屏幕宽度+j*5  &&  判断当前Size高度大于屏幕宽度-j*5
                if (itemSize.height < (deviceWidth + j * 5) && itemSize.height > (deviceWidth - j * 5)) {
                    if (selectSize != null) { //如果之前已经找到一个匹配的宽度
                        if (abs(deviceHeigh - itemSize.width) < abs(deviceHeigh - selectSize.width)) { //求绝对值算出最接近设备高度的尺寸
                            selectSize = itemSize;
                            continue;
                        }
                    } else {
                        selectSize = itemSize;
                    }
                }
            }
        }
        if (selectSize != null) {
            return selectSize;

        }
        return selectSize;

    }
}