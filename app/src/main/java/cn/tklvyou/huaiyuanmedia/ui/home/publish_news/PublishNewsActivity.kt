package cn.tklvyou.huaiyuanmedia.ui.home.publish_news

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.helper.AccountHelper
import cn.tklvyou.huaiyuanmedia.ui.adapter.GridImageAdapter
import cn.tklvyou.huaiyuanmedia.ui.camera.TakePhotoActivity
import cn.tklvyou.huaiyuanmedia.ui.video_edit.VideoEditActivity
import cn.tklvyou.huaiyuanmedia.utils.QiniuUploadManager
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_publish_news.*
import java.io.File
import java.io.Serializable
import java.lang.StringBuilder

class PublishNewsActivity : BaseActivity<PublishNewsPresenter>(), PublishNewsContract.View {
    override fun setQiniuToken(token: String) {
        qiniuToken = token
    }

    override fun initPresenter(): PublishNewsPresenter {
        return PublishNewsPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_publish_news
    }

    private var selectList: MutableList<LocalMedia>? = null
    private var adapter: GridImageAdapter? = null
    private var isVideo = false
    private var imagePath = ""
    private var page = "V视"

    private var imagesBuilder: StringBuilder = StringBuilder()
    private val imageFiles = ArrayList<File>()
    private var videoUrl = ""

    private var qiniuManager: QiniuUploadManager? = null

    private var qiniuToken = ""

    override fun initView(savedInstanceState: Bundle?) {
        commonTitleBar.toggleStatusBarMode()
        hideTitleBar()

        qiniuManager = QiniuUploadManager.getInstance(this)

        selectList = intent.getSerializableExtra("data") as MutableList<LocalMedia>
        isVideo = intent.getBooleanExtra("isVideo", true)
        page = intent.getStringExtra("page")
        var maxLength = 0
        if (page == "V视") {
            maxLength = 60
        } else {
            maxLength = 70
        }

        etContent.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))

        if (!isVideo) {
            picRecyclerView.visibility = View.VISIBLE
            videoLayout.visibility = View.GONE
            ivAddVideo.visibility = View.GONE

            picRecyclerView.layoutManager = GridLayoutManager(this, 3)
            adapter = GridImageAdapter(this, onAddPicClickListener)
            adapter!!.list = selectList
            adapter!!.setSelectMax(9)
            picRecyclerView.adapter = adapter
            adapter!!.setOnItemClickListener { position, v ->
                val media = selectList!![position]
                val pictureType = media.pictureType
                val mediaType = PictureMimeType.pictureToVideo(pictureType)
                when (mediaType) {
                    1 ->
                        // 预览图片
                        PictureSelector.create(this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectList)
                    2 ->
                        // 预览视频
                        PictureSelector.create(this).externalPictureVideo(media.path)
                    3 ->
                        // 预览音频
                        PictureSelector.create(this).externalPictureAudio(media.path)
                }
            }

        } else {
            picRecyclerView.visibility = View.GONE
            videoLayout.visibility = View.VISIBLE

            imagePath = intent.getStringExtra("videoImage")
            ivVideo.setImageDrawable(BitmapDrawable(imagePath))

            ivVideo.setOnClickListener {
                if (selectList != null && selectList!!.size > 0) {
                    // 预览视频
                    PictureSelector.create(this).externalPictureVideo(selectList!![0].path)
                }
            }

            ivDelete.setOnClickListener {
                selectList?.clear()
                videoLayout.visibility = View.GONE
                ivAddVideo.visibility = View.VISIBLE
            }

            ivAddVideo.setOnClickListener {
                val intent = Intent(this, TakePhotoActivity::class.java)
                intent.putExtra("page", page)
                intent.putExtra("is_video", true)
                startActivity(intent)
                finish()
            }

        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {
            hideSoftInput(etContent.windowToken)

            if (etContent.text.toString().trim().isEmpty()) {
                ToastUtils.showShort("请输入内容")
                return@setOnClickListener
            }

            if (qiniuToken.isEmpty()) {
                mPresenter.getQiniuToken()
                ToastUtils.showShort("初始化上传配置中，请重新尝试")
                return@setOnClickListener
            }

            if (selectList.isNullOrEmpty()) {

                if (page == "V视") {
                    ToastUtils.showShort("请上传拍摄视频")
                } else {
                    if (isVideo) {
                        ToastUtils.showShort("请上传拍摄视频")
                    } else {
                        ToastUtils.showShort("请上传拍摄图片")
                    }

                }
                return@setOnClickListener
            }

            showLoading()
            if (page == "V视") {
                mPresenter.qiniuUploadFile(File(selectList!![0].path), true, qiniuToken, "" + AccountHelper.getInstance().uid, qiniuManager)
            } else {
                if (isVideo) {
                    mPresenter.qiniuUploadFile(File(selectList!![0].path), true, qiniuToken, "" + AccountHelper.getInstance().uid, qiniuManager)
                } else {
                    selectList = adapter!!.list
                    selectList!!.forEach {
                        if (it.isCompressed || (it.isCut && it.isCompressed)) {
                            imageFiles.add(File(it.compressPath))
                        } else {
                            imageFiles.add(File(it.path))
                        }
                    }
                    mPresenter.qiniuUploadMultiImage(imageFiles, qiniuToken, "" + AccountHelper.getInstance().uid, qiniuManager)
                }
            }
        }

        mPresenter.getQiniuToken()
    }

    override fun uploadImageSuccess(url: String) {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(selectList!![0].path)
        val durationTime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()

        if (page == "V视") {
            mPresenter.publishVShi(etContent.text.toString().trim(), videoUrl, url, "" + durationTime / 1000)
        } else {
            mPresenter.publishSuiShouPai(etContent.text.toString().trim(), "", videoUrl, url, "" + durationTime / 1000)
        }

    }


    override fun uploadVideoSuccess(url: String) {
        this.videoUrl = url
        mPresenter.qiniuUploadFile(File(imagePath), false, qiniuToken, "" + AccountHelper.getInstance().uid, qiniuManager)
    }

    override fun uploadImagesSuccess(urls: MutableList<String>) {
        for ((index, item) in urls.withIndex()) {
            if (index != urls.size - 1) {
                imagesBuilder.append("$item,")
            } else {
                imagesBuilder.append(item)
            }
        }
        mPresenter.publishSuiShouPai(etContent.text.toString().trim(), imagesBuilder.toString(), "", "", "")
    }


    override fun publishSuccess() {
        showSuccess("")
        finish()
    }


    private val onAddPicClickListener = object : GridImageAdapter.onAddPicClickListener {
        override fun onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(this@PublishNewsActivity)
                    .openGallery(PictureMimeType.ofImage())
                    .theme(R.style.picture_default_style)
                    .maxSelectNum(9)
                    .minSelectNum(1)
                    .selectionMode(PictureConfig.MULTIPLE)
                    .previewImage(true)
                    .isCamera(true)
                    .enableCrop(false)
                    .compress(true)
                    .previewEggs(true)
                    .openClickSound(false)
                    .selectionMedia(selectList)
                    .forResult(PictureConfig.CHOOSE_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择
                    selectList = PictureSelector.obtainMultipleResult(data)
                    adapter!!.list = selectList
                    adapter!!.notifyDataSetChanged()
                }


                10 -> {
                    selectList = PictureSelector.obtainMultipleResult(data)
                    val intent = Intent(this, VideoEditActivity::class.java)
                    intent.putExtra("page", page)
                    intent.putExtra("data", selectList as Serializable)
                    intent.putExtra("hasBack", true)
                    startActivityForResult(intent, 12)
                }

                12 -> {
                    videoLayout.visibility = View.VISIBLE
                    ivAddVideo.visibility = View.GONE

                    picRecyclerView.visibility = View.GONE
                    videoLayout.visibility = View.VISIBLE

                    imagePath = intent.getStringExtra("videoImage")
                    ivVideo.background = BitmapDrawable(imagePath)

                }

            }

        }
    }


}
