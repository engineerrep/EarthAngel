package com.earth.angel.mine.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.appphoto.PhotoEditActivity
import com.earth.angel.databinding.ActivityMyecogiftBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.event.ShareShowEvent
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.mine.MineModel
import com.earth.angel.mine.adapter.MyEcoGiftAdapter
import com.earth.angel.user.ui.MyFriendsActivity
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GiftEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyEcoGiftActivity : BaseActivity<ActivityMyecogiftBinding>() {

    private val mMineModel by viewModel<MineModel>()
    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var listGift: ArrayList<GiftEntity> = ArrayList()
    private var pageNum: Int = 1
    private var adapter: MyEcoGiftAdapter? = null

    override fun getLayoutId(): Int = R.layout.activity_myecogift

    override fun fishActivity() {
        super.fishActivity()
        DataReportUtils.getInstance().report("My_listing_exit")

    }

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            handler = this@MyEcoGiftActivity
            tvTitleCenter.text = getString(R.string.lab_others)
            llRight.setOnClickListener {
                if (DateUtils.isFastClick()){
                    startActivity.launch(
                        Intent(
                            this@MyEcoGiftActivity,
                            PhotoEditActivity::class.java
                        ).putExtra("flag", true)
                    )
                }
            }
            adapter = MyEcoGiftAdapter(listGift,
                upedit = {
                    startActivity.launch(
                        Intent(
                            this@MyEcoGiftActivity,
                            PhotoEditActivity::class.java
                        ).putExtra("giftEntity", it)
                    )

                }, upport = {
                    var blockDialog = DialogCommon(title = getString(R.string.dialog_Unlist),content = getString(R.string.dialog_removed),onBlockClick = {

                        unload(it)
                    })
                    blockDialog.show(
                        supportFragmentManager, ""
                    )
                }, uprepot = {
                    var blockDialog = DialogCommon(title = getString(R.string.dialog_repost),onBlockClick = {
                        reshelf(it)
                    })
                    blockDialog.show(
                        supportFragmentManager, ""
                    )
                }, updelete = {
                    var blockDialog = DialogCommon(title = getString(R.string.dialog_delete),onBlockClick = {
                        delete(it)
                    })
                    blockDialog.show(
                        supportFragmentManager, ""
                    )
                }, upsend = {
                    if(it.isExchange==0){
                        startActivity.launch(
                            Intent(
                                this@MyEcoGiftActivity,
                                MyFriendsActivity::class.java
                            ).putExtra("type", it.id)
                        )
                    }

                })

            val layoutPager = LinearLayoutManager(this@MyEcoGiftActivity)
            rvList.layoutManager = layoutPager
            rvList.setAdapter(adapter)
            adapter?.setOnItemClickListener { _, _, position ->
                GiftDetailsActivity.openGiftDetailsActivityFromUser(
                    this@MyEcoGiftActivity,
                    listGift[position]
                )
            }
            rvList?.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listGift.clear()
                    pageNum = 1
                    giftData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    giftData()
                }
            })
            mAppViewModel.showLoadingProgress.observe(this@MyEcoGiftActivity, Observer {
                if (it) mLoadingDialog?.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog?.dismiss()
            })
        }
        mAppViewModel.showLoading()
        giftData()
    }

    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == 100) {
                listGift.clear()
                pageNum = 1
                giftData()
            }

        }

    private fun reshelf(item: GiftEntity) {
        launch {
            mEcoGiftGorupsModel.reshelfReleaseRecord(item.id.toLong())
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .catch {
                }.collectLatest {
                    if (it.isOk(this@MyEcoGiftActivity)) {
                        it.data?.let {
                            item.releaseStatus = 0
                            item.createTime = it.createTime
                            EventBus.getDefault().post(ShareShowEvent())

                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    private fun delete(item: GiftEntity) {
        launch {
            mEcoGiftGorupsModel.deleteReleaseRecord(item.id.toLong())
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .catch {
                }.collectLatest {
                    if (it.isOk(this@MyEcoGiftActivity)) {
                        listGift.remove(item)
                        adapter?.notifyDataSetChanged()
                        EventBus.getDefault().post(ShareShowEvent())
                        checkEmptyData()

                    }
                }
        }
    }

    private fun unload(item: GiftEntity) {
        launch {
            mMineModel.unload(item.id.toLong())
                .onStart { mAppViewModel.showLoading() }
                .onCompletion { mAppViewModel.dismissLoading() }
                .catch {
                }.collectLatest {
                    if (it.isOk(this@MyEcoGiftActivity)) {
                        item.releaseStatus = 2
                        adapter?.notifyDataSetChanged()
                        EventBus.getDefault().post(ShareShowEvent())
                    }
                }
        }
    }

    private fun checkEmptyData() {
        if (listGift.size == 0) {
            mBinding?.llEmpty.visibility = View.VISIBLE
        } else {
            mBinding?.llEmpty.visibility = View.GONE
        }
    }

    private fun giftData() {

        launch {
            mMineModel.ownPageList(pageNum, 10, null)
                .onCompletion {
                    mAppViewModel.dismissLoading()
                }
                .catch {
                }.collectLatest {
                    if (it.isOk(this@MyEcoGiftActivity)) {

                        it.data?.let { list ->
                            if (list.isNotEmpty()) {
                                listGift.addAll(list)
                                adapter?.notifyDataSetChanged()
                            }
                        }
                        mBinding?.rvList?.let { it1 ->
                            it1.finishRefresh(true)
                            it1.finishLoadMore(true)

                            if (pageNum > 1 && (listGift == null || listGift.size <= 0)) {
                                it1.setLoadMoreEnable(false)
                            } else {
                                it1.setLoadMoreEnable(true)
                            }
                        }
                        checkEmptyData()
                    }

                }

        }
    }

    companion object {
        fun openMyEcoGiftActivity(context: Context?) {
            val intent = Intent(context, MyEcoGiftActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }

}