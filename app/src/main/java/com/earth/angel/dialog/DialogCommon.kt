package com.earth.angel.dialog

import android.os.Bundle
import android.view.View
import com.earth.angel.R
import com.earth.angel.databinding.DialogCommonBinding
import com.earth.libbase.base.BaseDialogFragment


class DialogCommon(
    var title: String?=null,
    var content: String?=null,
    var onBlockClick: () -> Unit = {},
    var onDissMissClick: () -> Unit = {}
) :
    BaseDialogFragment<DialogCommonBinding>() {


    override fun layoutId() = R.layout.dialog_common

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