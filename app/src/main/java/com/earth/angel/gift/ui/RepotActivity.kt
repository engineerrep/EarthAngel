package com.earth.angel.gift.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.ActivityRepotBinding
import com.earth.angel.gift.adapter.ReportAdapter
import com.earth.angel.search.SearchModel
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.entity.GiftEntity
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepotActivity : BaseActivity<ActivityRepotBinding>() {
    private val mViewModel by viewModel<SearchModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    private var list: ArrayList<String>? = ArrayList()

    private var mReportAdapter: ReportAdapter?=null
    private var giftEntity: GiftEntity? = null

    override fun getLayoutId(): Int =R.layout.activity_repot

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            showActionBar()
            btSubmit.isEnabled=false
            ReportAdapter.selectPosition=-1
            tvTitleCenter.text = getString(R.string.lab_report)
            giftEntity = intent.getSerializableExtra("giftEntity") as GiftEntity?
            mAppViewModel.showLoadingProgress.observe(this@RepotActivity,
                {
                    if (it) mLoadingDialog.showAllowStateLoss(
                        supportFragmentManager,
                        mLoadingDialog::class.simpleName!!
                    )
                    else mLoadingDialog.dismiss()
                })
            list?.add(getString(R.string.text_Items))
            list?.add(getString(R.string.text_Fraud))
            list?.add(getString(R.string.text_Spam))
            list?.add(getString(R.string.text_Inappropriate))
            list?.add(getString(R.string.text_Other))
            mReportAdapter=ReportAdapter(list)
            val layoutManager1 = LinearLayoutManager(this@RepotActivity)
            rlv.layoutManager = layoutManager1
            rlv.adapter=mReportAdapter
            mReportAdapter?.setOnItemClickListener { adapter, view, position ->

                ReportAdapter.selectPosition=position
                mReportAdapter?.notifyDataSetChanged()

                btSubmit.isEnabled = ReportAdapter.selectPosition!=-1

            }
            btSubmit.setOnClickListener {
              if(ReportAdapter.selectPosition!=-1){
                  report(list!![ReportAdapter.selectPosition])
              }

            }

        }
    }
    private fun report(str: String) {
        giftEntity?.let {
            launch {
                mViewModel.report(str, it.id.toLong()).catch {}
                    .onStart { mAppViewModel.showLoading()}
                    .onCompletion { mAppViewModel.dismissLoading()}
                    .collectLatest {
                        if (it.isOk(this@RepotActivity)) {
                            toast(getString(R.string.text_success))
                            finish()
                        }
                    }
            }
        }
    }

}