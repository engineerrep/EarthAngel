package com.earth.angel.photo

import android.net.Uri
import android.os.Bundle
import com.earth.angel.R
import com.earth.angel.base.PhotoUploadType
import com.earth.angel.databinding.ActivityEditPhotoBinding
import com.earth.angel.photo.gallery.GalleryModel
import com.earth.angel.util.EventBusHelper
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.util.Storage
import org.jetbrains.anko.doAsyncResult


class EditPhotoActivity : BaseActivity<ActivityEditPhotoBinding>() {

    companion object {
        const val EDIT_PHOTO_URL = "edit_photo_url"
    }

    private var photoType: Int = 1
    private var takePhoto: Uri? = null

    override fun getLayoutId(): Int = R.layout.activity_edit_photo
    private var photos = mutableListOf<GalleryModel>()

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.handler = this

        photoType =
            intent.extras?.getInt(AddPhotoActivity.PHOTO_TYPE, PhotoUploadType.PUBLIC)
                ?: PhotoUploadType.PUBLIC

        intent.getStringExtra(EDIT_PHOTO_URL).let {
            takePhoto = Uri.parse(it)
        }

        mBinding!!.cancel.setOnClickListener {
            finish()
        }
        mBinding!!.upload.setOnClickListener {
            //  val clipData: Bitmap? = mBinding!!.cropImageView.clip()
            doAsyncResult {
                Storage.saveImage(mBinding!!.cropImageView.clip())
            }.get().let {
                //   EventBusHelper.post(ClipPhotoEvent(it))
                var bean = GalleryModel()
                bean.path = it
                photos.add(bean)
                EventBusHelper.post(SelectedMutablePhotoEvent(photos, photoType))
                finish()
            }
        }
        mBinding!!.rotate.setOnClickListener {
            mBinding!!.cropImageView.rotateImage()
        }
        initBitmap()
    }

    //todo 应该考虑流异常的情况,后面再说吧
    private fun initBitmap() {
        takePhoto?.let {
            var bat = Storage.getBitmapFromUri(this, takePhoto)
            bat?.let {
                mBinding!!.cropImageView.setImageBitmap(
                    it
                )
            }
        }

    }


}