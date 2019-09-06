package cn.tklvyou.huaiyuanmedia.ui.audio

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.Contacts
import cn.tklvyou.huaiyuanmedia.utils.InterfaceUtils
import cn.tklvyou.huaiyuanmedia.utils.YBitmapUtils
import cn.tklvyou.huaiyuanmedia.widget.SharePopupWindow
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
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
import kotlinx.android.synthetic.main.activity_service_webview.*
import com.tencent.smtt.sdk.*
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError


class ServiceWebviewActivity : BaseActivity<NullPresenter>() {

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_service_webview
    }


    private var url = ""
    private var shareTitle = ""
    private var other = false
    private var shareHandler: WbShareHandler? = null
    private var wxapi: IWXAPI? = null

    override fun initView(savedInstanceState: Bundle?) {
        url = intent.getStringExtra("url")
        val other = intent.getBooleanExtra("other", false)

        shareTitle = intent.getStringExtra("share_title")

        if (other) {
            setTitle("文章")
        } else {
            setTitle("服务")
        }

        val sharedEnable = intent.getBooleanExtra("share_enable", true) && url.isNotEmpty() && shareTitle.isNotEmpty()
        if (sharedEnable) {
            setPositiveImage(R.mipmap.icon_title_bar_share)
            setPositiveOnClickListener {
                doShare()
            }
        }


        setNavigationImage()
        setNavigationOnClickListener { finish() }

        mPageLoadingProgressBar.progressDrawable = this.resources
                .getDrawable(R.drawable.color_progressbar)

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }

        mWebView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(p0: WebView?, p1: Int) {
                if (p1 >= 95) {
                    mPageLoadingProgressBar.visibility = View.GONE
                } else {
                    mPageLoadingProgressBar.visibility = View.VISIBLE
                }

                mPageLoadingProgressBar.progress = p1

                super.onProgressChanged(p0, p1)
            }

            override fun onReceivedTitle(p0: WebView?, p1: String?) {
                super.onReceivedTitle(p0, p1)
                setTitle(p1)
            }

        }

        mWebView.setDownloadListener(object : DownloadListener {

            override fun onDownloadStart(arg0: String, arg1: String, arg2: String,
                                         arg3: String, arg4: Long) {

            }
        })

        val webSetting = mWebView.settings
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.setAppCachePath(this.getDir("appcache", 0).path)
        webSetting.databasePath = this.getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).path)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        mWebView.loadUrl(url)
        mWebView.setInitialScale(1)
        CookieSyncManager.createInstance(this)
        CookieSyncManager.getInstance().sync()

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack()
                return true
            } else
                return super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onDestroy() {
        if (mWebView != null)
            mWebView.destroy()
        super.onDestroy()
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
        sharePopupWindow.showAtScreenBottom(mWebView)
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
        webpage.webpageUrl = url
        val msg = WXMediaMessage(webpage)
        msg.title = shareTitle
        msg.description = "榴香怀远"
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.share_icon)
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
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url) //必填 	这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://medium2.tklvyou.cn/qiniu/20190812/FiYgpZ32gNGMbRGxesypz9sSWUbI.png") // 可选 分享图片的URL或者本地路径
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "榴香怀远")
        mTencent!!.shareToQQ(this, params, object : IUiListener {
            override fun onComplete(p0: Any?) {
//                mPresenter.getScoreByShare(id)
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
        webpage.webpageUrl = url
        val msg = WXMediaMessage(webpage)
        msg.title = shareTitle
        msg.description = "榴香怀远"
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.share_icon)
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
            mediaObject.description = "榴香怀远"
            val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.default_avatar)
            mediaObject.setThumbImage(YBitmapUtils.changeColor(bitmap))
            mediaObject.actionUrl = url
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
//            mPresenter.getScoreByShare(id)
            InterfaceUtils.getInstance().remove(this)
        }

    }


}
