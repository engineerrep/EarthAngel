package com.earth.angel.appphoto

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.appphoto.adapter.GroupPostAdapter
import com.earth.angel.appphoto.adapter.PhotoPostAdapter
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.base.PhotoUploadType
import com.earth.angel.base.PhotoUploadType.PHOTOEDIT
import com.earth.angel.databinding.ActivityPhotoPostBinding
import com.earth.angel.photo.SelectedMutablePhotoEvent
import com.earth.angel.photo.gallery.GalleryModel
import com.earth.angel.util.BitmapUtil
import com.earth.angel.util.EventBusHelper
import com.earth.angel.util.PreferencesAppHelper
import com.earth.angel.view.PointPopMessageUtil
import com.earth.libbase.Config
import com.earth.libbase.base.*
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.CategoryEntity
import com.earth.libbase.entity.GroupEntity
import com.earth.libbase.network.OSSManager
import com.earth.libbase.network.baserequest.BasePhotoRequest
import com.earth.libbase.network.request.BaseFileRequest
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.service.OssInterface
import com.earth.libbase.service.OssService
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.Storage
import com.earth.libhome.group.GroupAssociatedActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_photo_post.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


@Route(path = Constance.PhotoPostActivityURL)
class PhotoPostActivity : BaseActivity<ActivityPhotoPostBinding>(){
    @Autowired(name = "id")
    @JvmField
    var id: String? = null

    @Autowired(name = "GroupID")
    @JvmField
    var mGroupID: String? = null


    private val mPhotoModelModel by viewModel<PhotoModel>()
    private val mAppViewModel by viewModel<AppViewModel>()

    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    private var imgList: ArrayList<String> = ArrayList()
    private var mainimg: ImageUploadEntity? = null

    private var imgAdapter: PhotoPostAdapter? = null
    private var listPhotos = mutableListOf<GalleryModel>()
    private var layoutPager: LinearLayoutManager? = null
    private var footerView: View? = null
    private val maxSize: Int? = 12
    private var itemCode: Int? = null
    private var upImage: String? = null // 去服务器解析图片

    private var mGroupPostAdapter: GroupPostAdapter? = null
    private var listGroupPost: ArrayList<GroupEntity> = ArrayList()


    override fun getLayoutId(): Int = R.layout.activity_photo_post

    override fun initActivity(savedInstanceState: Bundle?) {
        fitSystemBar()
        EventBusHelper.register(this)
        ARouter.getInstance().inject(this@PhotoPostActivity)
        mBinding.run {
            // 初始化图片服务器
            setViewActionBarHight(BankPhotoDelete)
            BankPhotoDelete.setOnClickListener {
                finish()
            }
            ivBaseAddPhoto?.setOnClickListener {
                getPhoto()
            }
            //照片加载
            imgAdapter = PhotoPostAdapter(this@PhotoPostActivity, listPhotos, upDade = {
                listPhotos.remove(it)
                imgAdapter?.notifyDataSetChanged()
            })
            layoutPager = LinearLayoutManager(
                this@PhotoPostActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            AddPictureRlv.layoutManager = layoutPager
            AddPictureRlv.adapter = imgAdapter
            imgAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.PublishEditPhotoActivityURL)
                        .withString("position", position.toString())
                        .navigation()
                    delayLaunch(500) {
                        block = {
                            EventBusHelper.post(SelectedMutablePhotoEvent(listPhotos, PHOTOEDIT))
                        }
                    }
                }
            }
            footerView = layoutInflater.inflate(R.layout.footer_addview, null)
            footerView?.let {
                imgAdapter?.addFooterView(it)
                val mFooterAddview: RelativeLayout = it.findViewById(R.id.footer_addview_add)
                mFooterAddview.setOnClickListener {
                    getPhoto()
                }
            }
            // 照片编辑
            PhotoModify.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.PublishEditPhotoActivityURL)
                        .navigation()
                    delayLaunch(500) {
                        block = {
                            EventBusHelper.post(SelectedMutablePhotoEvent(listPhotos, PHOTOEDIT))
                        }
                    }
                }
            }

            // 人物信息
            BaseApplication.myBaseUser?.run {
                headImgUrl?.let { itHead ->
                    Glide.with(HomeHeadImage.context)
                        .load(itHead)
                        .into(HomeHeadImage)
                }
                nickName?.let { itName ->
                    HomeName.text = "Hi. $itName"
                }
            }

            WhatThisModify.setOnClickListener {
                junpPhotoEtidDetailsActivity("Title",1)
            }
            etPhoneTitle.setOnClickListener {
                junpPhotoEtidDetailsActivity("Title",1)
            }
            tvPhotoDetails.setOnClickListener {
                junpPhotoEtidDetailsActivity("Details",2)
            }
            etPhoneDetails.setOnClickListener {
                junpPhotoEtidDetailsActivity("Details",2)
            }
            postCond.showContent( getString(R.string.label_Used))
            postCond.setOnClickListener {
                jumpPhotoEtidDetailsActivity(getString(R.string.base_Condition),postCond.getContent(),1)
            }
            postCate.setOnClickListener {
                jumpPhotoEtidDetailsActivity(getString(R.string.base_Category),postCate.getContent(),1)
            }
            postEco.showContent(getString(R.string.label_Free))
            postEco.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    val mPointPopMessageUtil = PointPopMessageUtil()
                    mPointPopMessageUtil.showPopupWindow(
                        this@PhotoPostActivity,
                        postNsv!!,
                        {
                            postEco.showContent(it)
                        },
                        postEco.getContent()
                    )
                }
            }


            tvPostAGift.isEnabled = false
            tvPostAGift.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    imgList.clear()
                    if (id.isNullOrEmpty()) {
                        if(!listPhotos.isEmpty()){
                            val parts = arrayListOf<MultipartBody.Part>()
                            for(item in listPhotos){
                                val  file=File(item.path)
                                val requestFile: RequestBody =
                                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                                val body =
                                    MultipartBody.Part.createFormData("files", file.name, requestFile)
                                parts.add(body)
                            }
                            launch {
                                mPhotoModelModel.upload(parts)
                                    .onStart {
                                        mAppViewModel.showLoading()
                                    }
                                    .catch {  }
                                    .onCompletion {
                                    }
                                    .collectLatest {
                                        if( it.data.isNullOrEmpty()){
                                            mAppViewModel.dismissLoading()
                                        }else{
                                            it.data?.let {
                                                for(item in it){
                                                    imgList.add(0, item)
                                                }
                                                post()
                                            }
                                        }
                                    }
                            }
                        }
                    } else {
                        update()
                    }

                }
            }
            tvSaveDraft.setOnClickListener {
                val title = etPhoneTitle.text.toString()
                val description = etPhoneDetails.text.toString()
                val bean = BasePhotoSaveEntity(
                    itemTitle = title,
                    description = description,
                    pictureResources = listPhotos
                )
                PreferencesAppHelper.saveTodayTime(this@PhotoPostActivity, Gson().toJson(bean))
                finish()
            }
            EarthAngelApp.mBasePhotoRequest?.run {
                itemTitle?.let {
                    etPhoneTitle.text = it
                }
                description?.let {
                    etPhoneDetails.text = it
                }
                pictureResources?.let {
                    if (it.size > 0) {
                        ivBaseAddPhoto?.visibility = View.GONE
                        for (bean in it) {
                            listPhotos.add(bean)
                        }
                        imgAdapter?.notifyDataSetChanged()
                        if (listPhotos.size == 12) {
                            footerView?.let {
                                imgAdapter?.removeAllFooterView()
                            }
                        }
                    }
                }
            }

            //  var gridLayoutManager = GridLayoutManager(this@PhotoPostActivity, 2)
            mGroupPostAdapter = GroupPostAdapter(listGroupPost, mDelete = {
                listGroupPost.remove(it)
                mGroupPostAdapter?.notifyDataSetChanged()
                if (listGroupPost.size == 0) {
                    mBinding.etAssociated.visibility = View.VISIBLE
                    mBinding.rlvGroupPost.visibility = View.GONE
                } else {
                    mBinding.etAssociated.visibility = View.GONE
                    mBinding.rlvGroupPost.visibility = View.VISIBLE
                }
            })
            rlvGroupPost.layoutManager = LinearLayoutManager(
                this@PhotoPostActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rlvGroupPost.adapter = mGroupPostAdapter
            etAssociated.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance()
                        .build(Constance.GroupAssociatedActivityURL)
                        .withSerializable("Group", listGroupPost)
                        .navigation(this@PhotoPostActivity, 1)
                }
            }
            tvAssociatedEd.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance()
                        .build(Constance.GroupAssociatedActivityURL)
                        .withSerializable("Group", listGroupPost)
                        .navigation(this@PhotoPostActivity, 1)
                }
            }
            mAppViewModel?.showLoadingProgress.observe(this@PhotoPostActivity, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })


        }
        getGiftList()
        mGroupID?.let {
            communityList()
        }
    }
    private fun junpPhotoEtidDetailsActivity(type: String ,code: Int){
        if (BaseDateUtils.isFastClick()) {
            ARouter.getInstance().build(Constance.PhotoEtidContentActivityURL)
                .withString("TYPE", type)
                .withString("Content", mBinding.etPhoneDetails.text.toString())
                .navigation(this@PhotoPostActivity, code)
        }
    }
    private fun jumpPhotoEtidDetailsActivity(type: String ,description: String,code: Int){
        if (BaseDateUtils.isFastClick()) {
            ARouter.getInstance().build(Constance.PhotoEtidDetailsActivityURL)
                .withString("TYPE",type)
                .withString("description", description)
                .navigation(this@PhotoPostActivity, code)
        }
    }

    private fun checkCanPost() {
        mBinding.tvPostAGift.isEnabled = when {
            mBinding.etPhoneTitle.text.toString().trim().isNullOrEmpty() -> false
            listPhotos.size == 0 -> false
            mBinding.postCond.getContent().isNullOrEmpty() -> false
            mBinding.postCate.getContent().isNullOrEmpty() -> false
            else -> {
                true
            }
        }
    }
     // 获取图片
    private fun getPhoto() {
        val permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsList.add(1, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionsList.add(2, Manifest.permission.CAMERA)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                var intSize = maxSize!! - listPhotos.size
                ARouter.getInstance().build(Constance.CameraV2ActivityURL)
                    .withInt("SIZE", intSize)
                    .navigation()
            }
            onPermissionsNeverAsked = {
                longToast("Open permissions in settings")
                var intent = Intent()
                intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
                intent.data = Uri.parse("package:" + this@PhotoPostActivity.packageName);
                startActivity(intent)
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Subscribe
    fun onEvent(event: SelectedMutablePhotoEvent) {
        when (event.photoType) {
            PhotoUploadType.PUBLIC -> {
                if (event.photos.isNotEmpty()) {
                    checkCanPost()
                    if (event.photos.size > 0) {
                        ivBaseAddPhoto?.visibility = View.GONE
                        if (id.isNullOrEmpty()) {
                            listPhotos.addAll(event.photos)
                            imgAdapter?.notifyDataSetChanged()
                        } else {
                            // 修改
                            val parts = arrayListOf<MultipartBody.Part>()
                            for (item in event.photos) {
                                    val  file=File(item.path)
                                    val requestFile: RequestBody =
                                        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                                    val body =
                                        MultipartBody.Part.createFormData("files", file.name, requestFile)
                                    parts.add(body)
                            }
                            launch {
                                mPhotoModelModel.upload(parts)
                                    .onStart {
                                        mAppViewModel.showLoading()
                                    }
                                    .catch {  }
                                    .onCompletion {
                                    }
                                    .collectLatest {
                                        if( it.data.isNullOrEmpty()){
                                            mAppViewModel.dismissLoading()
                                        }else{
                                            mAppViewModel.dismissLoading()
                                            it.data?.let {
                                                for(item in it){
                                                    var bean = GalleryModel()
                                                    bean.path = item
                                                    listPhotos.add(bean)
                                                }
                                            }
                                            imgAdapter?.notifyDataSetChanged()
                                        }
                                    }
                            }



                        }

                        checkFooterView(event)
                    }
                }
                // 图片识别判定，如果首页变化 就上传
                if (listPhotos.size > 0) {
                    var newupupImage = listPhotos[0].path
                    if (newupupImage !== upImage) {
                        newupupImage?.let {
                            BitmapUtil.imageToBase64(it)?.let {
                                postImage(it, newupupImage)
                            }
                        }

                    }
                }
            }
            PhotoUploadType.PHOTOEDITBANK -> {
                checkCanPost()
                if (event.photos.isNotEmpty()) {
                    if (event.photos.size > 0) {
                        listPhotos.clear()
                        ivBaseAddPhoto?.visibility = View.GONE
                        listPhotos.addAll(event.photos)
                        imgAdapter?.notifyDataSetChanged()
                        checkFooterView(event)
                    }
                }
            }

        }
    }

    private fun checkFooterView(event: SelectedMutablePhotoEvent) {

        if (listPhotos.size == 12) {
            footerView?.let {
                imgAdapter?.removeAllFooterView()
            }
        } else if (listPhotos.size < 12) {
            if (!imgAdapter!!.hasFooterLayout()) {
                footerView?.let {
                    imgAdapter?.addFooterView(it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            when (requestCode) {
                1 -> {
                    data?.let {
                        val ss = data.getStringExtra("content")
                        etPhoneTitle?.setText(ss)
                        checkCanPost()
                    }
                }
                2 -> {
                    data?.let {
                        val ss = data.getStringExtra("content")
                        etPhoneDetails?.setText(ss)
                    }
                }
            }
        }
        if (requestCode == 1) {
            when (resultCode) {
                PhotoEtidDetailsActivity.ConditionCode -> {
                    data?.let {
                        val ss = data.getSerializableExtra("content") as CategoryEntity
                        ss?.run {
                            mBinding.postCond.showContent(description)
                            checkCanPost()
                        }
                    }
                }
                PhotoEtidDetailsActivity.CategoryCode -> {
                    data?.let {
                        val ss = data.getSerializableExtra("content") as CategoryEntity
                        ss?.run {
                            mBinding.postCate.showContent(description)
                            this@PhotoPostActivity.itemCode = itemCode
                            checkCanPost()
                        }
                    }
                }
                GroupAssociatedActivity.GroupAssociatedCode -> {
                    data?.let {
                        var mGroupList: ArrayList<GroupEntity> =
                            data.getSerializableExtra("Group") as ArrayList<GroupEntity>
                        mGroupList.run {
                            if (mGroupList.size == 0) {
                                mBinding.etAssociated.visibility = View.VISIBLE
                                mBinding.rlvGroupPost.visibility = View.GONE
                            } else {
                                mBinding.etAssociated.visibility = View.GONE
                                mBinding.rlvGroupPost.visibility = View.VISIBLE
                            }
                            listGroupPost.clear()
                            listGroupPost.addAll(mGroupList)
                            mGroupPostAdapter?.notifyDataSetChanged()

                        }
                    }
                }

            }
        }
    }

    fun update() {
        val title = etPhoneTitle.text.toString()
        val description = etPhoneDetails.text.toString()
        val mConditionContent = mBinding.postCond.getContent()
        val mPostEco = mBinding.postEco.getContent()
        if (title.trim().isEmpty() || title.length > 100) {
            toast(R.string.label_Title_Length)
            return
        }
        if (description.length > 500) {
            toast(R.string.label_Content_Length)
            return
        }
        var mainPicture = ""
        if (listPhotos.size == 0) {
            toast(R.string.base_tips_Picture)
            return
        }
        if (itemCode == null || itemCode == 0) {
            return
        }
        if (mConditionContent.isNullOrEmpty()) {
            return
        }
        if (listPhotos.size > 0) {
            mainPicture = listPhotos[0].path.toString()
            listPhotos.removeAt(0)
        }
        for (item in listPhotos) {
            item.path?.let {
                imgList.add(it)
            }
        }
        var point = if (mPostEco == getString(R.string.label_Free)) {
            0
        } else {
            mPostEco.toInt()
        }
        //同步群
        var imgListId: ArrayList<String> = ArrayList()

        for (item in listGroupPost) {
            imgListId.add(item.id)
        }

        id?.let {
            launch {
                mPhotoModelModel.releaseRecordUpdate(
                    BasePhotoRequest(
                        itemCondition = mConditionContent,
                        itemClassification = itemCode,
                        id = it.toLong(),
                        itemTitle = title,
                        description = description,
                        mainPicture = mainPicture,
                        pictureResources = imgList,
                        score = point,
                        communityIds = imgListId
                    )
                ).onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    MessageObservable.messageObservable.newMessage(UpdateEntity(postProduct = "postProduct"))
                    finish()
                }
            }
        }


    }

    fun post() {
        val title = etPhoneTitle.text.toString()
        val description = etPhoneDetails.text.toString()
        val mConditionContent = mBinding.postCond.getContent()
        val mPostEco = mBinding.postEco.getContent()

        if (title.trim().isEmpty() || title.length > 100) {
            toast(R.string.label_Title_Length)
            return
        }
        if (description.length > 500) {
            toast(R.string.label_Content_Length)
            return
        }
        if (itemCode == null || itemCode == 0) {
            return
        }
        if (mConditionContent.isNullOrEmpty()) {
            return
        }
        var mainPicture = ""
        if (imgList.size > 0) {
            // 把主图片删除
            mainimg?.let {
                it.uploadpath?.let {
                    mainPicture = it
                    for (item in imgList) {
                        if (item == it) {
                            imgList.remove(item)
                            break
                        }
                    }
                }
            }
        }
        var point = if (mPostEco == getString(R.string.label_Free)) {
            0
        } else {
            mPostEco.toInt()
        }
        //同步群
        var imgListId: ArrayList<String> = ArrayList()

        for (item in listGroupPost) {
            imgListId.add(item.id)
        }

        launch {
            mPhotoModelModel.releaseRecordAdd(
                BasePhotoRequest(
                    itemCondition = mConditionContent,
                    itemClassification = itemCode,
                    itemTitle = title,
                    description = description,
                    mainPicture = mainPicture,
                    pictureResources = imgList,
                    score = point,
                    communityIds = imgListId
                )
            ).onStart {
            }.catch {
            }.onCompletion {
                mAppViewModel.dismissLoading()
            }.collectLatest {
                MessageObservable.messageObservable.newMessage(UpdateEntity(postProduct = "postProduct"))
                it.data?.let {
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id", it.releaseRecordId)
                        .withString("userid", BaseApplication.myBaseUser?.userId)
                        .withString("success", it.whetherNeed)
                        .navigation()
                }
                finish()
            }
        }
    }

    private fun postImage(base64: String, path: String) {
        launch {
            mPhotoModelModel.picByIo(
                BaseFileRequest(base64 = base64)
            )
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    upImage = path
                    it.data?.let {
                        if (it.size>0&&it.isNotEmpty()) {
                            if(it[0]!=null){
                                mBinding?.postCate?.showContent(it[0].description)
                                this@PhotoPostActivity.itemCode = it[0].itemCode
                            }
                            checkCanPost()
                        }
                    }
                }
        }

    }

    private fun getGiftList() {
        launch {
            id?.let {
                mPhotoModelModel.releaseGistOne(
                    CommentRequest(releaseRecordId = it.toLong())
                )
                    .onStart {
                        mAppViewModel.showLoading()
                    }.catch {
                    }.onCompletion {
                        mAppViewModel.dismissLoading()
                    }.collectLatest {
                        if (it.isOk(this@PhotoPostActivity)) {
                            it.data?.pictureResources?.let {
                                ivBaseAddPhoto?.visibility = View.GONE
                                for (item in it) {
                                    var bean = GalleryModel()
                                    bean.path = item
                                    listPhotos.add(bean)
                                }
                                imgAdapter?.notifyDataSetChanged()
                            }

                            it.data?.itemTitle?.let {
                                mBinding.etPhoneTitle?.text = it
                            }
                            it.data?.description?.let {
                                mBinding.etPhoneDetails?.text = it
                            }
                            it.data?.itemCondition?.let {
                                mBinding.postCond.showContent(it)
                            }
                            it.data?.itemClassification?.let {
                                this@PhotoPostActivity.itemCode = it
                            }
                            it.data?.itemClassificationDescription?.let {
                                mBinding.postCate.showContent(it)

                            }
                            it.data?.score?.let {
                                if (it == 0) {
                                    mBinding.postEco.showContent(getString(R.string.label_Free))
                                } else {
                                    mBinding.postEco.showContent(it.toString())
                                }
                            }
                            checkCanPost()
                        }
                    }
            }
        }
    }

    private fun communityList() {
        launch {
            mPhotoModelModel.communityList(CommentRequest(pageNum = 1, pageSize = 100))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@PhotoPostActivity)) {
                        it.data?.list?.let {
                            listGroupPost.clear()
                            for (item in it) {
                                if (item.id == mGroupID) {
                                    listGroupPost.add(item)
                                }
                            }
                            if (listGroupPost.size == 0) {
                                mBinding.etAssociated.visibility = View.VISIBLE
                                mBinding.rlvGroupPost.visibility = View.GONE
                            } else {
                                mBinding.etAssociated.visibility = View.GONE
                                mBinding.rlvGroupPost.visibility = View.VISIBLE
                            }
                            mGroupPostAdapter?.notifyDataSetChanged()

                        }
                    }
                }
        }
    }

}