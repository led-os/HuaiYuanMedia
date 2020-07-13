package cn.tklvyou.huaiyuanmedia.ui.camera.zhuan_pan

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.widget.ImageView
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.Contacts
import cn.tklvyou.huaiyuanmedia.common.Contacts.SHARE_DOWNLOAD_URL
import cn.tklvyou.huaiyuanmedia.model.LotteryModel
import cn.tklvyou.huaiyuanmedia.model.LotteryResultModel
import cn.tklvyou.huaiyuanmedia.utils.InterfaceUtils
import cn.tklvyou.huaiyuanmedia.utils.JSON
import cn.tklvyou.huaiyuanmedia.utils.YBitmapUtils
import cn.tklvyou.huaiyuanmedia.widget.SharePopupWindow
import cn.tklvyou.huaiyuanmedia.widget.dailog.ConfirmDialog
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.cretin.www.wheelsruflibrary.listener.RotateListener
import com.cretin.www.wheelsruflibrary.view.WheelSurfView
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.share.WbShareCallback
import com.sina.weibo.sdk.share.WbShareHandler
import com.sina.weibo.sdk.utils.Utility
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.android.synthetic.main.activity_zhuan_pan.*


class ZhuanPanActivity : BaseActivity<ZhuanPanPresenter>(), ZhuanPanContract.View {
    override fun addNum() {
        num++
        tvNum.text = "剩余转盘次数：$num"
    }

    override fun initPresenter(): ZhuanPanPresenter {
        return ZhuanPanPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_zhuan_pan
    }

    private lateinit var idList: MutableList<Int>
    private lateinit var scoreStr: String

    private var num = 0
    private var shareHandler: WbShareHandler? = null
    private var wxapi: IWXAPI? = null
    private var shareTitle = "下载抽石榴籽"

    override fun initView(savedInstanceState: Bundle?) {
        hideTitleBar()

        btnBack.setOnClickListener {
            finish()
        }

        btnShare.setOnClickListener {
            doShare()
        }

        mPresenter.getLotteryModel()
    }

    override fun setLotteryModel(model: LotteryModel) {
        num = model.num
        tvNum.text = "剩余转盘次数：" + model.num

        //颜色
        val colors = arrayOf<Int>(Color.parseColor("#FFC6B1"),
                Color.parseColor("#FCB195"),
                Color.parseColor("#FFC6B1"),
                Color.parseColor("#FCB195"),
                Color.parseColor("#FFC6B1"),
                Color.parseColor("#FCB195"))

        idList = model.data.map { it.id }.toMutableList()


        //文字
        val des = model.data.map { it.name }.toTypedArray()

        LogUtils.e(JSON.toJSONString(idList), JSON.toJSONString(des))

//        //图标
//        var mListBitmap: List<Bitmap> = ArrayList()
//        for (i in colors.indices) {
//            mListBitmap.add(BitmapFactory.decodeResource(getResources(), R.mipmap.iphone))
//        }
//        //主动旋转一下图片
//        mListBitmap = WheelSurfView.rotateBitmaps(mListBitmap)

        //获取第三个视图
        val build = WheelSurfView.Builder()
                .setmColors(colors)
                .setmHuanImgRes(R.mipmap.zhuan_pan_yuan_bg)
                .setmGoImgRes(R.mipmap.zhuan_pan_go)
                .setmDeses(des)
                .setmTypeModel(1)
                .setmTypeNum(6)
                .build()
        wheelSurfView.setConfig(build)

        //添加滚动监听
        wheelSurfView.setRotateListener(object : RotateListener {
            override fun rotateEnd(position: Int, des: String) {
                wheelSurfView.goBtn.isEnabled = true
                val dialog = ConfirmDialog(this@ZhuanPanActivity)
                dialog.setTitle("新增石榴籽")

                dialog.setStyleMessage(SpanUtils().appendLine("恭喜您").setHorizontalAlign(Layout.Alignment.ALIGN_CENTER).setFontSize(17, true).setForegroundColor(Color.parseColor("#FF3C44"))
                        .append("获得$scoreStr").setHorizontalAlign(Layout.Alignment.ALIGN_CENTER).setForegroundColor(Color.parseColor("#FF3C44"))
                        .create())

                dialog.setYesOnclickListener("知道了") {
                    dialog.dismiss()
                }

                dialog.show()
            }

            override fun rotating(valueAnimator: ValueAnimator) {

            }

            override fun rotateBefore(goImg: ImageView) {
                if (num > 0) {
                    wheelSurfView.goBtn.isEnabled = false
                    mPresenter.startLottery()
                } else {
                    ToastUtils.showShort("转盘次数不足")
                }

            }
        })
    }

    override fun setLotteryResult(model: LotteryResultModel?) {
        if (model != null) {
            num--
            tvNum.text = "剩余转盘次数：$num"
            scoreStr = model.name
            wheelSurfView.startRotate(idList.size - idList.indexOf(model.id) + 1)
        } else {
            wheelSurfView.goBtn.isEnabled = true
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        wheelSurfView.setRotateListener(null)
    }

    private fun doShare() {
        val sharePopupWindow = SharePopupWindow(this)
        sharePopupWindow.setISharePopupWindowClickListener(object : SharePopupWindow.ISharePopupWindowClickListener {
            override fun onWxClick() {
                shareToWX()
                sharePopupWindow.dismiss()
            }

            override fun onWxFriendClick() {
                shareToWXFriend()
                sharePopupWindow.dismiss()
            }

            override fun onQQClick() {
                shareToQQ()
                sharePopupWindow.dismiss()
            }

            override fun onWBClick() {
                shareToWB()
                sharePopupWindow.dismiss()
            }

        })
        sharePopupWindow.showAtScreenBottom(layout)
    }


    /**
     * 判断是否安装微信
     */
    private fun isWeiXinAppInstall(): Boolean {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(this, Contacts.WX_APPID)
        if (wxapi!!.isWXAppInstalled) {
            return true
        } else {
            ToastUtils.showShort("您未安装微信客户端")
            return false
        }
    }


    private fun shareToWXFriend() {
        if (!isWeiXinAppInstall()) {
            ToastUtils.showShort("您未安装微信")
            return
        }
        if (!isWXAppSupportAPI()) {
            ToastUtils.showShort("您的微信版本不支持分享功能")
            return
        }

        val webpage = WXWebpageObject()
        webpage.webpageUrl = SHARE_DOWNLOAD_URL
        val msg = WXMediaMessage(webpage)
        msg.title = shareTitle
        msg.description = "榴乡怀远"
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.img_logo)
        val thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true)
        bmp.recycle()
        msg.thumbData = ImageUtils.bitmap2Bytes(YBitmapUtils.changeColor(thumbBmp), Bitmap.CompressFormat.JPEG)
        val req = SendMessageToWX.Req()
        req.transaction = "webpage" + System.currentTimeMillis()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneTimeline
        wxapi!!.sendReq(req)

        InterfaceUtils.getInstance().add(onClickResult)
    }


    /**
     * 是否支持分享到朋友圈
     */
    fun isWXAppSupportAPI(): Boolean {
        if (isWeiXinAppInstall()) {
            val wxSdkVersion = wxapi!!.wxAppSupportAPI
            return wxSdkVersion >= 0x21020001
        } else {
            return false
        }
    }


    private var mTencent: Tencent? = null
    private fun shareToQQ() {
        mTencent = Tencent.createInstance(Contacts.QQ_APPID, application)

        if (!mTencent!!.isQQInstalled(this)) {
            ToastUtils.showShort("您未安装QQ客户端")
            return
        }

        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle)
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "摘要") //可选，最长40个字
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, SHARE_DOWNLOAD_URL) //必填 	这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "榴乡怀远")
        mTencent!!.shareToQQ(this, params, object : IUiListener {
            override fun onComplete(p0: Any?) {
                mPresenter.shareAward()
                ToastUtils.showShort("分享成功")
            }

            override fun onCancel() {
                ToastUtils.showShort("取消分享")
            }

            override fun onError(p0: UiError?) {
                ToastUtils.showShort("分享失败：" + p0?.errorMessage)
            }

        })

    }

    private fun shareToWX() {
        if (!isWeiXinAppInstall()) {
            ToastUtils.showShort("您未安装微信")
            return
        }
        if (!isWXAppSupportAPI()) {
            ToastUtils.showShort("您的微信版本不支持分享功能")
            return
        }

        val webpage = WXWebpageObject()
        webpage.webpageUrl = SHARE_DOWNLOAD_URL
        val msg = WXMediaMessage(webpage)
        msg.title = shareTitle
        msg.description = "榴乡怀远"
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.img_logo)
        val thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true)
        bmp.recycle()
        msg.thumbData = ImageUtils.bitmap2Bytes(YBitmapUtils.changeColor(thumbBmp), Bitmap.CompressFormat.JPEG)
        val req = SendMessageToWX.Req()
        req.transaction = "webpage" + System.currentTimeMillis()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession
        wxapi!!.sendReq(req)

        InterfaceUtils.getInstance().add(onClickResult)

    }

    private fun shareToWB() {
        val pinfo = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
        var isInstall = false
        if (pinfo != null) {
            pinfo.forEach {

                val pn = it.packageName
                if (pn == "com.sina.weibo") {
                    isInstall = true
                }
            }
        }
        if (!isInstall) {
            ToastUtils.showShort("您未安装微博")
        } else {
            shareHandler = WbShareHandler(this)
            shareHandler!!.registerApp()

            val weiboMessage = WeiboMultiMessage()

            val mediaObject = WebpageObject()
            mediaObject.identify = Utility.generateGUID()
            mediaObject.title = shareTitle
            mediaObject.description = "榴乡怀远"
            val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.default_avatar)
            mediaObject.setThumbImage(YBitmapUtils.changeColor(bitmap))
            mediaObject.actionUrl = SHARE_DOWNLOAD_URL
            mediaObject.defaultText = "Webpage"
            weiboMessage.mediaObject = mediaObject

            shareHandler!!.shareMessage(weiboMessage, false)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (shareHandler != null) {
            shareHandler!!.doResultIntent(intent, object : WbShareCallback {
                override fun onWbShareFail() {
                    ToastUtils.showShort("分享失败")
                }

                override fun onWbShareCancel() {
                    ToastUtils.showShort("取消分享")
                }

                override fun onWbShareSuccess() {
                    mPresenter.shareAward()
                    ToastUtils.showShort("分享成功")
                }

            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (mTencent != null) {
            Tencent.onActivityResultData(requestCode, resultCode, data, null)
        }
    }

    private var onClickResult = object : InterfaceUtils.OnClickResult {
        override fun onResult(msg: String?) {
            ToastUtils.showShort("分享成功")
            mPresenter.shareAward()
            InterfaceUtils.getInstance().remove(this)
        }

    }


}
