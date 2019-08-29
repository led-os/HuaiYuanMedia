package cn.tklvyou.huaiyuanmedia.ui.service

import android.os.Bundle
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseWebViewActivity
import cn.tklvyou.huaiyuanmedia.model.PointRuleModel
import kotlinx.android.synthetic.main.activity_point_rule.*

class PointRuleActivity : BaseWebViewActivity<PointRulePresenter>(),PointRuleContract.View {

    override fun initPresenter(): PointRulePresenter {
        return PointRulePresenter()
    }

    override fun getActivityLayoutID(): Int {
       return R.layout.activity_point_rule
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("石榴籽规则")
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        initWebView(webView)

        mPresenter.getPointRule()
    }


    override fun onRetry() {
        super.onRetry()
        mPresenter.getPointRule()
    }

    override fun setTitleContent(title: String) {
    }

    override fun setPointRule(model: PointRuleModel) {
        tvTitle.text = model.name
        tvName.text = model.nickname
        tvTime.text = model.time
        loadHtml(model.content)
    }


}
