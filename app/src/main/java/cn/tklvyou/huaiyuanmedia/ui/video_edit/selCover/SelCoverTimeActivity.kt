package cn.tklvyou.huaiyuanmedia.ui.video_edit.selCover

import VideoHandle.EpEditor
import VideoHandle.OnEditorListener
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues.COMR_FROM_SEL_COVER_TIME_ACTIVITY
import cn.tklvyou.huaiyuanmedia.ui.video_edit.selCover.view.ThumbnailSelTimeView
import cn.tklvyou.huaiyuanmedia.utils.YFileUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_sel_cover_time.*
import kotlinx.android.synthetic.main.pop_video_loading.*
import java.lang.ref.WeakReference
import java.util.ArrayList

class SelCoverTimeActivity : BaseActivity<NullPresenter>() {

    companion object {
        val SEL_TIME = 0
        val SUBMIT = 1
        val SAVE_BITMAP = 2
    }

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_sel_cover_time
    }

    private var mVideoPath = ""


    private var mSelStartTime = 0.5f
    private var mIsSelTime: Boolean = false//是否点了完成按钮

    var mVideoHeight: Int = 0
    var mVideoWidth: Int = 0
    var mVideoDuration: Int = 0
    var mVideoRotation: String = ""
    private var mSelCoverAdapter: SelCoverAdapter? = null

    private val mBitmapList = ArrayList<Bitmap>()

    private val myHandler = MyHandler(this)

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("选择封面")
        setNavigationImage()
        setNavigationOnClickListener {
            saveCoverBitmapToSDCard()
        }
        setPositiveText("完成")
        setPositiveOnClickListener {
            mIsSelTime = true
            saveCoverBitmapToSDCard()
        }

        mVideoPath = intent.getStringExtra(StaticFinalValues.VIDEOFILEPATH)

        initThumbs()
        initSetParam()
        initRvView()
        initListener()
    }

    private fun saveCoverBitmapToSDCard() {
        setPositiveOnClickListener { }

        if (mIsSelTime) {
            if (mSelStartTime < 0.5f) {
                mSelStartTime = 0.5f
            }
        } else {
            mSelStartTime = 0.5f
        }
        mSelCoverVideoView.pause()
        mPopVideoLoadingFl.visibility = View.VISIBLE
        mTvHint.text = "保存封面中..."
        val commands = YFileUtils.videoCatchImg(mVideoPath, mSelStartTime)
        EpEditor.execCmd(commands[0], 0, object : OnEditorListener {
            override fun onSuccess() {
                runOnUiThread {
                    mPopVideoLoadingFl.visibility = View.GONE
                    ToastUtils.showShort("选取成功")
                    val intent = Intent()
                    intent.putExtra("coverPath", commands[1])
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

            }

            override fun onFailure() {
                runOnUiThread {
                    mPopVideoLoadingFl.visibility = View.GONE
                    mSelCoverVideoView.start()
                    ToastUtils.showShort("选取失败")
                    setPositiveOnClickListener {
                        mIsSelTime = true
                        saveCoverBitmapToSDCard()
                    }
                }

            }

            override fun onProgress(progress: Float) {

            }
        })
    }


    private fun initThumbs() {
        val mediaMetadata = MediaMetadataRetriever()
        mediaMetadata.setDataSource(this, Uri.parse(mVideoPath))
        mVideoRotation = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
        mVideoWidth = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
        mVideoHeight = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        mVideoDuration = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        val frame = 10
        val frameTime = mVideoDuration / frame * 1000
        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void): Boolean? {
                for (x in 0 until frame) {
                    val bitmap = mediaMetadata.getFrameAtTime((frameTime * x).toLong(), MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    val msg = myHandler.obtainMessage()
                    msg.what = SAVE_BITMAP
                    msg.obj = bitmap
                    msg.arg1 = x
                    myHandler.sendMessage(msg)
                }
                mediaMetadata.release()
                return true
            }

            override fun onPostExecute(result: Boolean?) {
                myHandler.sendEmptyMessage(SUBMIT)
            }
        }.execute()
    }

    private fun initSetParam() {
        val layoutParams = mSelCoverVideoView.getLayoutParams()
        if (mVideoRotation == "0" && mVideoWidth > mVideoHeight) {//本地视频横屏 0表示竖屏
            layoutParams.width = 1120
            layoutParams.height = 630
        } else {
            layoutParams.width = 630
            layoutParams.height = 1120
        }

        mSelCoverVideoView.setLayoutParams(layoutParams)
        mSelCoverVideoView.setVideoPath(mVideoPath)
        mSelCoverVideoView.start()
        mSelCoverVideoView.getDuration()
    }

    private fun initRvView() {
        mSelCoverAdapter = SelCoverAdapter(this)
        val linearLayoutManager = object : LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        mCutRecyclerView.setLayoutManager(linearLayoutManager)
        mCutRecyclerView.setAdapter(mSelCoverAdapter)
    }

    private fun initListener() {
        mThumbSelTimeView.setOnScrollBorderListener(object : ThumbnailSelTimeView.OnScrollBorderListener {
            override fun OnScrollBorder(start: Float, end: Float) {

            }

            override fun onScrollStateChange() {
                myHandler.removeMessages(SEL_TIME)
                val rectLeft = mThumbSelTimeView.getRectLeft()
                mSelStartTime = mVideoDuration * rectLeft / 1000
                Log.e("Atest", "onScrollStateChange: $mSelStartTime")
                mSelCoverVideoView.seekTo(mSelStartTime.toInt())
                myHandler.sendEmptyMessage(SEL_TIME)
            }
        })
    }


    private class MyHandler(activityWeakReference: SelCoverTimeActivity) : Handler() {
        private val mActivityWeakReference: WeakReference<SelCoverTimeActivity>

        init {
            mActivityWeakReference = WeakReference(activityWeakReference)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivityWeakReference.get()
            if (activity != null) {
                when (msg.what) {
                    SEL_TIME -> {
                        activity.mSelCoverVideoView.seekTo(activity.mSelStartTime.toInt() * 1000)
                        activity.mSelCoverVideoView.start()
                        sendEmptyMessageDelayed(SEL_TIME, 1000)
                    }
                    SAVE_BITMAP -> activity.mBitmapList.add(msg.arg1, msg.obj as Bitmap)
                    SUBMIT -> {
                        activity.mSelCoverAdapter!!.addBitmapList(activity.mBitmapList)
                        sendEmptyMessageDelayed(SEL_TIME, 1000)
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(object : ContextWrapper(newBase) {
            override fun getSystemService(name: String): Any? {
                return if (Context.AUDIO_SERVICE == name) applicationContext.getSystemService(name) else super.getSystemService(name)
            }
        })
    }

}
