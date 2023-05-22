package com.earth.angel.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.databinding.ActivityShareV2BindingImpl
import com.earth.angel.mine.MineModel
import com.earth.angel.share.adapter.GiftImageChangeAdapter
import com.earth.angel.share.adapter.ShareViewAdapter
import com.earth.angel.share.adapter.UserShareAdapter
import com.earth.angel.share.modle.ShareModel
import com.earth.angel.util.ScreenUtil
import com.earth.angel.view.CustomPopupWindow
import com.earth.angel.view.NoScrollRecyclerView
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.network.request.ShareRequest
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.SoftKeyboardUtils
import com.earth.libbase.view.ShapedImageView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_share_v2.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel


class ShareActivity : BaseActivity<ActivityShareV2BindingImpl>() {

    private var customPopupWindow: CustomPopupWindow? = null
    private var customBottomWindow: CustomPopupWindow? = null
    private var mUserMainAdapter: UserShareAdapter? = null
    private var list: ArrayList<GiftEntity>? = ArrayList()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mMineModel by viewModel<MineModel>()
    private val mShareModel by viewModel<ShareModel>()
    private var mShareID: String? = null
    private var tvLink: TextView? = null
    private var etInput: TextInputEditText? = null
    private var ivBank: ImageView? = null
    private var llShareSave: LinearLayout? = null
    private var mUserID: String? = null
    private var mUserNAme: String? = null
    private var mGiftUrl: String? = null

    private var listView: ArrayList<View>? = ArrayList()
    private var mShareViewAdapter: ShareViewAdapter? = null
    private var etShare: TextView? = null
    private var listImage: ArrayList<String>? = ArrayList()
    private var mGiftImageChangeAdapter: GiftImageChangeAdapter? = null
    private var rlvImageView: RecyclerView? = null


    override fun getLayoutId(): Int = R.layout.activity_share_v2

    override fun initActivity(savedInstanceState: Bundle?) {
        ActivityStackManager.addShareActivity(this@ShareActivity)
        showActionBar()
        mBinding?.run {
            initPopEdit()
            initBottom()
            mUserID=intent.getStringExtra("mUserID")
            mUserNAme=intent.getStringExtra("mUserNAme")
            mGiftUrl=intent.getStringExtra("GiftUrl")

            ivBank?.setOnClickListener {
                customPopupWindow?.dismiss()
                SoftKeyboardUtils.hideKeyboardFrom(this@ShareActivity, etInput)
            }
            var loadingMoreListener: RecyclerView.OnScrollListener =
                object : RecyclerView.OnScrollListener() {
                    //到达顶部和底部监听
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        // OnScrollListener.SCROLL_STATE_IDLE; //停止滑动状态
                        // 记录当前滑动状态
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) { //当前状态为停止滑动

                            if (!rlvView.canScrollVertically(1)) { // 到达底部
                                llShareViewV2.visibility=View.GONE
                            } else if (!rlvView.canScrollVertically(-1)) { // 到达顶部
                            }else{
                                llShareViewV2.visibility=View.VISIBLE
                            }
                        }
                    }
                }
            val layoutPager1 = LinearLayoutManager(this@ShareActivity)
            rlvView.layoutManager = layoutPager1
            mShareViewAdapter=ShareViewAdapter(this@ShareActivity,listView,upDade = {
                rlvView.scrollToPosition(1)
            })
            rlvView?.adapter=mShareViewAdapter

            rlvView.addOnScrollListener(loadingMoreListener);

            tvShareLeftToolV2.setOnClickListener {
                ActivityStackManager.finishSahreAll()

            //    setResult(102)
            //    finish()
            }
            ivShareRightToolV2.visibility=View.GONE
            ivShareRightToolV2.setOnClickListener {
                ActivityStackManager.finishSahreAll()
            }
            llShareSave?.setOnClickListener {
                var str=etInput?.text.toString()
                if(str.length>40){
                    toast("Please enter a maximum of 40 characters")
                    return@setOnClickListener
                }
                customPopupWindow?.dismiss()
                etShare?.text = etInput?.text.toString()
                shareVersionUpdate(etInput?.text.toString())
                SoftKeyboardUtils.hideKeyboardFrom(this@ShareActivity, etInput)
            }
            llShareViewV2.setOnClickListener {
                customBottomWindow?.showAtLocation(
                    llShareViewV2,
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0
                )
            }
        }
        mAppViewModel.showLoadingProgress.observe(this@ShareActivity, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })
        onChatIntent()
        shareVersionAdd()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            ActivityStackManager.finishSahreAll()
        }
        return false

    }
    private fun initPopEdit() {
        customPopupWindow = CustomPopupWindow.Builder(this@ShareActivity)
            .setContentView(R.layout.pop_share_edit)
            .setwidth(WindowManager.LayoutParams.MATCH_PARENT)
            .setheight(ScreenUtil.getScreenHeight())
            .build()
        customPopupWindow?.fullWindow(false)
        etInput = customPopupWindow?.getItemView(R.id.etContent) as TextInputEditText
        ivBank = customPopupWindow?.getItemView(R.id.ivBank) as ImageView
        llShareSave = customPopupWindow?.getItemView(R.id.llShareSave) as LinearLayout
    }

    private fun initBottom() {
        customBottomWindow = CustomPopupWindow.Builder(this@ShareActivity)
            .setContentView(R.layout.pop_share_bottom)
            .setwidth(LinearLayout.LayoutParams.MATCH_PARENT)
            .setheight(LinearLayout.LayoutParams.WRAP_CONTENT)
            .build()
        val llCopyLink = customBottomWindow?.getItemView(R.id.llCopyLink) as LinearLayout
        tvLink = customBottomWindow?.getItemView(R.id.tvLink) as TextView

        val llShareVia = customBottomWindow?.getItemView(R.id.llShareVia) as LinearLayout
        val ivDelete = customBottomWindow?.getItemView(R.id.ivDelete) as ImageView
        llCopyLink.setOnClickListener {
            val cm = baseContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText(null, tvLink?.text.toString()))
            toast(getString(R.string.lab_Success))
        }
        llShareVia.setOnClickListener {
           // ShareUtil.shareText(this@ShareActivity, tvLink?.text.toString(), "EarthAngel")
        }
        ivDelete.setOnClickListener {
            customBottomWindow?.dismiss()
        }
    }


    private fun shareVersionUpdate(sendStr: String) {
        mShareID?.let {
            launch {
                mShareModel.shareVersionUpdate(
                    ShareRequest(
                        id = it.toLong(),
                        textContent = sendStr
                    )
                ).catch { }.collectLatest {
                    if (it.isOk(this@ShareActivity)) {
                        mShareID = it.data?.shareVersion?.id.toString()
                        tvLink?.text = it.data?.url
                    }
                }
            }
        }
    }

    private fun shareVersionAdd() {
        mUserNAme?.let {
            var sendStr = "Personalized eco gifts form $it"
            launch {
                mShareModel.shareVersionAdd(ShareRequest(userId = mUserID!!.toLong(),textContent = sendStr)).catch { }
                    .collectLatest {
                        if (it.isOk(this@ShareActivity)) {
                            mShareID = it.data?.shareVersion?.id.toString()
                            tvLink?.text = it.data?.url
                            etShare?.text = sendStr
                        }
                    }
            }
        }
    }

    private fun onChatIntent() {
         getUser()
    }
    private fun getUser() {
        EarthAngelApp.myProfileEntity?.id?.let {
            launch {
                mMineModel.tradablePageList(1, 4, it.toLong())
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .catch { }
                    .collectLatest {
                        if (it.isOk(this@ShareActivity)) {
                            it.data?.let {
                                list?.addAll(it)

                                mUserMainAdapter?.notifyDataSetChanged()
                                val inflater1 = LayoutInflater.from(this@ShareActivity)
                                val  view1 = inflater1.inflate(R.layout.fragment_share_edit, null)
                                view1?.run {
                                     etShare = this.findViewById(R.id.etShare)
                                    rlvImageView = this.findViewById(R.id.rlvImageView)
                                    initImage(list)
                                    val ivShareDown: ImageView  = this.findViewById(R.id.ivShareDown)
                                    //setFlickerAnimation(ivShareDown)
                                    mUserNAme?.let {
                                        var sendStr = "Personalized eco gifts form $it"
                                        etShare?.text=sendStr
                                    }
                                    etShare?.setOnClickListener {
                                        customPopupWindow?.showAtLocation(
                                            etShare,
                                            0, 0, 0
                                        )
                                        var str = etShare?.text.toString()
                                        etInput?.setText(str)
                                        etInput?.setSelection(str.length)
                                        SoftKeyboardUtils.showSoftInputFromWindow(this@ShareActivity, etInput)
                                    }
                                }
                                val inflater2 = LayoutInflater.from(this@ShareActivity)
                                val  view2 = inflater2.inflate(R.layout.layout_share_user, null)
                                listView?.add(view1)
                                listView?.add(view2)
                                view2?.run {
                                    val nikeName = EarthAngelApp.myProfileEntity?.nickName
                                    val shareViewTv: TextView = this.findViewById(R.id.shareViewTv)


                                    shareViewTv.text = nikeName
                                    val tvShareTitleCenter: TextView = this.findViewById(R.id.tvShareTitleCenter)


                                    tvShareTitleCenter.text = "$nikeName's PERSONALIZED ECO GIFTS"
                                    val mUserId = EarthAngelApp.myProfileEntity?.id
                                    val tvShareViewNum: TextView = this.findViewById(R.id.tvShareViewNum)

                                    tvShareViewNum.text = "ID :$mUserId"
                                    val shareViewiv: ShapedImageView = this.findViewById(R.id.ShareViewiv)

                                    EarthAngelApp.myProfileEntity?.headImgUrl?.let {
                                        Glide.with(this@ShareActivity)
                                            .load(it)
                                            .placeholder(R.mipmap.head_common)
                                            .into(shareViewiv)
                                    }

                                    val shareViewXlv: NoScrollRecyclerView = this.findViewById(R.id.shareViewXlv)
                                    val llShareAddFriend: LinearLayout = this.findViewById(R.id.llShareAddFriend)
                                    llShareAddFriend.visibility = View.INVISIBLE
                                    var gridLayoutManager = GridLayoutManager(this@ShareActivity, 2)
                                    mUserMainAdapter = UserShareAdapter(this@ShareActivity, list)
                                    shareViewXlv.layoutManager = gridLayoutManager
                                    shareViewXlv.adapter = mUserMainAdapter
                                }
                                mShareViewAdapter?.notifyDataSetChanged()
                            }
                        }
                    }
            }
        }
    }

    //实现图片闪烁效果
    private fun setFlickerAnimation(iv_chat_head: ImageView) {
        val animation: Animation =
            AlphaAnimation(1f, 0f) // Change alpha from fully visible to invisible
        animation.duration = 500 // duration - half a second
        animation.interpolator = LinearInterpolator() // do not alter animation rate
        animation.repeatCount = Animation.INFINITE // Repeat animation infinitely
        animation.repeatMode = Animation.REVERSE //
        iv_chat_head.animation = animation
    }
    private fun initImage(listGiftEntity: ArrayList<GiftEntity>?){
        listGiftEntity?.let {
            for(item in it ){
                if (!item.pictureResources[0].pictureUrl.isNullOrEmpty()) {
                    listImage?.add(item.pictureResources[0].pictureUrl)
                }
            }
            val layoutManager1: LinearLayoutManager =
                object : LinearLayoutManager(this@ShareActivity, HORIZONTAL, false) {
                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
            mGiftImageChangeAdapter=GiftImageChangeAdapter(this@ShareActivity,listImage)
            rlvImageView?.layoutManager=layoutManager1
            rlvImageView?.adapter=mGiftImageChangeAdapter
            if(listImage?.size!!>1){
                handler.postDelayed(runnable, 2000);//每两秒执行一次runnable.
            }
        }
    }
    private  var position = 0
    var handler = Handler()
    var runnable: Runnable = object : Runnable {
        override fun run() {
            listImage?.let {
                if(it.size>0){
                    if (position < it.size) {
                        position++
                        rlvImageView?.scrollToPosition(position)
                    } else {
                        position = 0
                        rlvImageView?.scrollToPosition(position)
                    }
                    handler.postDelayed(this, 2000)
                }
            }

        }
    }



    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)

    }

}