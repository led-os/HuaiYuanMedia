package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.model.PingXuanModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.ChannelPagerAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan.search.PingXuanSearchListActivity
import cn.tklvyou.huaiyuanmedia.utils.BannerGlideImageLoader
import cn.tklvyou.huaiyuanmedia.utils.CountDownUtil
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.activity_ping_xuan_details.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class PingXuanDetailsActivity : BaseActivity<PingXuanPresenter>(), PingXuanContract.View {


    override fun initPresenter(): PingXuanPresenter {
        return PingXuanPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_ping_xuan_details
    }

    private var mTabNameList = ArrayList<String>()
    private val mFragments = ArrayList<Fragment>()
    private lateinit var commonNavigator: CommonNavigator
    private var id = 0

    private lateinit var countDownUtil: CountDownUtil

    private var showPage = true
    override fun initView(savedInstanceState: Bundle?) {
        setTitle("评选")
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        id = intent.getIntExtra("id", 0)

        countDownUtil = CountDownUtil()

        mSmartRefreshLayout.setOnRefreshListener {
            mPresenter.getPingXuanDetails(id, showPage)
        }
        mSmartRefreshLayout.setEnableLoadMore(false)

        mPresenter.getPingXuanDetails(id, showPage)
    }

    override fun onRetry() {
        super.onRetry()
        mPresenter.getPingXuanDetails(id, showPage)
    }

    override fun setPingXuanModel(model: PingXuanModel) {
        if (!showPage) {
            mSmartRefreshLayout.finishRefresh()
            if(this::countDownUtil.isInitialized){
                countDownUtil.onDestroy()
                countDownUtil = CountDownUtil()
            }
        }else{
            initMagicIndicator(model.content)
        }
        showPage = false

        btnSearch.setOnClickListener {
            val intent = Intent(this, PingXuanSearchListActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

        initBanner(model.images)

        tvOptionNum.text = "总人数 ${model.option_num}"
        tvRecordNum.text = "总投票数 ${model.record_num}"
        tvVisitNum.text = "访问量 ${model.visit_num}"

        countDownUtil.start(model.endtime * 1000L, object : CountDownUtil.OnCountDownCallBack {
            override fun onFinish() {
                tvDay.text = "0 天"
                tvHour.text = "0 时"
                tvMinute.text = "0 分"
                tvSecond.text = "0 秒"
            }

            override fun onProcess(day: Int, hour: Int, minute: Int, second: Int) {
                tvDay.text = "$day 天"
                tvHour.text = "$hour 时"
                tvMinute.text = "$minute 分"
                tvSecond.text = "$second 秒"
            }

        })


    }


    private fun initMagicIndicator(content: String) {
        mTabNameList.clear()
        mFragments.clear()

        mTabNameList.add("默认排序")
        mTabNameList.add("人气排行")
        mTabNameList.add("活动规则")

        mFragments.add(initDefaultPingXuanListFragment(true))
        mFragments.add(initDefaultPingXuanListFragment(false))
        mFragments.add(initRuleFragment(content))

        commonNavigator = CommonNavigator(this)
        commonNavigator.isSkimOver = true
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mTabNameList.size
            }


            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = ColorTransitionPagerTitleView(context)
                simplePagerTitleView.normalColor = resources.getColor(R.color.default_black_text_color)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.colorAccent)
                simplePagerTitleView.text = mTabNameList[index]
                simplePagerTitleView.textSize = 15f
                simplePagerTitleView.setOnClickListener {
                    mViewPager.currentItem = index
                }

                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                val linePagerIndicator = LinePagerIndicator(context)
                linePagerIndicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                linePagerIndicator.lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
                linePagerIndicator.setColors(resources.getColor(R.color.colorAccent))
                return linePagerIndicator
            }
        }

        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mViewPager)

        mViewPager.adapter = ChannelPagerAdapter(mFragments, supportFragmentManager)
        mViewPager.offscreenPageLimit = mTabNameList.size
        commonNavigator.notifyDataSetChanged()
    }

    private fun initDefaultPingXuanListFragment(isNormal: Boolean): Fragment {
        val fragment = DefaultPingXuanListFragment()
        val mBundle = Bundle()
        mBundle.putInt("id", id)
        mBundle.putBoolean("isNormal", isNormal)
        fragment.arguments = mBundle
        return fragment
    }

    private fun initRuleFragment(content: String): RuleFragment {
        val ruleFragment = RuleFragment()
        val ruleBundle = Bundle()
        ruleBundle.putString("content", content)
        ruleFragment.arguments = ruleBundle
        return ruleFragment
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
        banner.setIndicatorGravity(BannerConfig.RIGHT)
        banner.setOnBannerListener { position ->
        }
        //banner设置方法全部调用完毕时最后调用
        banner.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::countDownUtil.isInitialized) {
            countDownUtil.onDestroy()
        }
    }

}
