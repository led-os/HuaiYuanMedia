package cn.tklvyou.huaiyuanmedia.ui.service

import android.graphics.Color
import android.os.Bundle
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseX5WebViewActivity
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.PointModel
import cn.tklvyou.huaiyuanmedia.widget.dailog.ConfirmDialog
import com.blankj.utilcode.util.SpanUtils
import kotlinx.android.synthetic.main.activity_goods_details.*

class GoodsDetailsActivity : BaseX5WebViewActivity<GoodsDetailPresenter>(), GoodsDetailContract.View {

    override fun initPresenter(): GoodsDetailPresenter {
        return GoodsDetailPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_goods_details
    }

    private var id = 0

    override fun initView(savedInstanceState: Bundle?) {
        setTitle("商品详情")
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        initWebView(webView)

        id = intent.getIntExtra("id", 0)
        mPresenter.getGoodsDetails(id)
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getGoodsDetails(id)
    }

    override fun setTitleContent(title: String) {

    }

    override fun setGoodsDetail(model: PointModel) {
        GlideManager.loadImg(model.image, iVBanner)
        tvName.text = model.name
        tvScore.text = "兑换：${model.score}石榴籽"
        tvStore.text = "库存：${model.stock}"
        loadHtml(model.content)

        if (model.stock <= 0) {
            btnExchange.isEnabled = false
            btnExchange.setBackgroundColor(Color.parseColor("#FFA0A0A0"))
        } else {
            btnExchange.setOnClickListener {

                val dialog = ConfirmDialog(this)
                dialog.setTitle("商品兑换")
                dialog.setStyleMessage(SpanUtils().append("是否消耗")
                        .append("" + model.score + "石榴籽").setForegroundColor(Color.parseColor("#FF3C44"))//resources.getColor(R.color.colorAccent)
                        .append("兑换此商品？")
                        .create())

                dialog.setYesOnclickListener("立即兑换") {
                    mPresenter.exchangeGoods(id)
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }


    override fun exchangeSuccess() {
        finish()
    }


}
