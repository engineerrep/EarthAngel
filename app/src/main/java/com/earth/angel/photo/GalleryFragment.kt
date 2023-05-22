package com.earth.angel.photo

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.curvydatingplus.ui.photo.UploadPhotoViewModel
import com.earth.angel.R
import com.earth.angel.base.dp2Px
import com.earth.angel.databinding.FrgPhotoGalleryBinding
import com.earth.angel.photo.gallery.GalleryDataSource
import com.earth.angel.photo.gallery.GalleryModel
import com.earth.angel.util.DataReportUtils
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.requestPermissions
import com.mason.common.widget.decoration.GridItemDecoration
import java.io.File


class GalleryFragment : BaseFragment<FrgPhotoGalleryBinding>() {
    private var photoType: Int = 1

    companion object {
        const val TYPE_CAMERA = 0
        const val TYPE_IMAGE = 1

        const val REQUEST_CODE_TAKE = 1001
    }

    private var listener: IAddPhotoListener? = null
    private var photos = mutableListOf<GalleryModel>()
    private var galleryModels = mutableListOf<GalleryModel>()
    private var supportMutable = false
    private var maxSelectedSize = 1

    private var takeImageFile: File? = null
    val uploadPhotoViewModel: UploadPhotoViewModel by activityViewModels()

    private val galleryAdapter by lazy {
        GalleryAdapter().apply {
            this.setOnItemClickListener { _, _, position ->
                if (photoType == 1) {
                    selectPhoto(photos[position])
                }else{
                    if (position == 0) {
                        takeCapture()
                    } else {
                        selectPhoto(photos[position])
                    }
                }

            }
        }
    }


    private fun selectPhoto(photo: GalleryModel) {
        if (galleryModels.contains(photo)) {
            galleryModels.remove(photo)
        } else {
            if (supportMutable) {
                if (galleryModels.size >= maxSelectedSize) {
                    //Toast.makeText(activity, string(R.string.no_more_than_four), Toast.LENGTH_LONG).show()
                    Log.d("gallery fragment", galleryModels.size.toString())
                } else {
                    galleryModels.add(photo)
                }
            } else {
                galleryModels.clear()
                galleryModels.add(photo)
            }

        }
        uploadPhotoViewModel.select(galleryModels)

        galleryAdapter.notifyDataSetChanged()
    }

    override fun getLayoutId(): Int = R.layout.frg_photo_gallery
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        this.mBinding?.handler = this
        arguments?.let {
            supportMutable = it.getBoolean(AddPhotoActivity.SUPPORT_MUTABLE, false)
            maxSelectedSize = it.getInt(AddPhotoActivity.Max_SELECTED_IMAGE_SIZE_KEY, 1)
            photoType = it.getInt(AddPhotoActivity.PHOTO_TYPE, 1)
        }
        if (photoType != 1) {
            photos.add(0, GalleryModel())

        }
        mBinding!!.recycler.addItemDecoration(GridItemDecoration(10.dp2Px()))
        mBinding!!.recycler.layoutManager = GridLayoutManager(context, 3)
        mBinding!!.recycler.adapter = galleryAdapter
        //   mBinding?.statusCode = LoadingStatusCode.Loading

        GalleryDataSource(
            this.requireActivity(),
            types = arrayListOf(GalleryModel.TYPE_IMAGE),
            loadedFolder = {
                photos.clear()
                //添加一个空位,用来当做第一个Camera
                if (photoType != 1) {
                    photos.add(0, GalleryModel())
                }
                photos.addAll(it[0].images)
                galleryAdapter.data = photos
                galleryAdapter.notifyDataSetChanged()
                //mBinding?.statusCode = LoadingStatusCode.Succeed
            }).start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? IAddPhotoListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun takeCapture() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.CAMERA)
        permissionsList.add(1, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                DataReportUtils.getInstance().report("On_camera")
                openCamera_2()
            }
        }
    }

    private var fileName: String? = ""
    private var mFilePath: String? = ""
    private fun openCamera_2() {
        val fileDir =
            requireActivity().getExternalFilesDir(null) //获取应用所在根目录/Android/data/your.app.name/file/ 也可以根据沙盒机制传入自己想传的参数，存放在指定目录
        fileName = "IMG_" + System.currentTimeMillis() + ".jpg"
        mFilePath = fileDir?.absolutePath + "/" + fileName
        var uri: Uri? = null
        val contentValues = ContentValues()
        //设置文件名
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Pictures")
        } else {
            contentValues.put(MediaStore.Images.Media.DATA, mFilePath)
        }
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG")
        uri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 启动系统相机
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_CODE_TAKE)
    }

    private fun getImageContentUri(
    ): Uri? {
        try {
            //查询的条件语句
            val selection = MediaStore.Images.Media.DISPLAY_NAME + "=? "
            //查询的sql
            //Uri：指向外部存储Uri
            //projection：查询那些结果
            //selection：查询的where条件
            //sortOrder：排序
            val cursor: Cursor = requireActivity().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                selection,
                arrayOf<String>(fileName!!),
                null
            )!!
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val urim = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(0)
                    )
                    //  Log.i("luingssd", "@$uri")
                    return urim

                } while (cursor.moveToNext())
            } else {
                //  Toast.makeText(this, "no photo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_TAKE) {

            if (resultCode == RESULT_OK) {
                if (resultCode == RESULT_OK) {

                    mFilePath?.let {
                        val dirPath = File(it).parentFile!!.absolutePath
                        val dirFile = File(dirPath)
                        if (dirFile.isDirectory) {
                            val galleryModel = GalleryModel().apply {
                                path = mFilePath
                                fileName=dirFile.parent
                                uri = getImageContentUri()
                                photo = GalleryModel.TYPE_PHOTO
                            }
                            photos.add(1, galleryModel)
                            selectPhoto(galleryModel)
                        }

                    }
                }

            }
        }
    }

    inner class GalleryAdapter() : BaseDelegateMultiAdapter<GalleryModel, BaseViewHolder>() {

        init {
            setMultiTypeDelegate(object : BaseMultiTypeDelegate<GalleryModel>() {
                override fun getItemType(data: List<GalleryModel>, position: Int): Int {
                    if (photoType != 1) {
                        if (position == 0) {
                            return TYPE_CAMERA
                        }

                    }
                    return TYPE_IMAGE
                }
            })
            if (photoType == 1) {
                getMultiTypeDelegate()
                    ?.addItemType(TYPE_IMAGE, R.layout.item_gallery_image)
            } else {
                getMultiTypeDelegate()
                    ?.addItemType(TYPE_CAMERA, R.layout.item_gallery_camera)
                    ?.addItemType(TYPE_IMAGE, R.layout.item_gallery_image)
            }

        }

        override fun convert(holder: BaseViewHolder, item: GalleryModel) {
            if (holder.itemViewType == TYPE_CAMERA) {
                // do nothing
            } else {
                //展示照片
                val imageView = holder.getView<ImageView>(R.id.iv_thumb)
                item?.uri?.let {

                    Glide.with(imageView)
                        .load(it)
                        //.apply( RequestOptions()
                        //.dontTransform()
                        //.placeholder(R.drawable.placeholder) )
                        .into(imageView)

                }
                val mask = holder.getView<View>(R.id.v_mask)
                val checked = holder.getView<ImageView>(R.id.gallery_check)
                val unChecked = holder.getView<View>(R.id.cb_check)
                if (galleryModels.contains(item)) {
                    //      mask.visibility = View.VISIBLE
                    checked.visibility = View.VISIBLE
                    unChecked.visibility = View.GONE
                } else {
                    //     mask.visibility = View.GONE
                    checked.visibility = View.GONE
                    unChecked.visibility = View.VISIBLE
                }
            }
        }
    }
}