package cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity
import cn.tklvyou.huaiyuanmedia.common.Contacts
import cn.tklvyou.huaiyuanmedia.model.PingXuanModel
import cn.tklvyou.huaiyuanmedia.ui.adapter.ChannelPagerAdapter
import cn.tklvyou.huaiyuanmedia.ui.home.ping_xuan.search.PingXuanSearchListActivity
import cn.tklvyou.huaiyuanmedia.utils.BannerGlideImageLoader
import cn.tklvyou.huaiyuanmedia.utils.CountDownUtil
import cn.tklvyou.huaiyuanmedia.utils.InterfaceUtils
import cn.tklvyou.huaiyuanmedia.utils.YBitmapUtils
import cn.tklvyou.huaiyuanmedia.widget.SharePopupWindow
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.share.WbShareCallback
import com.sina.weibo.sdk.share.WbShareHandler
import com.sina.weibo.sdk.utils.Utility
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
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

    private var shareTitle = ""
    private var shareHandler: WbShareHandler? = null
    private var wxapi: IWXAPI? = null


    override fun initView(savedInstanceState: Bundle?) {
        setTitle("", R.mipmap.home_title_logo)
        setNavigationImage()
        setNavigationOnClickListener { finish() }
        setPositiveImage(R.mipmap.icon_title_bar_share)


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
            setPositiveOnClickListener {
                doShare()
            }

            initMagicIndicator(model.content)
        }
        showPage = false

        shareTitle = model.name

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



    private fun doShare() {
        val sharePopupWindow = SharePopupWindow(this)
        sharePopupWindow.setISharePopupWindowClickListener(object : SharePopupWindow.ISharePopupWindowClickListener {
            override fun onWxClick() {
                shareToWX()
                sharePopupWindow.dismiss()
            }

            override fun onWxFriendClick() {
                shareToWXFriend()
                sharePopupWindow.dismiss()
            }

            override fun onQQClick() {
                shareToQQ()
                sharePopupWindow.dismiss()
            }

            override fun onWBClick() {
                shareToWB()
                sharePopupWindow.dismiss()
            }


        })
        sharePopupWindow.showAtScreenBottom(mSmartRefreshLayout)
    }


    /**
     * 判断是否安装微信
     */
    private fun isWeiXinAppInstall(): Boolean {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(this, Contacts.WX_APPID)
        if (wxapi!!.isWXAppInstalled) {
            return true
        } else {
            ToastUtils.showShort("您未安装微信客户端")
            return false
        }
    }


    private fun shareToWXFriend() {
        if (!isWeiXinAppInstall()) {
            ToastUtils.showShort("您未安装微信")
            return
        }
        if (!isWXAppSupportAPI()) {
            ToastUtils.showShort("您的微信版本不支持分享功能")
            return
        }

        val webpage = WXWebpageObject()
        webpage.webpageUrl =  Contacts.SHARE_SELECTION_URL + id
        val msg = WXMediaMessage(webpage)
        msg.title = shareTitle
        msg.description = "榴乡怀远"
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.img_logo)
        val thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true)
        bmp.recycle()
        msg.thumbData = ImageUtils.bitmap2Bytes(YBitmapUtils.changeColor(thumbBmp), Bitmap.CompressFormat.JPEG)
        val req = SendMessageToWX.Req()
        req.transaction = "webpage" + System.currentTimeMillis()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneTimeline
        wxapi!!.sendReq(req)

        InterfaceUtils.getInstance().add(onClickResult)
    }


    /**
     * 是否支持分享到朋友圈
     */
    fun isWXAppSupportAPI(): Boolean {
        if (isWeiXinAppInstall()) {
            val wxSdkVersion = wxapi!!.wxAppSupportAPI
            return wxSdkVersion >= 0x21020001
        } else {
            return false
        }
    }


    private var mTencent: Tencent? = null
    private fun shareToQQ() {
        mTencent = Tencent.createInstance(Contacts.QQ_APPID, application)

        if (!mTencent!!.isQQInstalled(this)) {
            ToastUtils.showShort("您未安装QQ客户端")
            return
        }

        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle)
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "摘要") //可选，最长40个字
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  Contacts.SHARE_SELECTION_URL
                + id) //必填 	这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "榴乡怀远")
        mTencent!!.shareToQQ(this, params, object : IUiListener {
            override fun onComplete(p0: Any?) {
//                mPresenter.getScoreByShare(id)
                ToastUtils.showShort("分享成功")
            }

            override fun onCancel() {
                ToastUtils.showShort("取消分享")
            }

            override fun onError(p0: UiError?) {
                ToastUtils.showShort("分享失败：" + p0?.errorMessage)
            }

        })

    }

    private fun shareToWX() {
        if (!isWeiXinAppInstall()) {
            ToastUtils.showShort("您未安装微信")
            return
        }
        if (!isWXAppSupportAPI()) {
            ToastUtils.showShort("您的微信版本不支持分享功能")
            return
        }

        val webpage = WXWebpageObject()
        webpage.webpageUrl =  Contacts.SHARE_SELECTION_URL + id
        val msg = WXMediaMessage(webpage)
        msg.title = shareTitle
        msg.description = "榴乡怀远"
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.img_logo)
        val thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true)
        bmp.recycle()
        msg.thumbData = ImageUtils.bitmap2Bytes(YBitmapUtils.changeColor(thumbBmp), Bitmap.CompressFormat.JPEG)
        val req = SendMessageToWX.Req()
        req.transaction = "webpage" + System.currentTimeMillis()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession
        wxapi!!.sendReq(req)

        InterfaceUtils.getInstance().add(onClickResult)

    }

    private fun shareToWB() {
        val pinfo = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
        var isInstall = false
        if (pinfo != null) {
            pinfo.forEach {

                val pn = it.packageName
                if (pn == "com.sina.weibo") {
                    isInstall = true
                }
            }
        }
        if (!isInstall) {
            ToastUtils.showShort("您未安装微博")
        } else {
            shareHandler = WbShareHandler(this)
            shareHandler!!.registerApp()

            val weiboMessage = WeiboMultiMessage()

            val mediaObject = WebpageObject()
            mediaObject.identify = Utility.generateGUID()
            mediaObject.title = shareTitle
            mediaObject.description = "榴乡怀远"
            val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.default_avatar)
            mediaObject.setThumbImage(YBitmapUtils.changeColor(bitmap))
            mediaObject.actionUrl =  Contacts.SHARE_SELECTION_URL + id
            mediaObject.defaultText = "Webpage"
            weiboMessage.mediaObject = mediaObject

            shareHandler!!.shareMessage(weiboMessage, false)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (shareHandler != null) {
            shareHandler!!.doResultIntent(intent, object : WbShareCallback {
                override fun onWbShareFail() {
                    ToastUtils.showShort("分享失败")
                }

                override fun onWbShareCancel() {
                    ToastUtils.showShort("取消分享")
                }

                override fun onWbShareSuccess() {
                    ToastUtils.showShort("分享成功")
                }

            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (mTencent != null) {
            Tencent.onActivityResultData(requestCode, resultCode, data, null)
        }
    }

    private var onClickResult = object : InterfaceUtils.OnClickResult {
        override fun onResult(msg: String?) {
            ToastUtils.showShort("分享成功")
            InterfaceUtils.getInstance().remove(this)
        }

    }



}
