package com.earth.angel.photo


import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.curvydatingplus.ui.photo.UploadPhotoViewModel
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.base.PhotoUploadType
import com.earth.angel.base.dp2Px
import com.earth.angel.databinding.ActivityAddPhotoBinding
import com.earth.angel.photo.EditPhotoActivity.Companion.EDIT_PHOTO_URL
import com.earth.angel.photo.gallery.GalleryModel
import com.earth.angel.util.DateUtils
import com.earth.angel.util.EventBusHelper
import com.earth.angel.util.compresshelper.CompressHelper
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.Storage
import kotlinx.android.synthetic.main.include_top_bar.*
import org.greenrobot.eventbus.Subscribe
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class AddPhotoActivity : BaseActivity<ActivityAddPhotoBinding>() {
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    companion object {
        const val SUPPORT_MUTABLE = "support_mutable"
        const val Max_SELECTED_IMAGE_SIZE_KEY = "max_selected_image_size"
        const val PHOTO_TYPE = "photo_type"
        const val TAKE_PHOTO_TYPE = "take_photo_type"
        const val Max_SELECTED_IMAGE_SIZE = 4
        const val ANIMATOR_DURATION = 300
        const val SUPPORT_EDIT = "support_edit"
    }

    //是否支持多张图片上传
    private var supportMutable = false

    private var maxSelectedSize = Max_SELECTED_IMAGE_SIZE

    //上传图片的类型
    private var photoType: Int = 1

    //是否进入编辑页面
    private var supportEdit = false


    val uploadPhotoViewModel: UploadPhotoViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.activity_add_photo
    private val fragments = mutableListOf<Fragment>()
    private var photos = mutableListOf<GalleryModel>()
    private val moveDistance = 64.dp2Px().toFloat()
    private var galleryFragment: GalleryFragment? = null

    private val photoAdapter by lazy {
        PhotoAdapter()
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ActivityStackManager.addShareActivity(this@AddPhotoActivity)
        showActionBar()
        mBinding.handler = this
        EventBusHelper.register(this)
        tvTitleCenter.text="All"
        supportMutable = intent.extras?.getBoolean(SUPPORT_MUTABLE, false) ?: false
        photoType =
            intent.extras?.getInt(PHOTO_TYPE, PhotoUploadType.PUBLIC) ?: PhotoUploadType.PUBLIC
        maxSelectedSize =
            intent.extras?.getInt(Max_SELECTED_IMAGE_SIZE_KEY) ?: Max_SELECTED_IMAGE_SIZE
        supportEdit = intent.extras?.getBoolean(SUPPORT_EDIT, false) ?: false

        fragments.run {
            clear()
            if (galleryFragment == null) {
                galleryFragment = GalleryFragment().apply {
                    arguments = bundleOf(
                        SUPPORT_MUTABLE to supportMutable,
                        Max_SELECTED_IMAGE_SIZE_KEY to maxSelectedSize,
                        PHOTO_TYPE to photoType
                    )
                }
            }
            add(galleryFragment!!)
        }
        mBinding?.viewPager.run {
            adapter = object : FragmentStateAdapter(this@AddPhotoActivity) {
                override fun getItemCount() = fragments.size
                override fun createFragment(position: Int) = fragments[position]
            }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            })
            offscreenPageLimit = fragments.size
        }
        mBinding?.run {
            mAppViewModel?.showLoadingProgress.observe(this@AddPhotoActivity, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
        }

        initSelectBottom()
        initTipsBottom()
        selectedPhotos()
    }

    private fun selectedPhotos() {
        uploadPhotoViewModel.galleryListItem.observe(this) {
            this.photos = it
            photoAdapter.data = this.photos
            photoAdapter.notifyDataSetChanged()
            if (this.photos.isEmpty()) {
                initTipsBottomAnimator(0.0f, ANIMATOR_DURATION)
                initSelectBottomAnimator(moveDistance, ANIMATOR_DURATION)
                mBinding.selectBottom.visibility = View.GONE
                mBinding.tipsBottom.visibility = View.VISIBLE
            } else {
                initSelectBottomAnimator(0.0f, ANIMATOR_DURATION)
                initTipsBottomAnimator(moveDistance, ANIMATOR_DURATION)
                mBinding.tipsBottom.visibility = View.GONE
                mBinding.selectBottom.visibility = View.VISIBLE
            }
        }
    }

    private fun initSelectBottom() {
        mBinding!!.tvUpload.setOnClickListener {
            if (DateUtils.isFastClick()){
                if (!supportEdit) {
                    mAppViewModel.showLoading()
                    Thread {
                        for (item in photos) {
                            if (item.uri != null) {
                                var bat = Storage.getBitmapFromUri(this@AddPhotoActivity, item.uri)
                                if (bat != null) {
                                    val file = Storage.getImageGalleryFile(this@AddPhotoActivity, bat)
                                    val newFile = CompressHelper.Builder(this@AddPhotoActivity)
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
                                    item.path = newFile.toString()
                                }
                            }
                        }
                        val mMessage = Message()
                        mMessage.what = 1
                        mHandler.sendMessage(mMessage)

                    }.start()



                    /*   photos[0]?.path?.let {
                           if (photos[0].uri != null) {
                               var bat = Storage.getBitmapFromUri(this, photos[0].uri)
                               if (bat != null) {
                                   val file = Storage.getImageGalleryFile(this, bat)
                                   *//*     val newFile: File = CompressHelper.getDefault(this)
                                     .compressToFile(file)*//*
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

                            photos[0].path = newFile.toString()
                            EventBusHelper.post(SelectedMutablePhotoEvent(photos, photoType))
                            finish()
                        }
                    }
                }*/
                } else {
                    photos[0].uri?.let {

                        var intent: Intent =
                            Intent(this@AddPhotoActivity, EditPhotoActivity::class.java).putExtra(
                                EDIT_PHOTO_URL,
                                it.toString(),
                            ).putExtra(PHOTO_TYPE, photoType)
                        startActivity(intent)
                        finish()
                    }
                }
            }


        }

        mBinding!!.uploadRecyclerView.run {
            layoutManager = LinearLayoutManager(this@AddPhotoActivity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = photoAdapter

        }


    }

    private fun getSDPath(): String {

        var sdDir: File? = null
        sdDir = EarthAngelApp.instance.getExternalFilesDir(null)
        return sdDir.toString()
    }

    private fun initTipsBottom() {

    }

    private fun initSelectBottomAnimator(moveDistance: Float, duration: Int) {
        val animator = ObjectAnimator.ofFloat(mBinding!!.selectBottom, "translationY", moveDistance)
        animator.duration = duration.toLong()
        animator.start()
    }

    private fun initTipsBottomAnimator(moveDistance: Float, duration: Int) {
        val animator = ObjectAnimator.ofFloat(mBinding!!.tipsBottom, "translationY", moveDistance)
        animator.duration = duration.toLong()
        animator.start()
    }


    @Subscribe
    fun onEvent(event: ClipPhotoEvent) {
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)
    }

    inner class PhotoAdapter :
        BaseQuickAdapter<GalleryModel, BaseViewHolder>(R.layout.item_photo_display_image) {
        override fun convert(holder: BaseViewHolder, item: GalleryModel) {
            val imageView = holder.getView<ImageView>(R.id.imgDisplay)
            item?.uri.let {
                Glide.with(imageView)
                    .load(it)
                    .into(imageView)
            }
        }

    }
    // 创建一个Handler
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    mAppViewModel.dismissLoading()
                    EventBusHelper.post(SelectedMutablePhotoEvent(photos, photoType))
                    finish()
                }
                // 这里的else相当于Java中switch的default;
                else -> {
                }
            }
        }
    }
}