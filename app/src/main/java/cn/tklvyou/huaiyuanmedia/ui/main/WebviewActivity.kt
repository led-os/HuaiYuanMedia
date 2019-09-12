package cn.tklvyou.huaiyuanmedia.ui.main

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
import cn.tklvyou.huaiyuanmedia.base.activity.BaseWebViewActivity
import cn.tklvyou.huaiyuanmedia.base.activity.BaseX5WebViewActivity
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


class WebviewActivity : BaseX5WebViewActivity<NullPresenter>() {
    override fun setTitleContent(title: String) {
        setTitle(title)
    }

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_service_webview
    }


    private var url = ""
    private var isAd =false
    override fun initView(savedInstanceState: Bundle?) {
        url = intent.getStringExtra("url")
        isAd = intent.getBooleanExtra("ad",false)
        setNavigationImage()
        setNavigationOnClickListener {
            if(isAd){
                startActivity(Intent(this@WebviewActivity,MainActivity::class.java))
            }
            finish()
        }

        initProgressBar(mPageLoadingProgressBar)
        initWebView(mWebView)

        mPageLoadingProgressBar.progressDrawable = this.resources
                .getDrawable(R.drawable.color_progressbar)

        loadUrl(url)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isAd){
                startActivity(Intent(this@WebviewActivity,MainActivity::class.java))
            }
            finish()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

}
