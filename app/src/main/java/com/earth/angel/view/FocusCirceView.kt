package com.earth.angel.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation

import android.view.animation.ScaleAnimation




class FocusCirceView(context: Context?, attrs: AttributeSet?=null, defStyleAttr: Int=0) :
    View(context, attrs, defStyleAttr) {

    private var paint: Paint? = null
    private var mX = (width / 2 //默认
            ).toFloat()
    private var mY = (height / 2).toFloat()

    fun setPoint(x: Float, y: Float) {
        mX = x
        mY = y
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint= Paint()
        paint?.color = Color.parseColor("#cccccc");
        paint?.setStyle(Paint.Style.STROKE);//空心圆
        paint?.setStrokeWidth(4F);
        canvas?.drawCircle(mX,mY, 20F, paint!!);
        canvas?.drawCircle(mX,mY,20F,paint!!);



    }
    fun deleteCanvas() {
        paint!!.reset()
        invalidate()
    }
    fun myViewScaleAnimation(myView: View) {
        val animation = ScaleAnimation(
            1.1f, 1f, 1.1f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.duration = 300
        animation.fillAfter = false
        animation.repeatCount = 0
        myView.startAnimation(animation)
    }

}