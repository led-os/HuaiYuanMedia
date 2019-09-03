package cn.tklvyou.huaiyuanmedia.ui.audio

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_service_webview.*
import com.tencent.smtt.sdk.*


class ServiceWebviewActivity : BaseActivity<NullPresenter>() {

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_service_webview
    }


    override fun initView(savedInstanceState: Bundle?) {
        val url = intent.getStringExtra("url")
        val other = intent.getBooleanExtra("other",false)
        if(other){
            setTitle("文章")
        }else{
            setTitle("服务")
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
                if(p1 >=95){
                    mPageLoadingProgressBar.visibility = View.GONE
                }else{
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

}
