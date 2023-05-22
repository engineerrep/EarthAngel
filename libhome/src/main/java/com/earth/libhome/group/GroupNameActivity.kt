package com.earth.libhome.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.network.request.GroupRequest
import com.earth.libhome.R
import com.earth.libhome.databinding.ActivityGroupNameBinding
import kotlinx.android.synthetic.main.libhome_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.GroupNameActivityURL)
class GroupNameActivity : BaseActivity<ActivityGroupNameBinding>() {
    @Autowired(name = "GroupName")
    @JvmField
    var mGroupName: String? = null

    @Autowired(name = "GroupID")
    @JvmField
    var id: String? = null
    private val mGroupApiModle by viewModel<GroupApiModle>()
    override fun getLayoutId(): Int = R.layout.activity_group_name

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        ARouter.getInstance().inject(this@GroupNameActivity)

        mBinding?.run {
            LibMineEtNameDone.isEnabled = false
            tvLibHomeLeftTool.setOnClickListener {
                finish()
            }
            mGroupName?.let {
                LibMineEtNameGroup.setText(it)
            }
            LibMineEtNameGroup.run {
                doAfterTextChanged {
                    checkCanLogin()
                }
            }
            LibMineEtNameDone?.setOnClickListener {
                var contentStr=mBinding.LibMineEtNameGroup.text.toString().trim()
                communityUpdate(GroupRequest(communityName = contentStr,id = id))
            }
        }
    }

    private fun checkCanLogin() {
        mBinding.LibMineEtNameDone.isEnabled = when {
            mBinding.LibMineEtNameGroup.text.isNullOrEmpty() -> false

            mBinding.LibMineEtNameGroup.text!!.length > 50 -> {
                false
            }

            else -> {
                true
            }
        }
    }
    private fun communityUpdate(bean: GroupRequest) {
        launch {
            mGroupApiModle.communityUpdate(bean)
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(this@GroupNameActivity)) {
                        var contentStr=mBinding.LibMineEtNameGroup.text.toString().trim()
                        val intent = Intent()
                        intent.putExtra("content", contentStr)
                        setResult(1, intent)
                        finish()
                    }

                }
        }
    }
}