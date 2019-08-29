package cn.tklvyou.huaiyuanmedia.ui.account.data

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.helper.AccountHelper
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.ui.setting.edit_phone.EditPhoneActivity
import cn.tklvyou.huaiyuanmedia.utils.CommonUtil
import cn.tklvyou.huaiyuanmedia.utils.QiniuUploadManager
import cn.tklvyou.huaiyuanmedia.widget.dailog.InputDialog
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_personal_data.*
import org.apache.commons.lang.StringUtils
import java.io.File
import java.util.*

/**
 *@description :个人资料
 *@company :途酷科技
 * @author :JenkinsZhou
 * @date 2019年07月31日17:23
 * @Email: 971613168@qq.com
 */
class PersonalDataActivity : BaseActivity<DataPresenter>(), IDataContract.DataView, View.OnClickListener {
    override fun setQiniuToken(token: String) {
        this.qiniuToken = token
    }

    override fun initPresenter(): DataPresenter {
        return DataPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_personal_data
    }


    private var imageList = ArrayList<String>()
    private var selectList: List<LocalMedia> = ArrayList()
    private var qiniuToken = ""


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("个人资料")
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        showData()
        civAvatar.setOnClickListener(this)
        tvSave.setOnClickListener(this)
        llNickName.setOnClickListener(this)
        llChangePhone.setOnClickListener(this)
        mPresenter.getQiniuToken()
    }


    override fun uploadSuccess(url: String) {
        edit(url)
    }

    override fun showInputDialog(content: String) {
        val dialog = InputDialog(mContext, "输入昵称", content)
        dialog.setConfirmListerner {
            setTextValue(tvNickName, dialog.content)
            dialog.dismiss()
        }
        dialog.setCancelListerner { dialog.dismiss() }
        dialog.show()
    }

    override fun editSuccess() {
        ToastUtils.showShort("修改成功")
        finish()
    }


    private fun setTextValue(textView: TextView, value: String) {
        textView.text = value
    }


    override fun onClick(v: View?) {
        if (v == null) {
            return
        }
        when (v.id) {
            R.id.civAvatar -> {
                selectPic()
            }
            R.id.llNickName -> {
                showInputDialog(getTextValue(tvNickName))
            }
            R.id.tvSave -> {
                doEdit()
            }
            R.id.llChangePhone -> {
                startActivityForResult(Intent(mContext, EditPhoneActivity::class.java),0)
            }

            else -> {
            }
        }
    }


    private fun selectPic() {
        PictureSelector.create(mContext)
                .openGallery(PictureMimeType.ofImage())
                // 最大图片选择数量
                .maxSelectNum(1)
                // 最小选择数量
                .minSelectNum(1)
                // 每行显示个数
                .imageSpanCount(4)
                // 多选 or 单选
                .selectionMode(PictureConfig.SINGLE)
                // 是否可预览图片
                .previewImage(true)
                // 是否可播放音频
                .enablePreviewAudio(false)
                // 是否显示拍照按钮
                .isCamera(true)
                // 图片列表点击 缩放效果 默认true
                .isZoomAnim(true)
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                // 是否裁剪
                .enableCrop(true)
                // 是否压缩
                .compress(true)
                //同步true或异步false 压缩 默认同步
                .synOrAsy(true)
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                // glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .glideOverride(160, 160)
                // 是否显示uCrop工具栏，默认不显示
                .hideBottomControls(false)
                // 是否显示gif图片
                .isGif(false)
                // 裁剪框是否可拖拽
                .freeStyleCropEnabled(false)
                // 是否传入已选图片
                .selectionMedia(selectList)
                // 小于100kb的图片不压缩
                .minimumCompressSize(100)
                //结果回调onActivityResult code
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data)
                    handlePictureSelectCallback()
                }

                0 ->{
                    tvPhoneNumber.text = data!!.getStringExtra("mobile")
                }

                else -> {
                }
            }
        }
    }


    private fun showData() {
        if (!AccountHelper.getInstance().isLogin) {
            return
        }
        val userInfoBean = AccountHelper.getInstance().userInfo
        GlideManager.loadCircleImg(userInfoBean.avatar, civAvatar, R.mipmap.default_avatar)
        tvNickName.text = CommonUtil.getNotNullValue(userInfoBean.nickname)
        tvPhoneNumber.text = CommonUtil.getNotNullValue(userInfoBean.mobile)
    }


    private fun handlePictureSelectCallback() {
        imageList.clear()
        for (localMedia in selectList) {
            imageList.add(localMedia.compressPath)
        }
        GlideManager.loadCircleImg(parsePath(imageList), civAvatar, CommonUtil.getDrawable(R.mipmap.default_avatar))
    }

    private fun doEdit() {
        val avatarPath = parsePath(imageList)
        if (avatarPath.isEmpty()) {
            //用户没有重置头像 则上传原头像
            imageList.add(AccountHelper.getInstance().avatar)
            edit(AccountHelper.getInstance().avatar)
        } else {
            //需要先上传头像
            val file = File(avatarPath)
            mPresenter.doUploadImage(file, qiniuToken, "" + AccountHelper.getInstance().uid, QiniuUploadManager.getInstance(this))
        }
    }

    private fun parsePath(imageList: List<String>?): String {
        if (imageList == null || imageList.isEmpty()) {
            return ""
        }
        return if (imageList.size == 1) {
            CommonUtil.getNotNullValue(imageList[0])
        } else StringUtils.join(imageList, ",")
    }

    private fun getTextValue(editText: TextView): String {
        return editText.text.toString()
    }

    private fun edit(url:String) {
        val userName = AccountHelper.getInstance().userName
        mPresenter.editUserInfo(url, getTextValue(tvNickName), userName, "")
    }
}