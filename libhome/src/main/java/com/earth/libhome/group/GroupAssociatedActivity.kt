package com.earth.libhome.group

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.entity.GroupEntity
import com.earth.libbase.network.request.CommentRequest
import com.earth.libhome.R
import com.earth.libhome.adapter.GroupAssociatedAdapter
import com.earth.libhome.databinding.ActivityGroupAssociatedBinding
import kotlinx.android.synthetic.main.libhome_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.GroupAssociatedActivityURL)
class GroupAssociatedActivity : BaseActivity<ActivityGroupAssociatedBinding>() {
    companion object {
        const val GroupAssociatedCode = 200
    }

    private val mGroupApiModle by viewModel<GroupApiModle>()


    private var mGroupPostAdapter: GroupAssociatedAdapter? = null
    private var listGroupPost: ArrayList<GroupEntity> = ArrayList()
    private var mList: ArrayList<GroupEntity>? = null
    override fun getLayoutId(): Int = R.layout.activity_group_associated

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            showActionBar()
            tvLibHomeLeftTool.setOnClickListener {
                exitPage()
            }
            tvLibMinTitleCenter.text = "Associated"

            toolbarLibHomeTabRight.text=getString(R.string.label_Save)
            toolbarLibHomeTabRight.setTextColor(ContextCompat.getColor(this@GroupAssociatedActivity, R.color.BaseThemColor))
            toolbarLibHomeTabRight.setOnClickListener {
                exitPage()
            }
            mList =
                intent.getSerializableExtra("Group") as ArrayList<GroupEntity>

            var gridLayoutManager = GridLayoutManager(this@GroupAssociatedActivity, 2)
            mGroupPostAdapter = GroupAssociatedAdapter(listGroupPost)
            rlvGroupAssociated.layoutManager = gridLayoutManager
            rlvGroupAssociated.adapter = mGroupPostAdapter
            mGroupPostAdapter?.setOnItemClickListener { adapter, view, position ->

                listGroupPost[position].isSelect = listGroupPost[position].isSelect != true
                mGroupPostAdapter?.notifyDataSetChanged()

            }

        }
        communityList()
    }

    private fun exitPage(){
        var listSelectGroupPost: ArrayList<GroupEntity> = ArrayList()
        for (item in listGroupPost) {
            if (item.isSelect) {
                listSelectGroupPost.add(item)
            }
        }
        val intent = Intent()
        intent.putExtra("Group", listSelectGroupPost)
        setResult(GroupAssociatedCode, intent)
        finish()
    }

    private fun communityList() {
        launch {
            mGroupApiModle.communityList(CommentRequest(pageNum = 1, pageSize = 100))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@GroupAssociatedActivity)) {
                        it.data?.list?.let {

                            listGroupPost.addAll(it)

                            for (item in listGroupPost) {
                                mList?.let {
                                    for (bean in it) {
                                        if (item.id == bean.id) {
                                            item.isSelect = true
                                        }
                                    }
                                }

                            }
                            mGroupPostAdapter?.notifyDataSetChanged()

                        }
                    }
                }
        }
    }

}