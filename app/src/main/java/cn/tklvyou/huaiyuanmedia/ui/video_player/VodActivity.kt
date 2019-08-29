package cn.tklvyou.huaiyuanmedia.ui.video_player

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.widget.SeekBar
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.ui.receiver.Observer
import cn.tklvyou.huaiyuanmedia.ui.receiver.PhoneCallStateObserver
import kotlinx.android.synthetic.main.activity_vod_player.*
import kotlinx.android.synthetic.main.activity_vod_player.btnBack
import java.util.*


/**
 * 播放页面
 */

class VodActivity : BaseActivity<NullPresenter>() {

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_vod_player
    }

    private var mVideoPath: String? = null
    private lateinit var mAudioManager: AudioManager
    private val UP_DATE_CODE = 555
    private val FADE_OUT = 1
    private var isFull = false
    private var mShowing = true
    private var sDefaultTimeout = 3000

    @SuppressLint("HandlerLeak")
    private var mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                FADE_OUT -> {
                    hideMediaController()
                }

                UP_DATE_CODE -> {
                    //当前时间
                    val currentPosition = mVideoView.currentPosition

                    //总时间
                    val duration = mVideoView.duration
                    //设置时间
                    mMediaTime.text = stringForTime(currentPosition)
                    mMediaTotalTime.text = stringForTime(duration)

                    //设置进度
                    mMediaProgress.max = duration.toInt()
                    mMediaProgress.progress = currentPosition.toInt()

                    sendEmptyMessageDelayed(UP_DATE_CODE, 500)

                }
            }

        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        hideTitleBar()
        btnBack.setOnClickListener {
            mVideoView.stopPlayback()
            finish()
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //保持屏幕常亮
        PhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, true)
        parseIntent()
        initAudioManager()
        setFullScreen()
        setPlayer()
        setScrollSeek()
        initPlayer()
    }


    private fun initAudioManager() {
        //获取音频管理器
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        mMediaSoundsProgress.max = streamMaxVolume
        mMediaSoundsProgress.progress = streamVolume

        //声音调节进度条
        mMediaSoundsProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }


    //处理与电话逻辑
    private val localPhoneObserver = Observer<Int> { phoneState ->
        if (phoneState == TelephonyManager.CALL_STATE_IDLE) {
            mVideoView.start()
        } else if (phoneState == TelephonyManager.CALL_STATE_RINGING) {
            mVideoView.pause()
        } else {
            Log.i("VodActivity", "localPhoneObserver onEvent " + phoneState!!)
        }
    }


    private fun parseIntent() {
        //接收MainActivity传过来的参数
        mVideoPath = intent.getStringExtra("videoPath")
    }

    private fun initPlayer() {
        mDYLoading.start()


        mVideoView.setBufferingIndicator(mDYLoading)
        mVideoView.setVideoPath(mVideoPath)
        mVideoView.setOnErrorListener { p0 ->
//            when (p0) {
//                PLOnErrorListener.MEDIA_ERROR_UNKNOWN -> {
//                    ToastUtils.showShort("未知错误")
//                }
//                PLOnErrorListener.ERROR_CODE_OPEN_FAILED -> {
//                    ToastUtils.showShort("播放器打开失败")
//                }
//                PLOnErrorListener.ERROR_CODE_IO_ERROR -> {
//                    ToastUtils.showShort("网络异常")
//                }
//                PLOnErrorListener.ERROR_CODE_SEEK_FAILED -> {
//                    ToastUtils.showShort("拖动失败")
//                }
//                PLOnErrorListener.ERROR_CODE_CACHE_FAILED -> {
//                    ToastUtils.showShort("预加载失败")
//                }
//                PLOnErrorListener.ERROR_CODE_HW_DECODE_FAILURE -> {
//                    ToastUtils.showShort("硬解失败")
//                }
//                PLOnErrorListener.ERROR_CODE_PLAYER_DESTROYED -> {
//                    ToastUtils.showShort("播放器已被销毁")
//                }
//                PLOnErrorListener.ERROR_CODE_PLAYER_VERSION_NOT_MATCH -> {
//                    ToastUtils.showShort("so 库版本不匹配，需要升级")
//                }
//                PLOnErrorListener.ERROR_CODE_PLAYER_CREATE_AUDIO_FAILED -> {
//                    ToastUtils.showShort("AudioTrack 初始化失败，可能无法播放音频")
//                }
//
//                else ->{
//                    ToastUtils.showShort("未知错误！")
//                }
//            }
            true
        }
        mVideoView.setOnCompletionListener {
            mMediaActions.setImageResource(R.drawable.ic_video_play)
            sDefaultTimeout = 0
            showMediaController(sDefaultTimeout)
        }
    }

    private fun setPlayer() {
        mMediaActions.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (mVideoView.isPlaying) {
                    mMediaActions.setImageResource(R.drawable.ic_video_play)
                    mVideoView.pause()
                    mHandler.removeMessages(UP_DATE_CODE)
                } else {
                    sDefaultTimeout = 3000
                    mMediaActions.setImageResource(R.drawable.exo_icon_pause)
                    mVideoView.start()
                    mHandler.sendEmptyMessage(UP_DATE_CODE)
                }
            }
        })
    }

    private fun setScrollSeek() {
        //播放器的进度条监听
        mMediaProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mMediaTime.text = stringForTime(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mHandler.removeMessages(UP_DATE_CODE)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                mVideoView.seekTo(progress.toLong())
                mHandler.sendEmptyMessage(UP_DATE_CODE)
            }
        })
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //当为横屏时候
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setConfigWh(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            isFull = true

            //音量键的显示
            mMediaSounds.visibility = View.VISIBLE
            mMediaSoundsProgress.visibility = View.VISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        } else {
            //当为竖屏时候
//            setConfigWh(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 240))
            isFull = false

            mMediaSounds.visibility = View.GONE
            mMediaSoundsProgress.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        }
    }

    private fun setFullScreen() {
        //设置全屏播放
        mMediaFullScreen.setOnClickListener {
            if (isFull) {
                //此时是全屏
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else {
                //此时是半屏
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

            }
        }
    }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        showMediaController(sDefaultTimeout)
        return true
    }

    private fun showMediaController(timeout: Int) {
        if (!mShowing) {
            mediaControllerLayout.visibility = View.VISIBLE
            mShowing = true
        }
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT)
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout.toLong())
        }
    }


    private fun hideMediaController() {
        if (mShowing) {
            mediaControllerLayout.visibility = View.GONE
            mShowing = false
        }
    }

    override fun onResume() {
        super.onResume()
        mVideoView.start()
        mHandler.sendEmptyMessage(UP_DATE_CODE)
        showMediaController(sDefaultTimeout)
    }

    override fun onPause() {
        super.onPause()
        mVideoView.pause()
    }

    override fun onStop() {
        super.onStop()
        mVideoView.stopPlayback()
    }


    override fun onBackPressed() {
        mVideoView.stopPlayback()
        finish()
        super.onBackPressed()
    }


    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeMessages(UP_DATE_CODE)
        mHandler.removeMessages(FADE_OUT)
        PhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, false)
    }

    private fun stringForTime(position: Long): String {
        val totalSeconds = (position / 1000.0 + 0.5).toInt()
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

}
