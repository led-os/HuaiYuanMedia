package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.base.activity.BaseX5WebViewActivity
import cn.tklvyou.huaiyuanmedia.helper.GlideManager
import cn.tklvyou.huaiyuanmedia.model.PingXuanPersionModel
import cn.tklvyou.huaiyuanmedia.utils.BannerGlideImageLoader
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.activity_ping_xuan_persion.*

class PingXuanPersionActivity : BaseX5WebViewActivity<PingXuanPersionPresenter>(), PingXuanContract.PersionView {


    override fun setTitleContent(title: String) {
    }


    override fun initPresenter(): PingXuanPersionPresenter {
        return PingXuanPersionPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_ping_xuan_persion
    }

    private var id = 0
    private var count = 0
    override fun initView(savedInstanceState: Bundle?) {
        setTitle("", R.mipmap.home_title_logo)
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        initWebView(mX5WebView)

        id = intent.getIntExtra("id", 0)
        mPresenter.getPingXuanPersionDetails(id)
    }


    override fun onRetry() {
        super.onRetry()
        mPresenter.getPingXuanPersionDetails(id)
    }

    override fun setPingXuanPersionModel(model: PingXuanPersionModel) {
        count = model.count
        GlideManager.loadCircleImg(model.image, ivImage)
        tvName.text = model.name
        tvCode.text = model.code
        tvRecordNum.text = "${model.count}票"
        tvSort.text = "第${model.rank}名"

//        if (model.images.isNotEmpty()){
        banner.visibility = View.VISIBLE
        initBanner(model.images)
//        }

        loadHtml(model.content)

        btnSubmit.setOnClickListener {
            mPresenter.vote(id)
        }
    }

    private fun initBanner(images: MutableList<String>) {
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
        //设置图片加载器
        banner.setImageLoader(BannerGlideImageLoader())
        //设置图片集合
        banner.setImages(images)
        //设置自动轮播，默认为true
        banner.isAutoPlay(true)
        //设置轮播时间
        banner.setDelayTime(3000)
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER)
        banner.setOnBannerListener { position ->
        }
        //banner设置方法全部调用完毕时最后调用
        banner.start()
    }

    override fun voteSuccess() {
        count++
        tvRecordNum.text = "${count}票"
    }

    override fun voteFailed() {
    }


}
