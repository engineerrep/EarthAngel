package com.earth.libmine.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.Constance
import com.earth.libbase.baseentity.BaseUser
import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.util.BaseDateUtils.isFastClick
import com.earth.libbase.view.loadingstatus.LoadingStatusCode
import com.earth.libmine.R
import com.earth.libmine.adapter.SaveUserItemAdapter
import com.earth.libmine.databinding.FragmentLibmineSaveenviBinding
import com.google.android.material.snackbar.Snackbar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yanzhenjie.recyclerview.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class LibMineSaveEnviFragment : BaseFragment<FragmentLibmineSaveenviBinding>() {
    private val mMineModle by viewModel<LibMineModle>()
    private var mSaveUserItemAdapter: SaveUserItemAdapter? = null
    private var listString: ArrayList<BaseUser> = ArrayList()
    private var pageNum: Int = 1

    override fun getLayoutId(): Int = R.layout.fragment_libmine_saveenvi

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            mSaveUserItemAdapter = SaveUserItemAdapter(activity, listString)
            // 加入的
            val layoutManager1 = LinearLayoutManager(activity)
            LibmineSaveUserRlv.layoutManager = layoutManager1
            LibmineSaveUserRlv.setSwipeMenuCreator(swipeMenuCreator)
            LibmineSaveUserRlv.setOnItemMenuClickListener(mMenuItemClickListener)
            LibmineSaveUserRlv.adapter = mSaveUserItemAdapter
            mSaveUserItemAdapter?.setOnItemClickListener { _, _, position ->
                if (isFastClick()) {
                    ARouter.getInstance().build(Constance.LibMineUserActivityURL)
                        .withString("userId", listString[position].userId)
                        .navigation()
                }

            }
            LibmineSaveUserSrl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listString.clear()
                    mSaveUserItemAdapter?.notifyDataSetChanged()
                    pageNum = 1
                    LibmineSaveUserSrl?.setEnableLoadMore(true)
                    concernedpageList()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageNum++
                    concernedpageList()
                }
            })
        }
        mBinding?.statusCode = LoadingStatusCode.Loading

        concernedpageList()
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private val swipeMenuCreator: SwipeMenuCreator = object : SwipeMenuCreator {
        override fun onCreateMenu(
            swipeLeftMenu: SwipeMenu,
            swipeRightMenu: SwipeMenu,
            position: Int
        ) {
            val width = resources.getDimensionPixelSize(R.dimen.head_image)

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            val height = ViewGroup.LayoutParams.MATCH_PARENT

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            run {
                val addItem: SwipeMenuItem =
                    SwipeMenuItem(activity).setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.base_saveuser_delete)
                        .setWidth(width)
                        .setHeight(height)
                //    swipeLeftMenu.addMenuItem(addItem) // 添加菜单到左侧。
                val closeItem: SwipeMenuItem =
                    SwipeMenuItem(activity).setBackground(R.mipmap.detalis_empty)
                        .setImage(R.mipmap.detalis_empty)
                        .setWidth(width)
                        .setHeight(height)
                //    swipeLeftMenu.addMenuItem(closeItem) // 添加菜单到左侧。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            run {
                val deleteItem: SwipeMenuItem =
                    SwipeMenuItem(activity).setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.base_saveuser_delete)
                        .setText("")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height)
                swipeRightMenu.addMenuItem(deleteItem) // 添加菜单到右侧。
                val addItem: SwipeMenuItem =
                    SwipeMenuItem(activity).setBackground(R.mipmap.detalis_empty)
                        .setText("添加")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height)
                //  swipeRightMenu.addMenuItem(addItem) // 添加菜单到右侧。
            }
        }
    }
    private val mMenuItemClickListener =
        OnItemMenuClickListener { menuBridge, position ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction // 左侧还是右侧菜单。
            val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                /*         Toast.makeText(
                             activity,
                             "list第$position; 右侧菜单第$menuPosition",
                             Toast.LENGTH_SHORT
                         )
                             .show()*/
                concernedDelete(position)
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(
                    activity,
                    "list第$position; 左侧菜单第$menuPosition",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    fun concernedpageList() {
        launch {
            mMineModle.concernedpageList(CommentRequest(pageNum = pageNum, pageSize = 10))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    mBinding?.statusCode = LoadingStatusCode.Succeed

                    if (it.isOk(activity)) {
                        it.data?.list?.let {
                            listString.addAll(it)
                            mSaveUserItemAdapter?.notifyDataSetChanged()
                        }
                    }
                    if (listString.size == it.data?.total) {
                        mBinding?.LibmineSaveUserSrl?.setEnableLoadMore(false)
                    }
                    if(listString.size==0){
                        mBinding?.LibmineSaveUserLL?.visibility=View.GONE
                    }else{
                        mBinding?.LibmineSaveUserLL?.visibility=View.VISIBLE
                    }
                    it.data?.total?.let {
                        mBinding?.LibmineSaveUserNum?.text = it.toString()
                        if (it == 1) {
                            mBinding?.tvLibmineSaveUserNum?.text = "Environmentalist"
                        } else {
                            mBinding?.tvLibmineSaveUserNum?.text = "Environmentalists"
                        }
                        if (it == 0) {
                            mBinding?.statusCode = LoadingStatusCode.Empty
                        }
                    }
                    mBinding?.LibmineSaveUserSrl?.let {
                        it.finishRefresh(true)
                        it.finishLoadMore(true)
                    }
                }
        }

    }

    private fun concernedDelete(positon: Int) {
        listString[positon].userId?.let {
            launch {
                mMineModle.concernedDelete(BaseUserRequest(userId = it.toLong()))
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        if (it.isOk(activity)) {
                            listString.removeAt(positon)
                            mSaveUserItemAdapter?.notifyDataSetChanged()
                            listString.let {
                                it.size.let {
                                    mBinding?.LibmineSaveUserNum?.text = it.toString()
                                    if (it == 1) {
                                        mBinding?.tvLibmineSaveUserNum?.text = "Environmentalist"
                                    } else {
                                        mBinding?.tvLibmineSaveUserNum?.text = "Environmentalists"
                                    }
                                    mBinding?.run {
                                        if (it == 0) {
                                            LibmineSaveUserLL?.visibility = View.GONE
                                            mBinding?.statusCode = LoadingStatusCode.Empty
                                        } else {
                                            LibmineSaveUserLL?.visibility = View.VISIBLE
                                        }
                                        val snackbar = Snackbar.make(
                                            CoordinatorLayout, "Successfully deleted 1 environmentalists", Snackbar.LENGTH_LONG
                                        )
                                        val mView = snackbar.view
                                        mView.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.BaseThemColor))
                                        snackbar.show()
                                    }
                                }
                            }
                        }
                    }
            }
        }


    }
}