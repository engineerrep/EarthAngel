package com.earth.angel.gift.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earth.angel.R
import com.earth.angel.appphoto.PhotoEditActivity
import com.earth.angel.appphoto.PhotoModel
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.databinding.ActivityGroupDetailsBinding
import com.earth.angel.dialog.*
import com.earth.angel.event.GroupCreatEvent
import com.earth.angel.gift.adapter.MemberAdapter
import com.earth.angel.gift.adapter.PageGiftAdapter
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.search.SearchModel
import com.earth.angel.user.UserModel
import com.earth.angel.user.ui.FriendListActivity
import com.earth.angel.util.DateUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.entity.HouseListEntity
import com.earth.libbase.entity.MemberEntity
import com.earth.libbase.entity.SearchDetailEntity
import com.earth.libbase.util.SoftKeyBroadManager
import com.earth.libbase.util.SoftKeyBroadManager.SoftKeyboardStateListener
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_group_details.*
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.fragment_ecogiftgroups.*
import kotlinx.android.synthetic.main.head_group_details.*
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.android.synthetic.main.include_top_bar.ivRightTool
import kotlinx.android.synthetic.main.include_top_bar.tvTitleCenter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class GroupDetailsActivity : BaseActivity<ActivityGroupDetailsBinding>() {

    private val mLoginViewModel by viewModel<EcoGiftGorupsModel>()
    private val mPhotoModelModel by viewModel<PhotoModel>()
    private val mViewModel by viewModel<SearchModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private val userModle by viewModel<UserModel>()

    private var pageNum: Int = 1
    private var pageNumNumber: Int = 1
    private var pageDialogNumNumber: Int = 1
    private var mBottomPW: DialogFilter? = null
    private var flag: Boolean? = null
    private var mhouseNumber: Long? = 0
    private var releaseTypes: ArrayList<Int>? = null
    private var listMenbertList: ArrayList<MemberEntity> = ArrayList()
    private var listPageGiftList: ArrayList<GiftEntity> = ArrayList()
    private var giftHouseEntity: HouseListEntity? = null
    private var searchDetailEntity: SearchDetailEntity? = null

    private var memberAdapter: MemberAdapter? = null
    private var adapterPageGiftList: PageGiftAdapter? = null
    private var mDialogMember: DialogMember? = null
    private var headView: View? = null
    private var headLayoutPager: LinearLayoutManager? = null

    private var fletView: View? = null

    private var position: Int? = null
    private var textTitle: String? = null
    private var sortField: String? = null
    private var sortType: Int? = null
    private val sortFieldStr: String = "sortField"
    private val highttolow: Int = 0
    private val lowtohight: Int = 1
    private var groupNewName: String? = null

    override fun getLayoutId(): Int = R.layout.activity_group_details

    override fun fishActivity() {
        super.fishActivity()
        groupNewName?.let {
            setResult(100)
        }
    }

    //重写onKeyDown方法,
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //当返回按键被按下
            //调用exit()方法
            groupNewName?.let {
                setResult(100)
            }
            finish()
        }
        return false
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        EventBus.getDefault().register(this@GroupDetailsActivity)
        mBinding?.run {
            giftHouseEntity = intent.getSerializableExtra("giftHouse") as HouseListEntity?
            searchDetailEntity =
                intent.getSerializableExtra("SearchDetailEntity") as SearchDetailEntity?
            textTitle = intent.getStringExtra("textTitle")
            tvTitleCenter.text = getString(R.string.lab_Eco_Gift_Group)
            listMenbertList.add(0, MemberEntity("", 0, 0, 0, "", "", false, false, false))
            memberAdapter = MemberAdapter(this@GroupDetailsActivity, listMenbertList, upDade = {
                if (it.nickName.isNullOrEmpty()) {
                    startActivity.launch(
                        Intent(
                            this@GroupDetailsActivity,
                            FriendListActivity::class.java
                        ).putExtra("type", FriendListActivity.member)
                            .putExtra("houseNumber", mhouseNumber)
                            .putExtra("listMenbertList", listMenbertList)
                    )
                } else {
                    UserMainActivity.openUserMainActivity(this@GroupDetailsActivity, it)
                }
            })

            adapterPageGiftList = PageGiftAdapter(this@GroupDetailsActivity, listPageGiftList,
                upDade = {
                    addLike(it - 1)
                }, upMessage = {
                    // comment
                }, upReport = {
                    startActivity(
                        Intent(this@GroupDetailsActivity, RepotActivity::class.java)
                            .putExtra("giftEntity", it)
                    )
                }, upUser = { giftEntity: GiftEntity, i: Int ->
                    position = i - 1
                    startActivity.launch(
                        Intent(
                            this@GroupDetailsActivity,
                            UserMainActivity::class.java
                        ).putExtra("GiftEntity", giftEntity)
                    )
                }, upLike = {
                    requestFriend(it.userId.toLong(), it)
                })
            val layoutPager1 = LinearLayoutManager(this@GroupDetailsActivity)
            rvList.layoutManager = layoutPager1
            rvList.setAdapter(adapterPageGiftList)
            adapterPageGiftList?.setOnItemClickListener { _, _, position ->
                startActivity.launch(
                    Intent(
                        this@GroupDetailsActivity,
                        GiftDetailsActivity::class.java
                    ).putExtra("giftEntity", listPageGiftList[position])
                )
            }
            rvList.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listPageGiftList.clear()
                    pageNum = 1
                    getPageList()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getPageList()
                }
            })
        }
        headView = layoutInflater.inflate(R.layout.head_group_details, null)
        fletView = layoutInflater.inflate(R.layout.empty_group_detail, null)
        fletView?.run {
            val btPost: Button = this.findViewById(R.id.btPost)
            btPost.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    startActivity.launch(
                        Intent(
                            this@GroupDetailsActivity,
                            PhotoEditActivity::class.java
                        ).putExtra("flag", true)
                    )
                }

            }
        }

        headView?.run {
            val rlvMember: RecyclerView = this.findViewById(R.id.rlvMember)
            val tvGroupName: TextView = this.findViewById(R.id.tvGroupName)
            val tvGroupID: TextView = this.findViewById(R.id.tvGroupID)
            val tvMembersAll: TextView = this.findViewById(R.id.tvMembersAll)
            val ivCommfluter: ImageView = this.findViewById(R.id.ivCommfluter)
            val titleGroups: TextView = this.findViewById(R.id.titleGroups)
            ivCommfluter.setOnClickListener {
                showDialog()
            }
            textTitle?.let {
                titleGroups.visibility = View.VISIBLE
                titleGroups.text = it
            }
            headLayoutPager = LinearLayoutManager(
                this@GroupDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            headLayoutPager?.stackFromEnd = true
            rlvMember.layoutManager = headLayoutPager
            rlvMember.adapter = memberAdapter
            giftHouseEntity?.run {
                mhouseNumber = number.toLong()
                tvGroupName.text = name
                tvGroupID.text = "Group ID : $number"
                ivRightTool.setOnClickListener {
                    startActivity.launch(
                        Intent(
                            this@GroupDetailsActivity,
                            GroupEditActivity::class.java
                        ).putExtra("giftHouse", this)
                    )
                }
            }
            searchDetailEntity?.run {
                mhouseNumber = number.toLong()
                tvGroupName.text = name
                tvGroupID.text = "Group ID : $number"
                ivRightTool.setOnClickListener {
                    startActivity.launch(
                        Intent(
                            this@GroupDetailsActivity,
                            GroupEditActivity::class.java
                        ).putExtra("SearchDetailEntity", this)
                    )
                }
            }
            tvMembersAll.setOnClickListener {
                mDialogMember = DialogMember(this@GroupDetailsActivity)
                    .setOnInterface(object : MemberInterface {
                        override fun refresh() {
                            pageDialogNumNumber = 1
                            getMember()
                        }

                        override fun more() {
                            pageDialogNumNumber++
                            getMember()
                        }

                        override fun join(userid: Long, bean: MemberEntity) {
                            if (bean.requestFrendsStatus) {
                                delete(userid, bean)
                            } else {
                                requestFriend(userid, bean)
                            }
                        }
                    }).create()
                pageDialogNumNumber = 1
                mDialogMember?.clear()
                getMember()
                mDialogMember?.show()
            }
        }
        headView?.let { itview ->
            adapterPageGiftList?.addHeaderView(itview)
        }
        root?.let {
            val softKeyBroadManager =
                SoftKeyBroadManager(it)
            softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener)

        }
        mAppViewModel.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })

        recommend()
        if (EarthAngelApp.mFilterEntity == null) {
            releaseTypes = null
            this@GroupDetailsActivity.flag = null
        } else {
            EarthAngelApp.mFilterEntity?.let {
                releaseTypes = ArrayList()
                it.releaseType?.let { it1 -> releaseTypes?.add(it1) }
                if (it.range == 0) {
                    this@GroupDetailsActivity.flag = false
                }
                if (it.range == 1) {
                    this@GroupDetailsActivity.flag = true
                }
            }
        }
        getPageList()

    }

    var softKeyboardStateListener: SoftKeyboardStateListener = object : SoftKeyboardStateListener {
        override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
            //   etPut.requestLayout()
        }

        override fun onSoftKeyboardClosed() {
            //    etPut.requestLayout()
        }
    }

    private fun requestFriend(userid: Long, bean: MemberEntity) {
        launch {
            userid?.let {
                userModle.requestFriend(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@GroupDetailsActivity)) {
                            toast(getString(R.string.test_Success))
                            bean.requestFrendsStatus = true
                            mDialogMember?.update()
                        }
                    }
            }
        }
    }

    private fun requestFriend(userid: Long, bean: GiftEntity) {
        launch {
            userid?.let {
                userModle.requestFriend(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@GroupDetailsActivity)) {
                            toast(getString(R.string.test_Success))
                            bean.requestFrendsStatus = true
                            adapterPageGiftList?.notifyDataSetChanged()
                        }
                    }
            }
        }
    }

    private fun delete(userid: Long, bean: MemberEntity) {
        launch {
            userid.let {
                userModle.deleteAgreeToBeFriends(it.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@GroupDetailsActivity)) {
                            toast(getString(R.string.lab_Success))
                            bean.requestFrendsStatus = false
                            mDialogMember?.update()
                        }
                    }
            }
        }
    }

    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                100 -> {
                    listPageGiftList.clear()
                    pageNum = 1
                    getPageList()
                }
                101 -> {

                }
                102 -> {
                    listMenbertList.clear()
                    listMenbertList.add(MemberEntity("", 0, 0, 0, "", "", false, false, false))
                    memberAdapter?.notifyDataSetChanged()
                    recommend()
                }
                103 -> {
                    //GroupEditActivity
                    setResult(100)
                    finish()
                }
                104 -> {
                    //GroupEditActivity
                    it.data?.let {
                        groupNewName = it.getStringExtra("GroupName")
                        groupNewName?.let {
                            giftHouseEntity?.name = it
                            searchDetailEntity?.name = it
                            tvGroupName.text = it
                        }

                    }
                }
            }
        }

    /*   @Subscribe(threadMode = ThreadMode.MAIN)
       fun onChatUserEvent(event: GroupCreatEvent) {
           listPageGiftList.clear()
           pageNum = 1
           getPageList()
       }*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: GroupCreatEvent) {
        if (event.isConcern != null) {
            event.userId?.let {
                for (item in listPageGiftList) {
                    if (item.userId.toLong() == event.userId) {
                        item.isConcern = event?.isConcern!!
                    }
                }
                for (item in listMenbertList) {
                    if (item.userId == event.userId) {
                        item.isConcern = event?.isConcern!!
                    }
                }
                adapterPageGiftList?.notifyDataSetChanged()
            }
        } else if (event.isLike != null) {
            event.userId?.let {
                for (item in listPageGiftList) {
                    if (item.id.toLong() == event.userId) {
                        if(event?.isLike!!){
                            item.releasedLikeNumbers= item.releasedLikeNumbers+1
                        }else{
                            if( item.releasedLikeNumbers!=0){
                                item.releasedLikeNumbers= item.releasedLikeNumbers-1
                            }
                        }
                        item.isLike = event?.isLike!!
                    }
                }
                adapterPageGiftList?.notifyDataSetChanged()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@GroupDetailsActivity)
    }

    private fun report(str: String, bean: GiftEntity) {
        bean?.let {
            launch {
                mViewModel.report(str, bean.id.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .collectLatest {
                        if (it.isOk(this@GroupDetailsActivity)) {
                            toast(getString(R.string.text_success))
                        }
                    }
            }
        }
    }

    private fun recommend() {
        //这里说的是最多10个人所以这里就不分页了
        launch {
            mLoginViewModel.giftHouseUser(mhouseNumber!!, pageNumNumber, 10)
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .catch { }.collectLatest {
                    if (it.isOk(this@GroupDetailsActivity)) {
                        it.data?.let {
                            listMenbertList.addAll(it)
                            memberAdapter?.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    private fun getMember() {
        mhouseNumber?.let { itMber ->
            launch {
                mLoginViewModel.giftHouseUser(itMber, pageDialogNumNumber, 10)
                    .onStart { mAppViewModel.showLoading() }
                    .onCompletion { mAppViewModel.dismissLoading() }
                    .catch { }
                    .collectLatest {
                        if (it.isOk(this@GroupDetailsActivity)) {
                            it.data?.let {
                                mDialogMember?.addtList(it)
                            }

                        }
                    }
            }
        }

    }


    private fun getPageList() {
        launch {
            mLoginViewModel.pageList(
                mhouseNumber!!,
                pageNum,
                10,
                flag,
                releaseTypes,
                sortField,
                sortType
            )
                .onStart {
                }.catch {
                }.onCompletion {
                    mBinding?.rvList?.let { it1 ->
                        it1.finishRefresh(true)
                        it1.finishLoadMore(true)
                    }
                }
                .collectLatest {
                    if (it.isOk(this@GroupDetailsActivity)) {
                        it.data?.let { list ->
                            if (list.isNotEmpty()) {
                                listPageGiftList.addAll(list)
                                adapterPageGiftList?.notifyDataSetChanged()

                            }
                        }
                    }
                    checkEmptyData()
                }
        }
    }

    private fun checkEmptyData() {
        if (listPageGiftList.size == 0) {
            if (adapterPageGiftList?.hasFooterLayout() == false) {
                fletView?.let { itview ->
                    adapterPageGiftList?.addFooterView(itview)
                }
            }

        } else {
            if (adapterPageGiftList?.hasFooterLayout() == true) {
                fletView?.let { itview ->
                    adapterPageGiftList?.removeFooterView(itview)
                }
            }

        }
    }

    private fun addLike(position: Int) {
        var relationStatus: Int = 0
        if (listPageGiftList[position].isLike) {
            relationStatus = 0
            launch {
                mLoginViewModel.deleteGoodsRelation(null, listPageGiftList[position].id.toLong())
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@GroupDetailsActivity)) {
                            listPageGiftList[position].isLike =
                                listPageGiftList[position].isLike != true
                            if(listPageGiftList[position].releasedLikeNumbers!=0){
                                listPageGiftList[position].releasedLikeNumbers=listPageGiftList[position].releasedLikeNumbers-1
                            }

                            adapterPageGiftList?.notifyDataSetChanged()
                        }
                    }
            }
        } else {
            relationStatus = 2
            launch {
                mLoginViewModel.goodsRelation(
                    relationStatus,
                    listPageGiftList[position].id.toLong()
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {

                        if (it.isOk(this@GroupDetailsActivity)) {
                            listPageGiftList[position].isLike =
                                listPageGiftList[position].isLike != true
                            listPageGiftList[position].releasedLikeNumbers=listPageGiftList[position].releasedLikeNumbers+1
                            adapterPageGiftList?.notifyDataSetChanged()
                        }

                    }
            }
        }


    }


    fun showDialog() {
        if (DateUtils.isFastClick()) {
            launch {
                mPhotoModelModel.sortLabel().catch { }.collectLatest {
                    if (it.isOk(this@GroupDetailsActivity)) {
                        it.data?.let { list ->
                            mBottomPW = DialogFilter(this@GroupDetailsActivity)
                                .setList(list)
                                .setOnInterface(object : DialogFilterInterface {
                                    override fun onBankString() {

                                        if (EarthAngelApp.mFilterEntity == null) {
                                            releaseTypes = null
                                            this@GroupDetailsActivity.flag = null
                                        } else {
                                            EarthAngelApp.mFilterEntity?.let {
                                                releaseTypes = ArrayList()
                                                it.releaseType?.let { it1 -> releaseTypes?.add(it1) }
                                                if (it.range == 0) {
                                                    this@GroupDetailsActivity.flag = false
                                                }
                                                if (it.range == 1) {
                                                    this@GroupDetailsActivity.flag = true
                                                }
                                            }

                                        }
                                        listPageGiftList.clear()
                                        adapterPageGiftList?.notifyDataSetChanged()
                                        pageNum = 1
                                        getPageList()
                                        mBottomPW?.disMiss()
                                    }

                                    override fun onDismiss() {
                                    }
                                })
                                .create()
                            mBottomPW?.show()
                        }
                    }

                }
            }
        }

    }
}