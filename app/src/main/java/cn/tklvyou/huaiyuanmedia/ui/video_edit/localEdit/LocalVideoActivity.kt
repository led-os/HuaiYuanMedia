package cn.tklvyou.huaiyuanmedia.ui.video_edit.localEdit

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import java.io.File
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.concurrent.Executors

import VideoHandle.CmdList
import VideoHandle.EpEditor
import VideoHandle.OnEditorListener
import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.MyApplication
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues.ISSAVEVIDEOTEMPEXIST
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues.VIDEOTEMP
import cn.tklvyou.huaiyuanmedia.ui.video_edit.localEdit.adapter.ThumbAdapter
import com.blankj.utilcode.util.ConvertUtils
import kotlinx.android.synthetic.main.activity_local_video.*
import kotlinx.android.synthetic.main.pop_video_loading.*


class LocalVideoActivity : BaseActivity<NullPresenter>(), MediaPlayerWrapper.IMediaCallback {

    companion object {
        private val SAVE_BITMAP = 1
        private val SUBMIT = 2
        private val ClEAR_BITMAP = 3
        private val CLIPPER_GONE = 4
        private val CLIPPER_FAILURE = 5
        private val TAG = "Atest"
        internal val VIDEO_PREPARE = 0
        internal val VIDEO_START = 1
        internal val VIDEO_UPDATE = 2
        internal val VIDEO_PAUSE = 3
        internal val VIDEO_CUT_FINISH = 4


        fun launch(activity: Activity, outputPath: String) {
            val intent = Intent(activity, LocalVideoActivity::class.java)
            intent.putExtra(StaticFinalValues.VIDEOFILEPATH, outputPath)
            activity.startActivity(intent)
        }

    }

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_local_video
    }


    private var mInputVideoPath: String = ""
    private var mOutVideoPath: String? = null
    private var rotate: Int = 0
    var mThumbAdapter: ThumbAdapter? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    var mStartTime = 0f
    var mEndTime: Float = 0.toFloat()
    var mRecyclerWidth: Int = 0
    var mTotolWidth: Int = 0
    var mThumbSelTime = 30//选择器选中的时间间隔
    var mVideoRotation: String? = null
    private var mInitRotation: Int = 0//视频初始旋转角度，竖屏为90，横屏为0
    private var isFailure = false
    var mLastTime: Long = 0
    private var lastTime: Long = 0
    private var isLocalPortrait = false
    var mSavevideotemp: String? = null
    private var isClickRotate = false//是否点击了旋转按钮
    var mAsyncTask: AsyncTask<Void, Void, Boolean>? = null
    private var mRotate = 0
    private val DIR: String? = null

    var mHorizontalScrollOffset: Int = 0

    private var isThreadStart = false
    private val mThread = Thread(Runnable {
        while (isPlaying) {
            isThreadStart = true
            val videoDuration = mLocalVideoView.videoDuration
            if (mStartTime > videoDuration || mEndTime < videoDuration) {
                mLocalVideoView.seekTo(mStartTime.toInt() / 1000)
                mLocalVideoView.start()
            }
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    })

    private var resumed: Boolean = false
    private var isDestroy: Boolean = false
    private var isPlaying = false
    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                VIDEO_PREPARE -> Executors.newSingleThreadExecutor().execute(update)
                VIDEO_START -> isPlaying = true
                VIDEO_UPDATE -> {
                }
                VIDEO_PAUSE -> isPlaying = false
                VIDEO_CUT_FINISH -> finish()
            }/*  int curDuration = mVideoView.getCurDuration();
                    if (curDuration > startPoint + clipDur) {
                        mVideoView.seekTo(startPoint);
                        mVideoView.start();
                    }*///TODO　已经渲染完毕了　
        }
    }
    private val update = Runnable {
        while (!isDestroy) {
            if (!isPlaying) {
                try {
                    Thread.sleep(200)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                continue
            }
            mHandler.sendEmptyMessage(VIDEO_UPDATE)
            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    var mVideoHeight: Int = 0
    var mVideoWidth: Int = 0
    var mVideoDuration: Int = 0

    private var mThumbBitmap: MutableList<Bitmap>? = ArrayList()

    private val myHandler = MyHandler(this)


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("视频裁剪")
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        setPositiveText("完成")
        setPositiveOnClickListener {

            if (System.currentTimeMillis() - lastTime < 500 || mPopVideoLoadingFl != null && mPopVideoLoadingFl!!.visibility == View.VISIBLE) {
                return@setPositiveOnClickListener
            }

            lastTime = System.currentTimeMillis()

            val cmd = CmdList()
            cmd.append("-y")
            cmd.append("-ss").append((mStartTime.toInt() / 1000).toString()).append("-t").append(mThumbSelTime.toString()).append("-accurate_seek")
            cmd.append("-i").append(mInputVideoPath)
            if (isLocalPortrait) {
                if (!isClickRotate && rotate == 0 || rotate == 90) {
                    rotate = 180
                } else {
                    isLocalPortrait = false
                    if (rotate == 0) {
                        rotate = 270
                    } else {
                        rotate = rotate - 90
                    }
                }
            }
            when (rotate) {
                0 -> {
                    cmd.append("-vcodec")
                    cmd.append("copy")
                    cmd.append("-acodec")
                    cmd.append("copy")
                }
                270 -> {
                    cmd.append("-filter_complex")
                    cmd.append("transpose=2")
                    cmd.append("-preset")
                    cmd.append("ultrafast")
                }
                180 -> {
                    cmd.append("-filter_complex")
                    cmd.append("vflip,hflip")
                    cmd.append("-preset")
                    cmd.append("ultrafast")
                }
                90 -> {
                    cmd.append("-filter_complex")
                    cmd.append("transpose=1")
                    cmd.append("-preset")
                    cmd.append("ultrafast")
                }
            }

            val file = File(ISSAVEVIDEOTEMPEXIST)
            if (!file.exists()) {
                file.mkdir()
            }
            mOutVideoPath = ISSAVEVIDEOTEMPEXIST + System.currentTimeMillis() + ".mp4"
            if (!File(VIDEOTEMP).exists()) {
                File(VIDEOTEMP).mkdirs()
            }
            cmd.append(mOutVideoPath)
            mLocalVideoView!!.pause()
            exec(cmd)
        }

        initData()
        initView()
        initListener()


    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (mPopVideoLoadingFl != null && mPopVideoLoadingFl!!.visibility == View.VISIBLE) {
            true
        } else super.dispatchTouchEvent(ev)
    }


    private fun initListener() {
        mLocalRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //获取当前RecyclerView的滑动偏移距离2340
                mTotolWidth = mLocalRecyclerView!!.computeHorizontalScrollRange()
                mHorizontalScrollOffset = mLocalRecyclerView!!.computeHorizontalScrollOffset()
                val mThumbLeftPosition = mLocalThumbView!!.leftInterval + mHorizontalScrollOffset
                val v = mThumbLeftPosition / mTotolWidth.toFloat()
                mStartTime = mVideoDuration * v
                mEndTime = (mStartTime.toInt() + mThumbSelTime * 1000).toFloat()

                if (mLocalRecyclerView!!.computeHorizontalScrollExtent() + mHorizontalScrollOffset == mTotolWidth) {
                    val right = mLocalThumbView!!.rightInterval
                    val width = mLocalThumbView!!.totalWidth
                    if (right == width) {
                        mEndTime = mVideoDuration.toFloat()
                        mStartTime = mEndTime - mThumbSelTime * 1000
                    }
                }
                Log.e(TAG, "OnScrollBorder: mStartTime:" + mStartTime + "mEndTime:" + mEndTime)
            }
        })
        mLocalThumbView!!.setOnScrollBorderListener(object : ThumbnailView.OnScrollBorderListener {
            override fun OnScrollBorder(start: Float, end: Float) {
                mTotolWidth = mLocalRecyclerView!!.computeHorizontalScrollRange()
                val left = mLocalThumbView!!.leftInterval
                val mThumbLeftPosition = left + mHorizontalScrollOffset
                val v = mThumbLeftPosition / mTotolWidth.toFloat()
                mStartTime = mVideoDuration * v
                val right = mLocalThumbView!!.rightInterval
                mThumbSelTime = ((right - left) * 30 / MyApplication.screenWidth).toInt()
                val width = mLocalThumbView!!.totalWidth
                if (right == width) {
                    mThumbSelTime = (mVideoDuration - mStartTime.toInt()) / 1000
                }
                if (mThumbSelTime > 30) {
                    mThumbSelTime = 30
                }
                mLocalSelTimeTv!!.text = "已选取" + mThumbSelTime + "秒"
                mEndTime = mStartTime + mThumbSelTime * 1000
                Log.e(TAG, "OnScrollBorder: mStartTime:" + mStartTime + "mEndTime:" + mEndTime)
            }

            override fun onScrollStateChange() {
                Log.e(TAG, "OnScrollBorder: startTime$mStartTime             endTime ===             $mEndTime")
            }
        })

    }

    private fun initView() {
        mThumbAdapter = ThumbAdapter(this)
        mLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mLocalRecyclerView!!.layoutManager = mLinearLayoutManager
        mLocalRecyclerView!!.adapter = mThumbAdapter
        mLocalThumbView!!.setMinInterval(MyApplication.screenWidth / 6)
    }

    private fun initData() {
        mInputVideoPath = intent.getStringExtra(StaticFinalValues.VIDEOFILEPATH)
        initThumbs()//获取缩略图
        val srcList = ArrayList<String>()
        srcList.add(mInputVideoPath!!)
        mLocalVideoView!!.setVideoPath(srcList)
        mLocalVideoView!!.setIMediaCallback(this)
        initSetParam()
    }

    /**
     *
     */
    private fun initSetParam() {
        /*if(mVideoRotation.equals("90") && mVideoWidth > mVideoHeight || mVideoRotation.equals("0") && mVideoWidth < mVideoHeight){//本地相机视频竖屏//自定义相机视频
        }*/
        //todo:自定义相机录制视频的方向不对，长宽是对的，系统相机视频只可以获取正确是角度，不能通过长宽进行判断
        if (mVideoRotation == "0" && mVideoWidth > mVideoHeight) {//本地视频横屏
            Log.e(TAG, "initSetParam: ")
            //            mInitRotation = 90;
            //            mLocalVideoView.setRotation(mInitRotation);
        } else if (mVideoRotation == "90" && mVideoWidth > mVideoHeight) {//本地视频竖屏
            mInitRotation = 90
            isLocalPortrait = true
            setPortraitParam()
        } else if (mVideoRotation == "0" && mVideoWidth < mVideoHeight) { //保存视频竖屏
            setPortraitParam()
        } else if (mVideoRotation == "180" && mVideoWidth > mVideoHeight) {//本地视频横屏
            Log.e(TAG, "initSetParam: ")
        } else {
            mInitRotation = 90
            setPortraitParam()
        }
    }

    private fun setPortraitParam() {
        val layoutParams1 = mLocalVideoView!!.layoutParams
        layoutParams1.width = 630
        layoutParams1.height = 1120
        mLocalVideoView!!.layoutParams = layoutParams1
        mLocalVideoView!!.requestLayout()
    }

    private fun setLandScapeParam() {
        val layoutParams1 = mLocalVideoView!!.layoutParams
        layoutParams1.width = 1120
        layoutParams1.height = 630
        mLocalVideoView!!.layoutParams = layoutParams1
        mLocalVideoView!!.requestLayout()
    }


    fun translateVideo() {
        val cmd = CmdList()
        cmd.append("-y")
        cmd.append("-i")
        cmd.append(mOutVideoPath)
        cmd.append("-filter_complex")
        cmd.append("vflip,hflip")
        cmd.append("-preset")
        cmd.append("ultrafast")
        val file = File(ISSAVEVIDEOTEMPEXIST)
        if (!file.exists()) {
            file.mkdir()
        }
        mSavevideotemp = ISSAVEVIDEOTEMPEXIST + System.currentTimeMillis() + ".mp4"
        cmd.append(mSavevideotemp)
        isLocalPortrait = false
        exec(cmd)
    }

    fun exec(cmdList: CmdList) {
        mPopVideoLoadingFl!!.visibility = View.VISIBLE
        //        progressDialog = DialogManager.showProgressDialog(mContext);
        val cmds = cmdList.toTypedArray()
        val stringBuffer = StringBuffer()
        for (ss in cmds) {
            stringBuffer.append(ss).append(" ")
            Log.e("EpMediaF", "cmd:$ss     stringBuffer :  $stringBuffer")
        }
        EpEditor.execCmd(stringBuffer.toString(), 0, object : OnEditorListener {
            override fun onSuccess() {
                isFailure = false
                if (!isLocalPortrait) {
                    if (!TextUtils.isEmpty(mSavevideotemp)) {
                        if (File(mOutVideoPath!!).exists()) {
                            File(mOutVideoPath!!).delete()
                        }
                        val data = Intent()
                        data.putExtra(StaticFinalValues.VIDEOFILEPATH, mSavevideotemp!!)
                        setResult(Activity.RESULT_OK,data)
                    } else {
                        val data = Intent()
                        data.putExtra(StaticFinalValues.VIDEOFILEPATH, mOutVideoPath!!)
                        setResult(Activity.RESULT_OK,data)
                    }
                    myHandler.sendEmptyMessage(CLIPPER_GONE)
                    finish()
                } else {
                    translateVideo()
                }
            }

            override fun onFailure() {
                isFailure = true
                myHandler.sendEmptyMessage(CLIPPER_GONE)
            }

            override fun onProgress(v: Float) {}
        })
    }


    override fun onResume() {
        super.onResume()
        if (resumed) {
            mLocalVideoView!!.start()
        }
        resumed = true
    }

    override fun onPause() {
        super.onPause()
        mLocalVideoView!!.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        isDestroy = true
        mLocalVideoView!!.onDestroy()
        for (i in mThumbBitmap!!.indices) {
            mThumbBitmap!![i].recycle()
        }
        mThumbBitmap = null
        System.gc()
        mAsyncTask!!.cancel(true)
        mAsyncTask = null
    }


    override fun onBackPressed() {
        if (mPopVideoLoadingFl != null && mPopVideoLoadingFl!!.visibility == View.GONE) {
            super.onBackPressed()
        }
    }

    override fun onVideoPrepare() {
        mHandler.sendEmptyMessage(VIDEO_PREPARE)
    }

    override fun onVideoStart() {
        mHandler.sendEmptyMessage(VIDEO_START)
    }

    override fun onVideoPause() {
        mHandler.sendEmptyMessage(VIDEO_PAUSE)
    }

    override fun onCompletion(mp: MediaPlayer) {
        mLocalVideoView!!.seekTo(0)
        mLocalVideoView!!.start()
    }

    override fun onVideoChanged(info: VideoInfo) {}

    private fun initThumbs() {
        val mediaMetadata = MediaMetadataRetriever()
        mediaMetadata.setDataSource(mContext, Uri.parse(mInputVideoPath))
        mVideoRotation = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
        mVideoWidth = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
        mVideoHeight = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        mVideoDuration = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        if (mVideoDuration / 1000 > 30) {
            mThumbSelTime = 30
        } else {
            mThumbSelTime = mVideoDuration / 1000
        }
        mEndTime = ((mVideoDuration + 100) / 1000).toFloat()
        if (mEndTime < 30) {
            mLocalSelTimeTv!!.text = "已选取" + mEndTime + "秒"
        }
        val frame: Int
        val frameTime: Int
        if (mVideoDuration >= 29900 && mVideoDuration < 30300) {
            frame = 10
            frameTime = mVideoDuration / frame * 1000
        } else {
            frameTime = 3000 * 1000
            frame = mVideoDuration * 1000 / frameTime
        }
        mAsyncTask = object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void): Boolean? {
                myHandler.sendEmptyMessage(ClEAR_BITMAP)
                for (x in 0 until frame) {
                    val bitmap = mediaMetadata.getFrameAtTime((frameTime * x).toLong(), MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    val msg = myHandler.obtainMessage()
                    msg.what = SAVE_BITMAP
                    msg.obj = bitmap
                    msg.arg1 = x
                    Log.e(TAG, "doInBackground: $x")
                    myHandler.sendMessage(msg)
                }
                mediaMetadata.release()
                return true
            }

            override fun onPostExecute(result: Boolean?) {
                myHandler.sendEmptyMessage(SUBMIT)
            }
        }
        mAsyncTask!!.execute()
    }

    private class MyHandler(localVideoActivity: LocalVideoActivity) : Handler() {
        private val mWeakReference: WeakReference<LocalVideoActivity>

        init {
            mWeakReference = WeakReference(localVideoActivity)
        }

        override fun handleMessage(msg: Message) {
            val localVideoActivity = mWeakReference.get()
            if (localVideoActivity != null) {
                when (msg.what) {
                    CLIPPER_FAILURE -> {
                        Toast.makeText(localVideoActivity.mContext, "视频编译失败，请换个视频试试", Toast.LENGTH_LONG).show()
                        localVideoActivity.mPopVideoLoadingFl!!.visibility = View.GONE
                    }
                    CLIPPER_GONE -> localVideoActivity.mPopVideoLoadingFl!!.visibility = View.GONE
                    ClEAR_BITMAP -> localVideoActivity.mThumbBitmap!!.clear()
                    SAVE_BITMAP -> if (localVideoActivity.mThumbBitmap != null) {
                        localVideoActivity.mThumbBitmap!!.add(msg.arg1, msg.obj as Bitmap)
                    }
                    SUBMIT -> {
                        localVideoActivity.mThumbAdapter!!.addThumb(localVideoActivity.mThumbBitmap)
                        localVideoActivity.mThumbAdapter!!.setLoadSuccessCallBack(object : ThumbAdapter.LoadSuccessCallBack {
                            override fun callback() {
                                //获取recyclerView在屏幕中的长度1080
                                localVideoActivity.mRecyclerWidth = localVideoActivity.mLocalRecyclerView!!.computeHorizontalScrollExtent()
                                //获取recyclerView所有item的长度3420
                                localVideoActivity.mTotolWidth = localVideoActivity.mLocalRecyclerView!!.computeHorizontalScrollRange()
                                val i = localVideoActivity.mLocalRecyclerView!!.computeHorizontalScrollRange()
                                if (i < MyApplication.screenWidth) {
                                    if (i > MyApplication.screenWidth / 6) {
                                        localVideoActivity.mLocalThumbView!!.width = i + ConvertUtils.dp2px(1f)
                                    } else {
                                        localVideoActivity.mLocalThumbView!!.width = MyApplication.screenWidth / 6 - ConvertUtils.dp2px(10f)
                                    }
                                }
                                Log.e(TAG, "callback: $i")
                            }
                        })
                    }
                }
            }
        }
    }


}
