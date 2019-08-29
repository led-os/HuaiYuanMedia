package cn.tklvyou.huaiyuanmedia.ui.home

import android.os.Bundle
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseWebViewActivity
import kotlinx.android.synthetic.main.activity_banner_details.*

class BannerDetailsActivity : BaseWebViewActivity<NullPresenter>() {

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_banner_details
    }

    override fun initView(savedInstanceState: Bundle?) {
        val title = intent.getStringExtra("title")
        val html = intent.getStringExtra("content")

        setTitle(title)
        setNavigationImage()
        setNavigationOnClickListener {
            finish()
        }

        initWebView(webView = webView)

        loadHtml(html)
    }

    override fun setTitleContent(title: String) {

    }

}
