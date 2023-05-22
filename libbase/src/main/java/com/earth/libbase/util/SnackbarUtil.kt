package com.earth.libbase.util

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.earth.libbase.R
import com.earth.libbase.base.BaseApplication
import com.google.android.material.snackbar.Snackbar


object SnackbarUtil {

    fun showError(view: CoordinatorLayout, message: String) {

        val snackbar = Snackbar.make(
            view, message, Snackbar.LENGTH_LONG
        )
        val mView = snackbar.view
        val tvSnackBarText: TextView = mView.findViewById(R.id.snackbar_text)

        val lp: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.gravity=Gravity.CENTER
        tvSnackBarText.layoutParams=lp
        tvSnackBarText.gravity= Gravity.CENTER

        tvSnackBarText.setTextColor(ContextCompat.getColor(BaseApplication.instance,R.color.text_error))
        mView.setBackgroundColor(
            ContextCompat.getColor(BaseApplication.instance,
                R.color.transparent))
        snackbar.show()

    }
}