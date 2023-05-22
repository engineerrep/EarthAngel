package com.earth.libarticl

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libarticl.adapter.ArticleAdapter
import com.earth.libarticl.databinding.ActivityLibarticleListBinding
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.UserUpdateRequest
import com.earth.libbase.util.BaseDateUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.LibArticleListActivityURL)
class LibArticleListActivity : BaseActivity<ActivityLibarticleListBinding>() {
    private val mArticleModle by viewModel<ArticleModle>()
    private var mAdapter: ArticleAdapter? = null
    var listString: ArrayList<ArticleEntity> = ArrayList()
    private var pageNum: Int = 1

    private var headView: View? = null
    override fun getLayoutId(): Int =R.layout.activity_libarticle_list

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {
            headView = layoutInflater.inflate(R.layout.head_article, null)
            headView?.let {
                val ivTitle: ImageView = it.findViewById(R.id.LibArticleTitle)
                setViewActionBarHight(ivTitle)
            }
            mAdapter = ArticleAdapter(this@LibArticleListActivity, listString)
            var mLinearLayoutManager =
                LinearLayoutManager(this@LibArticleListActivity, LinearLayoutManager.VERTICAL, false)
            LibArticleRlv.run {
                layoutManager = mLinearLayoutManager
                adapter = mAdapter
            }
            mAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                      ARouter.getInstance()
                           .build(Constance.ArticleTextActivityURL)
                           .withSerializable("ArticleEntity", listString[position])
                           .navigation()
                }
            }
            headView?.let {
                val mArticleImageViewBank : ImageView=it.findViewById(R.id.ArticleImageViewBank)
                mArticleImageViewBank.setOnClickListener {
                    finish()
                }
                mAdapter?.addHeaderView(it)
            }
            libArticleSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum = 1
                    libArticleSrl.setEnableLoadMore(true)
                    listString.clear()
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

    fun updateUser(liveIn: String) {
        launch {
            mArticleModle.updateUser(UserUpdateRequest(liveIn = liveIn))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                }
        }
    }
    private fun getData() {
        launch {
            mArticleModle.pageArticlesList(CommentRequest(pageSize = 10, pageNum = pageNum))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    mBinding.libArticleSrl.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                    if (it.isOk(this@LibArticleListActivity)) {
                        it.data?.list?.let {
                            listString.addAll(it)
                            mAdapter?.notifyDataSetChanged()
                        }
                    }
                    if (listString.size == it.data?.total) {
                        mBinding?.libArticleSrl?.setEnableLoadMore(false)
                    } else {
                        mBinding?.libArticleSrl?.setEnableLoadMore(true)
                    }
                }
        }
    }
}