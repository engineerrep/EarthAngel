package com.earth.libarticl

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libarticl.adapter.ArticleMainAdapter
import com.earth.libarticl.databinding.FragmentLibarticleBinding
import com.earth.libbase.base.*
import com.earth.libbase.entity.ArticleMainEntity
import com.earth.libbase.network.request.UserUpdateRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.BaseScreenUtil
import com.earth.libbase.util.PreferencesHelper
import kotlinx.android.synthetic.main.fragment_libarticle.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.sp
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class LibArticleFragment : BaseFragment<FragmentLibarticleBinding>(), Observer {
    private val mArticleModle by viewModel<ArticleModle>()
    private var mAdapter: ArticleMainAdapter? = null
    private var listString: ArrayList<ArticleMainEntity> = ArrayList()
    private var pageNum: Int = 1

    private var headView: View? = null
    private var ivMainMessageNum: TextView? = null
    private var animator: ObjectAnimator? = null


    override fun getLayoutId(): Int = R.layout.fragment_libarticle

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            MessageObservable.messageObservable.addObserver(this@LibArticleFragment)
            headView = layoutInflater.inflate(R.layout.head_main_article, null)
            listString.add(
                ArticleMainEntity(
                    title = getString(R.string.Post_used_product),
                    content = getString(R.string.Post_used_contend)
                )
            )
            listString.add(
                ArticleMainEntity(
                    title = getString(R.string.Get_Eco_credit),
                    content = getString(R.string.Get_Eco_contend)
                )
            )
            listString.add(
                ArticleMainEntity(
                    title = getString(R.string.Exchange_other_product),
                    content = getString(R.string.Exchange_other_content)
                )
            )

            mAdapter = ArticleMainAdapter(requireContext(), listString)
            var mLinearLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            LibArticleRlv?.run {
                layoutManager = mLinearLayoutManager
                adapter = mAdapter
            }

            headView?.let {
                val ivMainMessage: ImageView = it.findViewById(R.id.ivMainMessage)
                val ivGroupInter: ImageView = it.findViewById(R.id.ivGroupInter)

                translate(ivGroupInter)
                ivMainMessageNum = it.findViewById(R.id.ivMainMessageNum)
                val ivMainBg: ImageView = it.findViewById(R.id.ivMainBg)
                ivMainBg.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance()
                            .build(Constance.GroupListActivityURL)
                            .navigation()
                    }
                }
                val lp: ConstraintLayout.LayoutParams =
                    ivMainMessage.layoutParams as ConstraintLayout.LayoutParams
                lp.setMargins(
                    0,
                    BaseScreenUtil.getActivityMessageHeight(),
                    requireActivity().sp(18),
                    0
                )
                ivMainMessage.layoutParams = lp
                ivMainMessage.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                        ARouter.getInstance()
                            .build(Constance.ChatMainFragmentURL)
                            .navigation()
                    }
                }

                mAdapter?.addHeaderView(it)
            }

            PreferencesHelper.getMessage(BaseApplication.instance).let {
                if (it == "0" || it == "") {
                    ivMainMessageNum?.visibility = View.GONE
                    ivMainMessageNum?.text = it
                } else {
                    ivMainMessageNum?.visibility = View.VISIBLE
                    ivMainMessageNum?.text = it
                }
            }

            mAdapter?.setOnItemClickListener { adapter, view, position ->
                if (BaseDateUtils.isFastClick()) {

                }
            }
            tvMainPost.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.PhotoPostActivityURL).navigation()
                }
            }
        }

    }


    private fun translate(imageView: ImageView) {
        animator = ObjectAnimator.ofFloat(imageView, "translationX", 0f, 50f, 0f, 0f)

        animator!!.duration = 1500 //动画时间

        animator!!.interpolator = BounceInterpolator() //实现反复移动的效果

        animator!!.repeatCount = -1 //设置动画重复次数

        animator!!.interpolator = LinearInterpolator() // 插值器，匀速

        animator!!.start() //启动动画


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

    override fun update(o: Observable?, arg: Any?) {
        o?.let {
            if (it is MessageObservable) {
                arg?.let {
                    var bean = it as UpdateEntity
                    bean?.let {
                        it.messageNum?.let {
                            if (it == "0") {
                                ivMainMessageNum?.visibility = View.GONE
                                ivMainMessageNum?.text = it
                            } else {
                                ivMainMessageNum?.visibility = View.VISIBLE
                                ivMainMessageNum?.text = it
                            }
                        }
                    }

                }
            }
        }
    }
}