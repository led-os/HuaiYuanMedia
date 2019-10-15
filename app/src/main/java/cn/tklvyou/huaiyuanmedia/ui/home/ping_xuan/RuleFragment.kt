package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan

import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseX5WebViewFragment
import kotlinx.android.synthetic.main.fragment_ping_xuan_rule.*


class RuleFragment : BaseX5WebViewFragment<NullPresenter>() {

    override fun setTitleContent(title: String) {

    }

    override fun getLoadingView(): View {
        return mX5WebView
    }


    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_ping_xuan_rule
    }

    override fun lazyData() {
    }


    override fun initView() {
        initWebView(mX5WebView)

        val content = mBundle.getString("content")

        loadHtml(content)
    }


    override fun onResume() {
        super.onResume()
        mX5WebView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mX5WebView.onPause()
    }


}
