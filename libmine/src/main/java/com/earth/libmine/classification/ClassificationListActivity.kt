package com.earth.libmine.classification

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.view.loadingstatus.LoadingStatusCode
import com.earth.libmine.R
import com.earth.libmine.adapter.MineGiftListAdapter
import com.earth.libmine.databinding.ActivityClassificationListBinding
import com.earth.libmine.ui.LibMineModle
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_classification_list.*
import kotlinx.android.synthetic.main.libmine_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

@Route(path = Constance.ClassificationListActivityURL)
class ClassificationListActivity : BaseActivity<ActivityClassificationListBinding>() {
    @Autowired(name = "itemCode")
    @JvmField
    var itemCode: String? = null
    @Autowired(name = "itemText")
    @JvmField
    var itemText: String? = null
    private var pageNum: Int = 1

    private var mMineGiftListAdapter: MineGiftListAdapter? = null

    private var listString: ArrayList<BaseGiftEntity> = ArrayList()

    private val mMineModle by viewModel<LibMineModle>()


    override fun getLayoutId(): Int = R.layout.activity_classification_list

    override fun initActivity(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this@ClassificationListActivity)
        showActionBar()
        mBinding?.run {
            tvLibMineLeftTool.setOnClickListener {
                finish()
            }
            tvLibMinTitleCenter.text=itemText
            mMineGiftListAdapter = MineGiftListAdapter(this@ClassificationListActivity, listString)
            var gridLayoutManager = GridLayoutManager(this@ClassificationListActivity, 2)
            libMinCationRlv.layoutManager = gridLayoutManager
            libMinCationRlv.adapter = mMineGiftListAdapter
            libMineCationSrl?.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum = 1
                    libMineCationSrl?.setEnableLoadMore(true)
                    listString.clear()
                    getData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getData()
                }
            })
            mMineGiftListAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()){
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id",listString[position].releaseRecordId)
                        .withString("userid",listString[position].userId)
                        .navigation()
                }
            }
            getData()
        }
    }

    private fun getData() {
        launch {
            mMineModle.itemClassificationPageList(
                CommentRequest(
                    itemCode = itemCode?.toInt(),
                    pageNum = pageNum,
                    pageSize = 10))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@ClassificationListActivity)) {
                        mBinding?.libMineCationSrl?.let {
                            it.finishRefresh(true)
                            it.finishLoadMore(true)
                        }
                        it.data?.list?.let {
                            listString.addAll(it)
                            mMineGiftListAdapter?.notifyDataSetChanged()
                        }
                        if (listString.size == it.data?.total) {
                            mBinding?.libMineCationSrl?.setEnableLoadMore(false)
                        }
                        if(listString.isEmpty()){
                            mBinding?.libMineCationSrl?.setEnableLoadMore(false)
                            mBinding?.LibClassEmpty.visibility= View.VISIBLE
                        }else{
                            mBinding?.LibClassEmpty.visibility= View.GONE
                        }
                    }
                }
        }
    }
}