package com.earth.angel.view

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.text.TextUtils
import com.earth.angel.base.EarthAngelApp


class AudioPlayer private constructor() {
    private var mRecordCallback: Callback? = null
    private var mPlayCallback: Callback? = null
    lateinit var path: String
        private set
    private var mPlayer: MediaPlayer? = null
    private var mRecorder: MediaRecorder? = null
    private val mHandler = Handler()
    fun startRecord(callback: Callback?) {
        mRecordCallback = callback
        try {
            path = CURRENT_RECORD_FILE + System.currentTimeMillis() + ".m4a"
            mRecorder = MediaRecorder()
            mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            // 使用mp4容器并且后缀改为.m4a，来兼容小程序的播放
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mRecorder!!.setOutputFile(path)
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mRecorder!!.prepare()
            mRecorder!!.start()
            // 最大录制时间之后需要停止录制
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({
                stopInternalRecord()
                onRecordCompleted(true)
                mRecordCallback = null
            }, 60 * 1000.toLong())
        } catch (e: Exception) {
            stopInternalRecord()
            onRecordCompleted(false)
        }
    }

    fun stopRecord() {
        stopInternalRecord()
        onRecordCompleted(true)
        mRecordCallback = null
    }

    private fun stopInternalRecord() {
        mHandler.removeCallbacksAndMessages(null)
        if (mRecorder == null) {
            return
        }
        mRecorder!!.release()
        mRecorder = null
    }

    fun startPlay(filePath: String, callback: Callback?) {
        path = filePath
        mPlayCallback = callback
        try {
            mPlayer = MediaPlayer()
            mPlayer!!.setDataSource(filePath)
            mPlayer!!.setOnCompletionListener {
                stopInternalPlay()
                onPlayCompleted(true)
            }
            mPlayer!!.prepare()
            mPlayer!!.start()
        } catch (e: Exception) {
            stopInternalPlay()
            onPlayCompleted(false)
        }
    }

    fun stopPlay() {
        stopInternalPlay()
        onPlayCompleted(false)
        mPlayCallback = null
    }

    private fun stopInternalPlay() {
        if (mPlayer == null) {
            return
        }
        mPlayer!!.release()
        mPlayer = null
    }

    val isPlaying: Boolean
        get() = mPlayer != null && mPlayer!!.isPlaying

    private fun onPlayCompleted(success: Boolean) {
        if (mPlayCallback != null) {
            mPlayCallback!!.onCompletion(success)
        }
        mPlayer = null
    }

    private fun onRecordCompleted(success: Boolean) {
        if (mRecordCallback != null) {
            mRecordCallback!!.onCompletion(success)
        }
        mRecorder = null
    }

    val duration: Int
        get() {
            if (TextUtils.isEmpty(path)) {
                return 0
            }
            var duration = 0
            try {
                val mp = MediaPlayer()
                mp.setDataSource(path)
                mp.prepare()
                duration = mp.duration
                duration = if (duration < MIN_RECORD_DURATION) {
                    0
                } else {
                    duration + MAGIC_NUMBER
                }
            } catch (e: Exception) {
            }
            if (duration < 0) {
                duration = 0
            }
            return duration
        }

    interface Callback {
        fun onCompletion(success: Boolean?)
    }

    companion object {
        private val TAG = AudioPlayer::class.java.simpleName
        val instance = AudioPlayer()
        private val CURRENT_RECORD_FILE = EarthAngelApp.instance.cacheDir.absolutePath + "auto_"
        private const val MAGIC_NUMBER = 500
        private const val MIN_RECORD_DURATION = 1000
    }

}