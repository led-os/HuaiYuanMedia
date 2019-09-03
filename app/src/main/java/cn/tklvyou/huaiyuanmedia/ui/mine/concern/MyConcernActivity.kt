package cn.tklvyou.huaiyuanmedia.ui.mine.concern

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.ui.adapter.ChannelPagerAdapter
import cn.tklvyou.huaiyuanmedia.utils.CommonUtil
import cn.tklvyou.huaiyuanmedia.utils.SizeUtil
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.android.synthetic.main.activity_my_concern.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView

/**
 *@description :我的关注
 *@company :途酷科技
 */
class MyConcernActivity : BaseActivity<NullPresenter>() {
    private val mFragments = ArrayList<Fragment>()
    private var mTabNameList = ArrayList<String>()
    private lateinit var commonNavigator: CommonNavigator
    private var mChannelPagerAdapter: ChannelPagerAdapter? = null
    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getActivityLayoutID(): Int {
        return R.layout.activity_my_concern
    }


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("我的关注")
        setNavigationImage()
        setNavigationOnClickListener { finish() }

        mTabNameList.add("栏目")
        mTabNameList.add("好友")

        mFragments.add(getFragmentByType(1))
        mFragments.add(getFragmentByType(2))
        initMagicIndicator()
        mChannelPagerAdapter = ChannelPagerAdapter(mFragments, fragmentManager)
        articleViewPager.adapter = mChannelPagerAdapter
        articleViewPager.offscreenPageLimit = mTabNameList.size
        commonNavigator.notifyDataSetChanged()
    }


    private fun getFragmentByType(type: Int): Fragment {
        val newsFragment = MyConcernFragment()
        val bundle = Bundle()
        bundle.putInt("type", type)
        newsFragment.arguments = bundle
        return newsFragment
    }

    private fun initMagicIndicator() {
        commonNavigator = CommonNavigator(this)
        commonNavigator.isSkimOver = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mTabNameList.size
            }


            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val clipPagerTitleView = ClipPagerTitleView(context)
                clipPagerTitleView.background = CommonUtil.getDrawable(R.color.colorAccent)
                clipPagerTitleView.text = mTabNameList[index]
                clipPagerTitleView.textSize = SizeUtil.sp2px(13f)
                clipPagerTitleView.textColor = Color.parseColor("#55FFFFFF")
                clipPagerTitleView.clipColor = CommonUtil.getColor(R.color.white)
                clipPagerTitleView.setOnClickListener { articleViewPager.setCurrentItem(index, false) }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                return null
            }
        }
        commonNavigator.isAdjustMode = true
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, articleViewPager)

    }


}