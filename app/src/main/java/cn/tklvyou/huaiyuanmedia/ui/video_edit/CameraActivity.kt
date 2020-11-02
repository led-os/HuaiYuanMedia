package cn.tklvyou.huaiyuanmedia.ui.video_edit

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.MyApplication
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues.*
import cn.tklvyou.huaiyuanmedia.ui.video_edit.record.beans.MediaObject
import cn.tklvyou.huaiyuanmedia.ui.video_edit.record.other.MagicFilterType
import cn.tklvyou.huaiyuanmedia.ui.video_edit.record.pop.PopupManager
import cn.tklvyou.huaiyuanmedia.ui.video_edit.record.ui.CameraView
import cn.tklvyou.huaiyuanmedia.ui.video_edit.record.ui.ProgressView
import cn.tklvyou.huaiyuanmedia.ui.video_edit.record.ui.SlideGpuFilterGroup
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_camera.*
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : BaseActivity<NullPresenter>(), View.OnTouchListener, View.OnClickListener, SlideGpuFilterGroup.OnFilterChangeListener {

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_camera
    }

    private val TAG = "RecorderActivity"

    private var videoFileName = ""

    //视频最大录制时长
    private val VIDEO_MAX_TIME = 90 * 1000
    var mNum = 0
    private var mLastTime: Long = 0
    var mRecordTimeInterval: Float = 0.toFloat()
    private var executorService: ExecutorService? = null
    private var mMediaObject: MediaObject? = null

    private val mMyHandler = MyHandler(this)
    private var isRecording = false

    private var page = "原创"
    private var takePictureType = false
    override fun initView(savedInstanceState: Bundle?) {
        baseTitleBar.hideTitleBar()

        val onlyVideo = intent.getBooleanExtra("is_video", false)
        page = intent.getStringExtra("page")

        initMagicIndicator(onlyVideo)

        if (mMediaObject == null) {
            mMediaObject = MediaObject()
        }

        mMatchingBack.setOnClickListener(this)
        mVideoRecordFinishIv.setOnClickListener(this)
        mMeetCamera.setOnClickListener(this)
        mBtnSpeed.setOnClickListener(this)
        mIndexDelete.setOnClickListener(this)
        mIndexAlbum.setOnClickListener(this)
        mCustomRecordImageView.setOnClickListener(this)
        mCountDownTv.setOnClickListener(this)
        mVideoFilter.setOnClickListener(this)

        executorService = Executors.newSingleThreadExecutor()
        mRecordCameraView.setOnTouchListener(this)
        mRecordCameraView.setOnFilterChangeListener(this)
        mVideoRecordProgressView.setMaxDuration(VIDEO_MAX_TIME, false)
        mVideoRecordProgressView.setOverTimeClickListener(object : ProgressView.OverTimeClickListener {
            override fun overTime() {
                mCustomRecordImageView.performClick()
            }

            override fun noEnoughTime() {
                mVideoRecordFinishIv.visibility = View.VISIBLE
            }

            override fun isArriveCountDown() {
                mCustomRecordImageView.performClick()
            }
        })

        mVideoRecordFinishIv.visibility = View.INVISIBLE

    }

    override fun onResume() {
        super.onResume()
        mVideoRecordProgressView.setData(mMediaObject)
    }

    override fun onClick(view: View) {
        if (System.currentTimeMillis() - mLastTime < 500) {
            return
        }
        mLastTime = System.currentTimeMillis()
        if (view.id != R.id.mIndexDelete) {
            if (mMediaObject != null) {
                val part = mMediaObject!!.currentPart
                if (part != null) {
                    if (part.remove) {
                        part.remove = false
                        if (mVideoRecordProgressView != null)
                            mVideoRecordProgressView.invalidate()
                    }
                }
            }
        }
        when (view.id) {

            R.id.mMatchingBack -> onBackPressed()

            R.id.mVideoRecordFinishIv -> {
                onStopRecording()
                mMyHandler.postDelayed({
                    if (mMediaObject != null) {
                        videoFileName = mMediaObject!!.mergeVideo()
                    }
                    VideoOptionActivity.launch(this@CameraActivity, videoFileName, page)
                    finish()
                }, 500)
            }

            R.id.mMeetCamera -> {
                mRecordCameraView.switchCamera()
                if (mRecordCameraView.cameraId == 1) {
                    mRecordCameraView.changeBeautyLevel(3)
                } else {
                    mRecordCameraView.changeBeautyLevel(0)
                }
            }
            R.id.mIndexDelete -> {
                val part = mMediaObject!!.getCurrentPart()
                if (part != null) {
                    if (part.remove) {
                        part.remove = false
                        mMediaObject!!.removePart(part, true)
                        if (mMediaObject!!.medaParts.size == 0) {
                            mIndexDelete.visibility = View.GONE
                            magicIndicator.visibility = View.VISIBLE
//                            mIndexAlbum.visibility = View.VISIBLE
                        }
                        if (mMediaObject!!.getDuration() < RECORD_MIN_TIME) {
                            mVideoRecordFinishIv.visibility = View.INVISIBLE
                            mVideoRecordProgressView.setShowEnouchTime(false)
                        }
                    } else {
                        part.remove = true
                    }
                }
            }


            R.id.mIndexAlbum -> {
                ToastUtils.showShort("敬请期待")
            }


            R.id.mCustomRecordImageView -> {
                if (takePictureType) {
                    onTakePicture()
                } else {
                    if (!isRecording) {
                        onStartRecording()
                    } else {
                        onStopRecording()
                    }
                }
            }

            R.id.mCountDownTv -> {
                val duration = mMediaObject!!.getDuration() / 1000
                hideOtherView()
                PopupManager(this).showCountDown(resources, duration, object : PopupManager.SelTimeBackListener {
                    override fun selTime(selTime: String, isDismiss: Boolean) {
                        if (!isDismiss) {
                            showOtherView()
                        } else {
                            mRecordTimeInterval = (java.lang.Float.parseFloat(selTime) - duration) * 1000
                            if (mRecordTimeInterval >= 29900) {
                                mRecordTimeInterval = 30350f
                            }
                            hideAllView()
                            mMyHandler.sendEmptyMessage(CHANGE_IMAGE)
                        }
                    }
                })
            }

            R.id.mVideoFilter -> {
                if (mRecordCameraView.cameraId == 0) {
                    Toast.makeText(this, "后置摄像头 不使用美白磨皮功能", Toast.LENGTH_SHORT).show()
                    return
                }
                hideOtherView()
                PopupManager(this).showBeautyLevel(mRecordCameraView.beautyLevel, object : PopupManager.SelBeautyLevel {
                    override fun selBeautyLevel(level: Int) {
                        showOtherView()
                        mRecordCameraView.changeBeautyLevel(level)
                    }
                })
            }

            R.id.mBtnSpeed -> {

            }
        }
    }

    override fun onFilterChange(type: MagicFilterType?) {
//        runOnUiThread {
//            if (type === MagicFilterType.NONE) {
//                ToastUtils.showShort("取消滤镜")
//            } else {
//                ToastUtils.showShort("当前滤镜切换为--$type")
//            }
//        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        mRecordCameraView.onTouch(event)

        if (mRecordCameraView.cameraId == 1) {
            return false
        }

        when (event.getAction()) {
            MotionEvent.ACTION_UP -> {
                val sRawX = event.getRawX()
                val sRawY = event.getRawY()
                var rawY = sRawY * MyApplication.screenWidth / MyApplication.screenHeight
                val rawX = rawY
                rawY = (MyApplication.screenWidth - sRawX) * MyApplication.screenHeight / MyApplication.screenWidth

                val point = Point(rawX.toInt(), rawY.toInt())
                mRecordCameraView.onFocus(point) { success, camera ->
                    if (success) {
                        mRecorderFocusIv.onFocusSuccess()
                    } else {
                        mRecorderFocusIv.onFocusFailed()
                    }
                }
                mRecorderFocusIv.startFocus(Point(sRawX.toInt(), sRawY.toInt()))
            }
        }

        return true
    }

    private fun initMagicIndicator(onlyVideo: Boolean) {
        val mFragmentContainerHelper = FragmentContainerHelper()
        val mDataList = ArrayList<String>()
        if (!onlyVideo) {
            takePictureType = true
            mDataList.add("拍照")
        }

        mDataList.add("录像")

        magicIndicator.setBackgroundColor(Color.BLACK)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isSkimOver = true
        val padding = UIUtil.getScreenWidth(this) / 2
        commonNavigator.rightPadding = padding
        commonNavigator.leftPadding = padding
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val clipPagerTitleView = ClipPagerTitleView(context)
                clipPagerTitleView.text = mDataList[index]
                clipPagerTitleView.textColor = Color.parseColor("#888888")
                clipPagerTitleView.clipColor = Color.WHITE
                clipPagerTitleView.setOnClickListener {
                    mFragmentContainerHelper.handlePageSelected(index)
                    if (!onlyVideo) { //同时支持拍照跟录像
                        if (index == 0) { //拍照界面样式
                            takePictureType = true
                            mVideoRecordProgressView.visibility = View.INVISIBLE
//                            mIndexAlbum.visibility = View.INVISIBLE
                            mCustomRecordImageView.setBackColor(Color.WHITE)
                        } else { //录制界面样式
                            takePictureType = false
                            mVideoRecordProgressView.visibility = View.VISIBLE
                            mCustomRecordImageView.setBackColor(Color.parseColor("#fc4253"))
                        }
                    }

                }
                return clipPagerTitleView
            }

            override fun getCount(): Int {
                return mDataList.size
            }

            override fun getIndicator(context: Context?): IPagerIndicator? {
                val indicator = BezierPagerIndicator(context)
                indicator.setColors(Color.WHITE, Color.RED)
                return indicator
            }

        }

        magicIndicator.navigator = commonNavigator
        mFragmentContainerHelper.attachMagicIndicator(magicIndicator)
        mFragmentContainerHelper.handlePageSelected(0, false)
    }


    private fun onTakePicture() {
        mRecordCameraView.doTakePicture(object : CameraView.OnTakePictureListener {
            override fun doTakePicture(bitmap: Bitmap, bool: Boolean) {
                val fileName = StaticFinalValues.SAVETOPHOTOPATH + System.currentTimeMillis() + ".jpeg"
                if (ImageUtils.save(bitmap, File(fileName), Bitmap.CompressFormat.JPEG)) {
                    val intent = Intent(this@CameraActivity, PicturePreviewActivity::class.java)
                    intent.putExtra("page", page)
                    intent.putExtra("path", fileName)
                    startActivity(intent)
                    finish()
                } else {
                    ToastUtils.showShort("保存图片失败，请重试")
                }
            }
        })
    }

    private fun onStartRecording() {
        isRecording = true
        val storageMp4 = getStorageMp4(System.currentTimeMillis().toString())
        val mediaPart = mMediaObject!!.buildMediaPart(storageMp4)
        mRecordCameraView.setSavePath(storageMp4)
        mRecordCameraView.startRecord()
        mCustomRecordImageView.startRecord()
        mVideoRecordProgressView.start()
        alterStatus()
    }

    private fun onStopRecording() {
        isRecording = false
        mRecordCameraView.stopRecord {
            mMyHandler.sendEmptyMessageDelayed(DELAY_DETAL, 100)
            LogUtils.e(System.currentTimeMillis())
        }
        mVideoRecordProgressView.stop()
        mCustomRecordImageView.stopRecord()
        alterStatus()

    }

    private fun showOtherView() {
        if (mMediaObject != null && mMediaObject!!.getMedaParts().size == 0) {
            mIndexDelete.visibility = View.GONE
            magicIndicator.visibility = View.VISIBLE
            mVideoRecordFinishIv.visibility = View.GONE
//            mIndexAlbum.visibility = View.VISIBLE
        } else {
            mIndexDelete.visibility = View.VISIBLE
            magicIndicator.visibility = View.INVISIBLE
            mVideoRecordFinishIv.visibility = View.VISIBLE
//            mIndexAlbum.visibility = View.GONE
        }
        mVideoFilter.visibility = View.VISIBLE
        mCountDownTv.visibility = View.VISIBLE
        mMatchingBack.visibility = View.VISIBLE
        mCustomRecordImageView.visibility = View.VISIBLE
        mMeetCamera.visibility = View.VISIBLE
        mBtnSpeed.visibility = View.VISIBLE
        mBtnMeiHua.visibility = View.VISIBLE
        mBtnLed.visibility = View.VISIBLE
    }

    private fun hideOtherView() {
        magicIndicator.visibility = View.INVISIBLE
        mMeetCamera.visibility = View.INVISIBLE
        mBtnSpeed.visibility = View.INVISIBLE
        mBtnMeiHua.visibility = View.INVISIBLE
        mBtnLed.visibility = View.INVISIBLE
        mIndexAlbum.visibility = View.INVISIBLE
        mIndexDelete.visibility = View.INVISIBLE
        mVideoRecordFinishIv.visibility = View.INVISIBLE
        mVideoFilter.visibility = View.INVISIBLE
        mCountDownTv.visibility = View.INVISIBLE
        mMatchingBack.visibility = View.INVISIBLE
        mCustomRecordImageView.visibility = View.INVISIBLE
    }

    private fun hideAllView() {
        hideOtherView()
        mVideoRecordFinishIv.visibility = View.GONE
        mVideoRecordProgressView.visibility = View.GONE
    }

    //正在录制中
    private fun alterStatus() {
        if (isRecording) {
            magicIndicator.visibility = View.INVISIBLE
            mMeetCamera.visibility = View.INVISIBLE
            mBtnSpeed.visibility = View.INVISIBLE
            mBtnMeiHua.visibility = View.INVISIBLE
            mBtnLed.visibility = View.INVISIBLE
//            mIndexAlbum.visibility = View.INVISIBLE
            mIndexDelete.visibility = View.INVISIBLE
            mVideoRecordFinishIv.visibility = View.INVISIBLE
            mVideoFilter.visibility = View.INVISIBLE
            mCountDownTv.visibility = View.INVISIBLE
            mMatchingBack.visibility = View.INVISIBLE
        } else {
            if (mMediaObject != null && mMediaObject!!.getMedaParts().size == 0) {
                mIndexDelete.visibility = View.GONE
                magicIndicator.visibility = View.VISIBLE
                mVideoRecordFinishIv.visibility = View.GONE
//                mIndexAlbum.visibility = View.VISIBLE
            } else {
                mIndexDelete.visibility = View.VISIBLE
                mVideoRecordFinishIv.visibility = View.VISIBLE
                magicIndicator.visibility = View.INVISIBLE
//                mIndexAlbum.visibility = View.GONE
            }
            mVideoFilter.visibility = View.VISIBLE
            mCountDownTv.visibility = View.VISIBLE
            mMatchingBack.visibility = View.VISIBLE
            mMeetCamera.visibility = View.VISIBLE
            mVideoRecordProgressView.visibility = View.VISIBLE
            mMeetCamera.visibility = View.VISIBLE
            mBtnSpeed.visibility = View.VISIBLE
            mBtnMeiHua.visibility = View.VISIBLE
            mBtnLed.visibility = View.VISIBLE
        }
    }

    private class MyHandler(videoRecordActivity: CameraActivity) : Handler() {

        private val mVideoRecordActivity: WeakReference<CameraActivity>

        init {
            mVideoRecordActivity = WeakReference<CameraActivity>(videoRecordActivity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mVideoRecordActivity.get()
            if (activity != null) {
                when (msg.what) {
                    DELAY_DETAL -> activity.mMediaObject!!.stopRecord(activity, activity.mMediaObject)
                    CHANGE_IMAGE -> {
                        when (activity.mNum) {
                            0 -> {
                                activity.mCountTimeDownIv.setVisibility(View.VISIBLE)
                                activity.mCountTimeDownIv.setImageResource(R.drawable.bigicon_3)
                                activity.mMyHandler.sendEmptyMessageDelayed(CHANGE_IMAGE, 1000)
                            }
                            1 -> {
                                activity.mCountTimeDownIv.setImageResource(R.drawable.bigicon_2)
                                activity.mMyHandler.sendEmptyMessageDelayed(CHANGE_IMAGE, 1000)
                            }
                            2 -> {
                                activity.mCountTimeDownIv.setImageResource(R.drawable.bigicon_1)
                                activity.mMyHandler.sendEmptyMessageDelayed(CHANGE_IMAGE, 1000)
                            }
                            else -> {
                                activity.mMyHandler.removeCallbacks(null)
                                activity.mCountTimeDownIv.setVisibility(View.GONE)
                                activity.mVideoRecordProgressView.setVisibility(View.VISIBLE)
                                activity.mCustomRecordImageView.setVisibility(View.VISIBLE)
                                activity.mCustomRecordImageView.performClick()
                                activity.mVideoRecordProgressView.setCountDownTime(activity.mRecordTimeInterval)
                            }
                        }
                        if (activity.mNum >= 3) {
                            activity.mNum = 0
                        } else {
                            activity.mNum++
                        }
                    }
                    OVER_CLICK -> activity.mCustomRecordImageView.performClick() //定时结束
                }
            }
        }
    }

    private fun getStorageMp4(s: String): String {
        val file: File
        val parent = Environment.getExternalStorageDirectory().absolutePath + "/HuaiYuan"
        val file1 = File(parent)
        if (!file1.exists()) {
            file1.mkdir()
        }
        file = File(parent, "$s.mp4")

        return file.path
    }


    override fun onDestroy() {
        super.onDestroy()
        mRecordCameraView.onDestroy()
    }

}
