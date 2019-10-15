package cn.tklvyou.huaiyuanmedia.base.fragment

import cn.tklvyou.huaiyuanmedia.base.BaseContract
import cn.tklvyou.huaiyuanmedia.widget.x5.X5WebView
import com.blankj.utilcode.util.LogUtils
import com.tencent.smtt.sdk.DownloadListener
import com.tencent.smtt.sdk.WebSettings
import org.jsoup.Jsoup
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import cn.tklvyou.huaiyuanmedia.base.interfaces.BackHandledInterface

abstract class BaseX5WebViewFragment<P : BaseContract.BasePresenter<*>> : BaseFragment<P>() {

    private var enablePadding = false
    private var mProgressBar: ProgressBar? = null
    private lateinit var mWebView: X5WebView
    protected var mBackHandledInterface: BackHandledInterface? = null


    public fun initProgressBar(mProgressBar: ProgressBar) {
        this.mProgressBar = mProgressBar
    }

    public fun initWebView(webView: X5WebView) {

        if (mActivity is BackHandledInterface) {
            this.mBackHandledInterface = mActivity as BackHandledInterface
            //告诉FragmentActivity，当前Fragment在栈顶
            mBackHandledInterface!!.setSelectedFragment(this)
        }


        this.mWebView = webView

        mWebView.webViewClient = object : com.tencent.smtt.sdk.WebViewClient() {
            override fun shouldOverrideUrlLoading(view: com.tencent.smtt.sdk.WebView?, url: String?): Boolean {
                return false
            }

            override fun onPageFinished(view: com.tencent.smtt.sdk.WebView?, url: String?) {
                if (enablePadding) {
                    //为webView 添加Padding
                    mWebView.loadUrl("javascript:document.body.style.padding=\"3%\"; void 0")
                }
                super.onPageFinished(view, url)
            }
        }

        mWebView.webChromeClient = object : com.tencent.smtt.sdk. WebChromeClient() {

            override fun onProgressChanged(p0: com.tencent.smtt.sdk.WebView?, p1: Int) {

                if (p1 >= 95) {
                    mProgressBar?.visibility = View.GONE
                } else {
                    mProgressBar?.visibility = View.VISIBLE
                }

                mProgressBar?.progress = p1
                super.onProgressChanged(p0, p1)
            }

            override fun onReceivedTitle(p0: com.tencent.smtt.sdk.WebView?, p1: String?) {
                super.onReceivedTitle(p0, p1)

                var title = ""
                if (p1 == null) {
                    title = ""
                } else {
                    title = p1
                }
                setTitleContent(title)

            }

        }


        val webSetting = mWebView.settings
        webSetting.allowFileAccess = true
//        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSetting.useWideViewPort = false
        webSetting.setSupportMultipleWindows(false)
        webSetting.loadWithOverviewMode = true
        webSetting.domStorageEnabled = true
        webSetting.setSupportZoom(false)
        webSetting.builtInZoomControls = true
        webSetting.displayZoomControls = false
        webSetting.javaScriptEnabled = true
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
//        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
//        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH)
        // webSetting.setPreFectch(true)

    }

    public fun loadUrl(url: String) {
        enablePadding = false
        mWebView.loadUrl(url)
    }

    public fun loadHtml(html: String) {
        enablePadding = true
        imageFillWidth(html)
    }


    protected abstract fun setTitleContent(title: String)

    /**
     * 处理图片视频填充手机宽度
     *
     * @param webView
     */
    private fun imageFillWidth(content: String) {
        val doc = Jsoup.parse(content)

        //修改视频标签
        val embeds = doc.getElementsByTag("embed")
        for (element in embeds) {
            //宽度填充手机，高度自适应
            element.attr("width", "100%").attr("height", "auto")
        }
        //webview 无法正确识别 embed 为视频，所以这里把这个标签改成 video 手机就可以识别了
        doc.select("embed").tagName("video")

        //控制图片的大小
        val imgs = doc.getElementsByTag("img")
        for (i in 0 until imgs.size) {
            //宽度填充手机，高度自适应
            imgs[i].attr("style", "width: 100%; height: auto;")
        }

        //对数据进行包装,除去WebView默认存在的一定像素的边距问题
        doc.body().append("<p></p>") //修复富文本为纯图片时部分手机显示不出来
        val data = "<html><head><style>img{width:100% !important;}</style></head><body style='margin:0;padding:0'>${doc}</body></html>"


//        加载使用 jsoup 处理过的 html 文本
//        webView.loadDataWithBaseURL(Contacts.DEV_BASE_URL, doc.toString(), "text/html", "UTF-8", null)
        mWebView.loadData(data, "text/html; charset=UTF-8", null)
    }


    override fun onDestroy() {
        super.onDestroy()
        mProgressBar = null
        mWebView.removeAllViews()
        try {
            mWebView.destroy()
        } catch (t: Throwable) {
        }

    }

}
