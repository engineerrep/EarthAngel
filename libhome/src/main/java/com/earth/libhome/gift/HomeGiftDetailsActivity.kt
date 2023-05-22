package com.earth.libhome.gift

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.earth.libbase.base.*
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.dialog.BaseDialogCommon
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.dialog.PointDialogCommon
import com.earth.libbase.entity.MessageListEntity
import com.earth.libbase.entity.SingEntity
import com.earth.libbase.event.GiftUpdateEvent
import com.earth.libbase.network.request.*
import com.earth.libbase.util.*
import com.earth.libbase.view.IndefinitePagerIndicator
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R
import com.earth.libhome.adapter.HomeGiftDetailAdapter
import com.earth.libhome.adapter.HomeGiftDetailsMessageAdapter
import com.earth.libhome.databinding.ActivityHomeGiftdetailsBinding
import com.earth.libhome.ui.*
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


@ExperimentalCoroutinesApi
@Route(path = Constance.HomeGiftDetailsActivityURL)
class HomeGiftDetailsActivity : BaseActivity<ActivityHomeGiftdetailsBinding>() {

    @Autowired(name = "id")
    @JvmField
    var id: String? = null

    @Autowired(name = "userid")
    @JvmField
    var userid: String? = null

    @Autowired(name = "success")
    @JvmField
    var success: String? = null

    private val mHomeModel by viewModel<HomeModle>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var imgAdapter: HomeGiftDetailAdapter? = null
    private var layoutPager: LinearLayoutManager? = null
    private var listPhotos = ArrayList<String>()
    private var snapHelper: PagerSnapHelper? = null
    private var collect: Boolean? = null
    private var mPocket: Boolean? = null
    private var mGiftId: String? = null
    private var head: View? = null

    private var mLibHomell: LinearLayout? = null
    private var mLibHomeUserSave: ImageView? = null
    private var mLibHomeGiftRlv: RecyclerView? = null
    private var mLibHomeIfpi: IndefinitePagerIndicator? = null
    private var mLibHomeGiftSaveImage: ImageView? = null
    private var mLibHomecl: ConstraintLayout? = null
    private var mLibHomeGiftTitle: TextView? = null
    private var mtvPageNum: TextView? = null

    private var mLibHomeMessageEmpty: ImageView? = null
    private var mLibHomeAddBag: TextView? = null
    private var mLibHomeMessage: TextView? = null
    private var mProductTimeTv: TextView? = null
    private var mProductConditionTv: TextView? = null
    private var mProductCategoryTv: TextView? = null
    private var mLibHomeTitleDeLy: Layer? = null
    private var mLibHomeDetailstv: TextView? = null
    private var mivlibHomeGiftImage: ShapedImageView? = null
    private var mivLibHomeName: TextView? = null
    private var mivLibHomedIntroduce: TextView? = null
    private var mHomeGiftMessage: TextView? = null
    private var mHomeGiftMessageHead: ShapedImageView? = null
    private var mHomeGiftMessageLL: LinearLayout? = null
    private var viewMessageLine: View? = null
    private var mProductCityLL: LinearLayout? = null
    private var mProductCityTv: TextView? = null
    private var footerView: View? = null
    private var mLibHomeGiftShareLL: LinearLayout? = null
    private var mMessageAdapter: HomeGiftDetailsMessageAdapter? = null
    private var listMessage = ArrayList<MessageListEntity>()
    private var pageNum: Int = 1
    private var mHomePopPostUtil: HomePopPostUtil? = null
    private var titleHight: Int = 0
    private var top1Hight: Int = 0
    private var top2Hight: Int = 0


    override fun getLayoutId(): Int = R.layout.activity_home_giftdetails

    override fun initActivity(savedInstanceState: Bundle?) {
        fitSystemBar()
        ARouter.getInstance().inject(this@HomeGiftDetailsActivity)
        mBinding.run {
            libHomeTopTV.height = BaseScreenUtil.getActivityMessageHeight()
            head = layoutInflater.inflate(R.layout.head_homegiftdetails, null)
            footerView = layoutInflater.inflate(R.layout.footer_homegiftdetails, null)
            footerView?.let {
                mLibHomeGiftShareLL = it.findViewById(R.id.LibHomeGiftShareLL)
            }

            libHomeTopTV.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                top1Hight=v.height
            }
            libHomeTop.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                top2Hight=v.height
            }
            libHomeGiftDetailsRlv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var totalScrollY = 0 // 列表滑动距离
                @SuppressLint("Range")
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    titleHight= top1Hight+top2Hight
                    Log.e("titleHight","titleHight"+titleHight)

                    totalScrollY += dy
                    Log.e("totalScrollY","totalScrollY"+totalScrollY)
                    if (totalScrollY <= 0) {
                        libHomeTop.alpha= 0F
                        libHomeTopTV.alpha= 0F
                      //  bgView.getBackground().mutate().setAlpha(255);
                    } else if (totalScrollY <= titleHight) {
                        libHomeTop.alpha= (255f - 255.0f / titleHight * totalScrollY)
                        libHomeTopTV.alpha= (255f - 255.0f / titleHight * totalScrollY)
                        Log.e("alpha","alpha"+(255f - 255.0f / titleHight * totalScrollY))
                        //   bgView.getBackground().mutate().setAlpha((Int) (255f - 255.0f / 120 * totalScrollY));
                    } else {
                       // bgView.getBackground().mutate().setAlpha(0);
                        libHomeTop.alpha= 255F
                        libHomeTopTV.alpha= 255F
                    }
                }
            })
            head?.let {
                mLibHomell = it.findViewById(R.id.LibHomell)
                mLibHomeUserSave = it.findViewById(R.id.LibHomeUserSave)
                mLibHomeGiftRlv = it.findViewById(R.id.LibHomeGiftRlv)
                mLibHomeIfpi = it.findViewById(R.id.LibHomeIfpi)
                mLibHomeGiftSaveImage = it.findViewById(R.id.LibHomeGiftSaveImage)
                mLibHomecl = it.findViewById(R.id.LibHomecl)
                mLibHomeGiftTitle = it.findViewById(R.id.LibHomeGiftTitle)
                mLibHomeMessageEmpty = it.findViewById(R.id.LibHomeMessageEmpty)
                mLibHomeAddBag = it.findViewById(R.id.LibHomeAddBag)
                mLibHomeMessage = it.findViewById(R.id.LibHomeMessage)
                mProductTimeTv = it.findViewById(R.id.ProductTimeTv)
                mProductConditionTv = it.findViewById(R.id.ProductConditionTv)
                mProductCategoryTv = it.findViewById(R.id.ProductCategoryTv)
                mLibHomeTitleDeLy = it.findViewById(R.id.LibHomeTitleDeLy)
                mLibHomeDetailstv = it.findViewById(R.id.LibHomeDetailstv)
                mivlibHomeGiftImage = it.findViewById(R.id.ivlibHomeGiftImage)
                mivLibHomeName = it.findViewById(R.id.ivLibHomeName)
                mivLibHomedIntroduce = it.findViewById(R.id.ivLibHomedIntroduce)
                mHomeGiftMessage = it.findViewById(R.id.HomeGiftMessage)
                mHomeGiftMessageHead = it.findViewById(R.id.HomeGiftMessageHead)
                mHomeGiftMessageLL = it.findViewById(R.id.HomeGiftMessageLL)
                viewMessageLine = it.findViewById(R.id.viewMessageLine)

                mProductCityLL = it.findViewById(R.id.ProductCityLL)
                mProductCityTv = it.findViewById(R.id.ProductCityTv)
                mtvPageNum=it.findViewById(R.id.tvPageNum)
                LibHomeUserArrowRight.setOnClickListener {
                    finish()
                }
                BaseApplication.myBaseUser?.let {
                    if (it.userId == userid) {
                        mLibHomell?.visibility = View.GONE
                        mLibHomeUserSave?.visibility = View.GONE
                    } else {
                        mLibHomell?.visibility = View.VISIBLE
                        mLibHomeUserSave?.visibility = View.VISIBLE
                    }
                    it.headImgUrl?.let {
                        Glide.with(this@HomeGiftDetailsActivity)
                            .load(it)
                            .placeholder(R.mipmap.base_comm_head)
                            .into(mHomeGiftMessageHead!!)
                    }
                }

                if (PreferencesHelper.getLocationName(this@HomeGiftDetailsActivity)
                        .isNullOrEmpty()
                ) {
                    mProductCityLL?.visibility = View.GONE
                } else {
                    mProductCityTv?.text =
                        PreferencesHelper.getLocationName(this@HomeGiftDetailsActivity)
                    mProductCityLL?.visibility = View.VISIBLE
                }
                imgAdapter =
                    HomeGiftDetailAdapter(this@HomeGiftDetailsActivity, listPhotos, upDade = {
                        listPhotos.remove(it)
                        imgAdapter?.notifyDataSetChanged()
                    })
                layoutPager = LinearLayoutManager(
                    this@HomeGiftDetailsActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                mLibHomeGiftRlv?.layoutManager = layoutPager
                mLibHomeGiftRlv?.adapter = imgAdapter
                mLibHomeIfpi?.attachToRecyclerView(mLibHomeGiftRlv)
                imgAdapter?.setOnItemClickListener { _, _, position ->
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.GiftPhotoActivity)
                            .withInt("position", position)
                            .withStringArrayList("ImgList", listPhotos)
                            .navigation()
                    }
                }
                snapHelper = PagerSnapHelper()
                snapHelper?.attachToRecyclerView(mLibHomeGiftRlv)
                mLibHomeGiftSaveImage?.setOnClickListener {
                    collect?.let {
                        if (it) {
                            id?.let {
                                //     goodsRelationDelete(it)
                            }
                        } else {
                            id?.let {
                                goodsRelationAdd(it)
                            }
                        }
                    }
                }
                mLibHomecl?.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        if (!userid.isNullOrEmpty()) {
                            ARouter.getInstance().build(Constance.LibMineUserActivityURL)
                                .withString("userId", userid)
                                .navigation()
                        }
                    }
                }
                mHomeGiftMessageLL?.setOnClickListener {
                    SoftKeyboardUtils.showSoftInputFromWindow(
                        this@HomeGiftDetailsActivity,
                        libHomeGiftDetailsEt
                    )
                }


                LibHomeGiftPakgeIv.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance().build(Constance.HomePackingBagActivityURL)
                            .navigation()
                    }
                }
                LibHomeGiftShareIv.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        id?.let {
                            ShareUtil.shareGiftText(
                                this@HomeGiftDetailsActivity,
                                id
                            )
                            firstShare()
                        }
                    }
                }
                LibHomeGiftMoreIv.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        val mHomePopMoreUtil = HomePopMoreUtil()
                        mHomePopMoreUtil.showPopupWindow(
                            this@HomeGiftDetailsActivity,
                            libHomeGiftDetailscl!!,
                            object : HomeOnItemClick {
                                override fun onItemClick() {
                                }
                                override fun onItemDeleteClick() {
                                    mGiftId?.let {
                                        giftDelete(it)
                                    }
                                }
                                override fun onItemOffClick() {
                                    mGiftId?.let {
                                        giftUnload(it)
                                    }
                                }
                                override fun onItemDelete(str: BaseGiftEntity?) {
                                }
                            })
                    }
                }

                mLibHomeUserSave?.setOnClickListener {
                    concernedAdd()
                }

            }
            mMessageAdapter =
                HomeGiftDetailsMessageAdapter(this@HomeGiftDetailsActivity, listMessage)
            var mLinearLayoutManager = LinearLayoutManager(
                this@HomeGiftDetailsActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            libHomeGiftDetailsRlv.layoutManager = mLinearLayoutManager
            libHomeGiftDetailsRlv.adapter = mMessageAdapter

            mMessageAdapter?.setOnItemLongClickListener(object : OnItemLongClickListener {
                override fun onItemLongClick(
                    adapter: BaseQuickAdapter<*, *>,
                    view: View,
                    position: Int
                ): Boolean {
                    var str = ""
                    var strTitle = ""
                    if (BaseApplication.myBaseUser?.userId == listMessage[position].userId.toString()) {
                        str = getString(R.string.base_tips_comment)
                        strTitle = getString(R.string.base_Delete_Comment)
                    } else {
                        str = getString(R.string.base_tips_Report)
                        strTitle = getString(R.string.base_tips_Report)
                    }
                    if (BaseDateUtils.isFastClick()) {
                        val mHomePopMessageUtil = HomePopMessageUtil()
                        mHomePopMessageUtil.showPopupWindow(
                            this@HomeGiftDetailsActivity,
                            LibHomeGiftMoreIv!!,
                            object : HomeOnItemClick {
                                override fun onItemClick() {
                                }

                                override fun onItemDeleteClick() {

                                    val blockDialog = BaseDialogCommon(
                                        title = str,
                                        onBlockClick = {
                                            if (BaseApplication.myBaseUser?.userId == listMessage[position].userId.toString()) {
                                                deleteMessage(listMessage[position])
                                            } else {
                                                reportMessage(listMessage[position])
                                            }
                                        })
                                    blockDialog.show(supportFragmentManager, str)

                                }

                                override fun onItemOffClick() {
                                }

                                override fun onItemDelete(str: BaseGiftEntity?) {
                                }
                            }, strTitle
                        )
                    }
                    return true
                }
            })



            head?.let {
                mMessageAdapter?.addHeaderView(it)
            }
            footerView?.let {
                mMessageAdapter?.addFooterView(it)
            }
            libHomeGiftDetailsSend.setOnClickListener {
                checkSend()
            }
            libHomeGiftDetailsEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        checkSend()
                    }
                   return true
                }
            })
            libHomeSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum = 1
                    libHomeSrl.setEnableLoadMore(true)
                    listMessage.clear()
                    getMessage()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getMessage()
                }
            })

            val softKeyBroadManager =
                SoftKeyBroadManager(root)
            softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener)


            root.viewTreeObserver.addOnGlobalLayoutListener(OnGlobalLayoutListener {
                val r = Rect()
                root.getWindowVisibleDisplayFrame(r)
                val displayHeight: Int = r.bottom - r.top
                val parentHeight: Int = root.height
                val softKeyHeight = parentHeight - displayHeight
                val lp: ConstraintLayout.LayoutParams =
                    libHomeGiftDetailsll.layoutParams as ConstraintLayout.LayoutParams
                lp.setMargins(
                    0,
                    0,
                    0,
                    softKeyHeight - BaseScreenUtil.getActivityMessageHeight()
                )
                libHomeGiftDetailsll.layoutParams = lp
            })
            
            mAppViewModel?.showLoadingProgress.observe(this@HomeGiftDetailsActivity, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
            getGiftList()
            getUser()
            getMessage()
        }
    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            success?.let {

                if (mHomePopPostUtil == null) {
                    mHomePopPostUtil = HomePopPostUtil()
                    mHomePopPostUtil?.showPopupWindow(
                        this@HomeGiftDetailsActivity,
                        mBinding.libHomeGiftDetailscl!!,
                        object : HomeOnItemClick {
                            override fun onItemClick() {}
                            override fun onItemDeleteClick() {
                                // Share Now
                                if (BaseDateUtils.isFastClick()) {
                                    id?.let {
                                        ShareUtil.shareGiftText(
                                            this@HomeGiftDetailsActivity,
                                            id
                                        )
                                        firstShare()
                                    }
                                }
                            }
                            override fun onItemOffClick() {
                                //Publish Again
                                if (BaseDateUtils.isFastClick()) {
                                    ARouter.getInstance().build(Constance.PhotoPostActivityURL)
                                        .navigation()
                                }
                            }
                            override fun onItemDelete(str: BaseGiftEntity?) {
                            }
                        },it)


                }


            }

        }
        super.onWindowFocusChanged(hasFocus)
    }

    var softKeyboardStateListener: SoftKeyBroadManager.SoftKeyboardStateListener = object :
        SoftKeyBroadManager.SoftKeyboardStateListener {
        override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
            //   etPut.requestLayout()
            mBinding.libHomeGiftDetailsll.visibility = View.VISIBLE
            /*     mBinding.run {
                     val lp: ConstraintLayout.LayoutParams =
                         libHomeGiftDetailsll.layoutParams as ConstraintLayout.LayoutParams
                     val might = keyboardHeightInPx -libHomeGiftDetailsll.height
                     lp.setMargins(
                         0,
                         0,
                         0,
                         might
                     )
                     libHomeGiftDetailsll.layoutParams = lp
                 }*/
        }

        override fun onSoftKeyboardClosed() {
            //    etPut.requestLayout()
            mBinding.libHomeGiftDetailsll.visibility = View.INVISIBLE

        }
    }

    private fun getMessage() {
        launch {
            id?.let {
                mHomeModel.recordMessagePageList(
                    CommentRequest(
                        pageNum = pageNum, pageSize = 10, releaseRecordId = it.toLong(), userId =
                        BaseApplication.myBaseUser?.userId?.toLong()
                    )
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@HomeGiftDetailsActivity)) {
                            it.data?.list?.let {
                                listMessage.addAll(it)
                                mMessageAdapter?.notifyDataSetChanged()
                            }
                            if (listMessage.size == it.data?.total) {
                                mBinding?.libHomeSrl?.setEnableLoadMore(false)
                            }
                            if(listMessage.size==0){
                                mHomeGiftMessage?.text = getString(R.string.base_Comment)
                                viewMessageLine?.visibility=View.GONE
                            }else{
                                mHomeGiftMessage?.text = getString(R.string.base_Comment)+" "+ listMessage.size
                                viewMessageLine?.visibility=View.VISIBLE
                            }
                            mBinding.libHomeSrl.let { libHomeSrl ->
                                libHomeSrl.finishRefresh(true)
                                libHomeSrl.finishLoadMore(true)
                            }
                        }
                    }
            }
        }
    }

    private fun deleteMessage(msg: MessageListEntity) {
        launch {
            id?.let {
                mHomeModel.recordMessageDelete(
                    MessageRequest(messageId = msg.messageId)
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@HomeGiftDetailsActivity)) {
                            toast(getString(R.string.base_Delete_success))
                            listMessage.remove(msg)
                            mMessageAdapter?.notifyDataSetChanged()
                        }
                    }
            }
        }
    }

    private fun reportMessage(msg: MessageListEntity) {
        launch {
            id?.let {
                mHomeModel.reportMessage(
                    MessageRequest(messageId = msg.messageId, description = msg.msg)
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@HomeGiftDetailsActivity)) {
                            toast(getString(R.string.base_Report_success))
                        }
                    }
            }
        }
    }
    private fun checkSend() {
        var message = mBinding.libHomeGiftDetailsEt.text.toString().trim()
        if(message.isNullOrEmpty()){
            toast(getString(R.string.label_Comment_Length))
            return
        }
        sendMessage(message)
    }


    private fun sendMessage(msg: String) {
        launch {
            id?.let {
                mHomeModel.recordMessageAdd(
                    RecordMessageAddRequest(
                        msg = msg,
                        releaseRecordId = it.toLong()
                    )
                )
                    .onStart {
                        mAppViewModel.showLoading()
                    }.catch {
                    }.onCompletion {
                        mAppViewModel.dismissLoading()
                    }.collectLatest {
                        if (it.isOk(this@HomeGiftDetailsActivity)) {
                            toast(getString(R.string.base_Comment_success))
                            mBinding.libHomeGiftDetailsEt.setText("")
                            it.data?.let {
                                listMessage.add(it)
                                mMessageAdapter?.notifyDataSetChanged()
                                SoftKeyboardUtils.closeInoutDecorView(this@HomeGiftDetailsActivity)
                                mBinding.libHomeGiftDetailsRlv.scrollToPosition(listMessage.size)
                                if(listMessage.size==0){
                                    mHomeGiftMessage?.text = getString(R.string.base_Comment)
                                }else{
                                    mHomeGiftMessage?.text = getString(R.string.base_Comment)+" "+ listMessage.size
                                }
                            }
                        }
                    }
            }
        }
    }


    private fun getGiftList() {
        launch {
            id?.let {
                mHomeModel.releaseGistOne(
                    CommentRequest(
                        releaseRecordId = it.toLong()
                    )
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@HomeGiftDetailsActivity)) {
                            it.data?.pictureResources?.let {
                                listPhotos.addAll(it)
                                imgAdapter?.notifyDataSetChanged()
                            }
                            if(listPhotos.size>1){
                                mLibHomeIfpi?.visibility=View.VISIBLE
                            }else{
                                mLibHomeIfpi?.visibility=View.INVISIBLE
                            }

                            it.data?.isLike?.let { itBoo ->
                                collect = itBoo
                                if (itBoo) {
                                    mLibHomeGiftSaveImage?.setImageResource(R.mipmap.user_saved)
                                } else {
                                    mLibHomeGiftSaveImage?.setImageResource(R.mipmap.user_save)
                                }
                            }
                            it.data?.itemTitle?.let {
                                mLibHomeGiftTitle?.text = it
                            }
                            it.data?.score?.let {
                                if(it==0){
                                    mtvPageNum?.text = getString(R.string.label_Free)
                                }else{
                                    mtvPageNum?.text = it.toString()
                                }
                            }
                            it.data?.releaseStatus?.let {
                                when (it) {
                                    BaseGiftEntity.EXCHANGED -> {
                                        mLibHomeMessageEmpty?.visibility = View.VISIBLE
                                        mLibHomeMessageEmpty?.setImageResource(R.mipmap.detalis_empty)
                                    }
                                    BaseGiftEntity.DELETE -> {
                                        mLibHomeMessageEmpty?.visibility = View.VISIBLE
                                        mLibHomeMessageEmpty?.setImageResource(R.mipmap.detalis_empty_removed)
                                    }
                                    BaseGiftEntity.REMOVE -> {
                                        mLibHomeMessageEmpty?.visibility = View.VISIBLE
                                        mLibHomeMessageEmpty?.setImageResource(R.mipmap.detalis_empty_removed)
                                    }
                                    else -> {
                                        mLibHomeMessageEmpty?.visibility = View.GONE
                                    }
                                }
                            }
                            it.data?.isPoket?.let {
                                mPocket = it
                                if (it) {
                                    mLibHomeAddBag?.setBackgroundResource(R.drawable.bg_corners_shopp_28_side)
                                    mLibHomeAddBag?.text = getString(R.string.base_Go_to_cart)
                                    mLibHomeAddBag?.setTextColor(
                                        ContextCompat.getColor(
                                            this@HomeGiftDetailsActivity,
                                            R.color.BaseThemColor
                                        )
                                    )
                                } else {
                                    mLibHomeAddBag?.setBackgroundResource(R.drawable.bg_corners_shopp_28_side)
                                    mLibHomeAddBag?.text = getString(R.string.base_Add_to_cart)
                                    mLibHomeAddBag?.setTextColor(
                                        ContextCompat.getColor(
                                            this@HomeGiftDetailsActivity,
                                            R.color.BaseThemColor
                                        )
                                    )
                                }
                            }
                            it.data?.releaseStatus?.let {
                                when (it) {
                                    0 -> {
                                        mLibHomeAddBag?.visibility = View.VISIBLE
                                        mLibHomeMessage?.visibility = View.VISIBLE
                                        mLibHomeMessageEmpty?.visibility = View.GONE
                                        BaseApplication.myBaseUser?.let {
                                            if (it.userId == userid) {
                                            mBinding.LibHomeDetalisEtCl.visibility=View.VISIBLE
                                            }
                                        }
                                    }
                                    2 -> {
                                        BaseApplication.myBaseUser?.let {
                                            if (it.userId == userid) {
                                                mBinding.LibHomeDetalisDElCl.visibility=View.VISIBLE
                                            }
                                        }
                                    }

                                    else -> {
                                        mLibHomeAddBag?.visibility = View.GONE
                                        mLibHomeMessage?.visibility = View.GONE
                                        mLibHomeMessageEmpty?.visibility = View.VISIBLE
                                    }
                                }
                            }
                            it.data?.let { baseIt ->
                                mGiftId = baseIt.releaseRecordId
                                if (baseIt.releaseStatus == BaseGiftEntity.DOING) {
                                    mLibHomeMessage?.visibility = View.VISIBLE
                                    mLibHomeAddBag?.visibility = View.VISIBLE
                                    mLibHomeMessageEmpty?.visibility = View.GONE
                                    mLibHomell?.visibility = View.VISIBLE
                                } else {
                                    mLibHomell?.visibility = View.GONE
                                    mLibHomeMessage?.visibility = View.GONE
                                    mLibHomeAddBag?.visibility = View.GONE
                                    mLibHomeMessageEmpty?.visibility = View.VISIBLE
                                }
                                mBinding?.LibHomeDetalisEtIV?.setOnClickListener {
                                    if (BaseDateUtils.isFastClick()) {
                                        ARouter.getInstance().build(Constance.PhotoPostActivityURL)
                                            .withString("id", id)
                                            .navigation()
                                        finish()
                                    }
                                }
                                mBinding.LibHomeDetalisShareIV.setOnClickListener {
                                    if (BaseDateUtils.isFastClick()) {
                                        ShareUtil.shareGiftText(
                                            this@HomeGiftDetailsActivity,
                                            baseIt.releaseRecordId
                                        )
                                        firstShare()
                                    }
                                }
                                if (BaseApplication.myBaseUser?.userId == baseIt.userId) {
                                    // 自己的物品
                                    mLibHomeGiftSaveImage?.visibility=View.GONE
                                    mLibHomeMessage?.visibility = View.GONE
                                    mLibHomeAddBag?.visibility = View.GONE
                                    when (baseIt.releaseStatus) {
                                        BaseGiftEntity.DOING -> {
                                            // 进行中的物品
                                            mBinding.LibHomeDetalisEtCl.visibility = View.VISIBLE
                                            mBinding.LibHomeDetalisDElCl.visibility = View.GONE
                                            mLibHomeGiftShareLL?.visibility = View.GONE
                                            mBinding.LibHomeGiftMoreIv.visibility = View.VISIBLE
                                        }
                                        BaseGiftEntity.REMOVE -> {
                                            // 下架的物品
                                            mBinding.LibHomeDetalisEtCl.visibility = View.GONE
                                            mBinding.LibHomeDetalisDElCl.visibility = View.VISIBLE
                                            mLibHomeGiftShareLL?.visibility = View.GONE
                                            mBinding.LibHomeGiftMoreIv.visibility = View.GONE
                                        }
                                    }
                                    mBinding.LibHomeDetalisOffIV.setOnClickListener {
                                        var blockDialog = BaseDialogCommon(
                                            title = "Confirm Off This Product?",
                                            onBlockClick = {
                                                giftUnload(baseIt.releaseRecordId)
                                            })
                                        blockDialog.show(
                                            supportFragmentManager, ""
                                        )
                                    }
                                    mBinding.LibHomeDetalisRelaunchTV.setOnClickListener {
                                        val blockDialog = BaseDialogCommon(
                                            title = "Confirm Relaunch",
                                            onBlockClick = {
                                                giftRelist(baseIt.releaseRecordId)
                                            })
                                        blockDialog.show(
                                            supportFragmentManager, ""
                                        )
                                    }
                                    mBinding.LibHomeDetalisDelIV.setOnClickListener {
                                        var blockDialog = BaseDialogCommon(
                                            title = "Do You Confirm To Delete This Product?",
                                            onBlockClick = {
                                                giftDelete(baseIt.releaseRecordId)
                                            })
                                        blockDialog.show(
                                            supportFragmentManager, ""
                                        )
                                    }
                                } else {
                                    mLibHomeGiftSaveImage?.visibility=View.VISIBLE
                                    mBinding?.LibHomeGiftMoreIv?.visibility = View.GONE
                                    mLibHomeMessage?.visibility = View.VISIBLE
                                    mLibHomeAddBag?.visibility = View.VISIBLE
                                    mLibHomeAddBag?.setOnClickListener {
                                        if (BaseDateUtils.isFastClick()) {
                                            mPocket?.let {
                                                if (it) {
                                                    ARouter.getInstance()
                                                        .build(Constance.HomePackingBagActivityURL)
                                                        .navigation()
                                                } else {
                                                    poketAdd(baseIt.releaseRecordId)
                                                }
                                            }
                                        }
                                    }
                                    mBinding?.LibHomeDetalisEtCl?.visibility = View.GONE
                                    mBinding?.LibHomeDetalisDElCl?.visibility = View.GONE
                                    mLibHomeGiftShareLL?.visibility = View.VISIBLE
                                    mLibHomeGiftShareLL?.setOnClickListener {
                                        if (BaseDateUtils.isFastClick()) {
                                            ShareUtil.shareGiftText(
                                                this@HomeGiftDetailsActivity,
                                                baseIt.releaseRecordId
                                            )
                                            firstShare()
                                        }
                                    }


                                }
                                mProductTimeTv?.text =
                                        //  BaseDateUtils.getPastTime(baseIt.createTime.toLong())
                                    ImDateTimeUtil.getTimeFormatText(Date(baseIt.createTime.toLong()))
                                mProductConditionTv?.text = baseIt.itemCondition
                                mProductCategoryTv?.text = baseIt.itemClassificationDescription
                                if (baseIt.description.isNullOrEmpty()) {
                                    mLibHomeTitleDeLy?.visibility = View.GONE
                                } else {
                                    mLibHomeTitleDeLy?.visibility = View.VISIBLE
                                    mLibHomeDetailstv?.text = baseIt.description
                                }
                            }
                        }

                    }
            }

        }
    }

    fun poketAdd(releaseRecordId: String) {
        launch {
            mHomeModel.poketAdd(AddGiftRequest(releaseRecordId.toDouble()))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@HomeGiftDetailsActivity)) {
                        it.data?.let {
                            EventBus.getDefault().post(GiftUpdateEvent())
                            mPocket = true
                            toast(getString(R.string.label_Added_Successfully))
                            mLibHomeAddBag?.setBackgroundResource(R.drawable.bg_corners_shopp_28_side)
                            mLibHomeAddBag?.text = getString(R.string.base_Go_to_cart)
                            mLibHomeAddBag?.setTextColor(
                                ContextCompat.getColor(
                                    this@HomeGiftDetailsActivity,
                                    R.color.BaseThemColor
                                )
                            )
                            getMyPocketNum()
                            ARouter.getInstance()
                                .build(Constance.HomePackingBagActivityURL)
                                .navigation()
                        }

                    }
                }
        }
    }

    private fun getMyPocketNum() {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mHomeModel.userSelectOne(CommonRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@HomeGiftDetailsActivity)) {
                            it.data?.let { ituser ->
                                PreferencesHelper.saveMyProfileCache(
                                    BaseApplication.instance,
                                    Gson().toJson(ituser)
                                )
                                ituser.poketNumber?.let {
                                    MessageObservable.messageObservable.newMessage(
                                        UpdateEntity(
                                            pockNum = it
                                        )
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }

    fun giftUnload(id: String) {
        launch {
            mHomeModel.giftUnload(CommentRequest(releaseRecordId = id.toLong()))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@HomeGiftDetailsActivity)) {
                        it.data?.let {
                            mBinding.LibHomeDetalisEtCl.visibility = View.GONE
                            mBinding.LibHomeDetalisDElCl.visibility = View.VISIBLE
                            mLibHomeGiftShareLL?.visibility = View.GONE
                            mBinding.LibHomeGiftMoreIv.visibility = View.GONE
                            EventBus.getDefault().post(GiftUpdateEvent())
                            MessageObservable.messageObservable.newMessage(
                                UpdateEntity(
                                    postProduct = getString(R.string.label_postProduct)
                                )
                            )
                            finish()
                        }
                    }
                }
        }
    }

    fun giftRelist(id: String) {
        launch {
            mHomeModel.giftRelist(CommentRequest(releaseRecordId = id.toLong()))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@HomeGiftDetailsActivity)) {
                        it.data?.let {
                            mBinding.LibHomeDetalisEtCl.visibility = View.VISIBLE
                            mBinding.LibHomeDetalisDElCl.visibility = View.GONE
                            mLibHomeGiftShareLL?.visibility = View.GONE
                            mBinding.LibHomeGiftMoreIv.visibility = View.VISIBLE
                            EventBus.getDefault().post(GiftUpdateEvent())
                            MessageObservable.messageObservable.newMessage(
                                UpdateEntity(
                                    postProduct = getString(R.string.label_postProduct)
                                )
                            )
                            finish()
                        }
                    }
                }
        }
    }

    fun giftDelete(id: String) {
        launch {
            mHomeModel.giftDelete(CommentRequest(releaseRecordId = id.toLong()))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@HomeGiftDetailsActivity)) {
                        it.data?.let {
                            mBinding.run {
                                toast(getString(R.string.label_Success))
                                MessageObservable.messageObservable.newMessage(
                                    UpdateEntity(
                                        postProduct = getString(R.string.label_postProduct)
                                    )
                                )
                                finish()

                            }
                        }
                    }
                }
        }
    }

    private fun concernedAdd() {
        userid?.let {
            launch {
                mHomeModel.concernedAdd(BaseUserRequest(userId = it.toLong()))
                    .onStart {
                        mAppViewModel.showLoading()
                    }.catch {
                    }.onCompletion {
                        mAppViewModel.dismissLoading()
                    }.collectLatest {
                        if (it.isOk(this@HomeGiftDetailsActivity)) {
                            mLibHomeUserSave?.setImageResource(R.mipmap.lib_home_user_saved)
                        }
                    }
            }
        }
    }

    private fun goodsRelationAdd(releaseRecordId: String) {
        launch {
            mHomeModel.goodsRelationAdd(
                GoodsRelationRequest(
                    releaseRecordId = releaseRecordId.toDouble(),
                    relationStatus = 2
                )
            )
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@HomeGiftDetailsActivity)) {
                        mLibHomeGiftSaveImage?.setImageResource(R.mipmap.user_saved)
                        collect = true
                    }
                }
        }
    }

    private fun goodsRelationDelete(id: String) {
        launch {
            mHomeModel.goodsRelationDelete(CommentRequest(releaseRecordId = id.toLong()))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@HomeGiftDetailsActivity)) {
                        collect = false
                        mLibHomeGiftSaveImage?.setImageResource(R.mipmap.user_save)
                    }
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUser() {
        launch {
            userid?.let {
                mHomeModel.userSelectOne(CommonRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@HomeGiftDetailsActivity)) {
                            it.data?.let { itUser ->
                                itUser.headImgUrl?.let {
                                    Glide.with(this@HomeGiftDetailsActivity)
                                        .load(it)
                                        .into(mivlibHomeGiftImage!!)
                                }
                                itUser.nickName?.let { nickName ->
                                    mivLibHomeName?.text = "Hi. $nickName"
                                }
                                itUser.postNumber?.let { postNumber ->
                                    if (postNumber == getString(R.string.label_1) || postNumber == getString(
                                            R.string.label_0
                                        )
                                    ) {
                                        mivLibHomedIntroduce?.text =
                                            postNumber+" "+getString(R.string.label_product)
                                    } else {
                                        mivLibHomedIntroduce?.text =
                                            postNumber+" "+getString(R.string.label_products)
                                    }
                                }
                                itUser.isConcern?.let {
                                    if (it) {
                                        mLibHomeUserSave?.setImageResource(R.mipmap.lib_home_user_saved)
                                    } else {
                                        mLibHomeUserSave?.setImageResource(R.mipmap.lib_home_user_save)
                                    }
                                }
                                mLibHomeMessage?.setOnClickListener {
                                    if (BaseDateUtils.isFastClick()) {
                                        if (itUser.userId != null) {
                                            id?.let {
                                                if (itUser.nickName == null) {
                                                    this@HomeGiftDetailsActivity.poketChatAdd(
                                                        itUser.userId!!,
                                                        "",
                                                        it
                                                    )
                                                } else {
                                                    this@HomeGiftDetailsActivity.poketChatAdd(
                                                        itUser.userId!!,
                                                        itUser.nickName!!,
                                                        it
                                                    )
                                                }

                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
            }
        }
    }

    fun poketChatAdd(userid: String, chatName: String, releaseRecordId: String) {
        launch {
            mHomeModel.poketAdd(AddGiftRequest(releaseRecordId.toDouble()))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    it.code?.let { itCode ->
                        if (itCode == 200) {
                            it.data?.let {
                                EventBus.getDefault().post(GiftUpdateEvent())
                                mPocket = true
                                mLibHomeAddBag?.setBackgroundResource(R.drawable.bg_corners_shopp_28_side)
                                mLibHomeAddBag?.text = getString(R.string.base_Go_to_cart)
                                mLibHomeAddBag?.setTextColor(
                                    ContextCompat.getColor(
                                        this@HomeGiftDetailsActivity,
                                        R.color.BaseThemColor
                                    )
                                )
                            }
                            getMyPocketNum()
                        }
                    }
                    ARouter.getInstance()
                        .build(Constance.ChatListActivityURL)
                        .withString("userid", userid)
                        .withString("chatName", chatName)
                        .withString("releaseRecordId", id)
                        .navigation()
                }
        }
    }

    fun firstShare() {
        launch {
            mHomeModel.shareIn()
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@HomeGiftDetailsActivity)) {
                        it.data?.let {
                            it.whetherNeed?.let { whetherNeed ->
                                if(whetherNeed== SingEntity.NEED){
                                    var title="Get 10 Eco credit for first \n sharing every day."
                                    val blockDialog = PointDialogCommon(
                                        content = title,
                                        onBlockClick = {
                                        })
                                    blockDialog.show(supportFragmentManager, title)
                                    MessageObservable.messageObservable.newMessage(UpdateEntity(messagePoint = UpdateEntity.POINT))
                                }
                            }
                        }
                    }
                }
        }
    }

}