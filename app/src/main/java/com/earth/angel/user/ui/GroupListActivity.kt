package com.earth.angel.user.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.ActivityGroupListBinding
import com.earth.angel.gift.adapter.MsgRemindAdapter
import com.earth.angel.gift.ui.GroupDetailsActivity
import com.earth.angel.gift.ui.UserMainActivity
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.search.SearchModel
import com.earth.angel.user.UserModel
import com.earth.angel.util.DateUtils
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.entity.HouseListEntity
import com.earth.libbase.util.OnGroupListener
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupListActivity : BaseActivity<ActivityGroupListBinding>() {
    private val mEcoGiftModel by viewModel<EcoGiftGorupsModel>()
    private val mSearchModel by viewModel<SearchModel>()
    private val userModle by viewModel<UserModel>()
    private var adapterGroups: MsgRemindAdapter? = null
    private var listGroup: ArrayList<HouseListEntity> = ArrayList()
    private var textTitle: String? = null
    private var pageNum: Int = 1

    override fun getLayoutId(): Int =R.layout.activity_group_list

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            // 加入的
            showActionBar()
            tvTitleCenter.text=getString(R.string.lab_Eco_Gift_Groups)
            val layoutManager1 = LinearLayoutManager(this@GroupListActivity)
            xrlv.layoutManager = layoutManager1
            adapterGroups = MsgRemindAdapter(this@GroupListActivity, listGroup, object :
                OnGroupListener {
                override fun onItemClick(position: Int) {
                    if (DateUtils.isFastClick()){
                        if (listGroup[position].type == 0) {
                            startActivity.launch(
                                Intent(
                                    this@GroupListActivity,
                                    GroupDetailsActivity::class.java
                                ).putExtra("giftHouse", listGroup[position])
                                    .putExtra("textTitle", textTitle)
                            )
                        } else {
                            UserMainActivity.openUserMainActivity(
                                this@GroupListActivity,
                                listGroup[position]
                            )
                        }
                    }

                }

                override fun onDeleteClick(position: Int) {
                    if (listGroup[position].type == 0) {
                        quitGroup(listGroup[position].number.toLong(), position)
                    } else {
                        deleteFriend(listGroup[position].number.toLong(), position)
                    }
                }
            })
            xrlv.setAdapter(adapterGroups)
            xrlv.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    notifyData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getJoined()
                }
            })
            getJoined()
        }
    }
    private fun notifyData() {
        listGroup.clear()
        adapterGroups?.notifyDataSetChanged()
        pageNum = 1
        getJoined()
    }
    private fun getJoined() {
        val gson = Gson()
        // adapterGroups?.setList(listOldstr)
        launch {
            mEcoGiftModel.indexPageList(pageNum, 100).onStart {
            }.catch {
            }.onCompletion {
            }.collectLatest {
                if (it.isOk(this@GroupListActivity)) {
                    it.data?.let {
                        for (item in it){
                            if(item.type==0){
                                listGroup.add(item)
                            }
                        }
                        adapterGroups?.notifyDataSetChanged()
                    }
                    if(listGroup.size==0&&pageNum==1){
                        mBinding?.llEmpty.visibility=View.VISIBLE
                    }else{
                        mBinding?.llEmpty.visibility=View.GONE
                    }
                    mBinding?.xrlv?.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                }
            }
        }
    }

    private fun deleteFriend(userid: Long, position: Int) {
        launch {
            userid?.let {
                userModle.deleteFriend(it.toLong()).catch {}
                    .onStart {}
                    .onCompletion { }
                    .collectLatest {
                        if (it.isOk(this@GroupListActivity)) {
                            adapterGroups?.removeData(position)
                        }
                    }
            }

        }
    }

    private fun quitGroup(houseNumber: Long, position: Int) {
        launch {
            mEcoGiftModel.quitGroup(houseNumber).catch { }
                .collectLatest {
                    if (it.isOk(this@GroupListActivity)) {
                        adapterGroups?.removeData(position)
                    }
                }
        }
    }
    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                100 -> {
                    notifyData()
                }
                101 -> {
                }
            }
        }
}