package cn.tklvyou.huaiyuanmedia.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.model.NewsMultipleItem
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseFragment
import cn.tklvyou.huaiyuanmedia.model.Channel
import cn.tklvyou.huaiyuanmedia.model.ChannelModel
import kotlinx.android.synthetic.main.fragment_home.*
import cn.tklvyou.huaiyuanmedia.ui.adapter.ChannelPagerAdapter
import cn.tklvyou.huaiyuanmedia.ui.camera.TakePhotoActivity
import cn.tklvyou.huaiyuanmedia.ui.home.new_list.NewsListFragment
import cn.tklvyou.huaiyuanmedia.ui.listener.OnChannelListener
import cn.tklvyou.huaiyuanmedia.ui.video_edit.VideoEditActivity
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle3.components.support.RxFragment
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import java.io.Serializable


class HomeFragment : BaseFragment<HomePresenter>(), HomeContract.View {

    override fun initPresenter(): HomePresenter {
        return HomePresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_home
    }

    private var mSelectedChannels = ArrayList<String>()
    private val mChannelFragments = ArrayList<RxFragment>()
    private var mChannelPagerAdapter: ChannelPagerAdapter? = null

    private var commonNavigator: CommonNavigator? = null

    private var isRefresh = false

    override fun initView() {
        homeTitleBar.setBackgroundResource(R.drawable.shape_gradient_common_titlebar)
        if (SPUtils.getInstance().getInt("groupId") != 2) {
            homeTitleBar.rightCustomView.visibility = View.GONE
        }

        homeTitleBar.rightCustomView.setOnClickListener {
            RxPermissions(this)
                    .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    .subscribe { granted ->
                        if (granted) { // Always true pre-M
                            val intent = Intent(context, TakePhotoActivity::class.java)
                            intent.putExtra("is_video", true)
                            intent.putExtra("page", "V视")
                            startActivity(intent)
                            isRefresh = true
                        } else {
                            ToastUtils.showShort("权限拒绝，无法使用")
                        }
                    }
        }

        initMagicIndicator()
        mPresenter.getHomeChannel()
    }

    override fun lazyData() {

    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            isRefresh = false
            if (mChannelFragments.size > 0) {
                (mChannelFragments[0] as NewsListFragment).refreshData()
            }
        }
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden && !isFirstResume) {
            mViewPager.setCurrentItem(0, false)
            if (mChannelFragments.size > 0) {
                (mChannelFragments[0] as NewsListFragment).refreshData()
            }
        }
    }

    private fun initMagicIndicator() {
        commonNavigator = CommonNavigator(context)
        commonNavigator!!.isSkimOver = true
        commonNavigator!!.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mSelectedChannels.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val clipPagerTitleView = ClipPagerTitleView(context)
                clipPagerTitleView.text = mSelectedChannels[index]
                clipPagerTitleView.textColor = Color.parseColor("#888888")
                clipPagerTitleView.clipColor = context.resources.getColor(R.color.colorAccent)
                clipPagerTitleView.setOnClickListener { mViewPager.setCurrentItem(index, false) }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                return null
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mViewPager)
    }


    override fun setHomeChannel(channelList: MutableList<String>?) {
        mSelectedChannels = channelList as ArrayList<String>
        commonNavigator!!.notifyDataSetChanged()

        initChannelFragments()

        mChannelPagerAdapter = ChannelPagerAdapter(mChannelFragments, childFragmentManager)
        mViewPager.adapter = mChannelPagerAdapter
        mViewPager.offscreenPageLimit = mSelectedChannels.size

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                if (p0 == 0) {
                    if (SPUtils.getInstance().getInt("groupId") == 2) {
                        homeTitleBar.rightCustomView.visibility = View.VISIBLE
                    } else {
                        homeTitleBar.rightCustomView.visibility = View.GONE
                    }
                } else {
                    homeTitleBar.rightCustomView.visibility = View.GONE
                }
            }

        })


        mPresenter.getTotalChannel()

    }


    override fun setTotalChannel(model: ChannelModel) {
        val selectChannel = ArrayList<Channel>()
        val unSelectChannel = ArrayList<Channel>()
        model.selected.forEach {
            selectChannel.add(Channel(it))
        }

        model.unselected.forEach {
            unSelectChannel.add(Channel(it))
        }


        ivAddChannel.setOnClickListener {
            val dialogFragment = ChannelDialogFragment.newInstance(selectChannel, unSelectChannel)
            dialogFragment.setOnChannelListener(object : OnChannelListener {
                override fun onItemMove(starPos: Int, endPos: Int) {
//                    listMove(selectChannel, starPos, endPos)
//                    listMove(unSelectChannel, starPos, endPos)
                }

                override fun onMoveToMyChannel(starPos: Int, endPos: Int) {
                    //移动到我的频道
//                    val channel = unSelectChannel.removeAt(starPos)
//                    selectChannel.add(endPos, channel)

//                    NewsListFragment newsFragment = new NewsListFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putString(Constant.CHANNEL_CODE, channel.channelCode);
//                    bundle.putBoolean(Constant.IS_VIDEO_LIST, channel.channelCode.equals(mChannelCodes[1]));
//                    newsFragment.setArguments(bundle);
//                    mChannelFragments.add(newsFragment);
                }

                override fun onMoveToOtherChannel(starPos: Int, endPos: Int) {
                    //移动到推荐频道
//                    unSelectChannel.add(endPos, selectChannel.removeAt(starPos))
//                    mChannelFragments.removeAt(starPos)
                }

            })
            dialogFragment.show(childFragmentManager, "CHANNEL")
            dialogFragment.setOnDismissListener {
                //                mChannelPagerAdapter!!.notifyDataSetChanged()
            }
        }
    }


    /**
     * 初始化已选频道的fragment的集合
     */
    private fun initChannelFragments() {
        for ((index, item) in mSelectedChannels.withIndex()) {
            val newsFragment = NewsListFragment()
            val bundle = Bundle()
            when (item) {
                "V视频" -> {
                    bundle.putInt("type", NewsMultipleItem.VIDEO)
                }
                "濉溪TV" -> {
                    bundle.putInt("type", NewsMultipleItem.TV)
                }
                "新闻" -> {
                    bundle.putInt("type", NewsMultipleItem.NEWS)
                }
                "视讯" -> {
                    bundle.putInt("type", NewsMultipleItem.SHI_XUN)
                }
                "问政" -> {
                    bundle.putInt("type", NewsMultipleItem.WEN_ZHENG)
                }
                "矩阵" -> {
                    bundle.putInt("type", NewsMultipleItem.JU_ZHENG)
                }
                "原创" -> {
                    bundle.putInt("type", NewsMultipleItem.WECHAT_MOMENTS)
                }
                "悦读" -> {
                    bundle.putInt("type", NewsMultipleItem.READING)
                }
                "悦听" -> {
                    bundle.putInt("type", NewsMultipleItem.LISTEN)
                }
                "党建" -> {
                    bundle.putInt("type", NewsMultipleItem.DANG_JIAN)
                }
                "专栏" -> {
                    bundle.putInt("type", NewsMultipleItem.ZHUAN_LAN)
                }
                "公告" -> {
                    bundle.putInt("type", NewsMultipleItem.GONG_GAO)
                }
                "直播" -> {
                    bundle.putInt("type", NewsMultipleItem.ZHI_BO)
                }

            }

            newsFragment.arguments = bundle
            mChannelFragments.add(newsFragment)//添加到集合中
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST && data != null) {
                // 图片、视频、音频选择结果回调
                val selectList = PictureSelector.obtainMultipleResult(data)
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                val intent = Intent(context, VideoEditActivity::class.java)
                intent.putExtra("page", "V视")
                intent.putExtra("data", selectList as Serializable)
                startActivity(intent)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        commonNavigator = null
        mChannelFragments.clear()
        mChannelPagerAdapter = null
    }

    fun reload() {
        mViewPager.setCurrentItem(0, false)
        if (mChannelFragments.size > 0) {
            (mChannelFragments[0] as NewsListFragment).refreshData()
        }
    }

    private fun listMove(datas: ArrayList<Channel>, starPos: Int, endPos: Int) {
        val o = datas.get(starPos)
        //先删除之前的位置
        datas.removeAt(starPos)
        //添加到现在的位置
        datas.add(endPos, o)
    }

}