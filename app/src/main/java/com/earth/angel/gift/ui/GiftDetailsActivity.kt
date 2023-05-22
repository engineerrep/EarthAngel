package com.earth.angel.gift.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.databinding.ActivityGiftDetailsBinding
import com.earth.angel.event.GroupCreatEvent
import com.earth.angel.gift.adapter.CommentAdapter
import com.earth.angel.gift.adapter.GiftDetailsAdapter
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.mine.MineModel
import com.earth.angel.search.SearchModel
import com.earth.angel.share.modle.ShareModel
import com.earth.angel.util.DateUtils
import com.earth.angel.util.TimeForUtils
import com.earth.angel.view.CustomPopupWindow
import com.earth.angel.view.GiftPhotoActivity
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.entity.PicTureEntity
import com.earth.libbase.network.request.ShareRequest
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.util.SoftKeyboardUtils
import com.earth.libbase.view.ShapedImageView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class GiftDetailsActivity : BaseActivity<ActivityGiftDetailsBinding>() {


    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()
    private val mViewModel by viewModel<SearchModel>()
    private val mMineModel by viewModel<MineModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mShareModel by viewModel<ShareModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var customBottomWindow: CustomPopupWindow? = null
    private var giftEntity: GiftEntity? = null
    private var mine: String? = ""
    private var releaseRecordId: Long? = null
    private var giftDetailsAdapter: GiftDetailsAdapter? = null
    private var listPageGiftList: ArrayList<PicTureEntity> = ArrayList()
    private var headView: View? = null
    private var headLayoutPager: LinearLayoutManager? = null
    private var mCommentAdapter: CommentAdapter? = null
    private var listComment: ArrayList<CommentEntity> = ArrayList()
    private var hView: View? = null
    private var pageNum: Int = 1
    private var goodId: String? = null
    private var tvLink: TextView? = null
    private var mUserId: Long? = null
    private var mnikeName: String? = null
    private var tvNumMessage: TextView? = null
    private var tvLikes: TextView? = null
    private var ivCommentsLikes: ImageView? = null
    private var tvNumLikes: TextView? = null


    override fun getLayoutId(): Int = R.layout.activity_gift_details

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            handler = this@GiftDetailsActivity
            mAppViewModel?.showLoadingProgress.observe(this@GiftDetailsActivity, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })

            giftEntity = intent.getSerializableExtra("giftEntity") as GiftEntity?
            goodId = intent.getStringExtra("GoodId")
            giftEntity?.let {
                initGood()
            }
            goodId?.let {
                selectData()
            }
            initBottom()
            shareVersionAdd()
        }

    }

    private fun initGood() {
        mBinding?.run {
            headView = layoutInflater.inflate(R.layout.head_giftdetails, null)
            headView?.run {
                val ivGiftMesssage: ShapedImageView = this.findViewById(R.id.ivGiftMesssage)
                val ivMore: ShapedImageView = this.findViewById(R.id.ivMore)
                val ivGiftShare: ShapedImageView = this.findViewById(R.id.ivGiftShare)
                val ivHead: ShapedImageView = this.findViewById(R.id.ivHead)
                val tvTitleCenter: TextView = this.findViewById(R.id.tvTitleCenter)
                val tvName: TextView = this.findViewById(R.id.tvName)
                val tv_content: TextView = this.findViewById(R.id.tv_content)
                val collection: ShapedImageView = this.findViewById(R.id.collection)
                val time: TextView = this.findViewById(R.id.time)
                tvLikes = this.findViewById(R.id.tvLikes)
                tvNumLikes = this.findViewById(R.id.tvNumLikes)
                tvNumMessage = this.findViewById(R.id.tvNumMessage)
                ivCommentsLikes = this.findViewById(R.id.ivCommentsLikes)
                val commentOnShare: ImageView = this.findViewById(R.id.commentShare)
                val commentLikes: ImageView = this.findViewById(R.id.commentLikes)
                val commentMessage: ImageView = this.findViewById(R.id.commentMessage)
                val tvLeftTool: ImageView = this.findViewById(R.id.tvLeftTool)
                val rlvPic: RecyclerView = this.findViewById(R.id.rlv)
                commentMessage.setOnClickListener {
                    SoftKeyboardUtils.showSoftInputFromWindow(this@GiftDetailsActivity, etComment)
                }
                commentLikes.setOnClickListener {
                    giftEntity?.let {
                        addLike(it)
                    }
                }
                tvLeftTool.setOnClickListener {
                    finish()
                }
                if (!intent.getStringExtra("mine").isNullOrEmpty()) {
                    ivGiftMesssage.visibility = View.GONE
                    ivMore.visibility = View.GONE
                }
                tvTitleCenter.text = getString(R.string.lab_Details)
                ivGiftShare.setOnClickListener {
                    if (DateUtils.isFastClick()) {
                        giftEntity?.let {
                            startActivity(
                                Intent(this@GiftDetailsActivity, RepotActivity::class.java)
                                    .putExtra("giftEntity", it)
                            )
                        }
                    }
                }
                ivHead.setOnClickListener {
                    if (DateUtils.isFastClick()) {
                        giftEntity?.let {
                            UserMainActivity.openUserMainActivity(this@GiftDetailsActivity, it)
                        }
                    }
                }
                giftEntity?.run {
                    releaseRecordId = this.id.toLong()
                    Glide.with(this@GiftDetailsActivity)
                        .load(headImgUrl)
                        .into(ivHead)
                    tvName.text = nickName
                    mUserId = userId.toLong()
                    mnikeName = nickName
                    if (description.isNullOrEmpty()) {
                        tv_content.visibility = View.GONE
                    } else {
                        tv_content.visibility = View.VISIBLE
                        tv_content.text = description
                    }
                    if (PreferencesHelper.getUserId(this@GiftDetailsActivity) == userId) {
                        ivGiftShare?.visibility = View.GONE
                    } else {
                        ivGiftShare?.visibility = View.VISIBLE
                    }
                    releasedLikeNumbers?.let {
                        initLikeView(it)
                    }
                    tvLikes?.setOnClickListener {
                        if (DateUtils.isFastClick()) {
                            releaseRecordId?.let {
                                UserLikeActivity.openUserLikeActivity(this@GiftDetailsActivity, it)

                            }
                        }
                    }
                    isLike?.let {
                        if (it) {
                            collection.setImageResource(R.mipmap.collect)
                        } else {
                            collection.setImageResource(R.mipmap.collection_un)
                        }
                    }
                    collection.setOnClickListener {
                        addLike(this@run)
                    }

                    createTime?.let {
                        time.text = TimeForUtils.getGiftDtailsTime(createTime.toLong())
                    }
                    if (pictureResources.isNotEmpty()) {

                        listPageGiftList.addAll(pictureResources)
                        giftDetailsAdapter = GiftDetailsAdapter(listPageGiftList, isExchange)

                        headLayoutPager = LinearLayoutManager(this@GiftDetailsActivity)
                        headLayoutPager?.stackFromEnd = true

                        rlvPic.layoutManager = headLayoutPager
                        rlvPic.adapter = giftDetailsAdapter
                        giftDetailsAdapter?.setOnItemClickListener { _, _, position ->
                            giftEntity?.let {
                                val intent =
                                    Intent(context, GiftPhotoActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra(
                                    GiftPhotoActivity.IMAGE_LIST,
                                    it
                                ).putExtra("position", position)
                                startActivity(intent)
                            }

                        }

                    }
                }
                commentOnShare.setOnClickListener {
                    customBottomWindow?.showAtLocation(
                        ivRightTool,
                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0
                    )
                }
            }

        /*    ivCommGiftShare.setOnClickListener {
                addComment()
                //ShareUtil.shareGoods(this@GiftDetailsActivity, giftEntity?.userId, giftEntity?.id)
                *//*  customBottomWindow?.showAtLocation(
                      ivRightTool,
                      Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0
                  )*//*
            }*/

            val layoutPager = LinearLayoutManager(this@GiftDetailsActivity)
            rlvComment.layoutManager = layoutPager

            mCommentAdapter = CommentAdapter(this@GiftDetailsActivity, listComment)
         /*   ivCommGiftMesssage.setOnClickListener {
                layoutPager?.scrollToPositionWithOffset(
                    mCommentAdapter?.itemCount!! - 1,
                    Integer.MIN_VALUE
                )
            }*/
            rlvComment.setAdapter(mCommentAdapter)
            rlvComment.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum = 1
                    listComment.clear()
                    mCommentAdapter?.notifyDataSetChanged()
                    getComment(false)
                }
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getComment(false)
                }
            })
            headView?.let {
                mCommentAdapter?.addHeaderView(it)
            }

            etComment.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        SoftKeyboardUtils.closeInoutDecorView(this@GiftDetailsActivity)
                        addComment()
                        return true
                    }
                    return false
                }
            })
        }
        getComment(false)
    }

    private fun initBottom() {
        customBottomWindow = CustomPopupWindow.Builder(this@GiftDetailsActivity)
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
            //ShareUtil.shareText(this@GiftDetailsActivity, tvLink?.text.toString(), "EarthAngel")
        }
        ivDelete.setOnClickListener {
            customBottomWindow?.dismiss()
        }
    }

    private fun shareVersionAdd() {
        this@GiftDetailsActivity.mnikeName?.let {
            var sendStr = "Here are $it’s eco gifts for you"
            launch {
                mShareModel.shareVersionAdd(
                    ShareRequest(
                        userId = this@GiftDetailsActivity.mUserId!!.toLong(),
                        textContent = sendStr
                    )
                ).catch { }
                    .collectLatest {
                        if (it.isOk(this@GiftDetailsActivity)) {
                            tvLink?.text = it.data?.url
                            tvLink?.visibility = View.GONE
                        }
                    }
            }
        }
    }


    private fun selectData() {
        goodId?.let {
            launch {
                mMineModel.releaseRecordOne(it.toLong()).onStart {
                    mAppViewModel.showLoading()
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }
                    .catch { }.collectLatest {
                        if (it.isOk(this@GiftDetailsActivity)) {
                            it.data?.let {
                                giftEntity = it
                                initGood()
                            }
                        }
                    }
            }
        }


    }

    private fun addComment() {
        val msg = mBinding?.etComment.text.toString().trim()
        if (msg.isEmpty()) {
            toast("Leave a comment")
            return
        }
        releaseRecordId?.let {
            launch {
                mEcoGiftGorupsModel.addComment(msg, it).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@GiftDetailsActivity)) {
                            toast(getString(R.string.lab_Success))
                            mBinding?.etComment.setText("")
                            pageNum = 1
                            listComment.clear()
                            mCommentAdapter?.notifyDataSetChanged()
                            getComment(true)
                        }

                    }
            }
        }
    }

    // 是否跳到第一个位置
    private fun getComment(top: Boolean) {
        giftEntity?.id?.let {
            launch {
                mEcoGiftGorupsModel.commentPageList(pageNum, 10, it.toLong()).catch {}
                    .onStart { }
                    .onCompletion { }
                    .collectLatest {
                        it.data?.let {
                            listComment.addAll(it)
                            mCommentAdapter?.notifyDataSetChanged()
                            mBinding?.rlvComment?.requestFocus()
                        }
                        if (top) {
                            if (listComment?.size > 0) {
                                mBinding?.rlvComment?.layoutManager?.scrollToPosition(1)
                            }
                        }
                        if (listComment.size == 0) {
                            tvNumMessage?.visibility = View.GONE
                        } else {
                            tvNumMessage?.visibility = View.VISIBLE
                            tvNumMessage?.text = listComment?.size.toString()
                        }
                        mBinding?.rlvComment?.let { it1 ->
                            it1.finishRefresh(true)
                            it1.finishLoadMore(true)
                        }
                    }
            }
        }

    }

    private fun report(str: String, bean: GiftEntity) {
        bean?.let {
            launch {
                mViewModel.report(str, bean.id.toLong()).catch {}
                    .onStart { }
                    .onCompletion { }
                    .collectLatest {
                        if (it.isOk(this@GiftDetailsActivity)) {
                            toast(getString(R.string.text_success))
                        }
                    }
            }
        }
    }

    private fun addLike(giftEntity: GiftEntity) {
        var relationStatus: Int = 0
        if (giftEntity.isLike) {
            relationStatus = 0
            launch {
                mEcoGiftGorupsModel.deleteGoodsRelation(null, giftEntity.id.toLong())
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@GiftDetailsActivity)) {
                            giftEntity.isLike =
                                giftEntity.isLike != true
                            if (giftEntity.releasedLikeNumbers != 0) {
                                giftEntity.releasedLikeNumbers = giftEntity.releasedLikeNumbers - 1
                            }
                            EventBus.getDefault()
                                .post(GroupCreatEvent(null, giftEntity.id.toLong(), false))
                            initLikeView(giftEntity.releasedLikeNumbers)
                        }
                    }
            }
        } else {
            relationStatus = 2
            launch {
                mEcoGiftGorupsModel.goodsRelation(
                    relationStatus,
                    giftEntity.id.toLong()
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@GiftDetailsActivity)) {
                            giftEntity.isLike =
                                giftEntity.isLike != true
                            giftEntity.releasedLikeNumbers = giftEntity.releasedLikeNumbers + 1
                            EventBus.getDefault()
                                .post(GroupCreatEvent(null, giftEntity.id.toLong(), true))
                            initLikeView(giftEntity.releasedLikeNumbers)
                        }
                    }
            }
        }


    }

    private fun initLikeView(releasedLikeNumbers: Int) {

        when (releasedLikeNumbers) {
            0 -> {
                tvLikes?.text = ""
                ivCommentsLikes?.visibility = View.GONE
                tvNumLikes?.visibility = View.GONE
            }
            1 -> {
                tvLikes?.text = "$releasedLikeNumbers Like"
                ivCommentsLikes?.visibility = View.VISIBLE
                tvNumLikes?.visibility = View.VISIBLE
                tvNumLikes?.text = releasedLikeNumbers.toString()
            }
            else -> {
                tvLikes?.text = "$releasedLikeNumbers Likes"
                ivCommentsLikes?.visibility = View.VISIBLE
                tvNumLikes?.visibility = View.VISIBLE
                tvNumLikes?.text = releasedLikeNumbers.toString()
            }
        }

    }

    companion object {
        fun openGiftDetailsActivity(context: Context?, giftEntity: GiftEntity) {
            val intent = Intent(context, GiftDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("giftEntity", giftEntity)
            context?.startActivity(intent)
        }

        fun openGiftDetailsActivityFromUser(context: Context?, giftEntity: GiftEntity) {
            val intent = Intent(context, GiftDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("giftEntity", giftEntity)
            intent.putExtra("mine", "mine")
            context?.startActivity(intent)
        }
    }
}