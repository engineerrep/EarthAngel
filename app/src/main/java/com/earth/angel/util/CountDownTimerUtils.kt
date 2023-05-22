package com.earth.angel.util

import android.os.CountDownTimer
import android.widget.Button

class CountDownTimerUtils(var button: Button,
                          millisInFuture: Long,
                          countDownInterval: Long
) : CountDownTimer(millisInFuture, countDownInterval) {

    override fun onTick(millisUntilFinished: Long) {
        button?.isEnabled=false
        button?.text = (millisUntilFinished / 1000).toString()+"s"
    }

    override fun onFinish() {
        button?.text = ("Send")
        button?.isEnabled=true

    }
}