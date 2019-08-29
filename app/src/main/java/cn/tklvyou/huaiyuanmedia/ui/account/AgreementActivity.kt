package cn.tklvyou.huaiyuanmedia.ui.account

import android.os.Bundle
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseWebViewActivity
import cn.tklvyou.huaiyuanmedia.model.SystemConfigModel
import kotlinx.android.synthetic.main.activity_agreement.*

class AgreementActivity : BaseWebViewActivity<AgreenmentPresenter>(),AccountContract.AgreenmentView {

    override fun setTitleContent(title: String) {
    }


    override fun initPresenter(): AgreenmentPresenter {
        return AgreenmentPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_agreement
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("注册协议")
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        initWebView(webView = webView)

        mPresenter.getSystemConfig()
    }

    override fun setSystemConfig(model: SystemConfigModel) {
        loadHtml(model.register)
    }


}
