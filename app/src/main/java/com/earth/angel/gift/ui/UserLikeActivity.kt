package com.earth.angel.gift.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.ActivityUserLikeBinding
import com.earth.angel.gift.adapter.UserLikedapter
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.entity.UserLocationEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserLikeActivity : BaseActivity<ActivityUserLikeBinding>() {
    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()
    private var giftEntity: GiftEntity? = null
    private var releaseRecordId: Long? = null
    private var pageNum: Int = 1
    private var list: ArrayList<UserLocationEntity>? = ArrayList()
    private var mShakedapter: UserLikedapter?=null

    override fun getLayoutId(): Int = R.layout.activity_user_like
    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            showActionBar()
            releaseRecordId = intent.getLongExtra("releaseRecordId", 0)
            tvTitleCenter.visibility= View.GONE
            list?.clear()
            mShakedapter = UserLikedapter(this@UserLikeActivity, list)
            val layoutManager1 = LinearLayoutManager(this@UserLikeActivity)
            rvList.layoutManager = layoutManager1
            rvList.setAdapter(mShakedapter)
            rvList.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    list?.clear()
                    pageNum=1
                    mShakedapter?.notifyDataSetChanged()
                    getData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getData()
                }
            })
        }
        getData()
    }

    private fun getData() {
        launch {
            mEcoGiftGorupsModel.likeFrends(pageNum, 10, releaseRecordId!!)
                 .onStart {
                }.onCompletion {
                }.catch { }.collectLatest {
                    if (it.isOk(this@UserLikeActivity)) {
                        it.data?.let {
                            list?.addAll(it)
                            mShakedapter?.notifyDataSetChanged()
                        }
                        mBinding?.rvList.finishLoadMore(true)
                        mBinding?.rvList.finishRefresh(true)
                        if(pageNum==1&&list?.size==0){
                            mBinding?.llEmpty.visibility=View.VISIBLE
                        }else{
                            mBinding?.llEmpty.visibility=View.GONE
                        }
                    }
                }
        }

    }

    companion object {
        fun openUserLikeActivity(context: Context?, releaseRecordId: Long) {
            val intent = Intent(context, UserLikeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("releaseRecordId", releaseRecordId)
            context?.startActivity(intent)
        }

    }

}