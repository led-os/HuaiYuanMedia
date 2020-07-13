package cn.tklvyou.huaiyuanmedia.ui.work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullContract
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseX5WebViewFragment
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.fragment_work.*

class WorkFragment : BaseX5WebViewFragment<NullPresenter>(){

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_work
    }

    override fun setTitleContent(title: String) {
    }


    override fun getLoadingView(): View {
        return mWebView
    }

    override fun initView() {
        mTitleBar.setBackgroundResource(R.drawable.shape_gradient_common_titlebar)
        initProgressBar(mPageLoadingProgressBar)

        initWebView(mWebView)

        loadUrl("http://bb.ahzwfw.gov.cn/bsdt-h5/bsdt-hall?districtCode=340321000000")

    }

    override fun onResume() {
        super.onResume()
        mWebView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mWebView.onPause()
    }


    override fun lazyData() {

    }

    override fun onBackPressed(): Boolean {
        return if(mWebView.canGoBack()){
            mWebView.goBack()
            true
        }else{
            false
        }
    }

}
