package com.earth.angel.search

import android.Manifest
import android.app.Service
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.appphoto.PhotoEditActivity
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.databinding.FragmentShakeBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.event.ShakeEvent
import com.earth.angel.gift.ui.UserMainActivity
import com.earth.angel.mine.MineModel
import com.earth.angel.search.adapter.Shakedapter
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.angel.util.EventBusHelper
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.requestPermissions
import com.earth.libbase.entity.UserLocationEntity
import com.earth.libbase.network.request.LocationRequest
import com.earth.libbase.util.LocationUtil
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel


class ShakeFragment : BaseActivity<FragmentShakeBinding>() {
    private val mViewModel by viewModel<SearchModel>()
    private val mMineModel by viewModel<MineModel>()

    //上一次晃动手机的时间
    private var lastTime: Long = 0
    private var soundPool: SoundPool? = null
    private var sound1 = 0
    private var vibrator: Vibrator? = null
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    private var mShakedapter: Shakedapter? = null
    private var list: ArrayList<UserLocationEntity>? = ArrayList()
    private var pageNum: Int = 1
    private var blockDialog: DialogCommon? = null

    override fun getLayoutId(): Int = R.layout.fragment_shake
    private var headView: View? = null
    private var longitude: Double? = null
    private var latitude: Double? = null
    private var flag: Boolean? =false
    override fun initActivity(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(Service.SENSOR_SERVICE) as SensorManager
        //获得一个加速度传感器
         sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //注册传感器监听，
        //1.监听器
        //2.加速度传感器
        //3.传感器灵敏度
        //传感器灵敏度分为四级，从上往下灵敏度依次降低
        //SENSOR_DELAY_FASTEST
        //SENSOR_DELAY_GAME
        //SENSOR_DELAY_UI
        //SENSOR_DELAY_NORMAL
        sensorManager!!.registerListener(
            sensorEventListener,
            sensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        initSoundPool()
        vibrator = getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        EventBusHelper.register(this)

        mBinding?.run {
            initShake(false)
            showActionBar()
  /*          val lp: ConstraintLayout.LayoutParams = rlEcoTopToolbar.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(0, ScreenUtil.getActivityMessageHeight(this@ShakeFragment), 0, 0)
            rlEcoTopToolbar.layoutParams = lp*/

            list?.clear()
            mShakedapter = Shakedapter(this@ShakeFragment, list,upDade = {
                if (DateUtils.isFastClick()){
                    flag=true
                    //解除对加速度传感器的监听
                    sensorManager!!.unregisterListener(sensorEventListener)
                    if (soundPool != null) {
                        //声音池释放资源
                        soundPool!!.release()
                    }
                    UserMainActivity.openUserMainActivity(this@ShakeFragment, it)
                }

            })
            val layoutManager1 = LinearLayoutManager(this@ShakeFragment)
            rlvShakeYes.layoutManager = layoutManager1
            rlvShakeYes.adapter = mShakedapter
            headView = layoutInflater.inflate(R.layout.head_shake, null)
            tvConnectBtn.setOnClickListener {
                getPermissions()
            }
            headView?.run {

            }
            PreferencesHelper.getLocation(this@ShakeFragment)?.let {
                if (it) {
                    mBinding?.lyConnect?.visibility = View.GONE
                } else {
                    mBinding?.lyConnect?.visibility = View.VISIBLE
                }
            }
        }

    }
    var sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = Math.abs(event.values[0])
            val y = Math.abs(event.values[1])
            val z = Math.abs(event.values[2])
            if (x > 17 || y > 17 || z > 17) {
                playSound()
                onShake()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
    private fun playSound() {
        //1.声音的id
        //2.3.表示左右声道的音量
        //4.优先级
        //5.是否循环
        //6.声音播放速率
        soundPool!!.play(sound1, 1f, 1f, 0, 0, 1f)
        //手机震动
        //1.表示震动的节奏off/on/off/on/off/on......
        //2.表示是否重复震动，-1表示不重复
        vibrator!!.vibrate(longArrayOf(100, 200, 100, 200, 100, 200), -1)
    }
    /**
     * 初始化声音池
     */
    private fun initSoundPool() {
        soundPool = if (Build.VERSION.SDK_INT > 20) {
            val builder = SoundPool.Builder()
            //1.最大并发流数
            builder.setMaxStreams(3)
            val aaBuilder = AudioAttributes.Builder()
            aaBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
            builder.setAudioAttributes(aaBuilder.build())
            builder.build()
        } else {
            SoundPool(3, AudioManager.STREAM_MUSIC, 0)
        }
        //加载一个音频文件
        sound1 = soundPool!!.load(this@ShakeFragment, R.raw.aw, 1)
    }
    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionsList.add(1, Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions {
            permissions = permissionsList
            onAllPermissionsGranted = {
                PreferencesHelper.saveLocation(this@ShakeFragment, true)
                if (LocationUtil().getLocationByNet(this@ShakeFragment) != null) {
                    latitude = LocationUtil().getLocationByNet(this@ShakeFragment).doublelist[0]
                    longitude = LocationUtil().getLocationByNet(this@ShakeFragment).doublelist[1]
                    mBinding?.lyConnect?.visibility = View.GONE
                }
            }
            onPermissionsDenied = {
                mBinding?.lyConnect?.visibility = View.VISIBLE
            }
            onPermissionsNeverAsked = {
                mBinding?.lyConnect?.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
            sensorManager!!.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )

    }

    override fun onStop() {
        super.onStop()
        sensorManager!!.unregisterListener(sensorEventListener)
        if (soundPool != null) {
            //声音池释放资源
            soundPool!!.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)
    }


/*    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            DataReportUtils.getInstance().report("Shake")
            sensorManager!!.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        } else {
            flag=false
            //解除对加速度传感器的监听
            sensorManager!!.unregisterListener(sensorEventListener)
            if (soundPool != null) {
                //声音池释放资源
                soundPool!!.release()
            }
        }
    }*/



     fun onShake(){
        if (DateUtils.isFastClick()) {
            pageNum++
            EarthAngelApp.myProfileEntity?.run {
                if (releasesNumber.toInt() > 0) {
                    DataReportUtils.getInstance().report("Shake_photo")
                    initShake(true)
                    postLocation()
                } else {
                    if (blockDialog == null) {
                            blockDialog = DialogCommon(
                                title = getString(R.string.dialog_Upload_continue),
                                onBlockClick = {
                                    DataReportUtils.getInstance().report("Shake_upload")
                                    flag=true
                                    blockDialog = null
                                    //解除对加速度传感器的监听
                                    sensorManager!!.unregisterListener(sensorEventListener)
                                    if (soundPool != null) {
                                        //声音池释放资源
                                        soundPool!!.release()
                                    }
                                    PhotoEditActivity.openPhotoEditActivity(this@ShakeFragment, true)
                                },
                                onDissMissClick = {
                                    DataReportUtils.getInstance().report("Nexttime")
                                    blockDialog = null
                                })
                            blockDialog?.show(
                                this@ShakeFragment.supportFragmentManager, ""
                            )

                    }
                }
            }

        }
        //   val values = objs[0] as FloatArray
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: ShakeEvent) {
        if(event.id==null){
            // 发布一个物品后
            list?.clear()
            mShakedapter?.notifyDataSetChanged()
            pageNum = 1
            select()
        }else{
            //点进主页以后，更新
            for(item in list!!){
                if(item.id==event.id){
                    item.requestFrendsStatus=event.requestFrendsStatus!!
                    break
                }
            }
            mShakedapter?.notifyDataSetChanged()
        }

    }



    private fun select() {
        launch {
            mMineModel.selectMine().catch { }.collectLatest {
                if (it.isOk(this@ShakeFragment)) {
                    it.data?.let { mine ->
                        PreferencesHelper.saveMyProfileCache(this@ShakeFragment, Gson().toJson(mine))
                    }
                }
            }
        }
    }

    private fun postLocation() {
        launch {

            mViewModel.postLocation(LocationRequest(pageNum,5,latitude,longitude)).catch {}
                .onStart { }
                .onCompletion { }
                .collectLatest {
                    if (it.isOk(this@ShakeFragment)) {
                        if (pageNum == 1) {
                            headView?.let { itview ->
                                mShakedapter?.removeAllHeaderView()
                                mShakedapter?.addHeaderView(itview)
                            }
                        }
                        it.data?.let {
                            if (it.size < 5) {
                                pageNum == 1
                            }
                            list?.clear()
                            list?.addAll(it)
                            mShakedapter?.notifyDataSetChanged()
                        }

                    }
                }
        }

    }

    private fun initShake(flag: Boolean) {
        mBinding?.run {
            if (flag) {
                ivShake.visibility = View.GONE
                tvLooking.visibility = View.GONE

            } else {
                ivShake.visibility = View.VISIBLE
                tvLooking.visibility = View.VISIBLE
            }
        }
    }
}