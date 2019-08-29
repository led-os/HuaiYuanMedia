package cn.tklvyou.huaiyuanmedia.ui.home.publish_wenzheng

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.helper.AccountHelper
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.NewsBean
import cn.tklvyou.huaiyuanmedia.ui.adapter.WenzhengGridImageAdapter
import cn.tklvyou.huaiyuanmedia.utils.GridDividerItemDecoration
import cn.tklvyou.huaiyuanmedia.utils.QiniuUploadManager
import cn.tklvyou.huaiyuanmedia.widget.dailog.DialogBindViewHolder
import cn.tklvyou.huaiyuanmedia.widget.dailog.SelectListDialog
import cn.tklvyou.huaiyuanmedia.widget.dailog.TBaseAdapter
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_publish_wenzheng.*
import java.io.File
import java.lang.StringBuilder

class PublishWenzhengActivity : BaseActivity<PublishWenzhengPresenter>(), PublishWenzhengContract.View {

    override fun setJuZhengHeader(beans: MutableList<NewsBean>) {
        this.wenzhengBeans = beans

        tvModuleSecond.setOnClickListener {
            val dialog = SelectListDialog(this)
            dialog.setTitle("选择问政对象")
            dialog.setLayoutManager(GridLayoutManager(this, 4))
            dialog.setAdapter(object : TBaseAdapter<NewsBean>(R.layout.item_juzheng_header_child_layout, wenzhengBeans) {
                override fun onBind(holder: DialogBindViewHolder, position: Int, t: NewsBean) {
                    holder.setText(R.id.tvNickName, t.nickname)
                    GlideManager.loadRoundImg(t.avatar, holder.getView(R.id.ivAvatar))
                }
            })
            dialog.setItemOnclickListener { holder, position, t, tDialog ->
                tvModuleSecond.text = (t as NewsBean).nickname
                dialog.dismiss()
            }
            dialog.show()
        }

    }

    override fun setQiniuToken(token: String) {
        this.qiniuToken = token
        mPresenter.getJuZhengHeader("问政")
    }

    override fun initPresenter(): PublishWenzhengPresenter {
        return PublishWenzhengPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_publish_wenzheng
    }

    private var selectList: MutableList<LocalMedia>? = null
    private var adapter: WenzhengGridImageAdapter? = null
    private var imagesBuilder: StringBuilder = StringBuilder()
    private val imageFiles = ArrayList<File>()

    private var moduleSecond = ""
    private var name = ""
    private var content = ""
    private var wenzhengBeans: MutableList<NewsBean> = ArrayList()
    private var qiniuToken = ""


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("发布问政")
        setNavigationText("", R.mipmap.icon_titlebar_back)
        setNavigationOnClickListener { finish() }

        selectList = ArrayList()

        picRecyclerView.layoutManager = GridLayoutManager(this, 4)
        adapter = WenzhengGridImageAdapter(this, onAddPicClickListener)
        adapter!!.list = selectList
        adapter!!.setSelectMax(9)
        picRecyclerView.adapter = adapter
        picRecyclerView.addItemDecoration(GridDividerItemDecoration(30, Color.WHITE))
        adapter!!.setOnItemClickListener { position, v ->
            val media = selectList!![position]
            val pictureType = media.pictureType
            val mediaType = PictureMimeType.pictureToVideo(pictureType)
            when (mediaType) {
                1 ->
                    // 预览图片
                    PictureSelector.create(this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectList)
            }
        }


        btnSubmit.setOnClickListener {
            moduleSecond = tvModuleSecond.text.toString().trim()
            name = etName.text.toString().trim()
            content = etContent.text.toString().trim()

            if (moduleSecond.isEmpty()) {
                ToastUtils.showShort("请选择问政对象")
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                ToastUtils.showShort("请输入标题")
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                ToastUtils.showShort("请输入内容")
                return@setOnClickListener
            }

            showLoading()
            imagesBuilder = StringBuilder()

            selectList!!.forEach {
                if (it.isCompressed || (it.isCut && it.isCompressed)) {
                    imageFiles.add(File(it.compressPath))
                } else {
                    imageFiles.add(File(it.path))
                }
            }
            if (imageFiles.size != 0) {
                mPresenter.qiniuUploadMultiImage(imageFiles, qiniuToken, "" + AccountHelper.getInstance().uid, QiniuUploadManager.getInstance(this))
            } else {
                mPresenter.publishWenZheng(moduleSecond, name, content, imagesBuilder.toString())
            }
        }

        mPresenter.getQiniuToken()


    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getQiniuToken()
    }

    override fun uploadImagesSuccess(urls: MutableList<String>) {
        for ((index, item) in urls.withIndex()) {
            if (index != urls.size - 1) {
                imagesBuilder.append("$item,")
            } else {
                imagesBuilder.append(item)
            }
        }
        mPresenter.publishWenZheng(moduleSecond, name, content, imagesBuilder.toString())
    }


    override fun publishSuccess() {
        showSuccess("")
        finish()
    }


    private val onAddPicClickListener = object : WenzhengGridImageAdapter.onAddPicClickListener {
        override fun onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(this@PublishWenzhengActivity)
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
            }

        }
    }


}
