package cn.tklvyou.huaiyuanmedia.ui.video_edit

import VideoHandle.EpEditor
import VideoHandle.OnEditorListener
import android.app.Activity
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues.COMR_FROM_SEL_COVER_TIME_ACTIVITY
import cn.tklvyou.huaiyuanmedia.common.StaticFinalValues.COMR_FROM_VIDEO_EDIT_TIME_ACTIVITY
import cn.tklvyou.huaiyuanmedia.ui.home.publish_news.PublishNewsActivity
import cn.tklvyou.huaiyuanmedia.ui.video_edit.localEdit.LocalVideoActivity
import cn.tklvyou.huaiyuanmedia.ui.video_edit.localEdit.MediaPlayerWrapper
import cn.tklvyou.huaiyuanmedia.ui.video_edit.localEdit.VideoInfo
import cn.tklvyou.huaiyuanmedia.ui.video_edit.selCover.SelCoverTimeActivity
import cn.tklvyou.huaiyuanmedia.utils.YFileUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_video_option.*
import kotlinx.android.synthetic.main.pop_video_loading.*
import java.util.*


class VideoOptionActivity : BaseActivity<NullPresenter>(), MediaPlayerWrapper.IMediaCallback {

    companion object {
        fun launch(activity: Activity, outputPath: String, page: String) {
            val intent = Intent(activity, VideoOptionActivity::class.java)
            intent.putExtra(StaticFinalValues.VIDEOFILEPATH, outputPath)
            intent.putExtra("page", page)
            activity.startActivity(intent)
        }
    }


    override fun initPresenter(): NullPresenter? {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_video_option
    }

    private val TAG = "TAG"

    private lateinit var file_path: String

    private var page = ""

    private var resumed: Boolean = false
    var mVideoHeight: Int = 0
    var mVideoWidth: Int = 0
    private var mVideoRotation: String? = ""
    private var mInitRotation: Int = 0//视频初始旋转角度，竖屏为90，横屏为0
    private var isLocalPortrait = false

    private var coverPath = ""

    override fun initView(savedInstanceState: Bundle?) {
        baseTitleBar.hideTitleBar()

        file_path = intent.getStringExtra(StaticFinalValues.VIDEOFILEPATH)
        page = intent.getStringExtra("page")

        val mediaMetadata = MediaMetadataRetriever()
        mediaMetadata.setDataSource(mContext, Uri.parse(file_path))
        mVideoRotation = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
        if (mVideoRotation == null) {
            mVideoRotation = ""
        }
        var time = 0
        try {
            mVideoWidth = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            mVideoHeight = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
            time = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        } catch (e: Exception) {
        }
        mBtnBack.setOnClickListener {
            finish()
        }

        val srcList = ArrayList<String>()
        srcList.add(file_path)
        mLocalVideoView.setVideoPath(srcList)
        mLocalVideoView.setIMediaCallback(this)

        initSetParam()

        mBtnCover.setOnClickListener {
            val intent2 = Intent(this@VideoOptionActivity, SelCoverTimeActivity::class.java)
            intent2.putExtra(StaticFinalValues.VIDEOFILEPATH, file_path)
            startActivityForResult(intent2, COMR_FROM_SEL_COVER_TIME_ACTIVITY)
        }

        mBtnCut.setOnClickListener {
            if (time < 5000) {
                ToastUtils.showShort("视频剪辑不能少于5秒")
            } else {
                val intent2 = Intent(this@VideoOptionActivity, LocalVideoActivity::class.java)
                intent2.putExtra(StaticFinalValues.VIDEOFILEPATH, file_path)
                startActivityForResult(intent2, COMR_FROM_VIDEO_EDIT_TIME_ACTIVITY)
            }
        }

        mBtnSubmit.setOnClickListener {
            if (coverPath.isEmpty()) {
                saveCoverBitmapToSDCard()
            } else {
                launchPublishNewsActivity()
            }
        }
        mediaMetadata.release()
    }

    private fun launchPublishNewsActivity() {
        val selectList = ArrayList<LocalMedia>()
        val localMedia = LocalMedia()
        localMedia.path = file_path
        selectList.add(localMedia)

        val intent = Intent(this, PublishNewsActivity::class.java)
        intent.putExtra("isVideo", true)
        intent.putExtra("page", page)
        intent.putExtra("videoImage", coverPath)
        intent.putExtra("data", selectList)
        startActivity(intent)
        finish()
    }


    override fun onResume() {
        super.onResume()
        if (resumed) {
            mLocalVideoView.start()
        }
        resumed = true
    }

    override fun onPause() {
        super.onPause()
        mLocalVideoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocalVideoView.onDestroy()
        System.gc()
    }


    private fun initSetParam() {
        //自定义相机录制视频的方向不对，长宽是对的，系统相机视频只可以获取正确是角度，不能通过长宽进行判断
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        when (requestCode) {

            StaticFinalValues.COMR_FROM_VIDEO_EDIT_TIME_ACTIVITY -> {
                file_path = data.getStringExtra(StaticFinalValues.VIDEOFILEPATH)
                val srcList = ArrayList<String>()
                srcList.add(file_path)
                mLocalVideoView.setVideoPath(srcList)
                mLocalVideoView.start()
            }

            StaticFinalValues.COMR_FROM_SEL_COVER_TIME_ACTIVITY -> {
                coverPath = data.getStringExtra("coverPath")
            }
        }

    }

    override fun onVideoPrepare() {
    }

    override fun onVideoStart() {
    }

    override fun onVideoPause() {
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mLocalVideoView.seekTo(0)
        mLocalVideoView.start()
    }

    override fun onVideoChanged(info: VideoInfo?) {
    }

    private fun setPortraitParam() {
        val layoutParams1 = mLocalVideoView.layoutParams
        layoutParams1.width = ScreenUtils.getScreenWidth()
        layoutParams1.height = ScreenUtils.getScreenHeight()
        mLocalVideoView.layoutParams = layoutParams1
        mLocalVideoView.requestLayout()
    }

    private fun setLandScapeParam() {
        val layoutParams1 = mLocalVideoView.layoutParams
        layoutParams1.width = ScreenUtils.getScreenHeight()
        layoutParams1.height = ScreenUtils.getScreenWidth()
        mLocalVideoView.layoutParams = layoutParams1
        mLocalVideoView.requestLayout()
    }


    private fun saveCoverBitmapToSDCard() {
        mPopVideoLoadingFl.visibility = View.VISIBLE
        mTvHint.text = "选取默认封面..."
        val commands = YFileUtils.videoCatchImg(file_path, 0.5f)
        EpEditor.execCmd(commands[0], 0, object : OnEditorListener {
            override fun onSuccess() {
                runOnUiThread {
                    mPopVideoLoadingFl.visibility = View.GONE
                    coverPath = commands[1]
                    launchPublishNewsActivity()
                }

            }

            override fun onFailure() {
                runOnUiThread {
                    mPopVideoLoadingFl.visibility = View.GONE
                    ToastUtils.showShort("选取默认封面失败，请重试")
                }
            }

            override fun onProgress(progress: Float) {

            }
        })
    }

}