package com.earth.angel.gift.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.earth.angel.R
import com.earth.angel.databinding.ActivityGroupEditBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.util.DataReportUtils
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.entity.HouseListEntity
import com.earth.libbase.entity.SearchDetailEntity
import com.earth.libbase.util.SoftKeyboardUtils
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupEditActivity : BaseActivity<ActivityGroupEditBinding>() {
    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()

    private var giftHouseEntity: HouseListEntity? = null

    private var searchDetailEntity: SearchDetailEntity? = null
    private var mhouseNumber: Long? = 0

    override fun getLayoutId(): Int =R.layout.activity_group_edit

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {

            showActionBar()
            giftHouseEntity = intent.getSerializableExtra("giftHouse") as HouseListEntity?
            searchDetailEntity=intent.getSerializableExtra("SearchDetailEntity") as SearchDetailEntity?
            giftHouseEntity?.run {
                mhouseNumber=number.toLong()
                etName.setText(this.name)
                tvTitleCenter.text = this.name
            }
            searchDetailEntity?.run {
                mhouseNumber=number.toLong()
                etName.setText(this.name)
                tvTitleCenter.text = this.name
            }
            tvQuit.setOnClickListener {
                mhouseNumber?.let {
                    var blockDialog = DialogCommon(title = "Confirm quit?",onBlockClick = {
                        quitGroup(it)
                    })
                    blockDialog.show(
                        supportFragmentManager, ""
                    )
                }
            }
            etName.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        SoftKeyboardUtils.closeInoutDecorView(this@GroupEditActivity)
                        DataReportUtils.getInstance().report("Search_keyboard")
                        var etStr = etName.text.toString().trim()
                        if(etStr.isEmpty()){
                            return true
                        }
                        if(etStr.length<3||etStr.length>20){
                            toast(getString(R.string.label_group_Length))
                            return true
                        }
                        updateGroup(mhouseNumber,etStr)
                        return true
                    }
                    return false
                }
            })
        }
    }
    private fun quitGroup(houseNumber: Long) {
        launch {
            mEcoGiftGorupsModel.quitGroup(houseNumber).catch { }
                .collectLatest {
                    if (it.isOk(this@GroupEditActivity)) {
                        setResult(103)
                        finish()
                    }
                }
        }
    }
    private fun updateGroup(houseNumber: Long?,houseName: String) {
        houseNumber?.let {
            launch {
                mEcoGiftGorupsModel.updateGroup(it,houseName).catch { }
                    .collectLatest {
                        if (it.isOk(this@GroupEditActivity)) {
                            setResult(104, Intent().putExtra("GroupName",houseName))
                            finish()
                        }
                    }
            }
        }

    }
}