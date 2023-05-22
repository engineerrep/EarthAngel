package com.earth.libmine.record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.Constance
import com.earth.libbase.dialog.BaseDialogCommon
import com.earth.libbase.entity.PointRecordEntity
import com.earth.libbase.entity.RecordEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.FriendUserRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.view.loadingstatus.LoadingStatusCode
import com.earth.libmine.R
import com.earth.libmine.adapter.LibMineIGotAdapter
import com.earth.libmine.databinding.LibmineFragmentGotBinding
import com.earth.libmine.ui.LibMineModle
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LibMineIgotFragment : BaseFragment<LibmineFragmentGotBinding>() {
    private var pageNum: Int = 1


    private val mMineModle by viewModel<LibMineModle>()
    private var listString: ArrayList<RecordEntity> = ArrayList()


    private var mLibMineIGotAdapter: LibMineIGotAdapter? = null

    override fun getLayoutId(): Int = R.layout.libmine_fragment_got

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            mLibMineIGotAdapter = LibMineIGotAdapter(requireContext(), listString, upDelete = {
                delete(listString[it])

            }, upMessage = {
                message(listString[it])

            })
            val mlayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            LibMineGotRlv.layoutManager = mlayoutManager
            LibMineGotRlv.adapter = mLibMineIGotAdapter
            mBinding?.statusCode = LoadingStatusCode.Loading

            mLibMineIGotAdapter?.setOnItemClickListener { adapter, view, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.RecordDetailsActivityURL)
                        .withSerializable("RECORD",listString[position])
                        .withString("userId",listString[position].toUserId)
                        .navigation()
                }
            }

            LibMineGotSRL.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageNum = 1
                    LibMineGotSRL.setEnableLoadMore(true)
                    listString.clear()
                    getScoreData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    getScoreData()
                }
            })
            getScoreData()

        }


    }

    private fun getScoreData() {
        launch {
            mMineModle.myTradingRecord(
                CommentRequest(
                    pageNum = pageNum,
                    pageSize = 10,
                    tradingType = "ASK_FOR"
                )
            )
                .onStart {
                }.catch {
                    mBinding?.statusCode = LoadingStatusCode.Error

                }.onCompletion {

                }.collectLatest {
                    mBinding?.statusCode = LoadingStatusCode.Succeed

                    if (it.isOk(requireContext())) {
                        it.data?.list?.let {
                            listString.addAll(it)
                            mLibMineIGotAdapter?.notifyDataSetChanged()
                        }
                    }
                    if (listString.size == 0) {
                        mBinding?.statusCode = LoadingStatusCode.Empty
                    }
                    mBinding?.LibMineGotSRL?.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                }
        }
    }

    private fun message(mRecordEntity: RecordEntity) {
        ARouter.getInstance().build(Constance.ChatListActivityURL)
            .withString("userid", mRecordEntity.toUserId)
            .withString("chatName", mRecordEntity.toNickName)
            .navigation()
    }

    private fun delete(mRecordEntity: RecordEntity) {
        var blockDialog = BaseDialogCommon(
            title = "Sure to delete the record?\n" +
                    " It's never found after deleting.",
            onBlockClick = {
                deleteData(mRecordEntity)
            })
        activity?.let {
            blockDialog.show(
                it?.supportFragmentManager, ""
            )
        }
    }

    private fun deleteData(mRecordEntity: RecordEntity) {
        launch {
            mMineModle.myTradingRecordDelete(FriendUserRequest(id =mRecordEntity.id.toLong() ))
                .onStart {
                }.catch {

                }.onCompletion {

                }.collectLatest {

                    if (it.isOk(requireContext())) {
                        listString.remove(mRecordEntity)
                        mLibMineIGotAdapter?.notifyDataSetChanged()
                    }

                }
        }
    }

}