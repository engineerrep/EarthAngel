package com.earth.libarticl

import android.os.Build
import android.os.Bundle
import android.webkit.*
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libarticl.databinding.ActivityArticletextBinding
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.base.MessageObservable
import com.earth.libbase.base.UpdateEntity
import com.earth.libbase.dialog.BaseDialogCommon
import com.earth.libbase.dialog.PointDialogCommon
import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.entity.SingEntity
import com.earth.libbase.network.request.CommentRequest
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


@Route(path = Constance.ArticleTextActivityURL)
class ArticleTextActivity : BaseActivity<ActivityArticletextBinding>() {
    private val mArticleModle by viewModel<ArticleModle>()


    override fun getLayoutId(): Int = R.layout.activity_articletext

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {
            showActionBar()
            ArticleImageViewBank.setOnClickListener {
                finish()
            }
            var mArticleEntity: ArticleEntity =
                intent.getSerializableExtra("ArticleEntity") as ArticleEntity
            mArticleEntity?.run {
                val settings: WebSettings = ArticleTv.getSettings()
                // 设置WebView支持JavaScript
                // 设置WebView支持JavaScript
                settings.javaScriptEnabled = true
                //支持自动适配
                //支持自动适配
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.setSupportZoom(false) //支持放大缩小

                settings.builtInZoomControls = false //显示缩放按钮

                settings.blockNetworkImage = true // 把图片加载放在最后来加载渲染

                settings.allowFileAccess = true // 允许访问文件

                settings.saveFormData = true
                settings.setGeolocationEnabled(true)
                settings.domStorageEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true /// 支持通过JS打开新窗口

                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                //设置不让其跳转浏览器
                //设置不让其跳转浏览器
                ArticleTv.setWebViewClient(object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        return false
                    }
                })

                // 添加客户端支持

                // 添加客户端支持
                ArticleTv.setWebChromeClient(WebChromeClient())
                // mWebView.loadUrl(TEXTURL);

                //不加这个图片显示不出来
                // mWebView.loadUrl(TEXTURL);

                //不加这个图片显示不出来
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    ArticleTv.getSettings()
                        .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
                }
                ArticleTv.getSettings().setBlockNetworkImage(false)

                //允许cookie 不然有的网站无法登陆

                //允许cookie 不然有的网站无法登陆
                val mCookieManager: CookieManager = CookieManager.getInstance()
                mCookieManager.setAcceptCookie(true)
                mCookieManager.setAcceptThirdPartyCookies(ArticleTv, true)


                getData(uniqueCode)
                singIn()
            }
        }
    }

    private fun getData(uniqueCode: String) {
        launch {
            mArticleModle.pageSelectOne(CommentRequest(uniqueCode = uniqueCode))
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@ArticleTextActivity)) {
                        it.data?.content?.let {
                            mBinding?.ArticleTv.loadDataWithBaseURL(null, it, "text/html", "utf-8", null);
                        }
                    }
                }
        }
    }
    fun singIn() {
        launch {
            mArticleModle.readRecord()
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@ArticleTextActivity)) {
                        it.data?.let {
                            it.whetherNeed?.let { whetherNeed ->
                                if(whetherNeed== SingEntity.NEED){
                                    var title="Get 10 Eco credit for first \n reading every day."
                                    val blockDialog = PointDialogCommon(
                                        content = title,
                                        onBlockClick = {

                                        })
                                    blockDialog.show(supportFragmentManager, title)

                                    MessageObservable.messageObservable.newMessage(UpdateEntity(messagePoint = UpdateEntity.POINT))

                                }
                            }
                        }
                    }
                }
        }
    }

}