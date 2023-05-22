package com.earth.angel.appphoto

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.*
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.appphoto.adapter.CutColorAdapter
import com.earth.angel.appphoto.adapter.CutTextAdapter
import com.earth.angel.appphoto.adapter.PhotoCutAdapter
import com.earth.angel.base.*
import com.earth.angel.databinding.ActivityPhotoUtBinding
import com.earth.angel.util.BitmapUtil
import com.earth.angel.util.ScreenUtil
import com.earth.angel.view.CustomPopupWindow
import com.earth.angel.view.GiftPhotoActivity
import com.earth.libbase.Config
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GoodsImgEntity
import com.earth.libbase.entity.PhotoCutEntity
import com.earth.libbase.network.OSSManager
import com.earth.libbase.network.img.Credentials
import com.earth.libbase.network.img.Signer
import com.earth.libbase.service.OssInterface
import com.earth.libbase.service.OssService
import com.earth.libbase.util.SoftKeyboardUtils
import com.earth.libbase.util.Storage
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_share_v2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.http.HttpHeaders

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.impl.client.HttpClients

import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.util.*


/**
 * 抠图与不抠图
 */

class CutPhotoActivity : BaseActivity<ActivityPhotoUtBinding>() , OssInterface {
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    companion object {
        const val IMG = "IMAGE"
        const val IMAGE_LIST = "IMAGE_LIST"
    }
    private var mPosition: Int? = null
    private var mPhotoEditAdapter: PhotoCutAdapter? = null
    private var listPicTureEntity: ArrayList<PhotoCutEntity>? = null
    private var mCutColorAdapter: CutColorAdapter? = null
    private var mCutTextAdapter: CutTextAdapter? = null
    private var listText= arrayListOf<Int>(R.mipmap.text_text,R.mipmap.text_one,R.mipmap.text_two,R.mipmap.text_three)

    private var mPhotoCutEntity: PhotoCutEntity? = null
    private var layoutPager: LinearLayoutManager? = null
    private var mOssService: OssService?=null
    private val flagColor=true
    private val flagText=false
    private var typeColor: Boolean=flagColor
    private var customPopupWindow: CustomPopupWindow? = null
    private var etInput: TextInputEditText? = null
    private var ivBank: ImageView? = null
    private var llShareSave: LinearLayout? = null
    private var listColor: ArrayList<Int>? =null
    override fun getLayoutId(): Int = R.layout.activity_photo_ut
    override fun setContentViewBefore() {
        super.setContentViewBefore()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            setResult(101, Intent().putExtra(GiftPhotoActivity.IMG, listPicTureEntity))
            finish()
        }
        return false

    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {
            photoViewBack.setOnClickListener {
                setResult(101, Intent().putExtra(GiftPhotoActivity.IMG, listPicTureEntity))
                finish()
            }
            listColor = arrayListOf<Int>(Color.TRANSPARENT,getColor(R.color.et_white),getColor(R.color.et_grey))
            mOssService = OSSManager.initOSS(this@CutPhotoActivity, Config.OSS_ENDPOINT)
            mOssService?.setInterface(this@CutPhotoActivity)
            listPicTureEntity =
                intent.getSerializableExtra(GiftPhotoActivity.IMG) as ArrayList<PhotoCutEntity>

            mPosition = intent.getIntExtra("position", 0)
            llRlBg.post(Runnable {
               ivDv.setWidthHeight(llRlBg)
            })
            val outRect = Rect()
            window.decorView.getWindowVisibleDisplayFrame(outRect)
            llBgMain.layoutParams.height = outRect.bottom - outRect.top
            mCutColorAdapter= CutColorAdapter(listColor!!)
            CutColorAdapter.colorPosition=0
            var layoutPagerColor = LinearLayoutManager(this@CutPhotoActivity).apply {
                 orientation= LinearLayoutManager.HORIZONTAL
             }
            cutRlvColor.layoutManager=layoutPagerColor
            cutRlvColor.adapter=mCutColorAdapter
            mCutColorAdapter?.setOnItemClickListener { _, _, position ->
                CutColorAdapter.colorPosition=position
                cutPhotoView.setBackgroundColor(listColor!![position])
                mCutColorAdapter?.notifyDataSetChanged()
            }
            ivDv.setItemClick {
                customPopupWindow?.showAtLocation(
                    cutRlvTV,
                    0, 0, 0
                )
                var str = tvET?.text.toString()
                etInput?.setText(str)
                etInput?.setSelection(str.length)
                SoftKeyboardUtils.showSoftInputFromWindow(this@CutPhotoActivity, etInput)
            }
            mCutTextAdapter=CutTextAdapter(listText)
            CutTextAdapter.textPosition=0
            var layoutPagerText = LinearLayoutManager(this@CutPhotoActivity).apply {
                orientation= LinearLayoutManager.HORIZONTAL
            }
            cutRlvTV.layoutManager=layoutPagerText
            cutRlvTV.adapter=mCutTextAdapter
            mCutTextAdapter?.setOnItemClickListener { _, _, position ->
                CutTextAdapter.textPosition=position
                mCutTextAdapter?.notifyDataSetChanged()
                 ivDv.setWidthHeight(llRlBg)
                if(position==0){
                    dvImageView.setImageResource(0)
                }else{
                    dvImageView.setImageResource(listText[position])
                }
            }

        /*    tvET.setOnClickListener {

            }*/
            llIvET.setOnClickListener {
                typeColor=flagText
                initColorTV()
            }

            llIvCV.setOnClickListener {
                typeColor=flagColor
                initColorTV()
            }
            listPicTureEntity?.let {
                layoutPager = LinearLayoutManager(this@CutPhotoActivity)
                layoutPager?.orientation = LinearLayoutManager.HORIZONTAL
                cutrlv.layoutManager = layoutPager
                mPhotoEditAdapter = PhotoCutAdapter(it)
                PhotoCutAdapter.selectPosition = -1
                cutrlv.adapter = mPhotoEditAdapter
                mPosition?.let { itPosition ->
                    PhotoCutAdapter.selectPosition = itPosition
                    mPhotoEditAdapter?.notifyDataSetChanged()
                /*    if (it[itPosition].check) {
                        //
                        if (it[itPosition].changePath.equals("")) {
                            mPhotoCutEntity=it[itPosition]
                            upImg(it[itPosition].sdPath)
                        }else{
                            Glide.with(this@CutPhotoActivity)
                                .load(it[itPosition].changePath)
                                .into(cutPhotoView)
                        }
                    } else {
                        Glide.with(this@CutPhotoActivity)
                            .load(it[itPosition].path)
                            .into(cutPhotoView)
                    }*/
                    Glide.with(this@CutPhotoActivity)
                        .load(it[itPosition].sdPath)
                        .into(cutPhotoView)
                  //  initIv(it[itPosition].check)
                }
                mPhotoEditAdapter?.setOnItemClickListener { _, _, position ->
                    PhotoCutAdapter.selectPosition = position
                    mPhotoEditAdapter?.notifyDataSetChanged()
                    Glide.with(this@CutPhotoActivity)
                        .load(it[PhotoCutAdapter.selectPosition].sdPath)
                        .into(cutPhotoView)

                    /*       PhotoCutAdapter.selectPosition = position
                           mPhotoEditAdapter?.notifyDataSetChanged()
                         //  initIv(it[position].check)
                           if (it[PhotoCutAdapter.selectPosition].check) {
                               if (it[PhotoCutAdapter.selectPosition].changePath.equals("")) {
                                   mPhotoCutEntity=it[PhotoCutAdapter.selectPosition]
                                   upImg(it[PhotoCutAdapter.selectPosition].sdPath)
                                   llEtColor.visibility=View.VISIBLE
                               }else{
                                   Glide.with(this@CutPhotoActivity)
                                       .load(it[PhotoCutAdapter.selectPosition].changePath)
                                       .into(cutPhotoView)
                                   llEtColor.visibility=View.VISIBLE
                               }
                           }else {
                               Glide.with(this@CutPhotoActivity)
                                   .load(it[PhotoCutAdapter.selectPosition].path)
                                   .into(cutPhotoView)
                               llEtColor.visibility=View.GONE
                           }*/
                }
            }
            mCutColorAdapter
            tvConfirm.setOnClickListener {

                setResult(101, Intent().putExtra(GiftPhotoActivity.IMG, listPicTureEntity))
                finish()

            }
            tvSave.setOnClickListener {
                llEtColor.visibility=View.GONE
                listPicTureEntity!![PhotoCutAdapter.selectPosition].sdPath=BitmapUtil.confirm(llRlBg)
                Glide.with(this@CutPhotoActivity)
                    .load(  listPicTureEntity!![PhotoCutAdapter.selectPosition].sdPath)
                    .into(cutPhotoView)
                mPhotoEditAdapter?.notifyDataSetChanged()
                initRlv(false)
                ivDv.visibility=View.GONE
            }
            llBottom.setOnClickListener {
                if (PhotoCutAdapter.selectPosition != -1) {
                    listPicTureEntity?.let {
                        mPhotoCutEntity=it[PhotoCutAdapter.selectPosition]
                        //upImg(it[PhotoCutAdapter.selectPosition].sdPath)
                        llEtColor.visibility=View.VISIBLE
                        ivWaterLogin.visibility=View.VISIBLE
                        initRlv(true)

                    /*    it[PhotoCutAdapter.selectPosition].check = !it[PhotoCutAdapter.selectPosition].check
                          if (it[PhotoCutAdapter.selectPosition].check) {
                         if (it[PhotoCutAdapter.selectPosition].changePath.equals("")) {
                             mPhotoCutEntity=it[PhotoCutAdapter.selectPosition]
                             upImg(it[PhotoCutAdapter.selectPosition].sdPath)
                             llEtColor.visibility=View.VISIBLE
                             initRlv(true)
                         }else{
                             mPhotoEditAdapter?.notifyDataSetChanged()
                             Glide.with(this@CutPhotoActivity)
                                 .load(it[PhotoCutAdapter.selectPosition].changePath)
                                 .into(cutPhotoView)
                             llEtColor.visibility=View.VISIBLE
                             initRlv(true)

                         }

                     } else {
                              mPhotoEditAdapter?.notifyDataSetChanged()
                              Glide.with(this@CutPhotoActivity)
                             .load(it[PhotoCutAdapter.selectPosition].sdPath)
                             .into(cutPhotoView)
                              llEtColor.visibility=View.GONE
                              initRlv(false)

                          }*/

                       // initIv(it[PhotoCutAdapter.selectPosition].check)
                    }
                }
            }
            mAppViewModel.showLoadingProgress.observe(this@CutPhotoActivity, androidx.lifecycle.Observer {
                if (it) mLoadingDialog.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog.dismiss()
            })

            initPopEdit()
        }

    }
    private fun initPopEdit() {
        customPopupWindow = CustomPopupWindow.Builder(this@CutPhotoActivity)
            .setContentView(R.layout.pop_share_edit)
            .setwidth(WindowManager.LayoutParams.MATCH_PARENT)
            .setheight(ScreenUtil.getScreenHeight())
            .build()
        customPopupWindow?.fullWindow(false)
        etInput = customPopupWindow?.getItemView(R.id.etContent) as TextInputEditText
        ivBank = customPopupWindow?.getItemView(R.id.ivBank) as ImageView
        llShareSave = customPopupWindow?.getItemView(R.id.llShareSave) as LinearLayout
        ivBank?.setOnClickListener {
            customPopupWindow?.dismiss()
            SoftKeyboardUtils.hideKeyboardFrom(this@CutPhotoActivity, etInput)
        }
        llShareSave?.setOnClickListener {
            var str=etInput?.text.toString()
            if(str?.length!! <2||str?.length!!>20){
                return@setOnClickListener
            }
            customPopupWindow?.dismiss()

            mBinding?.tvET.text=etInput?.text
            SoftKeyboardUtils.hideKeyboardFrom(this@CutPhotoActivity, etInput)

        }

    }
/*    fun upImg(path: String) {
    CoroutineScope(Dispatchers.IO).launch {
            mAppViewModel.showLoading()
            val credentials = Credentials()
            credentials.accessKeyID = "AKLTNWMxNWY0ZmEyNWViNDlhYmI5MTZmOWI1MjE2NWEyZTk"
            credentials.secretAccessKey = "TVRFMU5EYzVOV1l5TVRRMU5EaGlORGxrTTJZNVlUWXpZemt4TlRnMFpEWQ=="
            credentials.region = "cn-north-1"
            credentials.service = "cv"
            *//* create signer *//*
            val signer = Signer()
            *//* create http client *//*
            val httpClient = HttpClients.createDefault()
            *//* prepare request *//*
            val request = HttpPost()
            try {
                request.uri = URI("https://visual.volcengineapi.com?Action=GeneralSegment&Version=2020-08-26")
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            request.addHeader(HttpHeaders.USER_AGENT, "volc-sdk-java/v1.0.0")
            val nvps: MutableList<NameValuePair> = java.util.ArrayList()
            nvps.add(BasicNameValuePair("image_base64", BitmapUtil.imageToBase64(path)))
            nvps.add(BasicNameValuePair("return_foreground_image", "1"))
            try {
                request.entity = UrlEncodedFormEntity(nvps, HTTP.UTF_8)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            try {
                signer.sign(request, credentials)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            *//* launch request *//*
            var response: CloseableHttpResponse? = null
            try {
                response = httpClient.execute(request)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            *//* status code *//*println(response!!.statusLine.statusCode) // 200

            *//* get response body *//*
            val entity = response!!.entity
            if (entity != null) {
                var result: String? = null
                try {
                    result = EntityUtils.toString(entity)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val Img: GoodsImgEntity = Gson().fromJson(result, GoodsImgEntity::class.java)
                if (Img.code == 10000) {
                    val ivPath= BitmapUtil.decoderBase64File(Img.data.foreground_image)
                    mOssService?.asyncPutImage(Storage.getFileSufix(ivPath), ivPath)
                    *//*  val mMessage = Message()
                      mMessage.obj= ivPath
                      mMessage.what = 1
                      mHandler.sendMessage(mMessage)*//*
                }else{
                    // 加载失败
                    mAppViewModel.dismissLoading()

                    mPhotoCutEntity?.sdPath = ""
                    val mMessage = Message()
                    mMessage.obj= ""
                    mMessage.what = 1
                    mHandler.sendMessage(mMessage)
                }
            }

            *//* close resources *//*try {
            response!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
            try {
                httpClient.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }*/

    // 创建一个Handler
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg?.what) {
                1 -> {
                    mAppViewModel.dismissLoading()
                    Glide.with(this@CutPhotoActivity)
                        .load(msg.obj)
                        .into(mBinding?.cutPhotoView)
                    mPhotoEditAdapter?.notifyDataSetChanged()
                }
                // 这里的else相当于Java中switch的default;
                else -> {
                }
            }
        }
    }

    private fun initIv(select: Boolean) {
        mBinding.run {
            if (select) {
                oldIv.visibility = View.INVISIBLE
                newIv.visibility = View.VISIBLE
            } else {
                oldIv.visibility = View.VISIBLE
                newIv.visibility = View.INVISIBLE
            }
        }
    }
    // 控制那个显示
    private fun initRlv(select: Boolean) {
        mBinding.run {
            if (select) {
                llColorTV.visibility = View.VISIBLE
                cutrlv.visibility=View.GONE
                llBottom.visibility=View.GONE
                tvConfirm.visibility=View.GONE
                tvSave.visibility=View.VISIBLE
            } else {
                llColorTV.visibility = View.GONE
                cutrlv.visibility=View.VISIBLE
                llBottom.visibility=View.VISIBLE
                tvConfirm.visibility=View.VISIBLE
                tvSave.visibility=View.GONE
            }
            initColorTV()
        }
    }
    // 控制那个显示
    private fun initColorTV() {
        mBinding.run {
            if (typeColor) {
                llIvCV.setImageResource(R.mipmap.text_et_bg_yes)
                llIvET.setImageResource(R.mipmap.color_bg)
                llColor.visibility=View.VISIBLE
                llTV.visibility=View.GONE
                ivDv.visibility=View.GONE
            }else{
                llIvCV.setImageResource(R.mipmap.text_et_bg)
                llIvET.setImageResource(R.mipmap.color_bg_yes)
                llColor.visibility=View.GONE
                llTV.visibility=View.VISIBLE
                ivDv.visibility=View.VISIBLE

            }
        }
    }
    override fun onBankString(path: String, str: String) {
        mPhotoCutEntity?.let {
            mPhotoCutEntity?.sdPath =str
        }
        val mMessage = Message()
        mMessage.obj = str
        mMessage.what = 1
        mHandler.sendMessage(mMessage)
    }

    override fun filed() {

    }


}