package com.earth.angel.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.earth.angel.R


/**
 * 聊天界面输入控件
 */
class ChatInput(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs),
    View.OnClickListener {
    private var btnAdd: ImageView? = null
    private var btnVoiceOrKeyboard: ImageView? = null
    private var btnAlbum: ImageView? = null
    private var btnCamera: ImageView? = null
    private var tvSendVoice: TextView? = null
    private var btnAlbumTv: TextView? = null
    private var btnCameraTv: TextView? = null
    private var editText: EditText? = null
    private var isHoldVoiceBtn = false
    private var inputMode = InputMode.NONE
    private val REQUEST_CODE_ASK_PERMISSIONS = 100
    private val voiceMaxTime = 30 //秒
    private val voiceTime = 0 //秒
    private var isSend = true
    private var isVoice = false


    private var mAudioCancel = false
    private var mStartRecordY = 0f
    private lateinit var mOnChatVoiceListener: OnChatVoiceListener

    private var onInputListener: OnInputListener? = null
    fun setOnInputListener(onInputListener: OnInputListener?) {
        this.onInputListener = onInputListener
    }

    fun setOnChatVoiceListener(mOnChatVoiceListener: OnChatVoiceListener) {
        this.mOnChatVoiceListener = mOnChatVoiceListener
    }


    private fun initView() {
        val activity = context as Activity
        btnAdd = findViewById<View>(R.id.btn_add) as ImageView
        btnAdd!!.setOnClickListener(this)
        btnVoiceOrKeyboard = findViewById<View>(R.id.btn_voice_or_keyboard) as ImageView
        btnVoiceOrKeyboard!!.setOnClickListener(this)
        btnAlbum = findViewById<View>(R.id.btn_album) as ImageView
        btnAlbum!!.setOnClickListener(this)
        btnCamera = findViewById<View>(R.id.btn_camera) as ImageView
        btnCamera!!.setOnClickListener(this)
        tvSendVoice = findViewById<View>(R.id.tv_send_voice) as TextView
        btnAlbumTv = findViewById<View>(R.id.btn_album_tv) as TextView
        btnAlbumTv!!.setOnClickListener(this)
        btnCameraTv = findViewById<View>(R.id.btn_camera_tv) as TextView
        btnCameraTv!!.setOnClickListener(this)
        editText = findViewById<View>(R.id.et_input) as EditText
        editText!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                updateView(InputMode.TEXT)
            }
        }
        editText!!.setOnEditorActionListener(TextView.OnEditorActionListener { view, p1, keyEvent ->
            onInputListener!!.sendText(
                view.text.toString()
            )
        })
        editText?.run {
            doAfterTextChanged {
                val content = it.toString()
                if (content.isNullOrEmpty()) {
                    btnAdd?.setImageResource(R.mipmap.chat_send_disable)
                } else {
                    btnAdd?.setImageResource(R.mipmap.chat_send_enable)
                }
                //   checkCanLogin()
            }
        }
        tvSendVoice!!.setOnTouchListener { v, event ->

            if (activity != null && requestAudio(activity)) {

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mAudioCancel = true
                        mStartRecordY = event.getY()
                        mOnChatVoiceListener?.onRecordStatusChanged(OnChatVoiceListener.RECORD_START)
                        tvSendVoice!!.background =
                            ContextCompat.getDrawable(context, R.drawable.chat_send_voice_hover)
                        AudioPlayer.instance.startRecord(object : AudioPlayer.Callback {
                            override fun onCompletion(success: Boolean?) {
                                recordComplete(success!!)
                            }
                        })
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.getY() - mStartRecordY < -100) {
                            mAudioCancel = true
                            mOnChatVoiceListener?.onRecordStatusChanged(OnChatVoiceListener.RECORD_CANCEL)
                        } else {
                            if (mAudioCancel) {
                                mOnChatVoiceListener?.onRecordStatusChanged(OnChatVoiceListener.RECORD_START)
                            }
                            mAudioCancel = false
                        }
                        tvSendVoice!!.background =
                            ContextCompat.getDrawable(context, R.drawable.chat_send_voice_hover)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        mAudioCancel = event.getY() - mStartRecordY < -100
                        mOnChatVoiceListener?.onRecordStatusChanged(OnChatVoiceListener.RECORD_STOP)
                        AudioPlayer.instance.stopRecord()
                        tvSendVoice!!.background =
                            ContextCompat.getDrawable(context, R.drawable.chat_send_voice)
                    }
                }
                true

            } else {
                false
            }

        }


    }

    private fun recordComplete(success: Boolean) {
        val duration: Int = AudioPlayer.instance.duration

        if (mOnChatVoiceListener != null) {
            if (!success || duration == 0) {
                mOnChatVoiceListener.onRecordStatusChanged(OnChatVoiceListener.RECORD_FAILED)
                return
            }
            if (mAudioCancel) {
                mOnChatVoiceListener.onRecordStatusChanged(OnChatVoiceListener.RECORD_CANCEL)
                return
            }
            if (duration < 1000) {
                mOnChatVoiceListener.onRecordStatusChanged(OnChatVoiceListener.RECORD_TOO_SHORT)
                return
            }
            mOnChatVoiceListener.onRecordStatusChanged(OnChatVoiceListener.RECORD_STOP)
        }
        if (onInputListener != null && success) {
            onInputListener!!.sendVoice(AudioPlayer.instance.path, duration)
        }
    }

    private fun updateView(mode: InputMode) {
        if (mode == inputMode) return
        leavingCurrentState()
        when (mode.also { inputMode = it }) {
            InputMode.MORE -> {
                btnAlbum!!.visibility = VISIBLE
                btnAlbumTv!!.visibility = VISIBLE
                btnCamera!!.visibility = VISIBLE
                btnCameraTv!!.visibility = VISIBLE
                btnVoiceOrKeyboard!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.chat_pho
                    )
                )
                editText!!.visibility = VISIBLE
                tvSendVoice!!.visibility = GONE
            }
            InputMode.TEXT -> {
                if (editText!!.requestFocus()) {
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }
                btnAlbum!!.visibility = GONE
                btnAlbumTv!!.visibility = GONE
                btnCamera!!.visibility = GONE
                btnCameraTv!!.visibility = GONE
                btnVoiceOrKeyboard!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.chat_pho
                    )
                )
                editText!!.visibility = VISIBLE
                tvSendVoice!!.visibility = GONE

            }
            InputMode.VOICE -> {
                btnAlbum!!.visibility = GONE
                btnAlbumTv!!.visibility = GONE
                btnCamera!!.visibility = GONE
                btnCameraTv!!.visibility = GONE
                btnVoiceOrKeyboard!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.icon_chat_keyboard
                    )
                )
                editText!!.visibility = GONE
                tvSendVoice!!.visibility = VISIBLE
            }
        }
    }

    private fun leavingCurrentState() {
        when (inputMode) {
            InputMode.TEXT -> {
                val view = (context as Activity).currentFocus
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                editText!!.clearFocus()
                btnAlbum!!.visibility = GONE
                btnAlbumTv!!.visibility = GONE
                btnCamera!!.visibility = GONE
                btnCameraTv!!.visibility = GONE
                btnVoiceOrKeyboard!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.chat_pho
                    )
                )
            }
            InputMode.MORE -> {
                btnAlbum!!.visibility = VISIBLE
                btnAlbumTv!!.visibility = VISIBLE
                btnCamera!!.visibility = VISIBLE
                btnCameraTv!!.visibility = VISIBLE
                btnVoiceOrKeyboard!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.chat_pho
                    )
                )
            }
            InputMode.VOICE -> {
                btnAlbum!!.visibility = GONE
                btnAlbumTv!!.visibility = GONE
                btnCamera!!.visibility = GONE
                btnCameraTv!!.visibility = GONE
                btnVoiceOrKeyboard!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.icon_chat_keyboard
                    )
                )
            }
        }
    }


    override fun onClick(v: View) {
        val activity = context as Activity
        val id = v.id
        if (id == R.id.btn_add) {
            //  updateView(if (inputMode == InputMode.MORE) InputMode.TEXT else InputMode.MORE)

            if (editText?.text.toString().isNotEmpty()) {
                onInputListener!!.sendText(
                    editText?.text.toString()
                )
            }

        }
        if (id == R.id.btn_album || id == R.id.btn_album_tv) {
            if (activity != null && requestStorage(activity)) {
                if (onInputListener != null) {
                    onInputListener!!.sendAlbum()
                }
            }
        }
        if (id == R.id.btn_camera || id == R.id.btn_camera_tv) {
            if (activity != null && requestCamera(activity)) {
                if (onInputListener != null) {
                    onInputListener!!.sendCamera()
                }
            }
        }
        if (id == R.id.tv_send_voice) {
            if (activity != null && requestAudio(activity)) {
                updateView(InputMode.TEXT)
            }
        }
        if (id == R.id.btn_voice_or_keyboard) {
            /*    if (isVoice) {
                    updateView(InputMode.TEXT)
                } else {
                    updateView(InputMode.VOICE)
                }
                isVoice = !isVoice*/
            if (onInputListener != null) {
                onInputListener!!.sendAlbum()
            }
        }

    }

    /**
     * 获取输入框文字
     */
    val text: Editable
        get() = editText!!.text

    /**
     * 设置输入框文字
     */
    fun setText(text: String?) {
        editText!!.setText(text)
    }

    /**
     * 设置输入模式
     */
    fun setInputMode(mode: InputMode) {
        updateView(mode)
    }

    enum class InputMode {
        TEXT, VOICE, MORE, NONE
    }

    private fun requestCamera(activity: Activity): Boolean {
        if (afterM()) {
            val hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA)
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_ASK_PERMISSIONS
                )
                return false
            }
        }
        return true
    }

    private fun requestAudio(activity: Activity): Boolean {
        if (afterM()) {
            val hasPermission = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_CODE_ASK_PERMISSIONS
                )
                return false
            }
        }
        return true
    }

    private fun requestStorage(activity: Activity): Boolean {
        if (afterM()) {
            val hasPermission =
                activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_ASK_PERMISSIONS
                )
                return false
            }
        }
        return true
    }

    private fun afterM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.chat_input, this)
        initView()
    }
}

interface OnInputListener {
    fun sendVoice(path: String, duration: Int)
    fun sendText(text: String?): Boolean
    fun sendAlbum()
    fun sendCamera()
}

interface OnChatVoiceListener {
    fun onRecordStatusChanged(status: Int)

    companion object {
        const val RECORD_START = 1
        const val RECORD_STOP = 2
        const val RECORD_CANCEL = 3
        const val RECORD_TOO_SHORT = 4
        const val RECORD_FAILED = 5
    }
}