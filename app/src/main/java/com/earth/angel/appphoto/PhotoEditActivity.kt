package com.earth.angel.appphoto

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.earth.angel.R
import com.earth.angel.appphoto.adapter.MyAdapter
import com.earth.angel.appphoto.adapter.MyAdapterListener
import com.earth.angel.appphoto.adapter.MyItemTouchHelperCallback
import com.earth.angel.base.*
import com.earth.angel.databinding.ActivityPhotoEditBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.dialog.DialogType
import com.earth.angel.dialog.PwInterface
import com.earth.angel.event.ShakeEvent
import com.earth.angel.event.ShareShowEvent
import com.earth.angel.photo.CameraV2Activity
import com.earth.angel.photo.SelectedMutablePhotoEvent
import com.earth.angel.share.ShareActivity
import com.earth.angel.util.*
import com.earth.angel.view.GiftPhotoActivity
import com.earth.libbase.Config
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.entity.PhotoCutEntity
import com.earth.libbase.network.OSSManager
import com.earth.libbase.network.request.PictureUrlRequest
import com.earth.libbase.network.request.ReleaseRecordRequest
import com.earth.libbase.service.OssInterface
import com.earth.libbase.service.OssService
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.util.ShareUtil
import com.earth.libbase.util.Storage.getFileSufix
import kotlinx.android.synthetic.main.activity_edit_photo.*
import kotlinx.android.synthetic.main.activity_photo_edit.*
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.android.synthetic.main.item_chat.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class PhotoEditActivity : BaseActivity<ActivityPhotoEditBinding>(), OssInterface {

    private val mPhotoModelModel by viewModel<PhotoModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private lateinit var mAdapter: MyAdapter
    private var mBottomPW: DialogType? = null
    private var giftEntity: GiftEntity? = null
    private var listPhotoCutEntity: ArrayList<PhotoCutEntity> = ArrayList()
    //OSS的上传下载
    private var releaseType: Int? = null
    private val maxText = 500//最多显示的字数
    private val maxTextNum = 7//最多显示的字数
    private var sendMode: Int? = 0
    private var newOrOld: Int? = 0
    private var id: Long? = null
    private var flagExit: Boolean? = null // 是否直接打开照相页
    private var helper: ItemTouchHelper? = null
    private var mOssService: OssService? = null
    private var mPicList = ArrayList<PictureUrlRequest>()
    private var description: String? = ""
    private var newPathUpload: Int = 0 // 修改的时候上传了几个图片
    private var oldPathUpload: Int = 0 // 修改的时候上传了几个图片


    override fun getLayoutId(): Int = R.layout.activity_photo_edit
    override fun fishActivity() {
        super.fishActivity()
        DataReportUtils.getInstance().report("Post_exit")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initActivity(savedInstanceState: Bundle?) {
        ActivityStackManager.addShareActivity(this@PhotoEditActivity)
        DataReportUtils.getInstance().report("Post")
        EventBusHelper.register(this)
        showActionBar()
        mBinding.run {
            handler = this@PhotoEditActivity
            tvTitleCenterPhoto.text = getString(R.string.label_Post)
            toolbarTabRightPhoto.visibility = View.INVISIBLE
            tvLeftToolPhoto.setOnClickListener {
                exit()
            }
            if (PreferencesHelper.getTodayTime(this@PhotoEditActivity) == DateUtils.getTodayDate()!!) {
                lyFeature.visibility = View.GONE
            } else {
                lyFeature.visibility = View.VISIBLE
            }
            lyFeature.setOnClickListener {
                PreferencesHelper.saveTodayTime(this@PhotoEditActivity)
                lyFeature.visibility = View.GONE
            }
            flagExit = intent.getBooleanExtra("flag", false)
            btSaveShare.isEnabled = false
            btSave.isEnabled = false
            listPhotoCutEntity.clear()
            mAdapter =
                MyAdapter(this@PhotoEditActivity, listPhotoCutEntity, object : MyAdapterListener {
                    override fun delete(position: Int) {
                        listPhotoCutEntity.removeAt(position)
                        for (i in listPhotoCutEntity.indices) {
                            if (listPhotoCutEntity[i].sdPath.equals("")) {
                                listPhotoCutEntity.removeAt(i)
                            }
                        }
                        if (listPhotoCutEntity.size < 9) {
                            listPhotoCutEntity.add(PhotoCutEntity(""))
                        }
                        mAdapter.notifyDataSetChanged()
                    }

                    override fun add(position: Int) {
                        if (listPhotoCutEntity[position].sdPath.equals("")) {
                            getPhoto()
                        } else {
                            //移除空选项
                            for (item in listPhotoCutEntity) {
                                if (item.sdPath.equals("")) {
                                    listPhotoCutEntity.remove(item)
                                }
                            }
                            startActivity.launch(
                                Intent(this@PhotoEditActivity, CutPhotoActivity::class.java)
                                    .putExtra(GiftPhotoActivity.IMG, listPhotoCutEntity)
                                    .putExtra("position", position)
                            )
                        }
                    }
                })
            mOssService = OSSManager.initOSS(this@PhotoEditActivity, Config.OSS_ENDPOINT)
            mOssService?.setInterface(this@PhotoEditActivity)
            photoRlv.layoutManager = GridLayoutManager(this@PhotoEditActivity, 3)
            photoRlv.adapter = mAdapter
            helper = ItemTouchHelper(MyItemTouchHelperCallback(mAdapter, this@PhotoEditActivity))
            helper?.attachToRecyclerView(photoRlv)
            giftEntity = intent.getSerializableExtra("giftEntity") as GiftEntity?
            giftEntity?.run {
                btSaveShare.isEnabled = true
                btSave.isEnabled = true
                this@PhotoEditActivity.id = id.toLong()
                listPhotoCutEntity.clear()
                if (!giftEntity?.pictureResources.isNullOrEmpty()) {
                    for (item in giftEntity?.pictureResources!!) {
                        //     mList.add(item.pictureUrl)
                       listPhotoCutEntity.add(
                            PhotoCutEntity(
                                item.pictureUrl
                            )
                        )
                        oldPathUpload++

                    }
                    if (listPhotoCutEntity.size < 9) {
                        listPhotoCutEntity.add(PhotoCutEntity(""))
                    }
                    mAdapter.notifyDataSetChanged()
                }

                etDescription.setText(description)
                if (valuation != 0.0) {
                    et_value.setText(valuation.toString())
                    tv_numVa.setText(et_value.text.toString().length.toString())
                }
                mBinding.tvNum.text = description?.length.toString()
                if (newOrOld == 0) {
                    rbTypeFree.isChecked = true
                }
                if (newOrOld == 1) {
                    rbTypeExchange.isChecked = true
                }
                launch {
                    mPhotoModelModel.sortLabel().catch { }
                        .onStart { mAppViewModel.showLoading() }
                        .onCompletion { mAppViewModel.dismissLoading() }
                        .collectLatest {
                            if (it.isOk(this@PhotoEditActivity)) {
                                it.data?.let { itList ->
                                    for (item in itList) {
                                        if (item.releaseType == releaseType) {
                                            this@PhotoEditActivity.releaseType = item.releaseType
                                            tvShoes.text = item.englishDescribe
                                            break
                                        }
                                    }
                                }
                            }
                        }
                }
            }
            etDescription.run {
                var wordNum: CharSequence? = null
                var selectionStart: Int
                var selectionEnd: Int
                doOnTextChanged { text, _, _, _ ->
                    wordNum = text!!
                    checkPost()
                }
                doAfterTextChanged {
                    maxText - it!!.length
                    tvNum.text = text?.length.toString()
                    selectionStart = getSelectionStart()
                    selectionEnd = getSelectionEnd()
                    if (wordNum!!.length > maxText) {
                        it.delete(selectionStart - 1, selectionEnd)
                        val tempSelection = selectionEnd
                        setSelection(tempSelection)
                    }
                }
            }
            etValue.run {
                var wordNum: CharSequence? = null
                var selectionStart: Int
                var selectionEnd: Int
                doOnTextChanged { text, _, _, _ ->
                    wordNum = text!!
                }
                doAfterTextChanged {
                    maxTextNum - it!!.length
                    tvNumVa.text = text?.length.toString()
                    selectionStart = getSelectionStart()
                    selectionEnd = getSelectionEnd()
                    if (wordNum!!.length > maxTextNum) {
                        it.delete(selectionStart - 1, selectionEnd)
                        val tempSelection = selectionEnd
                        setSelection(tempSelection)
                    }
                }
            }
            etDescription.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    //通知父控件不要干扰
                    v.parent.requestDisallowInterceptTouchEvent(true);
                }
                if (event.action == MotionEvent.ACTION_MOVE) {
                    //通知父控件不要干扰
                    v.parent.requestDisallowInterceptTouchEvent(true);
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    v.parent.requestDisallowInterceptTouchEvent(false);
                }
                false
            }
            layType.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    showDialog()

                }
            }
            btSave.setOnClickListener {
                DataReportUtils.getInstance().report("Bottom_save")
                if (DateUtils.isFastClick()) {
                    if (this@PhotoEditActivity.id == null) {
                        saveData()
                    } else {
                        updateData()
                    }
                }

            }
            btSaveShare.setOnClickListener {
                DataReportUtils.getInstance().report("Bottom_share")
                if (DateUtils.isFastClick()) {
                    if (this@PhotoEditActivity.id == null) {
                        saveData()
                    } else {
                        updateData()
                    }
                    ShareUtil.share(this@PhotoEditActivity)
                }
            }

            rg.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbTypeFree -> {
                        newOrOld = 0
                    }
                    R.id.rbTypeExchange -> {
                        newOrOld = 1
                    }
                }
            }
            flagExit?.let {
                if (it) {
                    getPhoto()
                }
            }
            if (this@PhotoEditActivity.id == null) {
                tvTitleCenterPhoto.text = getString(R.string.label_Post)
                btSave.text = getString(R.string.lab_post)
            } else {
                tvTitleCenterPhoto.text = getString(R.string.label_Edit)
                btSave.text = getString(R.string.lab_Save)
            }
        }
        mAppViewModel.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog.dismiss()
        })
        if (giftEntity == null) {
            initsortLabel()
        }
    }

    private fun initsortLabel() {
        launch {
            mPhotoModelModel.sortLabel().catch { }
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .collectLatest {
                    if (it.isOk(this@PhotoEditActivity)) {
                        it.data?.let { itList ->
                            for (item in itList) {
                                if (item.releaseType == 10000) {
                                    this@PhotoEditActivity.releaseType = item.releaseType
                                    tvShoes.text = item.englishDescribe
                                    break
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun showDialog() {
        launch {
            mPhotoModelModel.sortLabel().catch { }
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .collectLatest {
                    if (it.isOk(this@PhotoEditActivity)) {
                        it.data?.let { list ->
                            mBottomPW = DialogType(this@PhotoEditActivity)
                                .setList(list)
                                .setOnInterface(object : PwInterface {
                                    override fun onItemClick(position: Int) {
                                        releaseType = list[position].releaseType
                                    }

                                    override fun onBankString(str: String) {
                                        mBinding?.tvShoes.text = str
                                        mBottomPW?.disMiss()
                                    }

                                    override fun onDismiss() {
                                    }

                                    override fun onEmpty() {
                                        mBinding?.tvShoes.text = ""
                                        releaseType = null
                                        mBottomPW?.disMiss()
                                    }
                                })
                                .create()
                            mBottomPW?.show()
                        }
                    }

                }
        }
    }

    // 保存
    private fun saveData() {
        description = mBinding.etDescription.text.toString().trim()
        if (description!!.length < 2) {
            toast(getString(R.string.text_Description_Least))
            return
        }
        var value = mBinding.etValue.text.toString()
        if (description!!.isNotEmpty()) {
            DataReportUtils.getInstance().report("Enter_description")
        }
        var numValue: Double? = null
        if (value.isNotEmpty()) {
            numValue = value.toDouble()
        }
        for (item in listPhotoCutEntity) {
            if (item.sdPath.equals("")) {
                listPhotoCutEntity.remove(item)
            }
        }
        for (item in listPhotoCutEntity) {
            if (!item.sdPath.equals("")) {
                mOssService?.asyncPutImage(getFileSufix(item.sdPath), item.sdPath)
            }else if(item.sdPath.contains("https")){
                mPicList.add(PictureUrlRequest(item.sdPath))
            }
        }
        mAppViewModel.showLoading()
    }

    // 修改
    private fun updateData() {
        var description = mBinding.etDescription.text.toString()
        if (description.length < 2) {
            toast(getString(R.string.text_Description_Least))
            return
        }
        var value = mBinding.etValue.text.toString()
        if (description.isNotEmpty()) {
            DataReportUtils.getInstance().report("Enter_description")
        }
        newPathUpload=0
        for (item in listPhotoCutEntity) {
            if (item.sdPath.equals("")) {
                listPhotoCutEntity.remove(item)
            }
        }
        for (item in listPhotoCutEntity) {
            if (!item.sdPath.equals("")&&!item.sdPath.contains("https")) {
                newPathUpload++
            }
        }


        var flagNew =false
        for (item in listPhotoCutEntity) {
            if (!item.sdPath.equals("")&&!item.sdPath.contains("https")) {
                flagNew=true
                mOssService?.asyncPutImage(getFileSufix(item.sdPath), item.sdPath)
            }else if(item.sdPath.contains("https")){
                mPicList.add(PictureUrlRequest(item.sdPath))
            }
        }
        if(flagNew){
            mAppViewModel.showLoading()
        }else{
            var bean = ReleaseRecordRequest(
                id,
                description,
                releaseType,
                newOrOld,
                sendMode,
                mPicList,
                null
            )
          /*  launch {
                mPhotoModelModel.releaseRecordUpdate(bean).catch { }.collectLatest {
                    if (it.isOk(this@PhotoEditActivity)) {
                        toast(getString(R.string.lab_Success))
                        //  setResult(100)
                        //  finish()
                        EventBus.getDefault().post(ShareShowEvent())
                        startActivity.launch(
                            EarthAngelApp.myProfileEntity.let {
                                Intent(this@PhotoEditActivity, ShareActivity::class.java)
                                    .putExtra("mUserID", it?.id)
                                    .putExtra("mUserNAme", it?.nickName)
                            }
                        )
                    }
                }
            }*/
        }


    }


    companion object {
        fun openPhotoEditActivity(context: Context?, flag: Boolean?) {
            val intent = Intent(context, PhotoEditActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("flag", flag)
            context?.startActivity(intent)
        }

        fun openPhotoEditActivityEdit(context: Context?, bean: GiftEntity) {
            val intent = Intent(context, PhotoEditActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("giftEntity", bean)
            context?.startActivity(intent)
        }
    }

    private fun getPhoto() {
        val permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsList.add(1, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionsList.add(2, Manifest.permission.CAMERA)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                var size = CameraV2Activity.Max_Max_PHOTO_SIZE - (listPhotoCutEntity.size - 1)
                if (size > 0) {
                    startActivity.launch(
                        Intent(this@PhotoEditActivity, CameraV2Activity::class.java)
                            .putExtra(CameraV2Activity.Max_PHOTO, size)
                    )
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Subscribe
    fun onEvent(event: SelectedMutablePhotoEvent) {
        when (event.photoType) {
            PhotoUploadType.PUBLIC -> {
                if (event.photos.isNotEmpty()) {
                    if (event.photos.size > 0) {
                        // 去空选项
                        for(item in listPhotoCutEntity){
                            if(item.sdPath==""){
                                listPhotoCutEntity.remove(item)
                            }
                        }
                     //   mAppViewModel.showLoading()
                        for (item in event.photos) {
                            item.path?.let { it1 ->
                                var mPhotoCutEntity = PhotoCutEntity(it1)
                                listPhotoCutEntity.add(mPhotoCutEntity)
                          //      mOssService?.asyncPutImage(getFileSufix(it1), it1)
                            }
                        }
                        if (listPhotoCutEntity.size <= 8) {
                            listPhotoCutEntity.add(PhotoCutEntity(""))
                        }
                        mAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }


    override fun onBankString(path: String, str: String) {
           mPicList.add(PictureUrlRequest(str))
            if (this@PhotoEditActivity.id == null) {
                if(mPicList.size==listPhotoCutEntity.size){
                    mAppViewModel.dismissLoading()
                    // 保存上传完毕
                    var bean =
                        ReleaseRecordRequest(
                            null,
                            description,
                            releaseType,
                            newOrOld,
                            sendMode,
                            mPicList,
                            null
                        )
                    launch {
                        mPhotoModelModel.releaseRecord(bean).catch { }.collectLatest {
                            if (it.isOk(this@PhotoEditActivity)) {
                                EventBusHelper.post(ShakeEvent())
                                toast(getString(R.string.lab_Success))
                                this@PhotoEditActivity.id = it.data.toString().toLong()
                                EventBus.getDefault().post(ShareShowEvent())
                                EarthAngelApp.myProfileEntity.let {
                                    startActivity.launch(
                                        Intent(this@PhotoEditActivity, ShareActivity::class.java)
                                            .putExtra("mUserID", it?.id)
                                            .putExtra("mUserNAme", it?.nickName)
                                    )
                                }
                            } else {
                                toast(it.msg)
                            }
                        }
                    }

                }

            }else{
                // 修改上传完毕
                if(mPicList.size-oldPathUpload==newPathUpload){
                    mAppViewModel.dismissLoading()
                    var bean = ReleaseRecordRequest(
                        id,
                        description,
                        releaseType,
                        newOrOld,
                        sendMode,
                        mPicList,
                        null
                    )
            /*        launch {
                        mPhotoModelModel.releaseRecordUpdate(bean).catch { }.collectLatest {
                            if (it.isOk(this@PhotoEditActivity)) {
                                toast(getString(R.string.lab_Success))
                                //  setResult(100)
                                //  finish()
                                EventBus.getDefault().post(ShareShowEvent())

                                startActivity.launch(
                                    EarthAngelApp.myProfileEntity.let {
                                        Intent(this@PhotoEditActivity, ShareActivity::class.java)
                                            .putExtra("mUserID", it?.id)
                                            .putExtra("mUserNAme", it?.nickName)
                                    }

                                )
                            }
                        }
                    }*/
                }

            }


    }

    // 创建一个Handler
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    mAdapter.notifyDataSetChanged()
                    checkPost()
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

    private fun checkPost() {
        mBinding.run {
            if (listPhotoCutEntity.size > 1 && etDescription.text?.length!! > 2) {
                btSaveShare.isEnabled = true
                btSave.isEnabled = true
            } else {
                btSaveShare.isEnabled = false
                btSave.isEnabled = false
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)

    }

    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                100 -> {
                    it.data?.let { intentIt ->
                        flagExit?.let { itFlag ->
                            if (itFlag) {
                                // 没有照相并且，没有上传有图
                                if (intentIt.getIntExtra(
                                        "CA",
                                        0
                                    ) == 0 && listPhotoCutEntity.size < 2
                                ) {
                                    finish()
                                }
                            }
                        }

                    }
                }
                101 -> {
                    var mlistPicTureEntity =
                        it.data?.getSerializableExtra(GiftPhotoActivity.IMG) as ArrayList<PhotoCutEntity>
                    listPhotoCutEntity.clear()
                    listPhotoCutEntity.addAll(mlistPicTureEntity)
                    if (listPhotoCutEntity.size < 9) {
                        listPhotoCutEntity.add(PhotoCutEntity(""))
                    }
                    mAdapter.notifyDataSetChanged()
                    checkPost()

                }
                102 -> {
                    if (listPhotoCutEntity.size < 9) {
                        listPhotoCutEntity.add(PhotoCutEntity(""))
                    }
                    mAdapter.notifyDataSetChanged()
                    if (this@PhotoEditActivity.id == null) {
                        tvTitleCenterPhoto.text = getString(R.string.label_Post)
                        btSave.text = getString(R.string.lab_post)
                    } else {
                        tvTitleCenterPhoto.text = getString(R.string.label_Edit)
                        btSave.text = getString(R.string.lab_Save)
                    }
                }
            }
        }

    private fun exit() {
        if (checkExit()) {
            var blockDialog = DialogCommon(title = "You already have edited content, are you sure to back?",
                onBlockClick = {
                    finish()
                },
                onDissMissClick = {
                })
            blockDialog.show(
                supportFragmentManager, ""
            )
        } else {
            finish()
        }
    }

    private fun checkExit(): Boolean {
        var exitBoolean = false
        if (listPhotoCutEntity.size > 1) {
            exitBoolean = true
        }
        if (!mBinding.etDescription.text.isNullOrEmpty()) {
            exitBoolean = true
        }
        return exitBoolean
    }


}