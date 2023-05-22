package com.earth.libmine.classification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.entity.CategoryEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.view.loadingstatus.LoadingStatusCode
import com.earth.libmine.R
import com.earth.libmine.adapter.ClassificationMainAdapter
import com.earth.libmine.databinding.ActivityClassificationMainBinding
import com.earth.libmine.ui.LibMineModle
import kotlinx.android.synthetic.main.libmine_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
@Route(path = Constance.ClassificationMainActivityURL)
class ClassificationMainActivity : BaseActivity<ActivityClassificationMainBinding>() {

    private var adapter: ClassificationMainAdapter?=null
    private var list: ArrayList<CategoryEntity> = ArrayList()
    private val mMineModle by viewModel<LibMineModle>()

    override fun getLayoutId(): Int =R.layout.activity_classification_main

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            showActionBar()
            tvLibMineLeftTool.setOnClickListener {
                finish()
            }
            tvLibMinTitleCenter.text=getString(R.string.base_Category)
            var mG=GridLayoutManager(this@ClassificationMainActivity,2)
            adapter=ClassificationMainAdapter(this@ClassificationMainActivity,list)
            ClassifiCationRlv.layoutManager=mG
            ClassifiCationRlv.adapter=adapter

            adapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.ClassificationListActivityURL)
                        .withString("itemCode", list[position].itemCode.toString())
                        .withString("itemText", list[position].description)
                        .navigation()
                }
            }

            getData()
        }
    }

    private fun getData() {
        launch {
            mMineModle.categoryList()
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@ClassificationMainActivity)) {
                        it.data?.let {
                            list.addAll(it)
                            adapter?.notifyDataSetChanged()
                        }
                        if(list.isEmpty()){
                            mBinding?.LibEmpty.visibility= View.VISIBLE
                        }else{
                            mBinding?.LibEmpty.visibility= View.GONE
                        }
                    }
                }
        }
    }

}