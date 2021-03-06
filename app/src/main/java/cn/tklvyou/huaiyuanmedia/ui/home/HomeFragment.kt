package cn.tklvyou.huaiyuanmedia.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseFragment
import cn.tklvyou.huaiyuanmedia.model.MessageEvent
import cn.tklvyou.huaiyuanmedia.ui.account.LoginActivity
import cn.tklvyou.huaiyuanmedia.ui.adapter.ChannelPagerAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.new_list.GuanZhuFragment
import cn.tklvyou.huaiyuanmedia.ui.home.new_list.SectionListFragment
import cn.tklvyou.huaiyuanmedia.ui.home.publish_news.PublishNewsActivity
import cn.tklvyou.huaiyuanmedia.ui.home.search_list.SearchListActivity
import cn.tklvyou.huaiyuanmedia.ui.listener.OnChannelListener
import cn.tklvyou.huaiyuanmedia.ui.video_edit.CameraActivity
import com.adorkable.iosdialog.BottomSheetDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.fragment_home.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable


class HomeFragment : BaseFragment<HomePresenter>(), HomeContract.View {

    override fun initPresenter(): HomePresenter {
        return HomePresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_home
    }


    private var mSelectedChannels: MutableList<String> = ArrayList()
    private var mUnSelectChannel: MutableList<String> = ArrayList()
    private val mChannelFragments = ArrayList<Fragment>()
    private var mChannelPagerAdapter: ChannelPagerAdapter? = null

    private var commonNavigator: CommonNavigator? = null

    private var isRefresh = false
    private var isChoose = false

    override fun initView() {
        EventBus.getDefault().register(this)

        homeTitleBar.setBackgroundResource(R.drawable.shape_gradient_common_titlebar)
        homeTitleBar.centerSearchView.setBackgroundResource(R.drawable.shape_title_bar_search_radius_5_bg)
        homeTitleBar.centerSearchEditText.hint = SPUtils.getInstance().getString("search", "")
        homeTitleBar.setListener { v, action, extra ->
            when (action) {
                CommonTitleBar.ACTION_SEARCH -> {
                    val intent = Intent(context, SearchListActivity::class.java)
                    intent.putExtra("search", SPUtils.getInstance().getString("search", ""))
                    startActivity(intent)
                }
            }
        }

        homeTitleBar.rightCustomView.visibility = View.GONE

        homeTitleBar.rightCustomView.setOnClickListener {

            if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
                RxPermissions(this)
                        .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                        .subscribe { granted ->
                            if (granted) {
                                // Always true pre-M
                                BottomSheetDialog(context)
                                        .init()
                                        .setCancelable(true)    //设置手机返回按钮是否有效
                                        .setCanceledOnTouchOutside(true)  //设置 点击空白处是否取消 Dialog 显示
                                        //如果条目样式一样，可以直接设置默认样式
                                        .setDefaultItemStyle(BottomSheetDialog.SheetItemTextStyle("#000000", 16))
                                        .setBottomBtnStyle(BottomSheetDialog.SheetItemTextStyle("#ff0000", 18))
                                        .addSheetItem("拍摄") { which ->
                                            val intent = Intent(context, CameraActivity::class.java)
                                            intent.putExtra("page", "原创")
                                            startActivity(intent)
                                            isRefresh = true
                                        }
                                        .addSheetItem("从手机相册选择") { which ->
                                            isChoose = true
                                            // 进入相册 以下是例子：不需要的api可以不写
                                            PictureSelector.create(this@HomeFragment)
                                                    .openGallery(PictureMimeType.ofImage())
                                                    .theme(R.style.picture_default_style)
                                                    .maxSelectNum(9)
                                                    .minSelectNum(1)
                                                    .selectionMode(PictureConfig.MULTIPLE)
                                                    .previewImage(true)
                                                    .isCamera(true)
                                                    .enableCrop(false)
                                                    .compress(true)
                                                    .previewEggs(true)
                                                    .openClickSound(false)
                                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                                        }
                                        .show()
                            } else {
                                ToastUtils.showShort("权限拒绝，无法使用")
                            }
                        }
            } else {
                ToastUtils.showShort("请登录后操作")
                startActivity(Intent(context, LoginActivity::class.java))
            }
        }

        initMagicIndicator()

        initPageFragment()

        ivAddChannel.setOnClickListener {
            if (SPUtils.getInstance().getString("token", "").isEmpty()) {
                ToastUtils.showShort("请登录后操作")
                startActivity(Intent(context, LoginActivity::class.java))
                return@setOnClickListener
            }

            val dialogFragment = ChannelDialogFragment()
            dialogFragment.setOnChannelListener(object : OnChannelListener {
                override fun initChannelList(selectList: MutableList<String>, unSelectList: MutableList<String>) {
                    mSelectedChannels = selectList
                    mUnSelectChannel = unSelectList
//                    flushHomeChannel(mSelectedChannels)
//
//                    LogUtils.e(mSelectedChannels, mUnSelectChannel, mChannelFragments.size)
                }

                override fun onItemMove(starPos: Int, endPos: Int) {
                    listMove(mSelectedChannels, starPos, endPos)
//                    listMoveFragment(mChannelFragments, starPos, endPos)
                }

                override fun onMoveToMyChannel(starPos: Int, endPos: Int) {
                    //移动到我的频道
                    val channel = mUnSelectChannel.removeAt(starPos)
                    mSelectedChannels.add(endPos, channel)
                }

                override fun onMoveToOtherChannel(starPos: Int, endPos: Int) {
                    //移动到推荐频道
                    mUnSelectChannel.add(endPos, mSelectedChannels.removeAt(starPos))
//                    mChannelFragments.removeAt(starPos)
                }

            })
            dialogFragment.show(childFragmentManager, "CHANNEL")
            dialogFragment.setOnDismissListener(DialogInterface.OnDismissListener {
                //                commonNavigator!!.notifyDataSetChanged()
//                mChannelPagerAdapter!!.setData(mChannelFragments)
                if (SPUtils.getInstance().getString("token", "").isNotEmpty()) {
                    val channelBuilder = StringBuilder()
                    for (i in 0 until mSelectedChannels.size) {
                        if (i == mSelectedChannels.size - 1) {
                            channelBuilder.append(mSelectedChannels[i])
                        } else {
                            channelBuilder.append(mSelectedChannels[i] + ",")
                        }
                    }
                    mPresenter.saveHomeChannel(channelBuilder.toString())
                }
            })
        }

        mPresenter.getHomeChannel()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessageEvent(event: MessageEvent) {
        mPresenter.getHomeChannel()
    }

    private fun initPageFragment() {
        mChannelPagerAdapter = ChannelPagerAdapter(mChannelFragments, childFragmentManager)

        mViewPager.adapter = mChannelPagerAdapter
        mViewPager.offscreenPageLimit = mSelectedChannels.size
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {

                if (mSelectedChannels.size > p0) {
                    if (mSelectedChannels[p0] == "原创") {
                        homeTitleBar.rightCustomView.visibility = View.VISIBLE
                    } else {
                        homeTitleBar.rightCustomView.visibility = View.GONE
                    }


                } else {
                    homeTitleBar.rightCustomView.visibility = View.GONE
                }

            }

        })

    }

    override fun lazyData() {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden && !isFirstResume) {
            if (mChannelFragments.size > mViewPager.currentItem) {
                if (mChannelFragments[mViewPager.currentItem] is SectionListFragment) {
                    (mChannelFragments[mViewPager.currentItem] as SectionListFragment).closeAudioController()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isChoose) {
            isChoose = false
            isRefresh = true
        } else {
            if (isRefresh) {
                isRefresh = false
                if (mChannelFragments.size > mViewPager.currentItem) {
                    (mChannelFragments[mViewPager.currentItem] as SectionListFragment).refreshData()
                }
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
        initPageFragment()

        if (mChannelFragments.size > 1) {
            val isLogin = SPUtils.getInstance().getString("token", "").isNotEmpty()
            if (isLogin) {
                magicIndicator.onPageSelected(1)
                mViewPager.setCurrentItem(1, false)
            } else {
                magicIndicator.onPageSelected(0)
                mViewPager.setCurrentItem(0, false)
            }

        } else {
            magicIndicator.onPageSelected(0)
        }


    }


    /**
     * 初始化已选频道的fragment的集合
     */
    private fun initChannelFragments() {
        mChannelFragments.clear()
        val isLogin = SPUtils.getInstance().getString("token", "").isNotEmpty()
        for ((index, item) in mSelectedChannels.withIndex()) {

            val bundle = Bundle()
            bundle.putString("param", item)

            if (isLogin) {
                if (index == 1) {
                    bundle.putBoolean("is_first", true)
                }
            } else {
                if (index == 0) {
                    bundle.putBoolean("is_first", true)
                }
            }

            if (item == "关注") {
                val newsFragment = GuanZhuFragment()
                newsFragment.arguments = bundle
                mChannelFragments.add(newsFragment)//添加到集合中
            } else {

                val newsFragment = SectionListFragment()
                newsFragment.arguments = bundle
                mChannelFragments.add(newsFragment)//添加到集合中
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {

            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val intent = Intent(context, PublishNewsActivity::class.java)
                    intent.putExtra("page", "原创")
                    intent.putExtra("isVideo", false)
                    intent.putExtra("data", selectList as Serializable)
                    startActivity(intent)
                }

            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        commonNavigator = null
        mChannelFragments.clear()
        mChannelPagerAdapter = null
    }


    fun reload() {
        LogUtils.e(mChannelFragments.size, mViewPager.currentItem)
        if (mChannelFragments.size > mViewPager.currentItem) {
            if (mChannelFragments[mViewPager.currentItem] is SectionListFragment) {
                (mChannelFragments[mViewPager.currentItem] as SectionListFragment).refreshData()
            } else if (mChannelFragments[mViewPager.currentItem] is GuanZhuFragment) {
                (mChannelFragments[mViewPager.currentItem] as GuanZhuFragment).refreshData()
            }

        }
    }

    private fun listMove(datas: MutableList<String>, starPos: Int, endPos: Int) {
        val o = datas[starPos]
        //先删除之前的位置
        datas.removeAt(starPos)
        //添加到现在的位置
        datas.add(endPos, o)
    }

    private fun listMoveFragment(datas: MutableList<Fragment>, starPos: Int, endPos: Int) {
        val o = datas[starPos]
        //先删除之前的位置
        datas.removeAt(starPos)
        //添加到现在的位置
        datas.add(endPos, o)
    }

    fun  clickTabCallback(position : Int){
        for (mChannelFragment in mChannelFragments) {
            if(mChannelFragment is SectionListFragment ){
                if(position == 0){
                    mChannelFragment.onUserVisible()
                }else{
                    mChannelFragment.onUserInvisible()
                }

            }
        }
    }
}