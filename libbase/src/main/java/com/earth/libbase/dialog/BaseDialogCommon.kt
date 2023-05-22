package com.earth.libbase.dialog

import android.os.Bundle
import android.view.View
import com.earth.libbase.R
import com.earth.libbase.base.BaseDialogFragment
import com.earth.libbase.databinding.BaseDialogCommonBinding


class BaseDialogCommon(
    var title: String?=null,
    var content: String?=null,
    var onBlockClick: () -> Unit = {},
    var onDissMissClick: () -> Unit = {}
) :
    BaseDialogFragment<BaseDialogCommonBinding>() {


    override fun layoutId() = R.layout.base_dialog_common

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.apply {
            tvBlock.setOnClickListener {
                onBlockClick()
                dismiss()
            }

            tvCancel.setOnClickListener {
                onDissMissClick()
                dismiss()
            }
            if(title.isNullOrEmpty()){
                tvTitle.visibility=View.GONE
            }else{
                tvTitle.visibility=View.VISIBLE
                tvTitle.text = title

            }
            if(content.isNullOrEmpty()){
                tvContent.visibility=View.GONE
            }else{
                tvContent.visibility=View.VISIBLE
                tvContent.text=content

            }

        }


    }


}