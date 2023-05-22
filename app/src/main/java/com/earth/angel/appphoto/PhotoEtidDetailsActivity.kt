package com.earth.angel.appphoto

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.appphoto.adapter.DetailsTextAdapter
import com.earth.angel.databinding.ActivityPhotoetidDetailsBinding
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.entity.CategoryEntity
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


@Route(path = Constance.PhotoEtidDetailsActivityURL)
class PhotoEtidDetailsActivity : BaseActivity<ActivityPhotoetidDetailsBinding>() {

    companion object {
        const val ConditionCode = 100
        const val CategoryCode = 101

    }

    @Autowired(name = "TYPE")
    @JvmField
    var typeString: String? = null
    @Autowired(name = "description")
    @JvmField
    var description: String? = null
    private val mPhotoModelModel by viewModel<PhotoModel>()
    private var mDetailsTextAdapter: DetailsTextAdapter? = null
    private val mConditionList: ArrayList<CategoryEntity> = ArrayList()
    private val mCategoryList: ArrayList<CategoryEntity> = ArrayList()
    private var mCategoryEntity: CategoryEntity? = null

    override fun getLayoutId(): Int = R.layout.activity_photoetid_details

    override fun initActivity(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this@PhotoEtidDetailsActivity)
        showActionBar()
        mBinding.run {
            tvLeftTool.setOnClickListener {
                finish()
            }
            tvTitleCenter.text=typeString
            typeString?.let {
                when (it) {
                    getString(R.string.base_Condition) -> {
                        PhotoEtidContentTitle.text = getString(R.string.base_Condition_Product)
                        PhotoEtidDetailsTV.text = getString(R.string.base_Condition_Choose)
                        mConditionList.add(
                            CategoryEntity(
                                description = getString(R.string.label_New_with_Box),
                                choose = false
                            )
                        )
                        mConditionList.add(

                            CategoryEntity(
                                description =getString(R.string.label_New_with_Defects),
                                choose = false
                            )
                        )
                        mConditionList.add(
                            CategoryEntity(
                                description = getString(R.string.label_New_without_Box),
                                choose = false
                            )

                        )
                        mConditionList.add(
                            CategoryEntity(
                                description = getString(R.string.label_Used),
                                choose = false
                            )

                        )
                        description?.let {
                            for(item in mConditionList){
                                if(item.description==it){
                                    item.choose=true
                                    mCategoryEntity =item

                                    break
                                }
                            }
                        }

                        mDetailsTextAdapter = DetailsTextAdapter(mConditionList, choose = {
                            mCategoryEntity = mConditionList[it]
                            for (item in mConditionList) {
                                item.choose = false
                            }
                            mConditionList[it].choose = true
                            mDetailsTextAdapter?.notifyDataSetChanged()
                        })
                    }
                    getString(R.string.base_Category) -> {
                        getData()
                        PhotoEtidContentTitle.text = getString(R.string.base_Category_Product)
                        PhotoEtidDetailsTV.text = getString(R.string.base_Category_Choose)

                        mDetailsTextAdapter = DetailsTextAdapter(mCategoryList, choose = {
                            mCategoryEntity = mCategoryList[it]
                            for (item in mCategoryList) {
                                item.choose = false
                            }
                            mCategoryList[it].choose = true
                            mDetailsTextAdapter?.notifyDataSetChanged()
                        })
                    }
                    else -> {
                    }

                }


            }
            val linearLayoutManager = LinearLayoutManager(
                this@PhotoEtidDetailsActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            PhotoEtidDetailsRlv.layoutManager = linearLayoutManager
            PhotoEtidDetailsRlv.adapter = mDetailsTextAdapter
            PhotoDetailsDone.setOnClickListener {
                mCategoryEntity?.let {
                    typeString?.let {
                        when (it) {
                            getString(R.string.base_Condition) -> {
                                val intent = Intent()
                                intent.putExtra("content", mCategoryEntity)
                                setResult(ConditionCode, intent)
                                finish()
                            }
                            getString(R.string.base_Category) -> {
                                val intent = Intent()
                                intent.putExtra("content", mCategoryEntity)
                                setResult(CategoryCode, intent)
                                finish()
                            }
                            else -> {
                            }
                        }
                    }
                }

            }
        }

    }

    private fun getData() {
        launch {
            mPhotoModelModel.categoryList()
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@PhotoEtidDetailsActivity)) {
                        it.data?.let {
                            mCategoryList.addAll(it)
                            description?.let {
                                for(item in mCategoryList){
                                    if(item.description==it){
                                        item.choose=true
                                        mCategoryEntity = item
                                        break
                                    }
                                }
                            }
                            mDetailsTextAdapter?.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

}