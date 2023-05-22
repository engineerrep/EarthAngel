package com.earth.libhome.packet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.earth.libbase.base.*
import com.earth.libbase.baseentity.BasePocketEntity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.PocketDeleteRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.PreferencesHelper
import com.earth.libhome.R
import com.earth.libhome.adapter.HomePackingBagAdapter
import com.earth.libhome.databinding.ActivityHomePackingbagBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.libhome_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

@Route(path = Constance.HomePackingBagActivityURL)
class HomePackingBagActivity : BaseActivity<ActivityHomePackingbagBinding>() {

    private val mPocketModle by viewModel<PocketModle>()
    private val mAppViewModel by viewModel<AppViewModel>()

    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private var mHomePackingBagAdapter: HomePackingBagAdapter? = null
    private var listPocket: ArrayList<BasePocketEntity> = ArrayList()

    override fun getLayoutId(): Int = R.layout.activity_home_packingbag

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {

            tvLibHomeLeftTool.setOnClickListener {
                finish()
            }

            tvLibMinTitleCenter.text = getString(R.string.base_Cart)
            mHomePackingBagAdapter = HomePackingBagAdapter(
                this@HomePackingBagActivity,
                listPocket
            ) { str: String, main: Int, group: Int ->

                var id = listPocket[main].poketList[group].releaseRecordId
                poketBagPageList(id, main, group)

            }
            val layoutManager1 = LinearLayoutManager(this@HomePackingBagActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            HomePaketRlv.layoutManager = layoutManager1
            HomePaketRlv.adapter = mHomePackingBagAdapter

            mAppViewModel?.showLoadingProgress.observe(
                this@HomePackingBagActivity,
                androidx.lifecycle.Observer {
                    if (it) mLoadingDialog?.showAllowStateLoss(
                        supportFragmentManager,
                        mLoadingDialog::class.simpleName!!
                    )
                    else mLoadingDialog?.dismiss()
                })
            poketBagPageList()
        }
    }

    private fun poketBagPageList() {
        launch {
            mPocketModle.poketBagPageList(CommentRequest(pageNum = 1, pageSize = 10))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@HomePackingBagActivity)) {
                        it.data?.list?.let {
                            listPocket.addAll(it)
                            mHomePackingBagAdapter?.notifyDataSetChanged()
                        }
                        it.data?.total?.let {
                            if (it == 0) {
                                mBinding.HomePaketEmpty.visibility = View.VISIBLE
                            } else {
                                mBinding.HomePaketEmpty.visibility = View.GONE
                            }
                        }

                    }
                }
        }
    }

    private fun poketBagPageList(str: String, main: Int, group: Int) {
         var listPocketNum: ArrayList<Long> = ArrayList()
        listPocketNum.add(str.toLong())
        launch {
            mPocketModle.poketBagdelete(PocketDeleteRequest(listPocketNum))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@HomePackingBagActivity)) {

                        listPocket[main].poketList.removeAt(group)
                        if (listPocket[main].poketList.size == 0) {
                            listPocket.removeAt(main)
                        }
                        mHomePackingBagAdapter?.notifyDataSetChanged()
                        listPocket?.let {
                            if (listPocket.size == 0) {
                                mBinding.HomePaketEmpty.visibility = View.VISIBLE
                            } else {
                                mBinding.HomePaketEmpty.visibility = View.GONE
                            }
                        }
                        getUser()
                    }
                }
        }
    }

    private fun getUser() {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mPocketModle.userSelectOne(CommonRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(this@HomePackingBagActivity)) {
                            it.data?.let { ituser ->
                                PreferencesHelper.saveMyProfileCache(
                                    BaseApplication.instance,
                                    Gson().toJson(ituser)
                                )
                                ituser.poketNumber?.let {
                                    MessageObservable.messageObservable.newMessage(UpdateEntity(pockNum = it))
                                }
                            }
                        }
                    }
            }
        }
    }

}